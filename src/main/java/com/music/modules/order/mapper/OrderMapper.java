package com.music.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.modules.order.dto.OrderListDTO;
import com.music.modules.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper
 *
 * @author 黄晓倩
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询超时未支付订单
     *
     * @param timeout 超时时间
     * @return 订单列表
     */
    List<Order> selectExpiredOrders(@Param("timeout") LocalDateTime timeout);

    /**
     * 分页查询订单列表（包含用户名、剧目名、剧院名等信息）
     *
     * @param page     分页对象
     * @param orderNo  订单号
     * @param userId   用户ID
     * @param status   订单状态
     * @return 订单列表
     */
    IPage<OrderListDTO> selectOrderListWithDetails(
            Page<OrderListDTO> page,
            @Param("orderNo") String orderNo,
            @Param("userId") Long userId,
            @Param("status") Integer status
    );

    /**
     * 查询订单详情（包含用户名、剧目名、剧院名等信息）
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderListDTO selectOrderDetailById(@Param("orderId") Long orderId);
}
