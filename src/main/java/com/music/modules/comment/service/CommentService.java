package com.music.modules.comment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.comment.dto.CommentAuditDTO;
import com.music.modules.comment.dto.CommentQueryDTO;
import com.music.modules.comment.entity.Comment;

/**
 * 评论服务接口
 *
 * @author 黄晓倩
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询评论列表
     *
     * @param dto 查询参数
     * @return 评论列表
     */
    IPage<Comment> getCommentPage(CommentQueryDTO dto);

    /**
     * 审核评论
     *
     * @param dto 审核参数
     * @return 是否成功
     */
    boolean auditComment(CommentAuditDTO dto);

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 是否成功
     */
    boolean deleteComment(Long id);

    /**
     * 设置/取消优质评论
     *
     * @param id        评论ID
     * @param isQuality 是否优质
     * @return 是否成功
     */
    boolean setQualityComment(Long id, Integer isQuality);
}
