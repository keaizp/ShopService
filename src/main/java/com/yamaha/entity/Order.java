package com.yamaha.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal discountAmount;

    private Integer status;

    private Integer payType;

    private LocalDateTime payTime;

    private LocalDateTime deliveryTime;

    private LocalDateTime receiveTime;

    private Long addressId;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private String remark;

    private String cancelReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
