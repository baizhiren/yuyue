package com.yuyue.backend;

import com.google.gson.Gson;
import com.yuyue.backend.component.FreshDataTask;
import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.component.SaveRoomToRedis;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.service.SegmentService;
import com.yuyue.backend.service.UserService;
import com.yuyue.backend.vo.SegmentQueryResp;
import com.yuyue.backend.vo.SegmentQueryVo;
import org.assertj.core.api.ArraySortedAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@SpringBootTest
class BackendApplicationTests {

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    FreshDataTask freshDataTask;

    @Autowired
    SegmentService segmentService;

    @Autowired
    UserService userService;


    @Test
    void testRedis(){
        // 存储对象
        Person person = new Person("Jane Doe", 28);
        redisRepository.saveObject("person:2", person);

        Person retrievedPerson = redisRepository.findObject("person:2", Person.class);
        System.out.println(retrievedPerson.getName() + ", " + retrievedPerson.getAge());
    }

    @Test
    void testInsertHashToRedis(){
        // 存储对象
        //'6', '1', '科研楼1310', '2024-03-25 10:00:00', '2024-03-25 10:00:00', '1', NULL, NULL, NULL
        SegmentQueryResp data = new SegmentQueryResp();
        data.setTId(7);
        data.setRName("科研楼1310");
        data.setStartTime(new Date());
        data.setEndTime(new Date());
        data.setStatus(1);
        System.out.println(data.getStartTime());
        redisRepository.saveHashObject(RedisKey.segmentInfo + "1:" + data.getRName(), data.getTId() + "", data);
    }


    @Test
    void testLoadHashFromRedis(){
        // 存储对象
        //'6', '1', '科研楼1310', '2024-03-25 10:00:00', '2024-03-25 10:00:00', '1', NULL, NULL, NULL
        Map<String, String> allHashFields = redisRepository.getAllHashFields(RedisKey.segmentInfo + "1:" + "科研楼1310");
        Gson gson = new Gson();
        for(Map.Entry<String, String> entries : allHashFields.entrySet()){
            System.out.println("key: " + entries.getKey());
            System.out.println("value: " + gson.fromJson(entries.getValue(), SegmentQueryResp.class));
        }

    }

    @Test
    void testFreshDate(){
        freshDataTask.refreshData();
    }

    @Test
    void testFetchData(){
        SegmentQueryVo segmentQueryVo = new SegmentQueryVo();
        segmentQueryVo.setRoomName("科研楼610");
        segmentQueryVo.setWeek(1);

        List<SegmentQueryResp> segmentQueryResps = segmentService.querySegment(segmentQueryVo);
        System.out.println(segmentQueryResps);
    }

    @Test
    void testBookCountPlus(){
        UserEntity userEntity = new UserEntity();
        userEntity.setUId(9);
        userService.bookCountPlus(userEntity, 1);
    }

    @Test
    void testBookCountFresh(){
        userService.fresh_count();
    }

    @Autowired
    SaveRoomToRedis saveRoomToRedis;

    @Test
    void testSaveRoomToRedis(){
        saveRoomToRedis.save();
    }


    @Test
    void testCheckMultiExist(){
        List<Boolean> booleans = redisRepository.checkMultipleFieldsExistence("yuyue:segment:科研楼624:2", Arrays.asList("1946", "1947", "1948", 2999));
        assert booleans.get(0);
        assert booleans.get(1);
        assert booleans.get(2);
        assert !booleans.get(3);
    }

    @Test
    void testInsertMultiFieldsNotExist(){
        String key = "yuyue:segment:科研楼610:1:status";
        Boolean aBoolean = redisRepository.insertMultiHashKeyNotExist(key, Arrays.asList(1996, 1997, 1998), Arrays.asList("jack", "lilith", "mike"));
        System.out.println("插入：" + aBoolean);
    }








}
