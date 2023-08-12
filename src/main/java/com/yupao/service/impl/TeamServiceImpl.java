package com.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao.common.ErrorCode;
import com.yupao.exception.BusinessException;
import com.yupao.mapper.TeamMapper;
import com.yupao.model.VO.UserToTeamVO;
import com.yupao.model.VO.UserVO;
import com.yupao.model.domain.Team;
import com.yupao.model.domain.User;
import com.yupao.model.domain.UserToTeam;
import com.yupao.model.dto.TeamQuery;
import com.yupao.model.enums.TeamStatusEnum;
import com.yupao.model.request.JoinTeamRequest;
import com.yupao.model.request.UpdateTeamRequest;
import com.yupao.service.TeamService;
import com.yupao.service.UserService;
import com.yupao.service.UserToTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zcy
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2023-08-10 15:33:37
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserToTeamService userToTeamService;
    @Resource
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long createTeam(Team team, User loginUser) {
        //1. 请求参数校验
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //2. 是否登录,否则跳转到登录页
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        //3. 校验信息
        //        - 队伍人数>1<=5
        Integer maxCount = Optional.ofNullable(team.getMaxCount()).orElse(0);
        if (maxCount < 1 || maxCount > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数小于1人,或大于5人");
        }
        //        - 标题<=10
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题长度超过10");
        }
        //        - 描述<=200
        String description = team.getDescription();
        if (StringUtils.isBlank(description) || description.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述信息过长");
        }
        //        - 队伍类型:默认公开,私密,加密
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnum(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //        - 如果是加密类型,密码<=10
        if (statusEnum.getStatus().equals(2) && (StringUtils.isBlank(team.getPassword()) || team.getPassword().length() > 10)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过长");
        }
        //        - 超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间大于当前时间");
        }
        // 创建用户和当前登录用户id一致
        final Long userId = loginUser.getId();
        if (!team.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //        - 校验用户最多创建5个队伍
        // todo 可能同时创建多个用户
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        long createdTeamNum = count(queryWrapper);
        if (createdTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法再创建更多队伍");
        }
        //4. 添加队伍信息到队伍列表
        team.setId(null);
        boolean save = save(team);
        Long teamId = team.getId();
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "新建队伍信息失败");
        }
        //5. 添加**队伍<=>队员**到user_to_team表
        UserToTeam userToTeam = new UserToTeam();
        userToTeam.setUserId(team.getUserId());
        userToTeam.setTeamId(teamId);
        userToTeam.setJoinTime(new Date());
        boolean userToTeamRes = userToTeamService.save(userToTeam);
        if (!userToTeamRes) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存队伍信息失败");
        }
        return teamId;
    }

    @Override
    public List<UserToTeamVO> listTeams(TeamQuery teamQuery) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            String description = teamQuery.getDescription();
            Long userId = teamQuery.getUserId();
            String name = teamQuery.getName();
            Integer maxCount = teamQuery.getMaxCount();
            Integer status = teamQuery.getStatus();
            String searchText = teamQuery.getSearchText();
            queryWrapper.and(StringUtils.isNotBlank(searchText),
                    qw -> qw.like(Team::getName, searchText).or().like(Team::getDescription, searchText));
            queryWrapper.eq(id != null && id > 0, Team::getId, id);
            queryWrapper.like(StringUtils.isNotBlank(name), Team::getName, name);
            queryWrapper.like(StringUtils.isNotBlank(description), Team::getDescription, description);
            // 查询最大人数相等的
            queryWrapper.eq(maxCount != null && maxCount > 0, Team::getMaxCount, maxCount);
            // 根据创建者查询
            queryWrapper.eq(userId != null && userId > 0, Team::getUserId, userId);
            queryWrapper.eq(TeamStatusEnum.getTeamStatusEnum(status) != null
                    , Team::getStatus, status);
        }
        // 不展示过期队伍信息
        // select * from team where expireTime > current_time||expireTime==null
        queryWrapper.and(qw -> qw.ge(Team::getExpireTime, LocalDateTime.now()).or().isNull(Team::getExpireTime));
        List<Team> teamList = list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return Collections.emptyList();
        }
        // select * from team left join user_to_team ut on team.id==ut.teamId left join user on ut.userId == user.id
        ArrayList<UserToTeamVO> userToTeamVOS = new ArrayList<>();

        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            // 脱敏用户信息
            User safetyUser = userService.getSafetyUser(user);
            UserToTeamVO userToTeamVO = new UserToTeamVO();
            BeanUtils.copyProperties(team, userToTeamVO);
            UserVO userVO = new UserVO();
            if (user != null) {
                BeanUtils.copyProperties(safetyUser, userVO);
                userToTeamVO.setCreateUser(userVO);
                userToTeamVOS.add(userToTeamVO);
            }
        }


        return userToTeamVOS;
    }

    @Override
    public boolean updateTeam(UpdateTeamRequest updateTeamRequest, User loginUser) {
        Long id = updateTeamRequest.getId();
        Integer status = updateTeamRequest.getStatus();
        String description = updateTeamRequest.getDescription();
        String name = updateTeamRequest.getName();
        Date expireTime = updateTeamRequest.getExpireTime();
        String password = updateTeamRequest.getPassword();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (oldTeam.getStatus().equals(TeamStatusEnum.SECRET.getStatus()) && StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(updateTeamRequest, updateTeam);
        LambdaUpdateWrapper<Team> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(TeamStatusEnum.getTeamStatusEnum(status) != null, Team::getStatus, status);
        updateWrapper.set(StringUtils.isNotBlank(description), Team::getDescription, description);
        updateWrapper.set(StringUtils.isNotBlank(name), Team::getName, name);
        updateWrapper.set(new Date().before(expireTime), Team::getExpireTime, expireTime);
        updateWrapper.set(StringUtils.isNotBlank(password), Team::getPassword, password);
        return update(null, updateWrapper);
    }

    @Override
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser) {
        if (joinTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = joinTeamRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnum(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "这个私有队伍，您无法加入");
        }
        String password = joinTeamRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) &&
                (StringUtils.isBlank(password) || !password.equals(team.getPassword()))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");

        }
        // 用户已加入队伍的数量
        Long userId = loginUser.getId();
        LambdaQueryWrapper<UserToTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null && userId > 0, UserToTeam::getUserId, userId);
        long userJoinedCount = userToTeamService.count(queryWrapper);
        if (userJoinedCount > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已创建或加入5个队伍，无法继续加入");
        }
        // 不能重复加入队伍
        queryWrapper.eq(UserToTeam::getTeamId, teamId);
        boolean isDuplicateJoin = userToTeamService.count(queryWrapper) > 0;
        if (isDuplicateJoin) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入队伍");
        }
        // 已加入队伍的人数
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserToTeam::getTeamId, teamId);
        long teamJoinedCount = userToTeamService.count(queryWrapper);
        if (teamJoinedCount >= team.getMaxCount()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前队伍人数已满");
        }

        UserToTeam userToTeam = new UserToTeam();
        userToTeam.setUserId(userId);
        userToTeam.setTeamId(teamId);
        userToTeam.setJoinTime(new Date());
        return userToTeamService.save(userToTeam);
    }
}




