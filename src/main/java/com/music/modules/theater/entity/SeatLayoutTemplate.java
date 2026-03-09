package com.music.modules.theater.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 座位布局模板实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("seat_layout_template")
public class SeatLayoutTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 适用剧院类型：small/medium/large/concert
     */
    private String theaterType;

    /**
     * 总行数
     */
    private Integer totalRows;

    /**
     * 每行座位数（可用JSON配置不同行的座位数）
     */
    private Integer seatsPerRow;

    /**
     * VIP排数配置
     */
    private String vipRows;

    /**
     * 学生票排数配置
     */
    private String studentRows;

    /**
     * 详细布局配置（JSON格式）
     */
    private String layoutConfig;

    /**
     * 预览图
     */
    private String previewImage;

    /**
     * 是否默认模板
     */
    private Integer isDefault;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

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
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
