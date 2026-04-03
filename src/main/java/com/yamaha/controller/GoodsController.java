package com.yamaha.controller;

import com.yamaha.entity.Goods;
import com.yamaha.service.GoodsService;
import com.yamaha.util.CosUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CosUtil cosUtil;

    @GetMapping
    public ResponseEntity<List<Goods>> getAllGoods() {
        return ResponseEntity.ok(goodsService.getAllGoods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goods> getGoodsById(@PathVariable Long id) {
        return goodsService.getGoodsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Goods> createGoods(@RequestBody Goods goods) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goodsService.saveGoods(goods));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goods> updateGoods(@PathVariable Long id, @RequestBody Goods goods) {
        return goodsService.getGoodsById(id)
                .map(existingGoods -> {
                    goods.setId(id);
                    return ResponseEntity.ok(goodsService.saveGoods(goods));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoods(@PathVariable Long id) {
        return goodsService.getGoodsById(id)
                .map(existingGoods -> {
                    goodsService.deleteGoods(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("folder") String folder) {
        try {
            String imagePath = cosUtil.uploadFile(file, folder);
            return ResponseEntity.ok(imagePath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败: " + e.getMessage());
        }
    }
}