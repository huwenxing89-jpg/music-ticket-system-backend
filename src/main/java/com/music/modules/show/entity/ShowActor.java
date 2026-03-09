package com.music.modules.show.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 剧目演员关联实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("show_actor")
public class ShowActor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 剧目ID
     */
    private Long showId;

    /**
     * 演员ID
     */
    private Long actorId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 主演标记：0-普通演员，1-主演
     */
    @TableField(exist = false)
    private Integer isMain;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
