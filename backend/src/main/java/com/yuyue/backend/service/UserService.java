package com.yuyue.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuyue.backend.entity.WeChatSession;
import com.yuyue.backend.vo.RegisterVo;
import io.renren.common.utils.PageUtils;
import com.yuyue.backend.entity.UserEntity;

import java.util.Map;

/**
 * 
 *
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void bookCountPlus(UserEntity user);

    WeChatSession getSessionInfo(String code);

    UserEntity getUserByVID(String v_id);

    String register(RegisterVo registerVo);

    int getBookCount(String teacherName);

    void fresh_count();




}

