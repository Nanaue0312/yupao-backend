package com.yupao.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @Date 2023/2/15
 * @Author zcy
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 8272815022658676052L;

    private String userAccount;
    private String userPassword;

}
