package com.music.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统配置更新DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "系统配置更新参数")
public class SystemConfigUpdateDTO {

    @Schema(description = "配置键", required = true)
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值", required = true)
    @NotBlank(message = "配置值不能为空")
    private String configValue;
}
