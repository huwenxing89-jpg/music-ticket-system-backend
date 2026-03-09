package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 订单统计DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "订单统计数据")
public class OrderStatisticsDTO {

    @Schema(description = "总订单数")
    private Long totalOrders;

    @Schema(description = "待支付订单数")
    private Long unpaidOrders;

    @Schema(description = "已完成订单数")
    private Long completedOrders;

    @Schema(description = "总销售额")
    private Double totalSales;

    @Schema(description = "今日销售额")
    private Double todaySales;

    @Schema(description = "近7天销售额趋势")
    private List<Map<String, Object>> salesTrend;

    @Schema(description = "订单状态分布")
    private Map<String, Long> statusDistribution;
}
