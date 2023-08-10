package com.yupao.script;

import com.yupao.mapper.UserMapper;
import com.yupao.model.domain.User;
import com.yupao.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;

@SpringBootTest
class InsertUsersTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @Test
    void handleInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        final long NUM = 1000L;
        stopWatch.start();
        ArrayList<User> users = new ArrayList<>();
        for (long i = 0; i < NUM; i++) {
            User user = new User();
            user.setId(i + 100);
            user.setUsername("测试数据" + i);
            user.setUserAccount("ceshiyonghu");
            user.setAvatarUrl("");
            user.setGender(1);
            user.setUserPassword("12345678");
            user.setTele("123456");
            user.setEmail("123123@qq.com");
            user.setTags("[]");
            user.setPlanetCode("5544");
            user.setProfile("测试数据");
            users.add(user);
        }
        userService.saveBatch(users);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}