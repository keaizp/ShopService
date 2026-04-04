package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.entity.User;
import com.yamaha.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestParam("code") String code) {
        User user = userService.login(code);
        return Result.success(user);
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
