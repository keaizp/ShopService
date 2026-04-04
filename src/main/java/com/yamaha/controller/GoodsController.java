package com.yamaha.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yamaha.common.PageDTO;
import com.yamaha.common.Result;
import com.yamaha.entity.Goods;
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

    @PostMapping
    public Result<Boolean> save(@RequestBody Goods goods) {
        return Result.success(goodsService.save(goods));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Goods goods) {
        goods.setId(id);
        return Result.success(goodsService.updateById(goods));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(goodsService.removeById(id));
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam("folder") String folder) {
        try {
            String imagePath = cosUtil.uploadFile(file, folder);
            return Result.success(imagePath);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
