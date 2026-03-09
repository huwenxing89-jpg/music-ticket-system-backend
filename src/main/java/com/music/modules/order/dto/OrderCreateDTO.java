package com.music.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建订单DTO
 *
 * @author 黄晓倩
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 剧目ID
     */
    @NotNull(message = "剧目ID不能为空")
    @Schema(description = "剧目ID")
    private Long showId;

    /**
     * 场次ID
     */
    @NotNull(message = "场次ID不能为空")
    @Schema(description = "场次ID")
    private Long sessionId;

    /**
     * 座位列表
     */
    @NotEmpty(message = "请选择座位")
    @Schema(description = "座位列表")
    private List<SeatDTO> seats;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    @Schema(description = "联系人姓名")
    private String contactName;

    /**
     * 联系人手机
     */
    @NotBlank(message = "联系人手机不能为空")
    @Schema(description = "联系人手机")
    private String contactPhone;

    /**
     * 座位DTO
     */
    @Data
    @Schema(description = "座位信息")
    public static class SeatDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 排号
         */
        @NotNull(message = "座位排号不能为空")
        @Schema(description = "排号")
        private Integer rowNum;

        /**
         * 列号
         */
        @NotNull(message = "座位列号不能为空")
        @Schema(description = "列号")
        private Integer colNum;

        /**
         * 价格
         */
        @NotNull(message = "座位价格不能为空")
        @Schema(description = "价格")
        private java.math.BigDecimal price;
    }
}
