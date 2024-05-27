package com.yuyue.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.yuyue.backend.constant.UserStatus;
import com.yuyue.backend.entity.WeChatSession;
import com.yuyue.backend.exception.UserRegisterErrorEnum;
import com.yuyue.backend.service.SessionService;
import com.yuyue.backend.tool.ObjectUtil;
import com.yuyue.backend.utility.UserContext;
import com.yuyue.backend.vo.RegisterVo;
import com.yuyue.backend.vo.UserSaveToRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.yuyue.backend.dao.UserDao;
import com.yuyue.backend.entity.UserEntity;
import com.yuyue.backend.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    WeChatService weChatService;

    @Autowired
    SessionService sessionService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public int getBookCount(String teacherName){
        return this.baseMapper.getBookCount(teacherName);
    }

    @Override
    public void fresh_count() {
        boolean update = lambdaUpdate()
            .set(UserEntity::getBookCount, 0)
            .update();
    }

    @Override
    public UserSaveToRedis getUserInfo() {
        UserEntity user = UserContext.getUser();
        UserSaveToRedis userSaveToRedis = new UserSaveToRedis();
        ObjectUtil.copyProperties(user, userSaveToRedis);
        return userSaveToRedis;
    }

    @Override
    public void bookCountPlus(UserEntity user, int number) {
        this.baseMapper.bookCountPlus(user.getUId(), number);
    }

    public WeChatSession getSessionInfo(String code) {
        // 向微信服务器请求换取openid和session_key
        String sessionKey = weChatService.getSessionKey(code);
        Gson gson = new Gson();
        WeChatSession session = gson.fromJson(sessionKey, WeChatSession.class);

        return session;
    }

    @Override
    public UserEntity getUserByVID(String v_id) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserEntity::getVId, v_id); // 使用方法引用来指定字段
        UserEntity user =  this.baseMapper.selectOne(lambdaQueryWrapper);// 执行查询
        return user;
    }

    @Override
    public String register(RegisterVo registerVo) {
        String code = registerVo.getCode();
        System.out.println("in register..." +  registerVo);
        WeChatSession sessionInfo = this.getSessionInfo(code);


        String openid = sessionInfo.getOpenid();
        if(openid == null){
            throw UserRegisterErrorEnum.WeChatCodeError.toException();
        }

        //先检查用户是否注册过
        UserEntity user = getUserByVID(sessionInfo.getOpenid());

        if(user == null){
            UserEntity userEntity = new UserEntity();
            userEntity.setTeacherName(registerVo.getTeacherName());
            userEntity.setUName(registerVo.getUserName());
            userEntity.setVId(sessionInfo.getOpenid());
            userEntity.setStatus(UserStatus.ACTIVE.getCode());
            this.save(userEntity);
            user = userEntity;
        }

        String sessionId = sessionService.createSession(user);

        return sessionId;
    }


}