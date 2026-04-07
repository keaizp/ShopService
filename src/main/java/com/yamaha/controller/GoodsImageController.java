package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.entity.GoodsImage;
import com.yamaha.service.GoodsImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods/images")
@RequiredArgsConstructor
public class GoodsImageController {

    private final GoodsImageService goodsImageService;

    @GetMapping("/goods/{goodsId}")
    public Result<List<GoodsImage>> listByGoodsId(@PathVariable Long goodsId) {
        return Result.success(goodsImageService.listByGoodsId(goodsId));
    }

    @PostMapping("/batch")
    public Result<Boolean> saveBatch(@RequestBody List<GoodsImage> images) {
        return Result.success(goodsImageService.saveBatch(images));
    }

    @DeleteMapping("/goods/{goodsId}")
    public Result<Boolean> removeByGoodsId(@PathVariable Long goodsId) {
        return Result.success(goodsImageService.removeByGoodsId(goodsId));
    }
}