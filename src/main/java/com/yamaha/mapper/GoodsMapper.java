package com.yamaha.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yamaha.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
