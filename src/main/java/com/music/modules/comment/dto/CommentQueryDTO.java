package com.music.modules.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评论查询DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "评论查询参数")
public class CommentQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "剧目ID")
    private Long showId;

    @Schema(description = "审核状态（0-待审核，1-已通过，2-已拒绝）")
    private Integer auditStatus;

    @Schema(description = "审核状态（别名）")
    private Integer status;

    @Schema(description = "关键词（用户名/剧目名/评论内容）")
    private String keyword;
}
