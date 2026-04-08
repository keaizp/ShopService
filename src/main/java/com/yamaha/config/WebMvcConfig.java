package com.yamaha.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先执行认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/admin/login", "/user/login", "/user/phoneLogin",
                        "/goods/list", "/goods/page", "/goods/{id}",
                        "/goods/images/goods/{goodsId}",
                        "/goods/params/goods/{goodsId}",
                        "/goods/specs/{goodsId}",
                        "/goods/skus/{goodsId}",
                        "/categories/list",
                        "/error"
                );

        // 再执行管理员权限拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/admin/login", "/user/login", "/user/phoneLogin",
                        "/goods/list", "/goods/page", "/goods/{id}",
                        "/goods/images/goods/{goodsId}",
                        "/goods/params/goods/{goodsId}",
                        "/goods/specs/{goodsId}",
                        "/goods/skus/{goodsId}",
                        "/categories/list",
                        "/error"
                );
    }
}
