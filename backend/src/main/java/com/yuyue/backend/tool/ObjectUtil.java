package com.yuyue.backend.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObjectUtil {

    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    public static void main(String[] args) {
        TargetClass t = new TargetClass();
        copyProperties(new SourceClass(), t);
        System.out.println(t);
    }



}
@Data
class SourceClass {
    private String name = "hello";
    private int age = 1;
    private String otherAttr = "xjkjksjdk";

    // getter 和 setter
}

@Data
class TargetClass {
    private String name;
    private int age;
    // getter 和 setter
}