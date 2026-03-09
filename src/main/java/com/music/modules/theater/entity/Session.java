package com.music.modules.theater.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 场次实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("session")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场次ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 剧目ID
     */
    private Long showId;

    /**
     * 剧院ID
     */
    private Long theaterId;

    /**
     * 演出时间
     */
    private LocalDateTime showTime;

    /**
     * 场次状态：0-未开始，1-进行中，2-已结束，3-已取消
     */
    private Integer status;

    /**
     * 可售座位数
     */
    private Integer availableSeats;

    /**
     * 已售座位数
     */
    private Integer soldSeats;

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
