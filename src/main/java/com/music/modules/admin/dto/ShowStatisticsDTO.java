package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 剧目统计DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "剧目统计数据")
public class ShowStatisticsDTO {

    @Schema(description = "总剧目数")
    private Long totalShows;

    @Schema(description = "在售剧目数")
    private Long activeShows;

    @Schema(description = "今日上映剧目数")
    private Long todayShows;

    @Schema(description = "热门剧目排行（按订单量）")
    private List<Map<String, Object>> hotShows;

    @Schema(description = "剧目分类统计")
    private Map<String, Long> categoryStatistics;
}
