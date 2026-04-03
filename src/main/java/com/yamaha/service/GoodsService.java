package com.yamaha.service;

import com.yamaha.entity.Goods;
import com.yamaha.repository.GoodsRepository;
import com.yamaha.util.CosUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoodsService {
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CosUtil cosUtil;

    public List<Goods> getAllGoods() {
        List<Goods> goodsList = goodsRepository.findAll();
        // 为每个商品生成完整的图片URL
        goodsList.forEach(goods -> {
            if (goods.getImage() != null) {
                goods.setImage(cosUtil.getFullImageUrl(goods.getImage()));
            }
        });
        return goodsList;
    }

    public Optional<Goods> getGoodsById(Long id) {
        Optional<Goods> goods = goodsRepository.findById(id);
        // 为商品生成完整的图片URL
        goods.ifPresent(g -> {
            if (g.getImage() != null) {
                g.setImage(cosUtil.getFullImageUrl(g.getImage()));
            }
        });
        return goods;
    }

    public Goods saveGoods(Goods goods) {
        return goodsRepository.save(goods);
    }

    public void deleteGoods(Long id) {
        goodsRepository.deleteById(id);
    }
}