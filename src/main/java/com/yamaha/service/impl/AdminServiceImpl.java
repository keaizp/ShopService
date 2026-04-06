package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.Admin;
import com.yamaha.mapper.AdminMapper;
import com.yamaha.service.AdminService;
import com.yamaha.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final JwtUtil jwtUtil;

    @Override
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        log.info("管理员登录, username: {}", username);

        // 根据用户名查询管理员
        Admin admin = this.getByUsername(username);
        if (admin == null) {
            log.error("管理员不存在, username: {}", username);
            throw new RuntimeException("管理员不存在");
        }

        // 验证密码
        if (!com.yamaha.util.PasswordUtil.matches(password, admin.getPassword())) {
            log.error("密码错误, username: {}", username);
            throw new RuntimeException("密码错误");
        }

        // 验证状态
        if (admin.getStatus() != 1) {
            log.error("管理员账号已禁用, username: {}", username);
            throw new RuntimeException("账号已禁用");
        }

        // 更新最后登录时间
        this.updateLastLoginTime(admin.getId());

        // 生成token
        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername());
        log.info("生成token成功, 管理员ID: {}", admin.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("admin", admin);
        result.put("token", token);
        return result;
    }

    @Override
    public void updateLastLoginTime(Long adminId) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginTime(LocalDateTime.now());
        this.updateById(admin);
    }
}