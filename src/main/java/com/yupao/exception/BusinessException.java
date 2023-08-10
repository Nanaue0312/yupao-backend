package com.yupao.exception;

import com.yupao.common.ErrorCode;

/**
 * @Date 2023/2/23
 * @Author zcy
 * @Description 自定义异常类
 */
public class BusinessException extends RuntimeException {
    private final int code;
    private final String description;

    public BusinessException(String msg, int code, String description) {
        super(msg);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
