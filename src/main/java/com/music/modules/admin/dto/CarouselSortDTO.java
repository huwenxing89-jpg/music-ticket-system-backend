package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 轮播图排序DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "轮播图排序参数")
public class CarouselSortDTO {

    @Schema(description = "轮播图ID", required = true)
    @NotNull(message = "轮播图ID不能为空")
    private Long id;

    @Schema(description = "排序值", required = true)
    @NotNull(message = "排序值不能为空")
    private Integer sort;
}
