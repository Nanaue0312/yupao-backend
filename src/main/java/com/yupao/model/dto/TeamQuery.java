package com.yupao.model.dto;

import com.yupao.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends BasePageRequest {
    /**
     * 队伍id
     */
    private Long id;
    /**
     * 搜索关键词，同时对队伍名称和描述搜索
     */
    private String searchText;
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
     * 创建者用户id
     */
    private Long userId;

    /**
     * 状态：0-公开，1-私密，2-加密
     */
    private Integer status;


}
