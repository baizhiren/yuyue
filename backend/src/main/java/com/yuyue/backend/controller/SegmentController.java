package com.yuyue.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.vo.AppointmentVo;
import com.yuyue.backend.vo.SegmentQueryResp;
import com.yuyue.backend.vo.SegmentQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuyue.backend.entity.SegmentEntity;
import com.yuyue.backend.service.SegmentService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author chao
 * @email chao@gmail.com
 * @date 2024-03-23 20:49:20
 */
@RestController
@RequestMapping("backend/segment")
public class SegmentController {
    @Autowired
    private SegmentService segmentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("backend:segment:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = segmentService.queryPage(params);
        return R.ok().put("page", page);
    }


    @RequestMapping("/appointment")
    public R makeAppointment(@RequestBody AppointmentVo appointmentVo){
        //获取用户id
        segmentService.makeAppointment(appointmentVo);
        return R.ok("2000", "预约成功");
    }





    @RequestMapping("/query")
    public R querySegment(@RequestBody SegmentQueryVo segmentQueryVo){
       List<SegmentQueryResp> res = segmentService.querySegment(segmentQueryVo);
       return R.ok().put("data", res);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{tId}")
    //@RequiresPermissions("backend:segment:info")
    public R info(@PathVariable("tId") Integer tId){
		SegmentEntity segment = segmentService.getById(tId);
        return R.ok().put("segment", segment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("backend:segment:save")
    public R save(@RequestBody SegmentEntity segment){
        segmentService.save(segment);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("backend:segment:update")
    public R update(@RequestBody SegmentEntity segment){
		segmentService.updateById(segment);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("backend:segment:delete")
    public R delete(@RequestBody Integer[] tIds){
		segmentService.removeByIds(Arrays.asList(tIds));

        return R.ok();
    }

}
