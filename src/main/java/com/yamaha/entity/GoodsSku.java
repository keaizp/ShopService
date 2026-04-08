package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("goods_sku")
public class GoodsSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    /** 规格组合 JSON，如 {"颜色":"黑色","尺寸":"S"} */
    private String specValues;

    private BigDecimal price;

    private Integer stock;

    /** SKU 独立图片 COS 路径（可选） */
    private String image;

    /** SKU 编码（可选） */
    private String skuCode;

    /** 状态 1=上架 0=下架 */
    private Integer status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
