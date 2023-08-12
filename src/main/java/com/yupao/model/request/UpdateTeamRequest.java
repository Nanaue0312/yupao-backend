package com.yupao.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UpdateTeamRequest implements Serializable {
    private static final long serialVersionUID = 5245667039712811353L;
    /**
     * 队伍id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述信息
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 状态：0-公开，1-私密，2-加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
