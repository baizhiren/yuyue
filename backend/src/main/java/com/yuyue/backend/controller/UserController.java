package com.yuyue.backend.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuyue.backend.entity.WeChatSession;
import com.yuyue.backend.service.impl.WeChatService;
import com.yuyue.backend.vo.RegisterVo;
import com.yuyue.backend.vo.UserSaveToRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.service.UserService;
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
@RequestMapping("backend/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("backend:user:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userService.queryPage(params);
        return R.ok().put("page", page);
    }

    @RequestMapping("/getUserInfo")
    //@RequiresPermissions("backend:user:list")
    public R getUserInfo(){
        UserSaveToRedis info = userService.getUserInfo();
        return R.ok().put("data", info);
    }



    /**
     * 信息
     */
    @RequestMapping("/info/{uId}")
    //@RequiresPermissions("backend:user:info")
    public R info(@PathVariable("uId") Integer uId){
		UserEntity user = userService.getById(uId);
        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("backend:user:save")
    public R save(@RequestBody UserEntity user){
		userService.save(user);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("backend:user:update")
    public R update(@RequestBody UserEntity user){
		userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("backend:user:delete")
    public R delete(@RequestBody Integer[] uIds){
		userService.removeByIds(Arrays.asList(uIds));

        return R.ok();
    }

    @RequestMapping("/register")
    public R register(@RequestBody RegisterVo registerVo){
        String sessionID = userService.register(registerVo);
        return R.ok().put("yuyueSessionId", sessionID);
    }
}
