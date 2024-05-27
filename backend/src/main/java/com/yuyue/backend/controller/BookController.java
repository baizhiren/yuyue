package com.yuyue.backend.controller;


import com.yuyue.backend.service.impl.BookService;
import com.yuyue.backend.vo.AppointmentVo;
import com.yuyue.backend.vo.CancelBookVo;
import com.yuyue.backend.vo.RecordResp;
import io.renren.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("backend/book")
public class BookController {
    @Autowired
    BookService bookService;

    @RequestMapping("/get_record")
    public R list(){
        List<RecordResp> historyRecord = bookService.getHistoryRecord();
        return R.ok().put("data", historyRecord);
    }

    @RequestMapping("/cancel_book")
    public R cancelBook(@RequestBody CancelBookVo cancelBookVo){
        bookService.cancelBook(cancelBookVo);
        return R.ok("2000", "取消预约成功");
    }
}
