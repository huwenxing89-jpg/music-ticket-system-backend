package com.music.modules.comment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.comment.dto.CommentAuditDTO;
import com.music.modules.comment.dto.CommentQueryDTO;
import com.music.modules.comment.entity.Comment;
import com.music.modules.comment.mapper.CommentMapper;
import com.music.modules.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 评论服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public IPage<Comment> getCommentPage(CommentQueryDTO dto) {
        Page<Comment> page = new Page<>(dto.getCurrent(), dto.getSize());
        return commentMapper.selectCommentPage(page, dto.getShowId(), null, dto.getAuditStatus(), dto.getKeyword());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditComment(CommentAuditDTO dto) {
        Comment comment = commentMapper.selectById(dto.getId());
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (comment.getAuditStatus() != 0) {
            throw new BusinessException("该评论已审核");
        }

        comment.setAuditStatus(dto.getAuditStatus());
        int result = commentMapper.updateById(comment);
        log.info("审核评论：id={}, auditStatus={}", dto.getId(), dto.getAuditStatus());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        int result = commentMapper.deleteById(id);
        log.info("删除评论：id={}", id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setQualityComment(Long id, Integer isQuality) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        comment.setIsQuality(isQuality);
        int result = commentMapper.updateById(comment);
        log.info("设置优质评论：id={}, isQuality={}", id, isQuality);
        return result > 0;
    }
}
