package com.yuyue.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import com.yuyue.backend.entity.AppointmentEntity;

import java.util.Map;

/**
 * 
 *
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
public interface AppointmentService extends IService<AppointmentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

