package com.yuyue.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "yuyue")
@Data
public class YuyueProperties {
    private List<Integer> weeks;
    private int gap;
    private int startTime;
    private int endTime;

    // getter 和 setter
}
