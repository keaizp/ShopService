package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.Goods;
import com.yamaha.mapper.GoodsMapper;
import com.yamaha.service.GoodsService;
import com.yamaha.util.CosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    private final CosUtil cosUtil;

    @Override
    public IPage<Goods> pageGoods(Long pageNum, Long pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Goods::getCreateTime);

        IPage<Goods> result = this.page(page, wrapper);

        result.getRecords().forEach(goods -> {
            if (goods.getImage() != null) {
                goods.setImage(cosUtil.getFullImageUrl(goods.getImage()));
            }
        });

        return result;
    }
}
