package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.Goods;
import com.yamaha.dto.GoodsDTO;
import com.yamaha.mapper.GoodsMapper;
import com.yamaha.service.GoodsService;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    private final CosUtil cosUtil;

    @Override
    public IPage<Goods> pageGoods(Long pageNum, Long pageSize) {
        log.info("分页查询商品, 页码: {}, 每页大小: {}", pageNum, pageSize);
        Page<Goods> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Goods::getCreateTime);

        IPage<Goods> result = this.page(page, wrapper);
        log.info("商品查询结果: 总条数 {}, 总页数 {}", result.getTotal(), result.getPages());

        result.getRecords().forEach(goods -> {
            if (goods.getImage() != null) {
                goods.setImage(cosUtil.getFullImageUrl(goods.getImage()));
            }
        });

        return result;
    }

    @Override
    public boolean saveGoods(GoodsDTO goodsDTO, MultipartFile file) throws IOException {
        log.info("保存商品: {}", goodsDTO.getName());
        // 上传图片
        String imagePath = cosUtil.uploadFile(file, "goods");
        
        // 创建商品
        Goods goods = new Goods();
        goods.setName(goodsDTO.getName());
        goods.setPrice(goodsDTO.getPrice());
        goods.setStock(goodsDTO.getStock());
        goods.setDescription(goodsDTO.getDescription());
        goods.setImage(imagePath);
        
        return this.save(goods);
    }

    @Override
    public boolean updateGoods(Long id, GoodsDTO goodsDTO, MultipartFile file) throws IOException {
        log.info("更新商品: ID={}, 名称={}", id, goodsDTO.getName());
        // 获取商品
        Goods goods = this.getById(id);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 更新商品信息
        goods.setName(goodsDTO.getName());
        goods.setPrice(goodsDTO.getPrice());
        goods.setStock(goodsDTO.getStock());
        goods.setDescription(goodsDTO.getDescription());
        
        // 如果有上传文件，更新图片
        if (file != null && !file.isEmpty()) {
            String imagePath = cosUtil.uploadFile(file, "goods");
            goods.setImage(imagePath);
        }
        
        return this.updateById(goods);
    }
}
