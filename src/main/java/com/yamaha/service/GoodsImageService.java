package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.GoodsImage;
import java.util.List;

public interface GoodsImageService extends IService<GoodsImage> {
    List<GoodsImage> listByGoodsId(Long goodsId);
    boolean saveBatch(List<GoodsImage> images);
    boolean removeByGoodsId(Long goodsId);
}