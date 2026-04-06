package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.dto.AdminLoginDTO;
import com.yamaha.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody AdminLoginDTO loginDTO) {
        Map<String, Object> result = adminService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(result);
    }
}