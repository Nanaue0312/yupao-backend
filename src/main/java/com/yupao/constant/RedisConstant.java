package com.yupao.constant;

import static com.yupao.constant.UserConstant.USER_LOGIN_STATE;

public interface RedisConstant {
    String SESSION_USER_LOGIN_STATE = "session:user:" + USER_LOGIN_STATE + ":";
    Long USER_LOGIN_STATE_EXPIRE = 10080L; // minutes
}
