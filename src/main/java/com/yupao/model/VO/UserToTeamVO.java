package com.yupao.model.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户信息封装类(脱敏)
 */
@Data
public class UserToTeamVO implements Serializable {
    private static final long serialVersionUID = 385125388252478222L;
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
    private Long userid;

    /**
     * 状态：0-公开，1-私密，2-加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 队伍的创建者
     */
    private UserVO createUser;
}
