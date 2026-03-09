package com.music.modules.cart.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 添加购物车请求DTO
 *
 * @author 黄晓倩
 */
@Data
public class AddCartDTO implements Serializable {

    /**
     * 场次ID
     */
    @NotNull(message = "场次ID不能为空")
    private Long sessionId;

    /**
     * 座位ID
     */
    @NotNull(message = "座位ID不能为空")
    private Long seatId;
}
