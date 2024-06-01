package com.yuyue.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.vo.AppointmentVo;
import com.yuyue.backend.vo.SegmentQueryResp;
import com.yuyue.backend.vo.SegmentQueryVo;
import io.renren.common.utils.PageUtils;
import com.yuyue.backend.entity.SegmentEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
public interface SegmentService extends IService<SegmentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SegmentQueryResp> querySegment(SegmentQueryVo segmentQueryVo);

    void makeAppointment(AppointmentVo appointmentVo);

    void updateDateBase(UserEntity user, List<Integer> tIds);
}

