package com.yupao.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupao.model.domain.Tag;

/**
 * @author zcy
 * @description 针对表【tag】的数据库操作Mapper
 * @createDate 2023-07-24 20:05:13
 * @Entity com.yupao.model.domain.Tag
 */
@Repository
public interface TagMapper extends BaseMapper<Tag> {

}
