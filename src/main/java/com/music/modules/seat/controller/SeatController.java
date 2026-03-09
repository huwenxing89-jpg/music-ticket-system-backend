package com.music.modules.seat.controller;

import com.music.common.result.Result;
import com.music.common.utils.JwtUtil;
import com.music.modules.seat.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 座位控制器（用户端）
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/seat")
@RequiredArgsConstructor
@Tag(name = "座位管理（用户端）")
public class SeatController {

    private final SeatService seatService;
    private final JwtUtil jwtUtil;

    /**
     * 获取场次座位状态
     */
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "获取场次座位状态")
    public Result<Map<String, Object>> getSessionSeatStatus(
            @Parameter(description = "场次ID") @PathVariable Long sessionId) {

        log.info("查询场次座位状态：sessionId={}", sessionId);
        Map<String, Object> data = seatService.getSessionSeatStatus(sessionId);
        return Result.success(data);
    }

    /**
     * 锁定座位
     */
    @PostMapping("/lock")
    @Operation(summary = "锁定座位")
    public Result<Void> lockSeats(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {

        Long sessionId = Long.valueOf(params.get("sessionId").toString());
        @SuppressWarnings("unchecked")
        List<Long> seatIds = (List<Long>) params.get("seatIds");

        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("用户{}锁定场次{}的座位", userId, sessionId);
        seatService.lockSeats(sessionId, seatIds, userId);
        return Result.success("座位锁定成功");
    }

    /**
     * 释放座位
     */
    @PostMapping("/unlock")
    @Operation(summary = "释放座位")
    public Result<Void> unlockSeats(@RequestBody Map<String, Object> params) {

        @SuppressWarnings("unchecked")
        List<Long> seatIds = (List<Long>) params.get("seatIds");

        log.info("释放座位：{}", seatIds);
        seatService.unlockSeats(seatIds);
        return Result.success("座位释放成功");
    }

    /**
     * 批量锁定座位
     */
    @PostMapping("/lock/batch")
    @Operation(summary = "批量锁定座位")
    public Result<Void> batchLockSeats(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {

        @SuppressWarnings("unchecked")
        List<Long> seatIds = (List<Long>) params.get("seatIds");

        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("用户{}批量锁定座位", userId);
        seatService.unlockSeats(seatIds);
        return Result.success("座位批量锁定成功");
    }
}
