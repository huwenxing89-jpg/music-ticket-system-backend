package com.music.modules.theater.controller;

import com.music.common.result.Result;
import com.music.modules.theater.entity.Theater;
import com.music.modules.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 剧院控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/theater")
@RequiredArgsConstructor
@Tag(name = "剧院管理")
public class TheaterController {

    private final TheaterService theaterService;

    /**
     * 获取剧院列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取剧院列表")
    public Result<List<Theater>> getTheaterList() {
        log.info("查询剧院列表");
        List<Theater> theaters = theaterService.list();
        return Result.success(theaters);
    }

    /**
     * 获取剧院详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取剧院详情")
    public Result<Theater> getTheaterDetail(@Parameter(description = "剧院ID") @PathVariable Long id) {
        log.info("查询剧院详情：id={}", id);
        Theater theater = theaterService.getById(id);
        return Result.success(theater);
    }

    /**
     * 获取剧院场次列表
     */
    @GetMapping("/{theaterId}/sessions")
    @Operation(summary = "获取剧院场次列表")
    public Result<Map<String, Object>> getTheaterSessions(
            @Parameter(description = "剧院ID") @PathVariable Long theaterId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        log.info("查询剧院场次列表：theaterId={}, page={}, size={}", theaterId, page, size);
        // TODO: 实现剧院场次列表查询
        Map<String, Object> result = new HashMap<>();
        result.put("records", Collections.emptyList());
        result.put("total", 0);
        result.put("current", page);
        result.put("size", size);

        return Result.success(result);
    }
}
