package com.yamaha.config;

import com.yamaha.common.Result;
import com.yamaha.util.JwtUtil;
import com.yamaha.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

        // 先尝试解析 token
        Claims claims;
        try {
            claims = jwtUtil.getClaimsByToken(token);
        } catch (ExpiredJwtException e) {
            log.warn("{} token已过期", request.getRequestURI());
            ResponseUtil.writeJson(response, Result.error(401, "登录已过期，请重新登录"));
            return false;
        }

        if (claims == null) {
            log.warn("{} 无效的token", request.getRequestURI());
            ResponseUtil.writeJson(response, Result.error(401, "无效的token"));
            return false;
        }

        // 检查是否过期
        if (jwtUtil.isTokenExpired(claims)) {
            log.warn("{} token已过期", request.getRequestURI());
            ResponseUtil.writeJson(response, Result.error(401, "登录已过期，请重新登录"));
            return false;
        }

        // 管理员token直接放行
        if ("admin".equals(claims.get("type"))) {
            return true;
        }

        // 用户token
        Object userIdObj = claims.get("userId");
        if (userIdObj == null) {
            log.warn("token中缺少userId");
            ResponseUtil.writeJson(response, Result.error(401, "无效的token"));
            return false;
        }

        request.setAttribute("userId", Long.valueOf(userIdObj.toString()));
        return true;
    }
}
