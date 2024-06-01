package com.yuyue.backend.service.impl;

import com.google.gson.internal.$Gson$Preconditions;
import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.constant.RoomStatus;
import com.yuyue.backend.constant.UserStatus;
import com.yuyue.backend.entity.Book;
import com.yuyue.backend.entity.BookAndSegment;
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
import io.renren.common.exception.RRException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("segmentService")
public class SegmentServiceImpl extends ServiceImpl<SegmentDao, SegmentEntity> implements SegmentService {


    @Autowired
    RedisRepository redisRepository;

    @Autowired
    UserService userService;

    @Autowired
    BookService bookService;

    @Autowired
    BookAndSegmentService bookAndSegmentService;

    //todo 这些配置文件可以动态变化吗
    @Value("${yuyue.max_book}")
    int max_book;

    @Value("${yuyue.cross_segment}")
    boolean cross_segment;

    @Autowired
    private SegmentServiceImpl self;



    private static final Logger logger = LogManager.getLogger(SegmentServiceImpl.class);
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

    boolean checkContinue(List<Integer> list){
        for(int i = 1; i < list.size(); i ++){
            if(list.get(i) != list.get(i - 1) + 1) return false;
        }
        return true;
    }



    //修改成批量插入，提高效率
    //todo 如果多个用户一直在同一个时间段预约，会不会出现反复预约不成功的情况？如何优化
    //todo 考虑把用户信息完整的保存到redis里，考虑更新操作
    @Override
    public void makeAppointment(AppointmentVo appointmentVo) {
        //获取用户信息
        UserEntity user = UserContext.getUser();
        if(StringUtil.isBlank(user.getTeacherName()) || StringUtil.isBlank(user.getUName())) {
            //用户第一次预约，新增姓名
            UserEntity userEntity = new UserEntity();
            userEntity.setUId(user.getUId());
            userEntity.setUName(appointmentVo.getStudentName());
            userEntity.setTeacherName(appointmentVo.getTeacherName());
            userService.updateById(userEntity);

            //todo 修改为hash可能好一点
            //修改redis中的数据
            user.setUName(appointmentVo.getStudentName());
            user.setTeacherName(appointmentVo.getTeacherName());
            redisRepository.saveObject(UserContext.getUserKey(), user);
        }
        if(user.getStatus() != UserStatus.ACTIVE.getCode()) throw MakeAppointmentErrorEnum.USER_NOT_VALID.toException();

        List<Integer> tIds  = appointmentVo.getTIds();
        if(!cross_segment && !checkContinue(tIds)) throw MakeAppointmentErrorEnum.SEGMENT_NOT_CONTINUE.toException();

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

        //尝 试预约
        //把u_id 的信息放在最后
        List<String> names = IntStream.range(0, tIds.size())
                .mapToObj(i -> user.getTeacherName() + ";" + user.getUName() + ";" + user.getUId())
                .collect(Collectors.toList());

        boolean success = redisRepository.insertMultiHashKeyNotExist(status_key, tIds, names);
        if(!success){
            throw MakeAppointmentErrorEnum.SEGMENT_ALREADY_BOOKED.toException();
        }


        try {
            self.updateDateBase(user, tIds);
        } catch (RRException e) {
            handleRedisRollback(e, key, tIds);
        } catch (Exception other) {
            logger.error("更新数据库未知异常", other);
            throw MakeAppointmentErrorEnum.UNKNOWN_ERROR.toException();
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public void updateDateBase(UserEntity user, List<Integer> tIds) {
        //todo 研究查询索引的效率问题

        //1. 更新segment表
        int count = this.baseMapper.updateMultiSegments(tIds);
        if(count != tIds.size()){
            throw  MakeAppointmentErrorEnum.DATABASE_SEGMENT_UPDATE_ERROR.toException();
        }



        //2. 更新book表
        Book book = new Book();
        book.setUId(user.getUId());
        book.setBookTime(new Date());
        boolean save = bookService.save(book);

        save = false;
        if(!save){
            throw  MakeAppointmentErrorEnum.DATABASE_BOOK_UPDATE_ERROR.toException();
        }



        int bookId = book.getBookId();
        List<BookAndSegment> bookAndSegments = tIds.stream().map(tid -> {
            BookAndSegment bookAndSegment = new BookAndSegment();
            bookAndSegment.setBookId(bookId);
            bookAndSegment.setTId(tid);
            return bookAndSegment;
        }).collect(Collectors.toList());

        //3. 更新book_segment 表
        save = bookAndSegmentService.saveBatch(bookAndSegments);
        if(!save){
            throw  MakeAppointmentErrorEnum.DATABASE_BOOK_AND_SEGMENT_UPDATE_ERROR.toException();
        }

        //4. 更新user表
        userService.bookCountPlus(user, tIds.size());
    }


    public RRException append(RRException e, String msg){
        e.setMsg(e.getMsg() + msg);
        return e;
    }


    private void handleRedisRollback(RRException originalException, String key, List<Integer> tIds) {
        boolean handle_exception = false;
        try {
            Boolean is_success = redisRepository.deleteMultiHashField(key + ":status", tIds);
            handle_exception = true;
            if (is_success) {
                logger.warn("Redis rollback success for {}: {} : {}", key, tIds, originalException.getMessage());
                throw append(originalException, ", Redis rollback success");
            } else {
                logger.error("Redis rollback failure for {}: {}, {}", key,  tIds, originalException.getMessage());
                throw append(originalException, ", Redis rollback failure");
            }

        } catch (Exception other) {
            if(!handle_exception){
                logger.error("Error during Redis rollback: ", other);
                throw MakeAppointmentErrorEnum.REDIS_ROLLBACK_ERROR.toException();
            }else throw other;
        }
    }

    public static void main(String[] args) {
        String x = "1";
        System.out.println(x.equals(1 + ""));
    }

}