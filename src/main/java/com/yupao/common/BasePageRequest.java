package com.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class BasePageRequest implements Serializable {

    private static final long serialVersionUID = -7447271651773839664L;
    /**
     * 页面大小
     */
    protected int pageSize = 20;
    /**
     * 当前页码
     */
    protected int pageNum = 1;
}
