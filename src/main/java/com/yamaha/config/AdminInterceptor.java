package com.yamaha.config;

import com.yamaha.common.Result;
import com.yamaha.util.JwtUtil;
import com.yamaha.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否是方法处理
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            
            // 检查方法是否标记了AdminRequired注解
            if (method.isAnnotationPresent(AdminRequired.class) || 
                handlerMethod.getBeanType().isAnnotationPresent(AdminRequired.class)) {
                // 获取token
                String token = request.getHeader("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                    log.warn("管理员登录token缺失");
                    ResponseUtil.writeJson(response, Result.error(401, "请先登录"));
                    return false;
                }
                
                token = token.substring(7);
                
                // 验证是否是管理员token
                if (!jwtUtil.isAdminToken(token)) {
                    log.warn("无效的管理员token");
                    ResponseUtil.writeJson(response, Result.error(403, "没有管理员权限"));
                    return false;
                }
                
                // 验证token是否过期
                if (jwtUtil.isTokenExpired(jwtUtil.getClaimsByToken(token))) {
                    log.warn("管理员token已过期");
                    ResponseUtil.writeJson(response, Result.error(401, "登录已过期"));
                    return false;
                }
                
                // 将管理员ID和用户名存入请求属性
                Long adminId = jwtUtil.getAdminIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                request.setAttribute("adminId", adminId);
                request.setAttribute("username", username);
            }
        }
        return true;
    }
}
