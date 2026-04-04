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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
