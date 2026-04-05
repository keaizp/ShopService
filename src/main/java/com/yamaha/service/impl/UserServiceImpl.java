package com.yamaha.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.dto.GetPhoneDTO;
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
            // 先获取String响应
            String responseStr = restTemplate.getForObject(url, String.class);
            log.info("微信API响应: {}", responseStr);
            
            // 解析为JSONObject
            JSONObject wxResponse = JSONObject.parseObject(responseStr);
            
            if (wxResponse.containsKey("errcode")) {
                String errMsg = wxResponse.getString("errmsg");
                log.error("微信登录失败: {}", errMsg);
                throw new RuntimeException("微信登录失败: " + errMsg);
            }
            
            String openid = wxResponse.getString("openid");
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
            
            // 生成token，包含角色信息
            String token = jwtUtil.generateToken(user.getId(), user.getOpenid(), user.getRole());
            log.info("生成token成功, 用户ID: {}, 角色: {}", user.getId(), user.getRole());
            
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
    public Map<String, Object> phoneLogin(String code, String phoneCode) {
        log.info("手机号登录, code: {}, phoneCode: {}", code, phoneCode);
        
        try {
            // 1. 获取 access_token
            String getTokenUrl = String.format(
                    "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${APPID}&secret=${SECRET}",
                    wxAppId, wxAppSecret
            );
            String responseStr = restTemplate.getForObject(getTokenUrl, String.class);
            log.info("getToken响应: {}", responseStr);
            JSONObject tokenResponse = JSONObject.parseObject(responseStr);
            String accessToken = tokenResponse.getString("access_token");


            // 2. 调用微信登录API获取token
            String getPhoneUrl = String.format(
                    "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s",
                    accessToken
            );
            GetPhoneDTO phoneDTO = new GetPhoneDTO();
            phoneDTO.setCode(phoneCode);
            responseStr = restTemplate.postForObject(getPhoneUrl,phoneDTO,String.class);
            log.info("getPhone响应: {}", responseStr);
            JSONObject phoneResponse = JSONObject.parseObject(responseStr);

            if ("0".equals(phoneResponse.getString("errcode"))){
                JSONObject phoneInfo = phoneResponse.getJSONObject("phone_info");
                String phoneNumber = phoneInfo.getString("phoneNumber");
                // 调用微信登录API获取session_key和openid
                String url = String.format(
                        "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                        wxAppId, wxAppSecret, code
                );
                // 先获取String响应
                responseStr = restTemplate.getForObject(url, String.class);
                log.info("微信API响应: {}", responseStr);
                // 解析为JSONObject
                JSONObject wxResponse = JSONObject.parseObject(responseStr);
                if (wxResponse.containsKey("errcode")) {
                    String errMsg = wxResponse.getString("errmsg");
                    log.error("微信登录失败: {}", errMsg);
                    throw new RuntimeException("微信登录失败: " + errMsg);
                }
                String openid = wxResponse.getString("openid");
                String sessionKey = wxResponse.getString("session_key");
                log.info("获取到openid: {}, sessionKey: {}", openid, sessionKey);

                User user = this.getByPhone(phoneNumber);
                if (user == null) {
                    log.info("新用户注册, 手机号: {}", phoneNumber);
                    user = new User();
                    user.setOpenid(openid);
                    user.setPhone(phoneNumber);
                    user.setNickname("用户" + phoneNumber.substring(7));
                    user.setStatus(1);
                    this.save(user);
                    log.info("新用户注册成功, ID: {}", user.getId());
                } else {
                    // 更新用户的openid
                    if (!openid.equals(user.getOpenid())) {
                        user.setOpenid(openid);
                        this.updateById(user);
                    }
                    log.info("用户登录, ID: {}", user.getId());
                }
                this.updateLastLoginTime(user.getId());

                // 生成token，包含角色信息
                String token = jwtUtil.generateToken(user.getId(), user.getOpenid(), user.getRole());
                log.info("生成token成功, 用户ID: {}, 角色: {}", user.getId(), user.getRole());

                Map<String, Object> result = new HashMap<>();
                result.put("user", user);
                result.put("token", token);
                return result;
            }else {
                log.error("获取手机号失败: {}", phoneResponse.getString("errmsg"));
                throw new RuntimeException("微信登录失败: " + phoneResponse.getString("errmsg"));
            }

        } catch (Exception e) {
            log.error("手机号登录异常", e);
            throw new RuntimeException("手机号登录失败: " + e.getMessage());
        }
    }

    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return this.getOne(wrapper);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
    }
}
