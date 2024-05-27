package com.yuyue.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@TableName("book_segment")
public class BookAndSegment {
    private int bookId;

    @JsonProperty("tId")
    private int tId;
}
