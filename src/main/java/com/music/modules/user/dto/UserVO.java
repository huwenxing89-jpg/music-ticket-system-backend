package com.music.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 *
 * @author 黄晓倩
 */
@Data
@ApiModel(description = "用户信息")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "是否实名认证：0-未认证，1-已认证")
    private Integer isVerified;

    @ApiModelProperty(value = "角色")
    private String role;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
