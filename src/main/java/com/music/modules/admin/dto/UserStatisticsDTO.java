package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户统计DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "用户统计数据")
public class UserStatisticsDTO {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "活跃用户数")
    private Long activeUsers;

    @Schema(description = "今日新增用户数")
    private Long todayNewUsers;

    @Schema(description = "近7天新增用户趋势")
    private List<Map<String, Object>> newUserTrend;

    @Schema(description = "用户角色分布")
    private Map<String, Long> roleDistribution;
}
