package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 操作日志查询DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "操作日志查询参数")
public class OperatorLogQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "操作类型（1-新增，2-修改，3-删除，4-查询，5-导出，6-其他）")
    private Integer operationType;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
