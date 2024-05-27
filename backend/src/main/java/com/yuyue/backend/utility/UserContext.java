package com.yuyue.backend.utility;

import com.yuyue.backend.entity.UserEntity;

public class UserContext {
    private static final ThreadLocal<UserEntity> userThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> userKey = new ThreadLocal<>();

    public static void setUser(UserEntity user) {
        userThreadLocal.set(user);
    }

    public static UserEntity getUser() {
        return userThreadLocal.get();
    }

    public static void setUserKey(String key) {
        userKey.set(key);
    }

    public static String getUserKey() {
        return userKey.get();
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
