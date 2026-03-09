package com.music.modules.comment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.comment.dto.CommentAuditDTO;
import com.music.modules.comment.dto.CommentQueryDTO;
import com.music.modules.comment.entity.Comment;
import com.music.modules.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端-评论控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@Tag(name = "管理端-评论管理")
@RestController
@RequestMapping("/api/admin/comment")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    /**
     * 分页查询评论列表
     */
    @Operation(summary = "分页查询评论列表")
    @GetMapping
    public Result<IPage<Comment>> getCommentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "剧目ID") @RequestParam(required = false) Long showId,
            @Parameter(description = "审核状态") @RequestParam(required = false) Integer status) {

        log.info("查询评论列表：page={}, size={}, showId={}, status={}", page, size, showId, status);

        CommentQueryDTO dto = new CommentQueryDTO();
        dto.setShowId(showId);
        dto.setAuditStatus(status);
        dto.setCurrent(page);
        dto.setSize(size);

        IPage<Comment> result = commentService.getCommentPage(dto);
        return Result.success(result);
    }

    /**
     * 获取评论详情
     */
    @Operation(summary = "获取评论详情")
    @GetMapping("/{id}")
    public Result<Comment> getCommentDetail(@Parameter(description = "评论ID") @PathVariable Long id) {
        log.info("查询评论详情：id={}", id);
        Comment comment = commentService.getById(id);
        return Result.success(comment);
    }

    /**
     * 审核评论
     */
    @Operation(summary = "审核评论")
    @PutMapping("/{id}/audit")
    public Result<Void> auditComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @RequestBody Map<String, Object> data) {

        Integer status = (Integer) data.get("status");
        String reason = (String) data.get("reason");

        log.info("审核评论：id={}, status={}, reason={}", id, status, reason);

        CommentAuditDTO dto = new CommentAuditDTO();
        dto.setId(id);
        dto.setStatus(status);
        dto.setReason(reason);

        commentService.auditComment(dto);
        return Result.success();
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        log.info("删除评论：id={}", id);
        commentService.deleteComment(id);
        return Result.success();
    }

    /**
     * 标记优质评论
     */
    @Operation(summary = "标记优质评论")
    @PutMapping("/{id}/quality")
    public Result<Void> markQualityComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @RequestBody Map<String, Boolean> data) {

        Boolean isQuality = data.get("isQuality");
        log.info("标记优质评论：id={}, isQuality={}", id, isQuality);
        commentService.setQualityComment(id, isQuality ? 1 : 0);
        return Result.success();
    }
}

