package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.GoodsParam;
import com.yamaha.mapper.GoodsParamMapper;
import com.yamaha.service.GoodsParamService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodsParamServiceImpl extends ServiceImpl<GoodsParamMapper, GoodsParam> implements GoodsParamService {

    @Override
    public List<GoodsParam> listByGoodsId(Long goodsId) {
        QueryWrapper<GoodsParam> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("sort_order");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean saveBatch(List<GoodsParam> params) {
        for (GoodsParam param : params) {
            param.setDeleted(0);
            param.setCreateTime(LocalDateTime.now());
            param.setUpdateTime(LocalDateTime.now());
        }
        return super.saveBatch(params);
    }

    @Override
    public boolean removeByGoodsId(Long goodsId) {
        QueryWrapper<GoodsParam> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        return baseMapper.delete(wrapper) > 0;
    }
}