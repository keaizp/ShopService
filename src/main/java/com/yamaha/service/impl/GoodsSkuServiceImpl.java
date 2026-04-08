package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.GoodsSku;
import com.yamaha.mapper.GoodsSkuMapper;
import com.yamaha.service.GoodsSkuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsSkuServiceImpl extends ServiceImpl<GoodsSkuMapper, GoodsSku> implements GoodsSkuService {

    @Override
    public List<GoodsSku> getByGoodsId(Long goodsId) {
        LambdaQueryWrapper<GoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsSku::getGoodsId, goodsId)
               .orderByAsc(GoodsSku::getId);
        return this.list(wrapper);
    }

    @Override
    public void deleteByGoodsId(Long goodsId) {
        LambdaQueryWrapper<GoodsSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsSku::getGoodsId, goodsId);
        this.remove(wrapper);
    }
}
