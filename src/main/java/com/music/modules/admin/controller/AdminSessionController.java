package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.session.entity.ShowSession;
import com.music.modules.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员-场次管理控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/session")
@RequiredArgsConstructor
@Tag(name = "管理员-场次管理")
public class AdminSessionController {

    private final SessionService sessionService;

    /**
     * 分页查询场次列表
     */
    @GetMapping
    @Operation(summary = "分页查询场次列表")
    public Result<Map<String, Object>> getSessionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "剧目ID") @RequestParam(required = false) Long showId,
            @Parameter(description = "剧院ID") @RequestParam(required = false) Long theaterId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        log.info("管理员查询场次列表：page={}, size={}, showId={}, theaterId={}, status={}",
            page, size, showId, theaterId, status);

        Page<ShowSession> pageParam = new Page<>(page, size);
        IPage<ShowSession> result = sessionService.getSessionList(pageParam, showId, theaterId, status);

        // 为每个场次添加座位统计信息
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> records = new ArrayList<>();

        for (ShowSession session : result.getRecords()) {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("id", session.getId());
            sessionData.put("showId", session.getShowId());
            sessionData.put("theaterId", session.getTheaterId());
            sessionData.put("showTime", session.getShowTime());
            sessionData.put("status", session.getStatus());
            sessionData.put("totalSeats", session.getTotalSeats());
            sessionData.put("createTime", session.getCreateTime());

            // 获取座位统计信息
            try {
                Map<String, Object> seatStats = sessionService.getSessionSeatStatus(session.getId());
                sessionData.put("soldSeats", seatStats.get("soldSeats") != null ? seatStats.get("soldSeats") : 0);
                sessionData.put("availableSeats", seatStats.get("availableSeats") != null ? seatStats.get("availableSeats") : 0);
                sessionData.put("lockedSeats", seatStats.get("lockedSeats") != null ? seatStats.get("lockedSeats") : 0);
            } catch (Exception e) {
                log.warn("获取场次{}座位统计失败：{}", session.getId(), e.getMessage());
                sessionData.put("soldSeats", 0);
                sessionData.put("availableSeats", session.getTotalSeats() != null ? session.getTotalSeats() : 0);
                sessionData.put("lockedSeats", 0);
            }

            records.add(sessionData);
        }

        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());
        response.put("pages", result.getPages());

        return Result.success(response);
    }

    /**
     * 获取场次详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取场次详情")
    public Result<ShowSession> getSessionDetail(@Parameter(description = "场次ID") @PathVariable Long id) {
        log.info("查询场次详情：id={}", id);
        ShowSession session = sessionService.getSessionDetail(id);
        return Result.success(session);
    }

    /**
     * 创建场次
     */
    @PostMapping
    @Operation(summary = "创建场次")
    public Result<Void> createSession(@RequestBody ShowSession session) {
        log.info("创建场次：showId={}, showTime={}", session.getShowId(), session.getShowTime());
        sessionService.createSession(session);
        return Result.success();
    }

    /**
     * 更新场次
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新场次")
    public Result<Void> updateSession(
            @Parameter(description = "场次ID") @PathVariable Long id,
            @RequestBody ShowSession session) {
        log.info("更新场次：id={}", id);
        sessionService.updateSession(id, session);
        return Result.success();
    }

    /**
     * 取消场次
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消场次")
    public Result<Void> cancelSession(
            @Parameter(description = "场次ID") @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> data) {
        String reason = data != null ? data.getOrDefault("reason", "") : "";
        log.info("取消场次：id={}, reason={}", id, reason);
        sessionService.cancelSession(id, reason);
        return Result.success();
    }

    /**
     * 获取场次座位销售情况
     */
    @GetMapping("/{sessionId}/seats")
    @Operation(summary = "获取场次座位销售情况")
    public Result<Map<String, Object>> getSessionSeatStatus(
            @Parameter(description = "场次ID") @PathVariable Long sessionId) {
        log.info("查询场次座位销售情况：sessionId={}", sessionId);
        Map<String, Object> result = sessionService.getSessionSeatStatus(sessionId);
        return Result.success(result);
    }
}
