package com.music.modules.comment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.common.utils.JwtUtil;
import com.music.modules.comment.entity.Comment;
import com.music.modules.comment.mapper.CommentMapper;
import com.music.modules.user.entity.User;
import com.music.modules.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户端-评论控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "用户端-评论管理")
public class CommentController {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /**
     * 添加评论
     */
    @PostMapping
    @Operation(summary = "添加评论")
    public Result<Void> addComment(HttpServletRequest request,
                                   @RequestBody Comment comment) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 设置评论信息
        comment.setUserId(userId);
        comment.setAuditStatus(1); // 已通过（无需审批）
        comment.setIsQuality(0);

        // 插入评论
        commentMapper.insert(comment);

        log.info("用户{}添加评论：showId={}, rating={}", userId, comment.getShowId(), comment.getRating());
        return Result.success();
    }

    /**
     * 获取剧目评论列表
     */
    @GetMapping("/list/{showId}")
    @Operation(summary = "获取剧目评论列表")
    public Result<IPage<Comment>> getCommentList(
            @Parameter(description = "剧目ID") @PathVariable Long showId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        log.info("查询剧目{}的评论列表：page={}, size={}", showId, current, size);

        // 分页查询已审核通过的评论（带用户信息）
        Page<Comment> page = new Page<>(current, size);
        IPage<Comment> result = commentMapper.selectCommentPage(page, showId, null, 1, null);

        return Result.success(result);
    }

    /**
     * 获取用户的评论列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的评论列表")
    public Result<IPage<Comment>> getMyComments(HttpServletRequest request,
                                                @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
                                                @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        log.info("查询用户{}的评论列表：page={}, size={}", userId, current, size);

        // 分页查询用户的所有评论（带用户信息）
        Page<Comment> page = new Page<>(current, size);
        IPage<Comment> result = commentMapper.selectCommentPage(page, null, userId, null, null);

        return Result.success(result);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(HttpServletRequest request,
                                      @Parameter(description = "评论ID") @PathVariable Long id) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 查询评论
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.fail("评论不存在");
        }

        // 检查是否是评论作者
        if (!comment.getUserId().equals(userId)) {
            return Result.fail("无权删除此评论");
        }

        // 删除评论
        commentMapper.deleteById(id);

        log.info("用户{}删除评论：id={}", userId, id);
        return Result.success();
    }
}
