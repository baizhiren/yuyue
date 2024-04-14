package com.yuyue.backend.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;


@Component
public class RedisRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T fromJson(String json, Class<T> clazz)  {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void saveObject(String key, Object object) {
        String json = toJson(object);
        redisTemplate.opsForValue().set(key, json);
    }


    public void saveHashObject(String key, String field, Object object)  {
        String json = toJson(object);
        redisTemplate.opsForHash().put(key, field, json);
    }

    public boolean insertHashKeyNotExist(String key, String field, String value){
        Boolean success = redisTemplate.opsForHash().putIfAbsent(key, field, value);
        return success;
    }

    public Map<String, String> getAllHashFields(String key){
        Map<Object, Object> tempMap = redisTemplate.opsForHash().entries(key);
        Map<String, String> resultMap = tempMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())
                ));
        return resultMap;
    }






    public <T> T findObject(String key, Class<T> clazz)  {
        String json = redisTemplate.opsForValue().get(key);
        return fromJson(json, clazz);
    }

    public void deleteKey(String key){
        redisTemplate.delete(key);
    }



    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public boolean hashFieldExists(String hashKey, String fieldName) {
        return redisTemplate.opsForHash().hasKey(hashKey, fieldName);
    }
    public String getHash(String key, String fieldName){
        return String.valueOf(redisTemplate.opsForHash().get(key, fieldName));
    }


}
