package com.yuyue.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeChatSession {
    String openid;
    String session_key;
}
