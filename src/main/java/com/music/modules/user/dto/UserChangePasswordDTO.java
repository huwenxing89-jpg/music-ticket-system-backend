package com.music.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 修改密码DTO
 *
 * @author 黄晓倩
 */
@Data
@ApiModel(description = "修改密码请求")
public class UserChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "原密码", required = true)
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$", message = "密码必须是6-20位，且包含字母和数字")
    private String newPassword;

    @ApiModelProperty(value = "确认新密码", required = true)
    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}
