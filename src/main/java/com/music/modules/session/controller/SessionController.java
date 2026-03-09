package com.music.modules.session.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.session.entity.ShowSession;
import com.music.modules.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 场次控制器（用户端）
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Tag(name = "场次管理（用户端）")
public class SessionController {

    private final SessionService sessionService;

    /**
     * 分页查询场次列表
     */
    @GetMapping
    @Operation(summary = "分页查询场次列表")
    public Result<IPage<ShowSession>> getSessionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long showId,
            @RequestParam(required = false) Long theaterId,
            @RequestParam(required = false) Integer status) {

        log.info("查询场次列表：page={}, size={}, showId={}, theaterId={}, status={}",
                page, size, showId, theaterId, status);

        Page<ShowSession> pageParam = new Page<>(page, size);
        IPage<ShowSession> result = sessionService.getSessionList(pageParam, showId, theaterId, status);

        return Result.success(result);
    }

    /**
     * 获取场次详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取场次详情")
    public Result<ShowSession> getSessionDetail(@PathVariable Long id) {
        log.info("查询场次详情：id={}", id);
        ShowSession session = sessionService.getSessionDetail(id);
        return Result.success(session);
    }
}
