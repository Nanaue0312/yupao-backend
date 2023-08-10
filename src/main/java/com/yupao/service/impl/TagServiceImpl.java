package com.yupao.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao.mapper.TagMapper;
import com.yupao.model.domain.Tag;
import com.yupao.service.TagService;

/**
 * @author zcy
 * @description 针对表【tag】的数据库操作Service实现
 * @createDate 2023-07-24 20:05:13
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
