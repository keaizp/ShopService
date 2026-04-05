package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.dto.CartAddDTO;
import com.yamaha.dto.CartClearDTO;
import com.yamaha.dto.CartUpdateDTO;
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
    public Result<Boolean> add(@RequestBody CartAddDTO cartAddDTO) {
        cartService.addToCart(cartAddDTO.getUserId(), cartAddDTO.getGoodsId(), cartAddDTO.getQuantity());
        return Result.success(true);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody CartUpdateDTO cartUpdateDTO) {
        cartService.updateQuantity(cartUpdateDTO.getId(), cartUpdateDTO.getQuantity());
        return Result.success(true);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        cartService.deleteById(id);
        return Result.success(true);
    }

    @DeleteMapping("/clear")
    public Result<Boolean> clear(@RequestBody CartClearDTO cartClearDTO) {
        cartService.deleteByUserId(cartClearDTO.getUserId());
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
