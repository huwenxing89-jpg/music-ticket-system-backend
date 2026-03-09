package com.music.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 *
 * @author 黄晓倩
 */
@Data
@ApiModel(description = "登录响应")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "访问令牌")
    private String token;

    @ApiModelProperty(value = "用户信息")
    private UserVO userInfo;
}
