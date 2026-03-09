package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 轮播图保存DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "轮播图保存参数")
public class CarouselSaveDTO {

    @Schema(description = "标题", required = true)
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "图片URL", required = true)
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    @Schema(description = "链接URL")
    private String linkUrl;

    @Schema(description = "排序（数字越小越靠前）")
    private Integer sort;

    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;
}
