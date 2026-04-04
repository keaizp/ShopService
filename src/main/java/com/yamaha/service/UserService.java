package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.User;

public interface UserService extends IService<User> {

    User getByOpenid(String openid);

    User login(String code);

    void updateLastLoginTime(Long userId);
}
