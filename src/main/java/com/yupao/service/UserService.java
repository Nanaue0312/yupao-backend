package com.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupao.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zcy
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-07-24 20:07:23
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验码
     * @param planetCode    星球编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser 原user
     * @return 脱敏后的user
     */
    User getSafetyUser(User originUser);

    /**
     * 移除登录态
     *
     * @param userId 当前用户id
     */
    int userLogout(Long userId);

    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     *
     * @param user      需要更新的用户信息
     * @param loginUser 当前登录用户
     * @return 是否更新成功
     */
    boolean updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户
     *
     * @param request 获取用户
     * @return
     */
    public User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser 当前登录用户
     * @return
     */
    boolean isAdmin(User loginUser);
}
