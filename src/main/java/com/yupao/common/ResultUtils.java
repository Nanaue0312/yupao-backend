package com.yupao.common;

/**
 * @Date 2023/2/22
 * @Author zcy
 * @Description 返回工具类
 */
public class ResultUtils {
    /**
     * 成功
     * 
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "ok");
    }

    /**
     * 失败
     * 
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String msg, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, msg, description);
    }

    /**
     * 失败
     * 
     * @param errorCode
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMsg(), description);
    }

    /**
     * 失败
     * 
     * @param code
     * @param msg
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String msg, String description) {
        return new BaseResponse<>(code, null, msg, description);
    }
}
