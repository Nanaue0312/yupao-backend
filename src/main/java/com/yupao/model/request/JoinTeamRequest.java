package com.yupao.model.request;

import lombok.Data;

@Data
public class JoinTeamRequest {
    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 队伍密码
     */
    private String password;

}
