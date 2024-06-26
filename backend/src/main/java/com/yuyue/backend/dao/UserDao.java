package com.yuyue.backend.dao;

import com.yuyue.backend.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
    int getBookCount(@Param("teacherName") String teacherName);

    void bookCountPlus(@Param("uId") int uId, @Param("number") int number);
}
