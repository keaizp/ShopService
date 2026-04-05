package com.yamaha.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yamaha.common.PageDTO;
import com.yamaha.common.Result;
import com.yamaha.config.AdminRequired;
import com.yamaha.entity.Goods;
import com.yamaha.dto.GoodsDTO;
import com.yamaha.service.GoodsService;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;
    private final CosUtil cosUtil;

    @GetMapping("/page")
    public Result<IPage<Goods>> page(PageDTO pageDTO) {
        return Result.success(goodsService.pageGoods(pageDTO.getPageNum(), pageDTO.getPageSize()));
    }

    @GetMapping("/list")
    public Result<List<Goods>> list() {
        List<Goods> goods = goodsService.list();
        for (Goods good : goods) {
            if (good != null && good.getImage() != null) {
                good.setImage(cosUtil.getFullImageUrl(good.getImage()));
            }
        }
        return Result.success(goods);
    }

    @GetMapping("/{id}")
    public Result<Goods> getById(@PathVariable Long id) {
        Goods goods = goodsService.getById(id);
        if (goods != null && goods.getImage() != null) {
            goods.setImage(cosUtil.getFullImageUrl(goods.getImage()));
        }
        return Result.success(goods);
    }

    @AdminRequired
    @PostMapping(consumes = "multipart/form-data")
    public Result<Boolean> save(@RequestPart("file") MultipartFile file,
                               @RequestPart("goods") GoodsDTO goodsDTO) {
        try {
            return Result.success(goodsService.saveGoods(goodsDTO, file));
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    @AdminRequired
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Result<Boolean> update(@PathVariable Long id,
                                 @RequestPart(value = "file", required = false) MultipartFile file,
                                 @RequestPart("goods") GoodsDTO goodsDTO) {
        try {
            return Result.success(goodsService.updateGoods(id, goodsDTO, file));
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @AdminRequired
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(goodsService.removeById(id));
    }
}
