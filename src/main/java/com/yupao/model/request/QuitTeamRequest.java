package com.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出登录请求体
 */
@Data
public class QuitTeamRequest implements Serializable {
    private static final long serialVersionUID = -1689236838266689159L;
    /**
     * 队伍id
     */
    private Long teamId;
}
