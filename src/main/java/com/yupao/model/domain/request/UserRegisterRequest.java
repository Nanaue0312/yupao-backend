package com.yupao.model.domain.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户注册请求体
 * 
 * @Date 2023/2/15
 * @Author zcy
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 2042283950567710186L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
