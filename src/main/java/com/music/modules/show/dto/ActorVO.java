package com.music.modules.show.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 演员信息VO（用于剧目详情展示）
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "演员信息VO")
public class ActorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 演员ID
     */
    @Schema(description = "演员ID")
    private Long id;

    /**
     * 演员姓名
     */
    @Schema(description = "演员姓名")
    private String name;

    /**
     * 演员头像URL
     */
    @Schema(description = "演员头像URL")
    private String avatar;

    /**
     * 演员简介
     */
    @Schema(description = "演员简介")
    private String introduction;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 主演标记：0-普通演员，1-主演
     */
    @Schema(description = "主演标记")
    private Integer isMain;
}
