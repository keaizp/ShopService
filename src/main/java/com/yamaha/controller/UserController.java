package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.dto.LoginDTO;
import com.yamaha.entity.User;
import com.yamaha.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO.getCode());
        return Result.success(result);
    }

    @PostMapping("/phoneLogin")
    public Result<Map<String, Object>> phoneLogin(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.phoneLogin(loginDTO.getCode(), loginDTO.getPhoneCode());
        return Result.success(result);
    }

    @GetMapping("/info")
    public Result<User> getInfo(@RequestParam("id") Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(userService.updateById(user));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}
