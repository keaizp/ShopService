package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    User getByOpenid(String openid);

    Map<String, Object> login(String code);

    Map<String, Object> phoneLogin(String code, String phoneCode);

    void updateLastLoginTime(Long userId);
}

