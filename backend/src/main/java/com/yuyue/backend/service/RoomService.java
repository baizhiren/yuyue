package com.yuyue.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import com.yuyue.backend.entity.RoomEntity;

import java.util.Map;

/**
 * 
 *
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
public interface RoomService extends IService<RoomEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

