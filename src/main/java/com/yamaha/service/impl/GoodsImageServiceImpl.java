package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.GoodsImage;
import com.yamaha.mapper.GoodsImageMapper;
import com.yamaha.service.GoodsImageService;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsImageServiceImpl extends ServiceImpl<GoodsImageMapper, GoodsImage> implements GoodsImageService {

    private final CosUtil cosUtil;

    @Override
    public List<GoodsImage> listByGoodsId(Long goodsId) {
        QueryWrapper<GoodsImage> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("sort_order");
        List<GoodsImage> images = baseMapper.selectList(wrapper);
        // 为图片URL添加完整路径
        images.forEach(image -> {
            if (image.getImageUrl() != null) {
                image.setImageUrl(cosUtil.getFullImageUrl(image.getImageUrl()));
            }
        });
        return images;
    }

    @Override
    public boolean saveBatch(List<GoodsImage> images) {
        for (GoodsImage image : images) {
            image.setDeleted(0);
            image.setCreateTime(LocalDateTime.now());
            image.setUpdateTime(LocalDateTime.now());
        }
        return super.saveBatch(images);
    }

    @Override
    public boolean removeByGoodsId(Long goodsId) {
        QueryWrapper<GoodsImage> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        return baseMapper.delete(wrapper) > 0;
    }
}