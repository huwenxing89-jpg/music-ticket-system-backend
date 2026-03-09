package com.music.modules.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.cart.dto.AddCartDTO;
import com.music.modules.cart.entity.Cart;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务接口
 *
 * @author 黄晓倩
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加到购物车
     *
     * @param dto   购物车数据
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean addToCart(AddCartDTO dto, Long userId);

    /**
     * 获取用户购物车列表（按场次分组）
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Map<String, Object>> getUserCart(Long userId);

    /**
     * 从购物车删除
     *
     * @param id    购物车ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeFromCart(Long id, Long userId);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearCart(Long userId);
}
