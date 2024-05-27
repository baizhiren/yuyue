package com.yuyue.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyue.backend.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookDao extends BaseMapper<Book> {
}
