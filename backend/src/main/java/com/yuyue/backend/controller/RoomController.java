package com.yuyue.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuyue.backend.utility.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuyue.backend.entity.RoomEntity;
import com.yuyue.backend.service.RoomService;
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
@RequestMapping("backend/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("backend:room:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = roomService.queryPage(params);
//        System.out.println(UserContext.getUser());
        return R.ok().put("page", page);
    }

    @RequestMapping("/get_all_names")
    //@RequiresPermissions("backend:room:list")
    public R list(){
        List<String> roomName = roomService.getAllNames();
        return R.ok().put("data", roomName);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{rId}")
    //@RequiresPermissions("backend:room:info")
    public R info(@PathVariable("rId") Integer rId){
		RoomEntity room = roomService.getById(rId);

        return R.ok().put("room", room);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("backend:room:save")
    public R save(@RequestBody RoomEntity room){
		roomService.save(room);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("backend:room:update")
    public R update(@RequestBody RoomEntity room){
		roomService.updateById(room);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("backend:room:delete")
    public R delete(@RequestBody Integer[] rIds){
		roomService.removeByIds(Arrays.asList(rIds));
        return R.ok();
    }

}
