package com.yuyue.backend.service.impl;

import com.google.gson.internal.$Gson$Preconditions;
import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.exception.MakeAppointmentErrorEnum;
import com.yuyue.backend.service.UserService;
import com.yuyue.backend.tool.ObjectUtil;
import com.yuyue.backend.utility.UserContext;
import com.yuyue.backend.vo.AppointmentVo;
import com.yuyue.backend.vo.SegmentQueryResp;
import com.yuyue.backend.vo.SegmentQueryVo;
import com.yuyue.backend.vo.SegmentSaveToRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.yuyue.backend.dao.SegmentDao;
import com.yuyue.backend.entity.SegmentEntity;
import com.yuyue.backend.service.SegmentService;


@Service("segmentService")
public class SegmentServiceImpl extends ServiceImpl<SegmentDao, SegmentEntity> implements SegmentService {


    @Autowired
    RedisRepository redisRepository;

    @Autowired
    UserService userService;

    @Value("${yuyue.max_book}")
    int max_book;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SegmentEntity> page = this.page(
                new Query<SegmentEntity>().getPage(params),
                new QueryWrapper<SegmentEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SegmentQueryResp> querySegment(SegmentQueryVo segmentQueryVo) {
        String roomName = segmentQueryVo.getRoomName();
        int week = segmentQueryVo.getWeek();

        String key = RedisKey.segmentInfo + roomName + ":" + week;
        Map<String, String> allHashFields = redisRepository.getAllHashFields(key);

        List<SegmentQueryResp> res = new ArrayList<>();

        for(Map.Entry<String, String> entry: allHashFields.entrySet()){
            //查询基本信息
            String tid = entry.getKey();
            String info = entry.getValue();
            SegmentQueryResp segmentQueryResp = new SegmentQueryResp();
            segmentQueryResp.setTId(Integer.parseInt(tid));
            SegmentSaveToRedis segmentSaveToRedis = redisRepository.fromJson(info, SegmentSaveToRedis.class);
            ObjectUtil.copyProperties(segmentSaveToRedis, segmentQueryResp);

            //查询状态信息
            String statusKey = key + ":status";
            boolean b = redisRepository.hashFieldExists(statusKey, tid);
            if(b){
                String status = redisRepository.getHash(statusKey, tid);
                segmentQueryResp.setStatus(Integer.parseInt(status));
            }else{
                segmentQueryResp.setStatus(1);
            }
            res.add(segmentQueryResp);
        }

        //按时间状态排序
        res.sort(Comparator.comparingInt(SegmentQueryResp::getTId));
        return res;
    }

    @Override
    public void makeAppointment(AppointmentVo appointmentVo) {
        //获取用户信息
        UserEntity user = UserContext.getUser();
        String tid = String.valueOf(appointmentVo.getTId());

        String teacherName = user.getTeacherName();
        int bookCount = userService.getBookCount(teacherName);
        if(bookCount >= max_book){
            throw MakeAppointmentErrorEnum.REACH_MAX_BOOK_LIMIT.toException();
        }

        //判断预约信息是否有效
        String key = RedisKey.segmentInfo + appointmentVo.getRoomName() + ":" + appointmentVo.getWeek();
        boolean check = redisRepository.hashFieldExists(key, tid);
        if(!check) throw MakeAppointmentErrorEnum.SEGMENT_NOT_EXIST.toException();

        String status_key = key + ":status";
        boolean exists = redisRepository.hashFieldExists(status_key, tid);


        if(exists == true){
            throw MakeAppointmentErrorEnum.SEGMENT_ALREADY_BOOKED.toException();
        }
        //尝试预约
        boolean success = redisRepository.insertHashKeyNotExist(status_key, tid, "2");
        if(!success){
            throw MakeAppointmentErrorEnum.SEGMENT_ALREADY_BOOKED.toException();
        }

        //预约成功
        int count = this.baseMapper.updateSegmentStatusAndUser(appointmentVo.getTId(), user.getUName(), user.getUId());
        if(count == 0){
            //todo 如果数据库异常应该怎么办，如何保证和redis的一致性？
            throw  MakeAppointmentErrorEnum.DATABASE_UPDATE_ERROR.toException();
        }
        userService.bookCountPlus(user);
    }

}