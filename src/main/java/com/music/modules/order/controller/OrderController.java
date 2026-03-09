package com.music.modules.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.common.utils.JwtUtil;
import com.music.modules.order.dto.OrderCreateDTO;
import com.music.modules.order.dto.OrderListDTO;
import com.music.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "订单管理")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Result<Map<String, Object>> createOrder(
            HttpServletRequest request,
            @Valid @RequestBody OrderCreateDTO dto) {
        log.info("创建订单：sessionId={}, seatCount={}", dto.getSessionId(), dto.getSeats().size());

        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        Map<String, Object> result = orderService.createOrder(dto, userId);
        return Result.success(result);
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取订单列表")
    public Result<Map<String, Object>> getOrderList(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {

        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("查询订单列表：userId={}, page={}, size={}, status={}", userId, page, size, status);

        // 创建分页对象
        Page<OrderListDTO> pageParam = new Page<>(page, size);

        // 调用Service层查询当前用户的订单列表
        IPage<OrderListDTO> orderPage = orderService.getOrderList(pageParam, null, userId, status);

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
    public Result<OrderListDTO> getOrderDetail(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long id) {
        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("查询订单详情：userId={}, id={}", userId, id);

        OrderListDTO order = orderService.getOrderDetail(id, userId);
        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long id) {
        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("取消订单：userId={}, id={}", userId, id);
        orderService.updateOrderStatus(id, 2, userId); // 2-已取消
        return Result.success();
    }

    /**
     * 申请退票
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "申请退票")
    public Result<Void> refundOrder(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> data) {
        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        String reason = data != null ? data.get("reason") : "";
        log.info("申请退票：userId={}, id={}, reason={}", userId, id, reason);

        // 先将订单状态改为退款中
        orderService.updateOrderStatus(id, 4, userId); // 4-退款中

        // TODO: 这里应该创建退款申请记录，等待管理员审核
        // 目前简化为直接调用退款处理
        // orderService.handleRefund(id, true, reason);

        return Result.success();
    }

    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单")
    public Result<Void> payOrder(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody Map<String, String> data) {
        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        String paymentMethod = data.get("paymentMethod");
        log.info("支付订单：userId={}, id={}, paymentMethod={}", userId, id, paymentMethod);

        // 更新订单状态为已支付
        orderService.updateOrderStatus(id, 1, userId); // 1-已支付

        // TODO: 这里应该调用真实的支付接口（支付宝/微信支付）

        return Result.success();
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取订单统计")
    public Result<Map<String, Object>> getOrderStatistics() {
        log.info("查询订单统计");
        // TODO: 实现订单统计查询
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", 0);
        stats.put("pendingOrders", 0);
        stats.put("completedOrders", 0);
        stats.put("cancelledOrders", 0);
        return Result.success(stats);
    }
}
