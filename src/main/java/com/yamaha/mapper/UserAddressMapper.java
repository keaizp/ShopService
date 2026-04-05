package com.yamaha.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yamaha.entity.UserAddress;

import java.util.List;

public interface UserAddressMapper extends BaseMapper<UserAddress> {
    List<UserAddress> selectByUserId(Long userId);
    UserAddress selectDefaultByUserId(Long userId);
}
