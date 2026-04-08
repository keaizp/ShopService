package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.UserAddress;
import com.yamaha.mapper.UserAddressMapper;
import com.yamaha.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public List<UserAddress> getByUserId(Long userId) {
        log.info("获取用户地址列表，用户ID: {}", userId);
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .orderByDesc(UserAddress::getIsDefault)
               .orderByDesc(UserAddress::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public UserAddress getDefaultByUserId(Long userId) {
        log.info("获取用户默认地址，用户ID: {}", userId);
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .eq(UserAddress::getIsDefault, 1)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public void setDefault(Long id, Long userId) {
        log.info("设置默认地址，地址ID: {}, 用户ID: {}", id, userId);

        // 先将用户的所有地址设置为非默认
        LambdaUpdateWrapper<UserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0);
        this.update(updateWrapper);

        // 再将指定地址设置为默认
        UserAddress address = this.getById(id);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            this.updateById(address);
        }
    }
}
