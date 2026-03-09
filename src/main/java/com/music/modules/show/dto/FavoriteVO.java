package com.music.modules.show.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏信息VO（包含剧目详情）
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "收藏信息VO")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class FavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 剧目ID
     */
    @Schema(description = "剧目ID")
    private Long showId;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    private LocalDateTime createTime;

    /**
     * 剧目名称
     */
    @Schema(description = "剧目名称")
    private String showTitle;

    /**
     * 剧目封面
     */
    @Schema(description = "剧目封面")
    private String poster;

    /**
     * 剧目描述
     */
    @Schema(description = "剧目描述")
    private String description;

    /**
     * 时长（分钟）
     */
    @Schema(description = "时长（分钟）")
    private Integer duration;

    /**
     * 类型（1-音乐剧，2-话剧，3-演唱会等）
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 状态（0-未开始，1-正在上映，2-已结束）
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 评分
     */
    @Schema(description = "评分")
    private BigDecimal rating;

    /**
     * 最低票价
     */
    @Schema(description = "最低票价")
    private BigDecimal minPrice;

    /**
     * 最高票价
     */
    @Schema(description = "最高票价")
    private BigDecimal maxPrice;
}
