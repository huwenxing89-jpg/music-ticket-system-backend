package com.music.modules.theater.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 剧院实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("theater")
public class Theater implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 剧院ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 剧院名称
     */
    private String name;

    /**
     * 剧院地址
     */
    private String address;

    /**
     * 所在地区
     */
    @TableField("region")
    private String region;

    /**
     * 剧院设施介绍
     */
    private String facilities;

    /**
     * 座位图模板（JSON格式）
     */
    private String seatMapTemplate;

    /**
     * VIP排数范围（如：1-3 或 1-3,8-10）
     */
    private String vipRows;

    /**
     * 学生票排数范围（如：8-10 或 倒数2排）
     */
    private String studentRows;

    /**
     * 座位布局配置（JSON格式）
     */
    private String seatLayout;

    /**
     * 布局类型：1-标准矩形，2-扇形，3-自定义
     */
    private Integer layoutType;

    /**
     * 总座位数
     */
    private Integer totalSeats;

    /**
     * 剧院图片URL
     */
    private String image;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 交通指南
     */
    private String trafficGuide;

    /**
     * 剧院简介
     */
    private String description;

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
