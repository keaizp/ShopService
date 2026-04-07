package com.yamaha.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Goods;
import com.yamaha.dto.GoodsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GoodsService extends IService<Goods> {

    IPage<Goods> pageGoods(Long pageNum, Long pageSize);
    Goods saveGoods(GoodsDTO goodsDTO);
    boolean saveGoods(GoodsDTO goodsDTO, MultipartFile file) throws IOException;
    boolean updateGoods(Long id, GoodsDTO goodsDTO, MultipartFile file) throws IOException;
}
