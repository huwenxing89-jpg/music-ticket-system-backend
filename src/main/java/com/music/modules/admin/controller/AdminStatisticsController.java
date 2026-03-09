package com.music.modules.admin.controller;

import com.music.common.result.Result;
import com.music.modules.admin.dto.*;
import com.music.modules.admin.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端-数据统计控制器
 *
 * @author 黄晓倩
 */
@Tag(name = "管理端-数据统计")
@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取仪表盘数据
     */
    @Operation(summary = "获取仪表盘数据")
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = statisticsService.getDashboardData();
        return Result.success(data);
    }

    /**
     * 获取用户统计数据
     */
    @Operation(summary = "获取用户统计数据")
    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserStatisticsDTO> getUserStatistics() {
        UserStatisticsDTO data = statisticsService.getUserStatistics();
        return Result.success(data);
    }

    /**
     * 获取订单统计数据
     */
    @Operation(summary = "获取订单统计数据")
    @GetMapping("/order")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<OrderStatisticsDTO> getOrderStatistics() {
        OrderStatisticsDTO data = statisticsService.getOrderStatistics();
        return Result.success(data);
    }

    /**
     * 获取剧目统计数据
     */
    @Operation(summary = "获取剧目统计数据")
    @GetMapping("/show")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ShowStatisticsDTO> getShowStatistics() {
        ShowStatisticsDTO data = statisticsService.getShowStatistics();
        return Result.success(data);
    }

    /**
     * 获取用户注册趋势
     */
    @Operation(summary = "获取用户注册趋势")
    @GetMapping("/user-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getUserTrend(@RequestParam(defaultValue = "30") Integer days) {
        Map<String, Object> data = statisticsService.getUserTrend(days);
        return Result.success(data);
    }

    /**
     * 获取订单趋势
     */
    @Operation(summary = "获取订单趋势")
    @GetMapping("/order-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getOrderTrend(@RequestParam(defaultValue = "7") Integer days) {
        Map<String, Object> data = statisticsService.getOrderTrend(days);
        return Result.success(data);
    }

    /**
     * 获取热门剧目排行
     */
    @Operation(summary = "获取热门剧目排行")
    @GetMapping("/hot-shows")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getHotShows(@RequestParam(defaultValue = "10") Integer top) {
        Map<String, Object> data = statisticsService.getHotShows(top);
        return Result.success(data);
    }

    /**
     * 获取销售额统计
     */
    @Operation(summary = "获取销售额统计")
    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getSalesStatistics() {
        Map<String, Object> data = statisticsService.getSalesStatistics();
        return Result.success(data);
    }

    /**
     * 获取统计数据（总览）
     */
    @Operation(summary = "获取统计数据")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> data = statisticsService.getDashboardData();
        return Result.success(data);
    }
}
