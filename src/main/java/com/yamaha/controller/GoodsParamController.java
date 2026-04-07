package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.entity.GoodsParam;
import com.yamaha.service.GoodsParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods/params")
@RequiredArgsConstructor
public class GoodsParamController {

    private final GoodsParamService goodsParamService;

    @GetMapping("/goods/{goodsId}")
    public Result<List<GoodsParam>> listByGoodsId(@PathVariable Long goodsId) {
        return Result.success(goodsParamService.listByGoodsId(goodsId));
    }

    @PostMapping("/batch")
    public Result<Boolean> saveBatch(@RequestBody List<GoodsParam> params) {
        return Result.success(goodsParamService.saveBatch(params));
    }

    @DeleteMapping("/goods/{goodsId}")
    public Result<Boolean> removeByGoodsId(@PathVariable Long goodsId) {
        return Result.success(goodsParamService.removeByGoodsId(goodsId));
    }
}