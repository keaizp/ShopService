package com.yamaha.config;

import com.yamaha.common.Result;
import com.yamaha.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否是方法处理
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            
            // 检查方法是否标记了AdminRequired注解
            if (method.isAnnotationPresent(AdminRequired.class) || 
                handlerMethod.getBeanType().isAnnotationPresent(AdminRequired.class)) {
                // 获取用户角色
                Integer role = (Integer) request.getAttribute("role");
                
                // 检查是否是管理员
                if (role == null || role != 2) {
                    log.warn("用户没有管理员权限");
                    ResponseUtil.writeJson(response, Result.error(403, "没有管理员权限"));
                    return false;
                }
            }
        }
        return true;
    }
}
