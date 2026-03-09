package com.music.modules.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 评论审核DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "评论审核参数")
public class CommentAuditDTO {

    @Schema(description = "评论ID", required = true)
    @NotNull(message = "评论ID不能为空")
    private Long id;

    @Schema(description = "审核状态（1-通过，2-拒绝）", required = true)
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "审核状态（别名）")
    private Integer status;

    @Schema(description = "拒绝原因")
    private String reason;
}
