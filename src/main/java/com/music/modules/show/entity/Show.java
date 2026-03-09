package com.music.modules.show.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.music.modules.show.dto.ActorVO;
import com.music.modules.show.dto.SessionVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 剧目实体类
 *
 * @author 黄晓倩
 */
@Data
@TableName("`show`")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Show implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 剧目ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 剧目名称
     */
    private String name;

    /**
     * 剧目简介
     */
    private String description;

    /**
     * 剧目类别（如：音乐剧、话剧、舞剧、歌剧等）
     * 注意：此字段不在数据库表中，仅用于前端展示
     */
    @TableField(exist = false)
    private String category;

    /**
     * 剧目海报URL
     */
    private String poster;

    /**
     * 剧目时长（分钟）
     */
    private Integer duration;

    /**
     * 语言（如：中文、英文等）
     */
    private String language;

    /**
     * 导演
     */
    private String director;

    /**
     * 编剧
     */
    private String playwright;

    /**
     * 首演时间
     */
    private String premiereDate;

    /**
     * 剧目类型：0-原创，1-引进，2-经典
     */
    private Integer type;

    /**
     * 剧目状态：0-即将上映，1-正在热映，2-已下映
     */
    private Integer status;

    /**
     * 评分（0-5分）
     */
    private BigDecimal rating;

    /**
     * VIP票价格
     */
    @TableField("vip_price")
    private BigDecimal vipPrice;

    /**
     * 普通票价格
     */
    @TableField("normal_price")
    private BigDecimal normalPrice;

    /**
     * 学生票价格
     */
    @TableField("student_price")
    private BigDecimal studentPrice;

    /**
     * 优惠票价格
     */
    @TableField("discount_price")
    private BigDecimal discountPrice;

    /**
     * 最低价格（用于展示）
     */
    @TableField(exist = false)
    private BigDecimal minPrice;

    /**
     * 最高价格（用于展示）
     */
    @TableField(exist = false)
    private BigDecimal maxPrice;

    /**
     * 场次列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SessionVO> sessions;

    /**
     * 演员列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<ActorVO> actors;

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
