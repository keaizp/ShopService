package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.GoodsSpec;
import com.yamaha.mapper.GoodsSpecMapper;
import com.yamaha.service.GoodsSpecService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsSpecServiceImpl extends ServiceImpl<GoodsSpecMapper, GoodsSpec> implements GoodsSpecService {

    @Override
    public List<GoodsSpec> getByGoodsId(Long goodsId) {
        LambdaQueryWrapper<GoodsSpec> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsSpec::getGoodsId, goodsId)
               .orderByAsc(GoodsSpec::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public void deleteByGoodsId(Long goodsId) {
        LambdaQueryWrapper<GoodsSpec> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsSpec::getGoodsId, goodsId);
        this.remove(wrapper);
    }
}
