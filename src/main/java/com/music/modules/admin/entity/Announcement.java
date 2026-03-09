package com.music.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体
 *
 * @author 黄晓倩
 */
@Data
@TableName("announcement")
public class Announcement {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型（1-系统公告，2-活动公告，3-维护公告）
     */
    private Integer type;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 是否置顶（0-否，1-是）
     */
    @TableField("is_top")
    private Integer isTop;

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
