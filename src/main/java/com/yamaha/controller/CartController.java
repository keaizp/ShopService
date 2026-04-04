package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.entity.Cart;
import com.yamaha.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Result<Boolean> add(@RequestParam("userId") Long userId,
                             @RequestParam("goodsId") Long goodsId,
                             @RequestParam("quantity") Integer quantity) {
        cartService.addToCart(userId, goodsId, quantity);
        return Result.success(true);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestParam("id") Long id,
                               @RequestParam("quantity") Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return Result.success(true);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        cartService.deleteById(id);
        return Result.success(true);
    }

    @DeleteMapping("/clear")
    public Result<Boolean> clear(@RequestParam("userId") Long userId) {
        cartService.deleteByUserId(userId);
        return Result.success(true);
    }

    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestParam("userId") Long userId) {
        return Result.success(cartService.getByUserId(userId));
    }

    @GetMapping("/check")
    public Result<Cart> check(@RequestParam("userId") Long userId,
                           @RequestParam("goodsId") Long goodsId) {
        return Result.success(cartService.getByUserIdAndGoodsId(userId, goodsId));
    }
}
