package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.Cart;
import com.yamaha.mapper.CartMapper;
import com.yamaha.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Override
    public void addToCart(Long userId, Long goodsId, Integer quantity) {
        log.info("添加商品到购物车, 用户ID: {}, 商品ID: {}, 数量: {}", userId, goodsId, quantity);
        Cart cart = this.getByUserIdAndGoodsId(userId, goodsId);
        if (cart != null) {
            // 已存在，更新数量
            cart.setQuantity(cart.getQuantity() + quantity);
            this.updateById(cart);
            log.info("购物车商品数量更新成功, 购物车ID: {}, 新数量: {}", cart.getId(), cart.getQuantity());
        } else {
            // 不存在，添加新记录
            cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setQuantity(quantity);
            cart.setSelected(1);
            this.save(cart);
            log.info("购物车商品添加成功, 购物车ID: {}", cart.getId());
        }
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setQuantity(quantity);
        this.updateById(cart);
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }

    @Override
    public List<Cart> getByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return this.list(wrapper);
    }

    @Override
    public Cart getByUserIdAndGoodsId(Long userId, Long goodsId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getGoodsId, goodsId);
        return this.getOne(wrapper);
    }
}
