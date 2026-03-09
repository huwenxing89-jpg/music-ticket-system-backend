package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.order.dto.OrderListDTO;
import com.music.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员-订单管理控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
@Tag(name = "管理员-订单管理")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 分页查询订单列表
     */
    @GetMapping
    @Operation(summary = "分页查询订单列表")
    public Result<Map<String, Object>> getOrderList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {

        log.info("管理员查询订单列表：page={}, size={}, orderNo={}, userId={}, status={}",
            page, size, orderNo, userId, status);

        Page<OrderListDTO> pageParam = new Page<>(page, size);
        IPage<OrderListDTO> orderPage = orderService.getOrderList(pageParam, orderNo, userId, status);

        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", orderPage.getRecords());
        result.put("total", orderPage.getTotal());
        result.put("current", orderPage.getCurrent());
        result.put("size", orderPage.getSize());
        result.put("pages", orderPage.getPages());

        return Result.success(result);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<OrderListDTO> getOrderDetail(@Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("查询订单详情：id={}", id);
        // 管理员可以查看所有订单，传入null作为userId跳过权限验证
        OrderListDTO order = orderService.getOrderDetail(id, null);
        return Result.success(order);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody Map<String, Integer> data) {
        log.info("更新订单状态：id={}, status={}", id, data.get("status"));
        // 管理员可以操作所有订单，传入null作为userId跳过权限验证
        orderService.updateOrderStatus(id, data.get("status"), null);
        return Result.success();
    }

    /**
     * 手动出票
     */
    @PostMapping("/{id}/issue-ticket")
    @Operation(summary = "手动出票")
    public Result<Void> issueTicket(@Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("手动出票：id={}", id);
        orderService.issueTicket(id);
        return Result.success();
    }

    /**
     * 处理退票申请
     */
    @PostMapping("/{orderId}/refund")
    @Operation(summary = "处理退票申请")
    public Result<Void> handleRefund(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @RequestBody Map<String, Object> data) {
        boolean approve = (boolean) data.getOrDefault("approve", false);
        String reason = (String) data.getOrDefault("reason", "");
        log.info("处理退票申请：orderId={}, approve={}, reason={}", orderId, approve, reason);
        orderService.handleRefund(orderId, approve, reason);
        return Result.success();
    }

    /**
     * 导出订单
     */
    @GetMapping("/export")
    @Operation(summary = "导出订单")
    public Result<Void> exportOrders(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {

        log.info("导出订单：startTime={}, endTime={}, status={}", startTime, endTime, status);
        // TODO: 实现订单导出
        return Result.success();
    }
}
