package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.GoodsParam;
import java.util.List;

public interface GoodsParamService extends IService<GoodsParam> {
    List<GoodsParam> listByGoodsId(Long goodsId);
    boolean saveBatch(List<GoodsParam> params);
    boolean removeByGoodsId(Long goodsId);
}