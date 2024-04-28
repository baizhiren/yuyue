package com.yuyue.backend.component;

import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.entity.RoomEntity;
import com.yuyue.backend.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaveRoomToRedis {
    @Autowired
    RedisRepository redisRepository;

    @Autowired
    RoomService roomService;


    public void save(){
        List<RoomEntity> roomEntities = roomService.getBaseMapper().selectList(null);

        List<String> collect = roomEntities.stream().map(RoomEntity::getName).collect(Collectors.toList());

        long count = redisRepository.saveSet(RedisKey.roomInfo, collect);
        System.out.println("插入" + count  + " 条数据");

    }







}
