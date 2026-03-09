package com.music.modules.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.modules.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 评论Mapper
 *
 * @author 黄晓倩
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表（带搜索和筛选）
     *
     * @param page        分页对象
     * @param showId      剧目ID（可选）
     * @param userId      用户ID（可选）
     * @param auditStatus 审核状态（可选）
     * @param keyword     关键词（可选）
     * @return 评论列表
     */
    IPage<Comment> selectCommentPage(Page<Comment> page,
                                      @Param("showId") Long showId,
                                      @Param("userId") Long userId,
                                      @Param("auditStatus") Integer auditStatus,
                                      @Param("keyword") String keyword);
}
