package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String openid;

    private String unionid;

    private String nickname;

    private String avatar;

    private String phone;

    private Integer gender;

    private Integer status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
