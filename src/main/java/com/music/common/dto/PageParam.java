package com.music.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页参数基类
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "分页参数")
public class PageParam {

    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
