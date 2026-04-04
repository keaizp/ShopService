package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.User;
import com.yamaha.mapper.UserMapper;
import com.yamaha.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return this.getOne(wrapper);
    }

    @Override
    public User login(String code) {
        log.info("用户登录, code: {}", code);
        // 这里需要调用微信API获取openid
        // 暂时模拟，实际项目中需要对接微信登录API
        String openid = "mock_openid_" + System.currentTimeMillis();
        log.info("获取到openid: {}", openid);
        
        User user = this.getByOpenid(openid);
        if (user == null) {
            log.info("新用户注册, openid: {}", openid);
            user = new User();
            user.setOpenid(openid);
            user.setNickname("用户" + openid.substring(0, 8));
            user.setStatus(1);
            this.save(user);
            log.info("新用户注册成功, ID: {}", user.getId());
        } else {
            log.info("用户登录, ID: {}", user.getId());
        }
        
        this.updateLastLoginTime(user.getId());
        return user;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
    }
}
