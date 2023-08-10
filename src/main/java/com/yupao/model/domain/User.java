package com.yupao.model.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户性别：0-女，1-男
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机号
     */
    private String tele;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 状态：0-删除，1-正常，2-封禁
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
     * 逻辑删除：0-删除，1-正常
     */
    private Integer isDelete;

    /**
     * 用户角色：0-普通用户，1-管理员
     */
    private Integer userRole;

    /**
     * 用户拥有的标签
     */
    private String tags;

    /**
     * 星球id
     */
    private String planetCode;
    /**
     * 个人简介
     */
    private String profile;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}