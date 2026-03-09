package com.music.modules.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 座位类型配置实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("seat_type_config")
public class SeatTypeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型代码：vip/normal/student/discount
     */
    private String typeCode;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 默认价格
     */
    private BigDecimal defaultPrice;

    /**
     * 显示颜色
     */
    private String color;

    /**
     * 图标
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 座位类型：0-普通座，1-VIP座，2-学生票，3-优惠票
     */
    @TableField(exist = false)
    private Integer seatType;
}
