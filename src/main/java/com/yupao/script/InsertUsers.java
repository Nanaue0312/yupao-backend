package com.yupao.script;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupao.common.ErrorCode;
import com.yupao.exception.BusinessException;
import com.yupao.model.domain.User;
import com.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class InsertUsers {
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    /**
     * 重点用户id列表
     */
    private List<Long> primaryUserIds = Arrays.asList(1L);
    private static final Gson gson = new Gson();

    /**
     * 批量插入用户
     */
    //@Scheduled
    /*public void handleInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        final Long NUM = 10000L;
        stopWatch.start();
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
            userMapper.insert(user);
        }
        stopWatch.stop();
        log.error(String.valueOf(stopWatch.getTotalTimeMillis()));
    }*/
    @Scheduled(cron = "0 18 * * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("yupao:precachejob:recommend:lock");
        try {
            if (lock.tryLock(0L, 1L, TimeUnit.MINUTES)) {
                System.out.println("getlock:" + Thread.currentThread().getName());
                for (Long userId : primaryUserIds) {
                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<User>(1L, 20L), userQueryWrapper);
                    String json = gson.toJson(userPage);
                    try {
                        stringRedisTemplate.opsForValue().set("yupao:user:recommend:" + userId, json, 30, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("redis cache failed in yupao:user:recommend:" + userId);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            // 只有自己的锁才能释放
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unlock:" + Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }
}
