package com.music.modules.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 *
 * @author 黄晓倩
 */
@Data
@TableName("comment")
public class Comment {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（不存储到数据库，从用户表关联查询）
     */
    @TableField(exist = false)
    private String username;

    /**
     * 用户头像（不存储到数据库，从用户表关联查询）
     */
    @TableField(exist = false)
    private String userAvatar;

    /**
     * 剧目ID
     */
    private Long showId;

    /**
     * 剧目名称
     */
    @TableField(exist = false)
    private String showName;

    /**
     * 场次ID
     */
    @TableField(exist = false)
    private Long sessionId;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 图片（JSON数组）
     */
    private String images;

    /**
     * 审核状态（0-待审核，1-已通过，2-已拒绝）
     */
    @TableField("status")
    @JsonProperty("status")
    private Integer auditStatus;

    /**
     * 是否优质评论（0-否，1-是）
     */
    @TableField("is_excellent")
    private Integer isQuality;

    /**
     * 点赞数
     */
    @TableField(exist = false)
    private Integer likeCount;

    /**
     * 回复数
     */
    @TableField(exist = false)
    private Integer replyCount;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
