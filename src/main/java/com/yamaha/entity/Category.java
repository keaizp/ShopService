package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    private Integer level;

    private Integer sortOrder;

    private Integer status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
