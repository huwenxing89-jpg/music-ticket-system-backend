package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录日志查询DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "登录日志查询参数")
public class LoginLogQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录状态（1-成功，0-失败）")
    private Integer status;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}
