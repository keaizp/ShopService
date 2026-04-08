package com.yamaha.dto;

import lombok.Data;

@Data
public class CartAddDTO {
    private Long userId;
    private Long goodsId;
    private Long skuId;
    private Integer quantity;
}
