package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 公告保存DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "公告保存参数")
public class AnnouncementSaveDTO {

    @Schema(description = "标题", required = true)
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "内容", required = true)
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "类型（1-系统公告，2-活动公告，3-维护公告）")
    private Integer type;

    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;

    @Schema(description = "是否置顶（0-否，1-是）")
    private Integer isTop;
}
