package com.yuyue.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyue.backend.entity.Book;
import com.yuyue.backend.entity.BookAndSegment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookAndSegmentDao extends BaseMapper<BookAndSegment> {
}
