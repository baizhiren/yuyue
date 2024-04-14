package com.yuyue.backend.service;

import com.yuyue.backend.entity.UserEntity;

public interface SessionService {
    String createSession(UserEntity userEntity);

    boolean validateSession(String sessionId);
}
