package com.yupao.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AddTeamRequest implements Serializable {
    private static final long serialVersionUID = -8243415474173298632L;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 队伍描述信息
     */
    private String description;

    /**
     * 最大用户数
     */
    private Integer maxCount;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建者用户id
     */
    private Long userId;

    /**
     * 状态：0-公开，1-私密，2-加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
