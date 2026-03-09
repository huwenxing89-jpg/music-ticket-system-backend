package com.music.modules.show.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 场次信息VO（用于剧目详情展示）
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "场次信息VO")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class SessionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场次ID
     */
    @Schema(description = "场次ID")
    private Long id;

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
     * 票价（最低价）
     */
    @Schema(description = "票价")
    private Integer price;

    /**
     * 剧院ID
     */
    @Schema(description = "剧院ID")
    private Long theaterId;

    /**
     * 剧院名称
     */
    @Schema(description = "剧院名称")
    private String theaterName;

    /**
     * 可用座位数
     */
    @Schema(description = "可用座位数")
    private Integer availableSeats;
}
