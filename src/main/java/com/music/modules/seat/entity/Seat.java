package com.music.modules.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 座位实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("seat")
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 座位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场次ID
     */
    private Long sessionId;

    /**
     * 排号
     */
    private Integer rowNum;

    /**
     * 列号
     */
    private Integer colNum;

    /**
     * 座位号（如：1排1号）
     */
    private String seatNumber;

    /**
     * 座位类型：0-普通座，1-VIP座，2-情侣座
     */
    private Integer seatType;

    /**
     * 座位价格
     */
    private BigDecimal price;

    /**
     * 座位状态：0-可选，1-已锁定，2-已售出
     */
    private Integer status;

    /**
     * 锁定用户ID（当状态为锁定时记录）
     */
    private Long lockedUserId;

    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;

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
