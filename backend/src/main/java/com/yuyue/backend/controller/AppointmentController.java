package com.yuyue.backend.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuyue.backend.entity.AppointmentEntity;
import com.yuyue.backend.service.AppointmentService;
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
@RequestMapping("backend/appointment")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("backend:appointment:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = appointmentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{aId}")
    //@RequiresPermissions("backend:appointment:info")
    public R info(@PathVariable("aId") Integer aId){
		AppointmentEntity appointment = appointmentService.getById(aId);

        return R.ok().put("appointment", appointment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("backend:appointment:save")
    public R save(@RequestBody AppointmentEntity appointment){
		appointmentService.save(appointment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("backend:appointment:update")
    public R update(@RequestBody AppointmentEntity appointment){
		appointmentService.updateById(appointment);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("backend:appointment:delete")
    public R delete(@RequestBody Integer[] aIds){
		appointmentService.removeByIds(Arrays.asList(aIds));

        return R.ok();
    }

}
