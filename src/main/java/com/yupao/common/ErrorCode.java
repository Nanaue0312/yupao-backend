package com.yupao.common;

/**
 * @Date 2023/2/22
 * @Author zcy
 * @Description 全局错误码
 */
public enum ErrorCode {
    SUCCESS(200, "ok", ""), PARAMS_ERROR(40000, "请求参数错误", ""), NULL_ERROR(40001, "请求数据为空", ""),
    NO_LOGIN(40100, "用户未登录", ""), NO_AUTH(40101, "无权限", ""), SYSTEM_ERROR(50000, "系统内部异常", "");

    private final int code;
    private final String msg;
    private final String description;

    ErrorCode(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }
}
