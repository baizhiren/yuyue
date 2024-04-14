package com.yuyue.backend.service.impl;

import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.service.SessionService;
import com.yuyue.backend.tool.ObjectUtil;
import com.yuyue.backend.utility.UserContext;
import com.yuyue.backend.vo.UserSaveToRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service("sessionService")
public class SessionServiceImpl implements SessionService {
    @Autowired
    RedisRepository redisRepository;

    @Override
    public String createSession(UserEntity userEntity) {
        //yuyeu:user:
        String sessionId = UUID.randomUUID().toString();
        String userInfo_prefix = RedisKey.userInfo;
        String key = userInfo_prefix + sessionId;

        UserSaveToRedis userSaveToRedis = new UserSaveToRedis();
        ObjectUtil.copyProperties(userEntity, userSaveToRedis);

        redisRepository.saveObject(key, userSaveToRedis);
        return sessionId;
    }

    //判断是否有效
    //若有效，保存用户信息
    @Override
    public boolean validateSession(String sessionId) {
        String userInfo_prefix = RedisKey.userInfo;
        String key = userInfo_prefix + sessionId;
        if(!redisRepository.hasKey(key)) return false;
        UserEntity user = redisRepository.findObject(key, UserEntity.class);
        UserContext.setUser(user);
        return true;
    }

}
