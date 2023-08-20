package com.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao.common.BaseResponse;
import com.yupao.common.ErrorCode;
import com.yupao.common.ResultUtils;
import com.yupao.exception.BusinessException;
import com.yupao.model.VO.UserToTeamVO;
import com.yupao.model.domain.Team;
import com.yupao.model.domain.User;
import com.yupao.model.domain.UserToTeam;
import com.yupao.model.dto.TeamQuery;
import com.yupao.model.request.AddTeamRequest;
import com.yupao.model.request.JoinTeamRequest;
import com.yupao.model.request.QuitTeamRequest;
import com.yupao.model.request.UpdateTeamRequest;
import com.yupao.service.TeamService;
import com.yupao.service.UserService;
import com.yupao.service.UserToTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/team")
@RestController
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserToTeamService userToTeamService;

    @PostMapping
    public BaseResponse<Long> createTeam(@RequestBody AddTeamRequest addTeamRequest, HttpServletRequest request) {
        if (addTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(addTeamRequest, team);
        return ResultUtils.success(teamService.createTeam(team, loginUser));
    }

    @DeleteMapping
    public BaseResponse<Boolean> deleteTeam(Long teamId, HttpServletRequest request) {
        if (teamId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.deleteTeam(teamId, loginUser);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍失败");
        }
        return ResultUtils.success(true);
    }

    @PutMapping
    public BaseResponse<Boolean> updateTeam(@RequestBody UpdateTeamRequest updateTeamRequest, HttpServletRequest request) {
        if (updateTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.updateTeam(updateTeamRequest, loginUser);
        if (!res) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍信息失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping
    public BaseResponse<Team> getTeamById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<UserToTeamVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<UserToTeamVO> teamList = teamService.listTeams(teamQuery);
        /**
         * 队伍idList
         */
        List<Long> teamIdList = teamList.stream().map(UserToTeamVO::getId).collect(Collectors.toList());
        // 查询当前用户是否已经加入队伍
        try {
            User loginUser = userService.getLoginUser(request);
            LambdaQueryWrapper<UserToTeam> userToTeamQueryWrapper = new LambdaQueryWrapper<>();
            userToTeamQueryWrapper.eq(UserToTeam::getUserId, loginUser.getId());
            userToTeamQueryWrapper.in(UserToTeam::getTeamId, teamIdList);
            List<UserToTeam> joinedTeamList = userToTeamService.list(userToTeamQueryWrapper);
            List<Long> joinedTeamIdList = joinedTeamList.stream().map(UserToTeam::getTeamId).collect(Collectors.toList());
            teamList.forEach(team -> team.setIsJoined(joinedTeamIdList.contains(team.getId())));
        } catch (Exception e) {
        }
        LambdaQueryWrapper<UserToTeam> userJoinedCountQueryWrapper = new LambdaQueryWrapper<>();
        userJoinedCountQueryWrapper.in(UserToTeam::getTeamId, teamIdList);

        // 获取加入队伍的人数
        Map<Long, List<UserToTeam>> userJoinedMap = userToTeamService.list(userJoinedCountQueryWrapper).stream().collect(Collectors.groupingBy(UserToTeam::getTeamId));
        teamList.forEach(team -> team.setJoinedCount(Optional.of(userJoinedMap.get(team.getId()).size()).orElse(0)));
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(teamQuery, team);
        } catch (BeansException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        return ResultUtils.success(teamService.page(
                new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()), queryWrapper)
        );
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if (joinTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.joinTeam(joinTeamRequest, loginUser);
        return ResultUtils.success(res);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody QuitTeamRequest quitTeamRequest, HttpServletRequest request) {
        if (quitTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.quitTeam(quitTeamRequest, loginUser);
        return ResultUtils.success(res);
    }

    /**
     * 获取用户创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my")
    public BaseResponse<List<UserToTeamVO>> listOwnTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        teamQuery.setUserId(loginUser.getId());
        List<UserToTeamVO> teamList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/joined")
    public BaseResponse<List<UserToTeamVO>> listJoinedTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        LambdaQueryWrapper<UserToTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserToTeam::getUserId, loginUser.getId());
        List<UserToTeam> userToTeamList = userToTeamService.list(queryWrapper);
        List<Long> teamIds = userToTeamList.stream().map(UserToTeam::getTeamId).collect(Collectors.toList());
        teamQuery.setTeamIds(teamIds);
        List<UserToTeamVO> teamList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamList);
    }
}
