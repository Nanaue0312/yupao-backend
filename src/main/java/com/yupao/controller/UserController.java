package com.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupao.common.BaseResponse;
import com.yupao.common.ErrorCode;
import com.yupao.common.ResultUtils;
import com.yupao.constant.UserConstant;
import com.yupao.exception.BusinessException;
import com.yupao.model.domain.User;
import com.yupao.model.domain.request.UserLoginRequest;
import com.yupao.model.domain.request.UserRegisterRequest;
import com.yupao.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final Gson gson = new Gson();

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 新用户id
     */
    @PostMapping("/register")
    public BaseResponse userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);

        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLoginOut(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userService.userLogout(userId);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);

        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.getById(currentUser.getId());
        // 检验用户是否存在
        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验数据是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean res = userService.updateUser(user, loginUser);
        return ResultUtils.success(res);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "该用户没有权限");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            userQueryWrapper.like("username", username);
        }
        List<User> users = userService.list(userQueryWrapper);
        List<User> safetyUsers =
                users.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(safetyUsers);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> searchRecommendUser(Long pageSize, Long pageNum, HttpServletRequest request) {
        // 先判断缓存中是否有数据
        String redisKey = String.format("yupao:user:recommend:%s", userService.getLoginUser(request).getId());
        String recommendUsersPageJson = stringRedisTemplate.opsForValue().get(redisKey);
        Page<User> recommendUsersPage = null;
        if (StringUtils.isNotBlank(recommendUsersPageJson)) {
            recommendUsersPage = gson.fromJson(recommendUsersPageJson,
                    new TypeToken<Page<User>>() {
                    }.getType());
        }
        if (recommendUsersPage != null) {
            return ResultUtils.success(recommendUsersPage);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (pageNum == null) {
            pageNum = 1L;
        }
        if (pageSize == null) {
            pageSize = 20L;
        }
        recommendUsersPage = userService.page(new Page<>(pageNum, pageSize), userQueryWrapper);
        recommendUsersPage.setRecords(recommendUsersPage.getRecords().stream()
                .map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList()));

        stringRedisTemplate.opsForValue().set(redisKey, gson.toJson(recommendUsersPage), 30L, TimeUnit.MINUTES);
        return ResultUtils.success(recommendUsersPage);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>>
    searchUserByTags(@RequestParam(value = "tagNameList", required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "该用户没有权限");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能小于0");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }


}
