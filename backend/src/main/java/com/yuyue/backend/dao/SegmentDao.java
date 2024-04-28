package com.yuyue.backend.dao;

import com.yuyue.backend.entity.SegmentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
@Mapper
public interface SegmentDao extends BaseMapper<SegmentEntity> {
    int updateSegmentStatusAndUser(@Param("tId") int tId, @Param("uName") String uName, @Param("uId") int uId);
    int updateMultiSegments(@Param("tIds") List<Integer> tIds, @Param("uName") String uName, @Param("uId") int uId);

}
