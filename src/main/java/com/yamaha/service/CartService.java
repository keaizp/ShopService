package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    void addToCart(Long userId, Long goodsId, Long skuId, Integer quantity);

    void updateQuantity(Long id, Integer quantity);

    void deleteById(Long id);

    void deleteByUserId(Long userId);

    List<Cart> getByUserId(Long userId);

    Cart getByUserIdAndGoodsIdAndSkuId(Long userId, Long goodsId, Long skuId);
}
