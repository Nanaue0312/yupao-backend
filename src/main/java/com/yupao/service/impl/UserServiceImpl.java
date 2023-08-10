package com.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupao.common.ErrorCode;
import com.yupao.constant.RedisConstant;
import com.yupao.constant.UserConstant;
import com.yupao.exception.BusinessException;
import com.yupao.mapper.UserMapper;
import com.yupao.model.domain.User;
import com.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zcy
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-07-24 20:07:23
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "zcy";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1.非空校验
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度小于8位");
        }
        if (planetCode.length() > 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "^[\\w-]{4,16}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户不能包含特殊字符");
        }
        // 账户不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户已存在");
        }
        // 星球编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planet_code", planetCode);
        count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已被使用");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        if (!this.save(user)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户保存失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.非空校验
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度小于8位");
        }
        // 账户不能包含特殊字符
        String validPattern = "^[\\w-]{4,16}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户不能包含特殊字符");
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount).eq("user_password", encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed，用户名密码不匹配");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名密码不匹配");
        }
        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4.记录用户登录态，redis分布式session
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(Long userId) {
        stringRedisTemplate.delete(RedisConstant.SESSION_USER_LOGIN_STATE + userId);
        return 1;
    }

    /**
     * 根据标签搜索用户（内存过滤）
     *
     * @param tagNameList 标签列表
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = list();
        Gson gson = new Gson();
        return users.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            Set<String> tagSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            tagSet = Optional.ofNullable(tagSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tagSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public boolean updateUser(User user, User loginUser) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Field[] fields = user.getClass().getDeclaredFields();
        boolean allPropAreNull = true;
        try {
            for (Field field : fields) {
                // 排除 id 属性
                if (!field.getName().equals("id") && !field.getName().equals("serialVersionUID")) {
                    field.setAccessible(true);
                    Object value = field.get(user);
                    if (value != null) {
                        allPropAreNull = false;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (allPropAreNull) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = user.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 仅管理员和本人可以修改
        // 判断更新用户的id是否存在
        User oldUser = getById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return updateById(user);
    }


    /**
     * 根据标签搜索用户（SQL查询）
     *
     * @param tagNameList 标签列表
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySql(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        // 拼接and查询
        for (String tagName : tagNameList) {
            userQueryWrapper = userQueryWrapper.like("tags", tagName);
        }
        List<User> users = list(userQueryWrapper);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        return user;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return (user != null && Objects.equals(user.getUserRole(), UserConstant.ADMIN_ROLE));
    }

    /**
     * 是否为管理员
     *
     * @param loginUser 当前登录用户
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return (loginUser != null && Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE));
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setTele(originUser.getTele());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setProfile(originUser.getProfile());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        return safetyUser;
    }
}
