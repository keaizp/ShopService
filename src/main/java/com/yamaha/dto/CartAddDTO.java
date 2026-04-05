package com.yamaha.dto;

import lombok.Data;

@Data
public class CartAddDTO {
    private Long userId;
    private Long goodsId;
    private Integer quantity;
}
