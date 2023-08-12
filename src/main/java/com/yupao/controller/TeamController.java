package com.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao.common.BaseResponse;
import com.yupao.common.ErrorCode;
import com.yupao.common.ResultUtils;
import com.yupao.exception.BusinessException;
import com.yupao.model.VO.UserToTeamVO;
import com.yupao.model.domain.Team;
import com.yupao.model.domain.User;
import com.yupao.model.dto.TeamQuery;
import com.yupao.model.request.AddTeamRequest;
import com.yupao.model.request.JoinTeamRequest;
import com.yupao.model.request.UpdateTeamRequest;
import com.yupao.service.TeamService;
import com.yupao.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/team")
@RestController
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

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
    public BaseResponse<Boolean> deleteTeam(Long teamId) {
        if (teamId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean res = teamService.removeById(teamId);
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
    public BaseResponse<Boolean> joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if (joinTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = teamService.joinTeam(joinTeamRequest, loginUser);
        return ResultUtils.success(res);
    }
}
