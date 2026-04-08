package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.dto.CartAddDTO;
import com.yamaha.dto.CartUpdateDTO;
import com.yamaha.entity.Cart;
import com.yamaha.entity.Goods;
import com.yamaha.entity.GoodsSku;
import com.yamaha.service.CartService;
import com.yamaha.service.GoodsService;
import com.yamaha.service.GoodsSkuService;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final GoodsService goodsService;
    private final GoodsSkuService goodsSkuService;
    private final CosUtil cosUtil;

    /** 获取购物车列表（合并商品 + SKU 信息） */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestAttribute("userId") Long userId) {
        List<Cart> carts = cartService.getByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart cart : carts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", cart.getId());
            item.put("goodsId", cart.getGoodsId());
            item.put("skuId", cart.getSkuId());
            item.put("quantity", cart.getQuantity());
            item.put("selected", cart.getSelected());

            Goods goods = goodsService.getById(cart.getGoodsId());
            if (goods != null) {
                item.put("name", goods.getName());
                // 默认使用商品主图和价格
                String image = goods.getImage() != null ? cosUtil.getFullImageUrl(goods.getImage()) : null;
                item.put("image", image);
                item.put("price", goods.getPrice());
                item.put("stock", goods.getStock());
            }

            // 如果有 SKU，用 SKU 的价格、库存、图片覆盖
            if (cart.getSkuId() != null) {
                GoodsSku sku = goodsSkuService.getById(cart.getSkuId());
                if (sku != null) {
                    item.put("price", sku.getPrice());
                    item.put("stock", sku.getStock());
                    item.put("specValues", sku.getSpecValues());
                    if (sku.getImage() != null) {
                        item.put("image", cosUtil.getFullImageUrl(sku.getImage()));
                    }
                }
            }

            result.add(item);
        }
        return Result.success(result);
    }

    /** 加入购物车 */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody CartAddDTO cartAddDTO, @RequestAttribute("userId") Long userId) {
        cartService.addToCart(userId, cartAddDTO.getGoodsId(), cartAddDTO.getSkuId(), cartAddDTO.getQuantity());
        return Result.success(true);
    }

    /** 更新数量 */
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody CartUpdateDTO cartUpdateDTO) {
        cartService.updateQuantity(cartUpdateDTO.getId(), cartUpdateDTO.getQuantity());
        return Result.success(true);
    }

    /** 删除单条 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        cartService.deleteById(id);
        return Result.success(true);
    }

    /** 清空购物车 */
    @DeleteMapping("/clear")
    public Result<Boolean> clear(@RequestAttribute("userId") Long userId) {
        cartService.deleteByUserId(userId);
        return Result.success(true);
    }
}
