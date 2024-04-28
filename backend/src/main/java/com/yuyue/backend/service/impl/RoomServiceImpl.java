package com.yuyue.backend.service.impl;

import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.constant.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.yuyue.backend.dao.RoomDao;
import com.yuyue.backend.entity.RoomEntity;
import com.yuyue.backend.service.RoomService;


@Service("roomService")
public class RoomServiceImpl extends ServiceImpl<RoomDao, RoomEntity> implements RoomService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<RoomEntity> page = this.page(
                new Query<RoomEntity>().getPage(params),
                new QueryWrapper<RoomEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    RedisRepository redisRepository;
    //去redis里查询
    @Override
    public List<String> getAllNames() {
        String key = RedisKey.roomInfo;
        return redisRepository.getSet(key);
    }
}