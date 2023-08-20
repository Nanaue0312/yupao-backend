package com.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupao.model.VO.UserToTeamVO;
import com.yupao.model.domain.Team;
import com.yupao.model.domain.User;
import com.yupao.model.dto.TeamQuery;
import com.yupao.model.request.JoinTeamRequest;
import com.yupao.model.request.QuitTeamRequest;
import com.yupao.model.request.UpdateTeamRequest;

import java.util.List;

/**
 * @author zcy
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2023-08-10 15:33:37
 */
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     *
     * @param team      创建的队伍信息
     * @param loginUser 当前用户
     * @return 是否创建成功
     */
    long createTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery 搜索条件
     * @return 查询到的队伍列表
     */
    List<UserToTeamVO> listTeams(TeamQuery teamQuery);

    /**
     * 更新队伍信息
     *
     * @param updateTeamRequest 更新的队伍信息
     * @param loginUser         登录用户
     * @return 是否更新成功
     */
    boolean updateTeam(UpdateTeamRequest updateTeamRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param joinTeamRequest 请求信息
     * @param loginUser       登录用户
     * @return 是否成功
     */
    boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param quitTeamRequest 退出队伍请求体
     * @param loginUser       登录用户
     * @return 是否成功
     */
    boolean quitTeam(QuitTeamRequest quitTeamRequest, User loginUser);

    /**
     * 删除(解散)队伍
     *
     * @param teamId    队伍id
     * @param loginUser 登录用户
     * @return 是否删除成功
     */
    boolean deleteTeam(Long teamId, User loginUser);
}
