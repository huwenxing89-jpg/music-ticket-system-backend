package com.music.modules.session.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 场次实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("`session`")
@Schema(description = "场次")
public class ShowSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场次ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "场次ID")
    private Long id;

    /**
     * 剧目ID
     */
    @Schema(description = "剧目ID")
    private Long showId;

    /**
     * 剧院ID
     */
    @Schema(description = "剧院ID")
    private Long theaterId;

    /**
     * 演出时间
     */
    @Schema(description = "演出时间")
    private LocalDateTime showTime;

    /**
     * 时长（分钟）
     */
    @Schema(description = "时长（分钟）")
    private Integer duration;

    /**
     * 票价
     */
    @Schema(description = "票价")
    private Integer price;

    /**
     * 票价类型
     */
    @TableField("price_types")
    @Schema(description = "票价类型")
    private String priceTypes;

    /**
     * VIP票价（覆盖剧目票价）
     */
    @Schema(description = "VIP票价")
    @TableField("vip_price")
    private java.math.BigDecimal vipPrice;

    /**
     * 普通票价（覆盖剧目票价）
     */
    @Schema(description = "普通票价")
    @TableField("normal_price")
    private java.math.BigDecimal normalPrice;

    /**
     * 学生票价（覆盖剧目票价）
     */
    @Schema(description = "学生票价")
    @TableField("student_price")
    private java.math.BigDecimal studentPrice;

    /**
     * 优惠票价（覆盖剧目票价）
     */
    @Schema(description = "优惠票价")
    @TableField("discount_price")
    private java.math.BigDecimal discountPrice;

    /**
     * 总座位数
     */
    @Schema(description = "总座位数")
    private Integer totalSeats;

    /**
     * 已售座位数
     */
    @Schema(description = "已售座位数")
    private Integer soldSeats;

    /**
     * 状态：0-未开始，1-进行中，2-已结束，3-已取消
     */
    @Schema(description = "状态：0-未开始，1-进行中，2-已结束，3-已取消")
    private Integer status;

    /**
     * 取消原因
     */
    @Schema(description = "取消原因")
    private String cancelReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    /**
     * 剧院名称（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    @Schema(description = "剧院名称")
    private String theaterName;

    /**
     * 剧目名称（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    @Schema(description = "剧目名称")
    private String showTitle;
}
