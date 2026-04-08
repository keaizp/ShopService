package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long goodsId;

    private Long skuId;

    private String specInfo;

    private String goodsName;

    private String goodsImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;

    private LocalDateTime createTime;
}
