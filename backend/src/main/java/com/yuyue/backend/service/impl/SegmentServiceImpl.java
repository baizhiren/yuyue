package com.yuyue.backend.service.impl;

import com.google.gson.internal.$Gson$Preconditions;
import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.constant.RoomStatus;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.exception.MakeAppointmentErrorEnum;
import com.yuyue.backend.service.UserService;
import com.yuyue.backend.tool.ObjectUtil;
import com.yuyue.backend.tool.StringUtil;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                if(status.equals(RoomStatus.NOT_AVAILABLE.getCode() + ""))
                    segmentQueryResp.setStatus(RoomStatus.NOT_AVAILABLE.getCode());
                else{
                    String[] names = status.split(";");
                    segmentQueryResp.setStatus(RoomStatus.BOOKED.getCode());
                    segmentQueryResp.setTeacherName(names[0]);
                    segmentQueryResp.setStudentName(names[1]);
                }
            }else{
                segmentQueryResp.setStatus(RoomStatus.NOT_BOOK.getCode());
            }
            res.add(segmentQueryResp);
        }
        //按时间状态排序
        res.sort(Comparator.comparingInt(SegmentQueryResp::getTId));
        return res;
    }
    //修改成批量插入，提高效率
    //todo 如果用户一直在同一个时间段预约，会不会出现反复预约不成功的情况？如何优化
    //todo 考虑把用户信息完整的保存到redis里，考虑更新操作
    //todo 处理是否需要连续预约
    @Override
    public void makeAppointment(AppointmentVo appointmentVo) {
        //获取用户信息
        UserEntity user = UserContext.getUser();
        if(StringUtil.isBlank(user.getTeacherName()) || StringUtil.isBlank(user.getUName()))  throw MakeAppointmentErrorEnum.NAME_EMPTY.toException();

        List<Integer> tIds  = appointmentVo.getTIds();

        String teacherName = user.getTeacherName();
        int bookCount = userService.getBookCount(teacherName);
        if(max_book != -1 && bookCount + appointmentVo.getTIds().size() > max_book){
            throw MakeAppointmentErrorEnum.REACH_MAX_BOOK_LIMIT.toException();
        }
        //判断预约信息是否有效
        String key = RedisKey.segmentInfo + appointmentVo.getRoomName() + ":" + appointmentVo.getWeek();
        List<Boolean> checks = redisRepository.checkMultipleFieldsExistence(key, tIds);
        if(checks.contains(false))
            throw MakeAppointmentErrorEnum.SEGMENT_NOT_EXIST.toException();

        String status_key = key + ":status";
        List<Boolean> exists = redisRepository.checkMultipleFieldsExistence(status_key, tIds);

        if(exists.contains(true)){
            throw MakeAppointmentErrorEnum.SEGMENT_ALREADY_BOOKED.toException();
        }

        //尝试预约
        List<String> names = IntStream.range(0, tIds.size())
                .mapToObj(i -> user.getTeacherName() + ";" + user.getUName())
                .collect(Collectors.toList());

        boolean success = redisRepository.insertMultiHashKeyNotExist(status_key, tIds, names);
        if(!success){
            throw MakeAppointmentErrorEnum.SEGMENT_ALREADY_BOOKED.toException();
        }

        //预约成功
        //todo 研究这条语句的查询索引的效率问题
        int count = this.baseMapper.updateMultiSegments(tIds, user.getUName(), user.getUId());
        if(count != tIds.size()){
            //todo 如果数据库异常应该怎么办，如何保证和redis的一致性？
            throw  MakeAppointmentErrorEnum.DATABASE_UPDATE_ERROR.toException();
        }
        userService.bookCountPlus(user, tIds.size());
    }

    public static void main(String[] args) {
        String x = "1";
        System.out.println(x.equals(1 + ""));
    }

}