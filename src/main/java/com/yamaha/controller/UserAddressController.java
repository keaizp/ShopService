package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.entity.UserAddress;
import com.yamaha.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userAddress")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list(@RequestAttribute("userId") Long userId) {
        List<UserAddress> addressList = userAddressService.getByUserId(userId);
        return Result.success(addressList);
    }

    @GetMapping("/default")
    public Result<UserAddress> getDefault(@RequestAttribute("userId") Long userId) {
        UserAddress address = userAddressService.getDefaultByUserId(userId);
        return Result.success(address);
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody UserAddress address, @RequestAttribute("userId") Long userId) {
        address.setUserId(userId);
        // 如果是第一个地址，自动设置为默认
        List<UserAddress> existingAddresses = userAddressService.getByUserId(userId);
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(1);
        }
        return Result.success(userAddressService.save(address));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody UserAddress address, @RequestAttribute("userId") Long userId) {
        UserAddress existingAddress = userAddressService.getById(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            return Result.error("地址不存在或无权限");
        }
        address.setId(id);
        address.setUserId(userId);
        return Result.success(userAddressService.updateById(address));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        UserAddress address = userAddressService.getById(id);
        if (address != null && address.getUserId().equals(userId)) {
            return Result.success(userAddressService.removeById(id));
        }
        return Result.error("地址不存在或无权限");
    }

    @PutMapping("/{id}/default")
    public Result<Boolean> setDefault(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        userAddressService.setDefault(id, userId);
        return Result.success(true);
    }
}
