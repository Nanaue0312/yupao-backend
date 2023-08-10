package com.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date 2023/2/22
 * @Author zcy
 * @Description 通用返回类
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -2178300884716240685L;
    private Integer code;
    private T data;
    private String msg;
    private String description;

    public BaseResponse(Integer code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;

    }

    public BaseResponse(Integer code, T data, String msg) {
        this(code, data, msg, "");
    }

    public BaseResponse(Integer code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMsg(), errorCode.getDescription());
    }
}
