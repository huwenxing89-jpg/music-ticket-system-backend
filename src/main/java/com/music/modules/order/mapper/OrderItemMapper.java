package com.music.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.modules.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细Mapper
 *
 * @author 黄晓倩
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
