package com.yuyue.backend;

import com.yuyue.backend.service.impl.SegmentServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling // 启用定时任务
public class YuyueApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuyueApplication.class, args);
    }
}
