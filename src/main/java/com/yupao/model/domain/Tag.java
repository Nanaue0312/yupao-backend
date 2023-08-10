package com.yupao.model.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

/**
 * 
 * @TableName tag
 */
@TableName(value = "tag")
@Data
public class Tag implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标签名
     */
    private String name;

    /**
     * 种类id
     */
    private Integer categoryId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 当前状态：0-删除，1-正常
     */
    @TableLogic
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}