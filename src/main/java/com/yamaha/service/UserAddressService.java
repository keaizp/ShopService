package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {
    List<UserAddress> getByUserId(Long userId);
    UserAddress getDefaultByUserId(Long userId);
    void setDefault(Long id, Long userId);
}
