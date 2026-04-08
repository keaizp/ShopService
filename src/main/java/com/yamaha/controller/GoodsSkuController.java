package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.config.AdminRequired;
import com.yamaha.entity.GoodsSpec;
import com.yamaha.entity.GoodsSku;
import com.yamaha.service.GoodsSpecService;
import com.yamaha.service.GoodsSkuService;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsSkuController {

    private final GoodsSpecService goodsSpecService;
    private final GoodsSkuService goodsSkuService;
    private final CosUtil cosUtil;

    // ── 规格属性接口 ────────────────────────────────────

    /** 获取商品规格列表 */
    @GetMapping("/specs/{goodsId}")
    public Result<List<GoodsSpec>> getSpecs(@PathVariable Long goodsId) {
        return Result.success(goodsSpecService.getByGoodsId(goodsId));
    }

    /** 批量保存商品规格（先删后插） */
    @AdminRequired
    @PostMapping("/specs/{goodsId}")
    public Result<Boolean> saveSpecs(@PathVariable Long goodsId, @RequestBody List<GoodsSpec> specs) {
        goodsSpecService.deleteByGoodsId(goodsId);
        for (int i = 0; i < specs.size(); i++) {
            GoodsSpec spec = specs.get(i);
            spec.setId(null);
            spec.setGoodsId(goodsId);
            spec.setSortOrder(i);
        }
        goodsSpecService.saveBatch(specs);
        return Result.success(true);
    }

    // ── SKU 接口 ─────────────────────────────────────────

    /** 获取商品 SKU 列表 */
    @GetMapping("/skus/{goodsId}")
    public Result<List<GoodsSku>> getSkus(@PathVariable Long goodsId) {
        List<GoodsSku> skus = goodsSkuService.getByGoodsId(goodsId);
        for (GoodsSku sku : skus) {
            if (sku.getImage() != null) {
                sku.setImage(cosUtil.getFullImageUrl(sku.getImage()));
            }
        }
        return Result.success(skus);
    }

    /** 批量保存商品 SKU（先删后插） */
    @AdminRequired
    @PostMapping("/skus/{goodsId}")
    public Result<Boolean> saveSkus(@PathVariable Long goodsId, @RequestBody List<GoodsSku> skus) {
        goodsSkuService.deleteByGoodsId(goodsId);
        for (GoodsSku sku : skus) {
            sku.setId(null);
            sku.setGoodsId(goodsId);
        }
        goodsSkuService.saveBatch(skus);
        return Result.success(true);
    }
}
