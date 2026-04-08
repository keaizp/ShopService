package com.yamaha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，因为我们使用JWT
            .csrf(csrf -> csrf.disable())
            // 禁用默认的表单登录
            .formLogin(form -> form.disable())
            // 禁用HTTP基本认证
            .httpBasic(basic -> basic.disable())
            // 配置会话管理为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置访问权限
            .authorizeHttpRequests(auth -> auth
                // 允许管理员登录接口
                .requestMatchers("/admin/login").permitAll()
                // 允许用户相关接口（鉴权由 AuthInterceptor 处理）
                .requestMatchers("/user/**").permitAll()
                // 允许用户地址接口
                .requestMatchers("/userAddress/**").permitAll()
                // 允许购物车接口（鉴权由 AuthInterceptor 处理）
                .requestMatchers("/cart/**").permitAll()
                // 允许商品相关接口
                .requestMatchers("/goods/**").permitAll()
                // 允许分类相关接口
                .requestMatchers("/categories/**").permitAll()
                // 允许上传相关接口
                .requestMatchers("/upload/**").permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
            );

        return http.build();
    }
}