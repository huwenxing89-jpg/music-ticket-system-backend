package com.music.modules.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 场次ID
     */
    private Long sessionId;

    /**
     * 剧目ID
     */
    private Long showId;

    /**
     * 剧院ID
     */
    private Long theaterId;

    /**
     * 座位数量
     */
    private Integer seatCount;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成，4-退款中，5-已退款
     */
    private Integer status;

    /**
     * 支付方式：alipay-支付宝，wechat-微信支付，balance-余额支付
     */
    private String payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付交易号
     */
    private String transactionId;

    /**
     * 订单过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
