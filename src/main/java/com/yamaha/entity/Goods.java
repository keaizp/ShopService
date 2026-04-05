package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private String image;

    private String description;

    private Integer status;

    private Integer sortOrder;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
