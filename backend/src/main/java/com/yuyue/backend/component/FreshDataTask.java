package com.yuyue.backend.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuyue.backend.config.YuyueProperties;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.entity.RoomEntity;
import com.yuyue.backend.entity.SegmentEntity;
import com.yuyue.backend.service.RoomService;
import com.yuyue.backend.service.SegmentService;
import com.yuyue.backend.service.UserService;
import com.yuyue.backend.tool.DateUtil;
import com.yuyue.backend.tool.ObjectUtil;
import com.yuyue.backend.vo.SegmentQueryResp;
import com.yuyue.backend.vo.SegmentSaveToRedis;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;

import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import java.util.List;

@Component
public class FreshDataTask {

    @Autowired
    RoomService roomService;

    @Autowired
    SegmentService segmentService;

    @Autowired
    UserService userService;

    @Autowired
    RedisRepository redisRepository;

    private final YuyueProperties yuyueProperties;
    private int gap;
    private int startTime;
    private int endTime;
    private List<Integer> weeks;

    @Autowired
    public FreshDataTask(YuyueProperties yuyueProperties) {
        System.out.println("this is constructor of FreshDataTask");
        this.yuyueProperties = yuyueProperties;
        this.gap = this.yuyueProperties.getGap();
        this.startTime = this.yuyueProperties.getStartTime();
        this.endTime = this.yuyueProperties.getEndTime();
        this.weeks = this.yuyueProperties.getWeeks();
    }

    //@Scheduled(cron = "0 0 10 * * FRI", zone = "Asia/Shanghai")
    @Scheduled(cron = "0 30 20 * * THU", zone = "Asia/Shanghai")
    public void refreshData() {

        System.out.println("正在按执行数据刷新任务...");

        userService.fresh_count();


        List<RoomEntity> roomEntities = roomService.getBaseMapper().selectList(new QueryWrapper<>());

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        // 获取当前日期
        LocalDate today = LocalDate.now(zoneId);

        // 获取下周的星期一
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));


        LocalDateTime startTimeArray[] = new LocalDateTime[7];
        LocalDateTime endTimeArray[] = new LocalDateTime[7];

        System.out.println(startTime);

        int startTimeHour = startTime / 60;
        int startTimeMinute = startTime % 60;


        int endTimeHour = endTime / 60;
        int endTimeMinute = endTime % 60;

        for (int i = 0; i < 7; i++) {
            LocalDate nextDay = nextMonday.plusDays(i);
            // 将日期时间设置为当天的00:00:00
            LocalDateTime startDateTime = LocalDateTime.of(nextDay, LocalTime.of(startTimeHour, startTimeMinute));
            LocalDateTime endDateTime = LocalDateTime.of(nextDay, LocalTime.of(endTimeHour, endTimeMinute));

            startTimeArray[i] = startDateTime;
            endTimeArray[i] = endDateTime;
        }

        for(RoomEntity room: roomEntities){
            for(int i = 1; i <= 7; i ++){
               if(!weeks.contains(i)) continue;
               LocalDateTime start = startTimeArray[i];
               LocalDateTime end = start.plusHours(gap);
               List<SegmentEntity> list = new ArrayList<>();
                while(start.isBefore(endTimeArray[i])){
                    //保存
                    SegmentEntity segment = new SegmentEntity();

                    segment.setStartTime(DateUtil.LocalToDate(start));
                    segment.setEndTime(DateUtil.LocalToDate(end));
                    segment.setWeek(i);

                    segment.setRId(room.getRId());
                    segment.setRName(room.getName());

                    segment.setStatus(1);

                    list.add(segment);
                    start = end;
                    end = start.plusHours(gap);
                }
                segmentService.saveBatch(list);

                String key = RedisKey.segmentInfo + room.getName() + ":" + i;
                String status_key = RedisKey.segmentInfo + room.getName() + ":" + i + ":status";
                redisRepository.deleteKey(key);
                redisRepository.deleteKey(status_key);

                //保存到redis中
                for(SegmentEntity segment: list){
                    SegmentSaveToRedis segmentQueryResp = new SegmentSaveToRedis();
                    ObjectUtil.copyProperties(segment, segmentQueryResp);
                    redisRepository.saveHashObject(
                            key, segment.getTId() + "", segmentQueryResp);
                }
            }
        }
    }

    public static void main(String[] args) {
        String x = "8:00";
        String[] split = x.split(":");
        System.out.println(split[1]);
    }
}
