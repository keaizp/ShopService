package com.yamaha.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Goods;

public interface GoodsService extends IService<Goods> {

    IPage<Goods> pageGoods(Long pageNum, Long pageSize);
}
