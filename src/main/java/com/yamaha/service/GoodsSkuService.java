package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.GoodsSku;

import java.util.List;

public interface GoodsSkuService extends IService<GoodsSku> {

    List<GoodsSku> getByGoodsId(Long goodsId);

    void deleteByGoodsId(Long goodsId);
}
