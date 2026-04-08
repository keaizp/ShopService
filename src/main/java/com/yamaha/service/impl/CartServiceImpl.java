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
    public void addToCart(Long userId, Long goodsId, Long skuId, Integer quantity) {
        log.info("添加商品到购物车, 用户ID: {}, 商品ID: {}, SKU ID: {}, 数量: {}", userId, goodsId, skuId, quantity);
        Cart cart = this.getByUserIdAndGoodsIdAndSkuId(userId, goodsId, skuId);
        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + quantity);
            this.updateById(cart);
            log.info("购物车商品数量更新成功, 购物车ID: {}, 新数量: {}", cart.getId(), cart.getQuantity());
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setSkuId(skuId);
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
    public Cart getByUserIdAndGoodsIdAndSkuId(Long userId, Long goodsId, Long skuId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getGoodsId, goodsId);
        if (skuId != null) {
            wrapper.eq(Cart::getSkuId, skuId);
        } else {
            wrapper.isNull(Cart::getSkuId);
        }
        return this.getOne(wrapper);
    }
}
