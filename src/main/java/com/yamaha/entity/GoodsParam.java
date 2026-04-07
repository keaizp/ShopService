package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("goods_param")
public class GoodsParam {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    @TableField("param_name")
    private String paramName;
    private String paramValue;
    @TableField("sort_order")
    private Integer sortOrder;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}