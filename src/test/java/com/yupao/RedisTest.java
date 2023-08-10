package com.yupao;

import com.google.gson.Gson;
import com.yupao.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        User user = new User();
        user.setId(1L);
        user.setUsername("yupi");
        Gson gson = new Gson();

        stringRedisTemplate.opsForValue().set("yupi", gson.toJson(user), 30L, TimeUnit.MINUTES);
        Object yupi = stringRedisTemplate.opsForValue().get("yupi");
        Assertions.assertNotNull(yupi);
    }
}
