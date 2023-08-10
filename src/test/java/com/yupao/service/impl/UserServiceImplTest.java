package com.yupao.service.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.yupao.model.domain.User;
import com.yupao.service.UserService;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Test
    void searchUserByTags() {
        List<User> userList = userService.searchUserByTags(Arrays.asList("java", "python"));
        Assert.notNull(userList);
    }
}