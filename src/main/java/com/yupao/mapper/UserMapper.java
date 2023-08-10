package com.yupao.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupao.model.domain.User;

/**
 * @author zcy
 * @description 针对表【user】的数据库操作Mapper
 * @createDate 2023-07-24 20:07:23
 * @Entity com.yupao.model.domain.User
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

}
