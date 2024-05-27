package com.yuyue.backend.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
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

    public long saveSet(String key, List<String> list){
        SetOperations<String, String> ops = redisTemplate.opsForSet();
        return ops.add(key, list.toArray(new String[0]));
    }

    public List<String> getSet(String key){
        SetOperations<String, String> ops = redisTemplate.opsForSet();
        Set<String> members = ops.members(key);
        return new ArrayList<>(members);
    }

    public List<Boolean> checkMultipleFieldsExistence(String hashKey, List<?> fields) {
        String luaScript = "local results = {} " +
                "for i, field in ipairs(KEYS) do " +
                "  results[i] = redis.call('HEXISTS', ARGV[1], field) " +
                "end " +
                "return results";
        List<String> fieldStrings = fields.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        DefaultRedisScript<List> script = new DefaultRedisScript<>(luaScript, List.class);


        List<Long> rawResults = redisTemplate.execute(script, fieldStrings, hashKey);
        System.out.println("raw results: " +  rawResults);
        List<Boolean> results = rawResults.stream()
                .map(value -> value == 1)
                .collect(Collectors.toList());
        System.out.println(results.get(0));
        return results;
    }
    public Boolean insertMultiHashKeyNotExist(String hashKey, List<?> fields, List<?> values) {
        String luaScript = "local results = {} " +
                "for i, field in ipairs(KEYS) do " +
                "  results[i] = redis.call('HEXISTS', ARGV[1], field) " +
                "end " +
                "return results";
        List<String> fieldStrings = fields.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        List<String> valueStrings = values.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        String args[] = new String[fields.size() * 2];
        for (int i = 0, id = 0; i < fieldStrings.size(); i ++) {
            args[id ++] = fieldStrings.get(i);
            args[id ++] = valueStrings.get(i);
        }
        // 创建脚本实例
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(
                "local key = KEYS[1] " +
                        "for i = 1, #ARGV, 2 do " +
                        "  if redis.call('hexists', key, ARGV[i]) == 1 then " +
                        "    return 0 " +
                        "  end " +
                        "end " +
                        "for i = 1, #ARGV, 2 do " +
                        "  redis.call('hset', key, ARGV[i], ARGV[i + 1]) " +
                        "end " +
                        "return 1");
        script.setResultType(Long.class);

        // 执行脚本
        Long result = redisTemplate.execute(script, Arrays.asList(hashKey), args);
        return result == 1;
    }
    
    public Boolean deleteMultiHashField(String key, List<Integer> ids){
        List<String> collect = ids.stream().map(item -> String.valueOf(item)).collect(Collectors.toList());
        String[] fields = collect.toArray(new String[0]);
        Long delete = redisTemplate.opsForHash().delete(key, fields);
        return delete == ids.size();
    }

    public List<String> getMultiHashField(String key, List<Integer> ids){
        List<Object> fieldKeys = ids.stream().map(Object::toString).collect(Collectors.toList());

        List<Object> objects = redisTemplate.opsForHash().multiGet(key, fieldKeys);

        return objects.stream()
                .map(object -> (String) object) // 假设所有返回的对象都可以安全地转换为String
                .collect(Collectors.toList());

    }







}
