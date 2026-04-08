package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("goods_spec")
public class GoodsSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private String name;

    /** JSON 数组，如 ["黑色","白色","红色"] */
    @TableField("`values`")
    private String values;

    private Integer sortOrder;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
