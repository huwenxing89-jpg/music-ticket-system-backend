package com.music.modules.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.order.dto.OrderCreateDTO;
import com.music.modules.order.dto.OrderListDTO;
import com.music.modules.order.entity.Order;

import java.util.Map;

/**
 * 订单服务接口
 *
 * @author 黄晓倩
 */
public interface OrderService extends IService<Order> {

    /**
     * 分页查询订单列表（包含用户名、剧目名、剧院名等信息）
     *
     * @param page    分页对象
     * @param orderNo 订单号
     * @param userId  用户ID
     * @param status  订单状态
     * @return 订单列表
     */
    IPage<OrderListDTO> getOrderList(Page<OrderListDTO> page, String orderNo, Long userId, Integer status);

    /**
     * 获取订单详情（包含用户名、剧目名、剧院名等信息）
     *
     * @param id     订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 订单详情
     */
    OrderListDTO getOrderDetail(Long id, Long userId);

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 新状态
     * @param userId 用户ID（用于权限验证，null表示管理员操作）
     * @return 是否成功
     */
    boolean updateOrderStatus(Long id, Integer status, Long userId);

    /**
     * 手动出票
     *
     * @param id 订单ID
     * @return 是否成功
     */
    boolean issueTicket(Long id);

    /**
     * 处理退票申请
     *
     * @param orderId 订单ID
     * @param approve 是否同意
     * @param reason  原因
     * @return 是否成功
     */
    boolean handleRefund(Long orderId, boolean approve, String reason);

    /**
     * 创建订单
     *
     * @param dto     订单创建DTO
     * @param userId  用户ID
     * @return 订单信息
     */
    Map<String, Object> createOrder(OrderCreateDTO dto, Long userId);
}
