package com.yuyue.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeChatService {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    private final RestTemplate restTemplate;

    public WeChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSessionKey(String jsCode) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", appId)
                .queryParam("secret", appSecret)
                .queryParam("js_code", jsCode)
                .queryParam("grant_type", "authorization_code")
                .toUriString();




        // 发送请求
        String response = restTemplate.getForObject(url, String.class);
        return response; // 这里返回的是微信接口的响应字符串
    }
}
