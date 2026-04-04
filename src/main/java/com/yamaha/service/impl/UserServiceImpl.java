package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.User;
import com.yamaha.mapper.UserMapper;
import com.yamaha.service.UserService;
import com.yamaha.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${wxAppId}")
    private String wxAppId;

    @Value("${wxAppSecret}")
    private String wxAppSecret;

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return this.getOne(wrapper);
    }

    @Override
    public Map<String, Object> login(String code) {
        log.info("用户登录, code: {}", code);
        
        // 调用微信登录API获取openid
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            wxAppId, wxAppSecret, code
        );
        
        try {
            Map<String, Object> wxResponse = restTemplate.getForObject(url, Map.class);
            log.info("微信API响应: {}", wxResponse);
            
            if (wxResponse == null || wxResponse.containsKey("errcode")) {
                String errMsg = wxResponse != null ? wxResponse.get("errmsg").toString() : "微信API调用失败";
                log.error("微信登录失败: {}", errMsg);
                throw new RuntimeException("微信登录失败: " + errMsg);
            }
            
            String openid = wxResponse.get("openid").toString();
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
            
            // 生成token
            String token = jwtUtil.generateToken(user.getId(), user.getOpenid());
            log.info("生成token成功, 用户ID: {}", user.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", token);
            return result;
            
        } catch (Exception e) {
            log.error("微信登录异常", e);
            throw new RuntimeException("微信登录失败: " + e.getMessage());
        }
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
    }
}
