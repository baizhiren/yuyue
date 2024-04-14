package com.yuyue.backend.utility;

import com.yuyue.backend.entity.UserEntity;

public class UserContext {
    private static final ThreadLocal<UserEntity> userThreadLocal = new ThreadLocal<>();

    public static void setUser(UserEntity user) {
        userThreadLocal.set(user);
    }

    public static UserEntity getUser() {
        return userThreadLocal.get();
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
