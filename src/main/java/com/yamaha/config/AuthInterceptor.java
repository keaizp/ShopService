package com.yamaha.config;

import com.yamaha.common.Result;
import com.yamaha.util.JwtUtil;
import com.yamaha.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            log.warn("{} 请求缺少Authorization头", request.getRequestURI());
            ResponseUtil.writeJson(response, Result.error(401, "未授权，请先登录"));
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            log.warn("无效的token");
            ResponseUtil.writeJson(response, Result.error(401, "无效的token"));
            return false;
        }

        // 获取用户角色
        Integer role = jwtUtil.getRoleFromToken(token);

        // 将用户ID和角色存储到请求中
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        return true;
    }
}
