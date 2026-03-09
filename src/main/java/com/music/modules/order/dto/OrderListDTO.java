package com.music.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "订单列表响应")
public class OrderListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long id;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 剧目ID
     */
    @Schema(description = "剧目ID")
    private Long showId;

    /**
     * 剧目名称
     */
    @Schema(description = "剧目名称")
    private String showTitle;

    /**
     * 剧目封面
     */
    @Schema(description = "剧目封面")
    private String showCover;

    /**
     * 剧院ID
     */
    @Schema(description = "剧院ID")
    private Long theaterId;

    /**
     * 剧院名称
     */
    @Schema(description = "剧院名称")
    private String theaterName;

    /**
     * 场次ID
     */
    @Schema(description = "场次ID")
    private Long sessionId;

    /**
     * 演出时间
     */
    @Schema(description = "演出时间")
    private LocalDateTime showTime;

    /**
     * 座位数量
     */
    @Schema(description = "座位数量")
    private Integer seatCount;

    /**
     * 座位信息
     */
    @Schema(description = "座位信息")
    private List<SeatInfo> seats;

    /**
     * 订单总金额
     */
    @Schema(description = "订单总金额")
    private BigDecimal totalPrice;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成，4-退款中，5-已退款
     */
    @Schema(description = "订单状态")
    private Integer status;

    /**
     * 支付方式
     */
    @Schema(description = "支付方式")
    private String payMethod;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 座位信息
     */
    @Data
    @Schema(description = "座位信息")
    public static class SeatInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 座位ID
         */
        @Schema(description = "座位ID")
        private Long id;

        /**
         * 座位号
         */
        @Schema(description = "座位号")
        private String seatNumber;

        /**
         * 排号
         */
        @Schema(description = "排号")
        private Integer rowNum;

        /**
         * 列号
         */
        @Schema(description = "列号")
        private Integer colNum;

        /**
         * 价格
         */
        @Schema(description = "价格")
        private BigDecimal price;
    }
}
