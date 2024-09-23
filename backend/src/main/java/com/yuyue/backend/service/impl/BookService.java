package com.yuyue.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuyue.backend.component.RedisRepository;
import com.yuyue.backend.constant.RedisKey;
import com.yuyue.backend.dao.BookDao;
import com.yuyue.backend.entity.Book;
import com.yuyue.backend.entity.BookAndSegment;
import com.yuyue.backend.entity.SegmentEntity;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.exception.CancelBookErrorEnum;
import com.yuyue.backend.exception.MakeAppointmentErrorEnum;
import com.yuyue.backend.service.SegmentService;
import com.yuyue.backend.service.UserService;
import com.yuyue.backend.tool.ObjectUtil;
import com.yuyue.backend.utility.UserContext;
import com.yuyue.backend.vo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookService extends ServiceImpl<BookDao, Book>{

    @Autowired
    BookAndSegmentService bookAndSegmentService;

    @Autowired
    SegmentServiceImpl segmentService;  // 注意，这是实现类，不是接口

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    UserService userService;


    private static final Logger logger = LogManager.getLogger(BookService.class);



    public List<RecordResp> getHistoryRecord2() {
        UserEntity user = UserContext.getUser();
        int uId = user.getUId();
        List<Book> books = this.baseMapper.selectList(new LambdaQueryWrapper<Book>().eq(Book::getUId, uId).last("limit 10"));

        List<CompletableFuture<RecordResp>> futureResponses = books.stream()
                .map(book -> CompletableFuture.supplyAsync(() -> getRecordResp(book)))
                .collect(Collectors.toList());

        // 等待所有异步操作完成
        List<RecordResp> responses = futureResponses.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return responses;
    }


    public List<RecordResp> getHistoryRecord(){
        UserEntity user = UserContext.getUser();
        int uId = user.getUId();
        //范围查询
        List<Book> books = this.baseMapper.selectList(new LambdaQueryWrapper<Book>().eq(Book::getUId, uId).last("limit 10"));

        List<RecordResp> resp = books.stream().map(book -> getRecordResp(book)).collect(Collectors.toList());

        return resp;
    }

    private RecordResp getRecordResp(Book book) {
        RecordResp recordResp = new RecordResp();
        recordResp.setBookTime(book.getBookTime());
        recordResp.setBookId(book.getBookId());

        //查出对应的segment id
        List<BookAndSegment> bookAndSegments = bookAndSegmentService.getBaseMapper().
                selectList(new LambdaQueryWrapper<BookAndSegment>().select(BookAndSegment::getTId).eq(BookAndSegment::getBookId, book.getBookId()));

        List<Integer> tIds = bookAndSegments.stream().map(BookAndSegment::getTId).collect(Collectors.toList());

        List<SegmentEntity> segmentEntities = segmentService.getBaseMapper().
                selectList(new LambdaQueryWrapper<SegmentEntity>().
                        select(SegmentEntity::getTId, SegmentEntity::getRName, SegmentEntity::getStartTime, SegmentEntity::getEndTime, SegmentEntity::getWeek).
                        in(SegmentEntity::getTId, tIds));

        //todo 少了边界检查
        recordResp.setRoomName(segmentEntities.get(0).getRName());
        recordResp.setWeek(segmentEntities.get(0).getWeek());


        List<Segment> segments = segmentEntities.stream().map(item -> {
            Segment segment = new Segment();
            ObjectUtil.copyProperties(item, segment);
            return segment;
        }).collect(Collectors.toList());

        recordResp.setSegments(segments);

        return recordResp;
    }


    @Transactional
    public void cancelBook(CancelBookVo cancelBookVo) {
        //为了维护数据的一致性，应该从bookId 直接查询, 而不是直接让前端返回tid
        int bookId = cancelBookVo.getBookId();

        //查询bookId对应的user_id, 看是否相同
        Book book = this.baseMapper.selectById(bookId);

        UserEntity user = UserContext.getUser();
        if(book == null || book.getUId() != user.getUId()) throw CancelBookErrorEnum.SEGMENT_NOT_BELONG_CURRENT_USER.toException();

        RecordResp recordResp = getRecordResp(book);

        List<Integer> tIds = recordResp.getSegments().stream().map(item -> item.getTId()).collect(Collectors.toList());


        String key = RedisKey.segmentInfo + cancelBookVo.getRoomName() + ":" + cancelBookVo.getWeek();

        //检查取消的预约日期是否已经过期
        List<Boolean> checks = redisRepository.checkMultipleFieldsExistence(key, tIds);
        if(checks.contains(false))
            throw CancelBookErrorEnum.SEGMENT_NOT_VALID.toException();

        List<String> dates = redisRepository.getMultiHashField(key, tIds);
        List<SegmentSaveToRedis> collect = dates.stream().map(item -> {
            SegmentSaveToRedis segmentSaveToRedis = redisRepository.fromJson(item, SegmentSaveToRedis.class);
            return segmentSaveToRedis;
        }).collect(Collectors.toList());

        Date date = new Date();
        for(SegmentSaveToRedis segment: collect){
            if(date.compareTo(segment.getStartTime()) > 0)
                throw CancelBookErrorEnum.SEGMENT_ALREADY_EXPIRE.toException();
        }

        //检查是否全是自己预约的时段
        //检查是否存在null值
//        List<String> bookInfos = redisRepository.getMultiHashField(key + ":status", tIds);
//        for(String bookInfo: bookInfos){
//            if(bookInfo == null) throw CancelBookErrorEnum.SEGMENT_NOT_BOOK.toException();
//            String[] split = bookInfo.split(";");
//            if(!split[2].equals(String.valueOf(user.getUId()) ))
//                throw CancelBookErrorEnum.SEGMENT_NOT_BELONG_CURRENT_USER.toException();
//        }


        //恢复status为1
        int count = segmentService.getBaseMapper().cancelMultiSegments(tIds);
        if(count != tIds.size()){
            //todo 如果数据库异常应该怎么办，如何保证和redis的一致性？
            throw CancelBookErrorEnum.UPDATE_SEGMENT_TABLE_ERROR.toException();
        }

        //修改book表和book_segment表

        boolean deleteBook  = this.remove(new LambdaQueryWrapper<Book>().eq(Book::getBookId, bookId));

        boolean deleteBookAndSegment = bookAndSegmentService.remove(new LambdaQueryWrapper<BookAndSegment>().eq(BookAndSegment::getBookId, bookId));

        if(!deleteBook) throw CancelBookErrorEnum.DELETE_BOOK_TABLE_ERROR.toException();
        if(!deleteBookAndSegment) throw CancelBookErrorEnum.DELETE_BOOK_AND_SEGMENT_TABLE_ERROR.toException();


        //减少预约次数
        userService.bookCountPlus(user, -tIds.size());

        //删除预约项
        Boolean success = redisRepository.deleteMultiHashField(key + ":status", tIds);
        if(!success) throw CancelBookErrorEnum.REDIS_KEY_DELETE_ERROR.toException();



    }
}
