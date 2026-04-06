package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Admin;

import java.util.Map;

public interface AdminService extends IService<Admin> {
    Map<String, Object> login(String username, String password);

    Admin getByUsername(String username);

    void updateLastLoginTime(Long adminId);
}