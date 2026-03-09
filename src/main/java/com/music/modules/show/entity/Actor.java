package com.music.modules.show.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 演员实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("actor")
public class Actor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 演员ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 演员姓名
     */
    private String name;

    /**
     * 演员头像URL
     */
    private String avatar;

    /**
     * 演员简介
     */
    private String introduction;

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

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
