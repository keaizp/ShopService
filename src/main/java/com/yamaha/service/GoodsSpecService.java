package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.GoodsSpec;

import java.util.List;

public interface GoodsSpecService extends IService<GoodsSpec> {

    List<GoodsSpec> getByGoodsId(Long goodsId);

    void deleteByGoodsId(Long goodsId);
}
