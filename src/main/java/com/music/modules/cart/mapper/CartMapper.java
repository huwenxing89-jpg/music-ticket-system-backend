package com.music.modules.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.modules.cart.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper接口
 *
 * @author 黄晓倩
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
