package com.music.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.order.dto.OrderCreateDTO;
import com.music.modules.order.dto.OrderListDTO;
import com.music.modules.order.entity.Order;
import com.music.modules.order.entity.OrderItem;
import com.music.modules.order.mapper.OrderItemMapper;
import com.music.modules.order.mapper.OrderMapper;
import com.music.modules.order.service.OrderService;
import com.music.modules.seat.entity.Seat;
import com.music.modules.seat.mapper.SeatMapper;
import com.music.modules.session.entity.ShowSession;
import com.music.modules.session.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SeatMapper seatMapper;
    private final SessionMapper sessionMapper;

    @Override
    public IPage<OrderListDTO> getOrderList(Page<OrderListDTO> page, String orderNo, Long userId, Integer status) {
        IPage<OrderListDTO> resultPage = orderMapper.selectOrderListWithDetails(page, orderNo, userId, status);

        // 为每个订单填充座位信息
        for (OrderListDTO orderDTO : resultPage.getRecords()) {
            List<OrderListDTO.SeatInfo> seats = getOrderSeats(orderDTO.getId());
            orderDTO.setSeats(seats);
        }

        return resultPage;
    }

    @Override
    public OrderListDTO getOrderDetail(Long id, Long userId) {
        OrderListDTO orderDTO = orderMapper.selectOrderDetailById(id);
        if (orderDTO == null) {
            throw new BusinessException("订单不存在");
        }

        // 权限验证：只能查看自己的订单
        if (userId != null && !orderDTO.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }

        // 填充座位信息
        List<OrderListDTO.SeatInfo> seats = getOrderSeats(id);
        orderDTO.setSeats(seats);

        return orderDTO;
    }

    /**
     * 获取订单的座位信息
     *
     * @param orderId 订单ID
     * @return 座位信息列表
     */
    private List<OrderListDTO.SeatInfo> getOrderSeats(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);

        return orderItems.stream().map(item -> {
            OrderListDTO.SeatInfo seatInfo = new OrderListDTO.SeatInfo();
            seatInfo.setId(item.getId());
            seatInfo.setSeatNumber(item.getSeatNumber());
            seatInfo.setPrice(item.getPrice());

            // 如果seat_number格式为 "1排1座"，解析行列号
            if (item.getSeatNumber() != null && item.getSeatNumber().contains("排")) {
                String[] parts = item.getSeatNumber().replace("排", "#").replace("座", "").split("#");
                if (parts.length == 2) {
                    try {
                        seatInfo.setRowNum(Integer.parseInt(parts[0]));
                        seatInfo.setColNum(Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        // 解析失败，保持默认值
                    }
                }
            }

            return seatInfo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(Long id, Integer status, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 权限验证：只能操作自己的订单（userId为null时表示管理员操作）
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        Integer oldStatus = order.getStatus();
        order.setStatus(status);
        int result = orderMapper.updateById(order);
        log.info("更新订单状态：id={}, userId={}, oldStatus={}, newStatus={}", id, userId, oldStatus, status);

        // 查询订单明细
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, id);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);

        // 如果订单状态从待支付(0)变为已支付(1)，更新座位状态为已售出(2)
        if (oldStatus == 0 && status == 1) {
            for (OrderItem item : orderItems) {
                Seat seat = seatMapper.selectById(item.getSeatId());
                if (seat != null && seat.getStatus() == 1) {
                    seat.setStatus(2); // 已售出
                    seat.setLockedUserId(null);
                    seat.setLockTime(null);
                    seatMapper.updateById(seat);
                    log.info("座位已售出：seatId={}, seatNumber={}", seat.getId(), seat.getSeatNumber());
                }
            }
            log.info("订单支付完成，已更新{}个座位状态为已售出", orderItems.size());
        }

        // 如果订单状态从待支付(0)变为已取消(2)，释放座位锁定
        if (oldStatus == 0 && status == 2) {
            for (OrderItem item : orderItems) {
                Seat seat = seatMapper.selectById(item.getSeatId());
                if (seat != null && seat.getStatus() == 1) {
                    seat.setStatus(0); // 释放为可选
                    seat.setLockedUserId(null);
                    seat.setLockTime(null);
                    seatMapper.updateById(seat);
                    log.info("订单取消，释放座位：seatId={}, seatNumber={}", seat.getId(), seat.getSeatNumber());
                }
            }
            log.info("订单已取消，已释放{}个座位", orderItems.size());
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean issueTicket(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("只有已支付的订单才能出票");
        }

        // 更新为已完成状态
        order.setStatus(3);
        int result = orderMapper.updateById(order);
        log.info("手动出票：id={}", id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRefund(Long orderId, boolean approve, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (approve) {
            // 同意退票，更新为已退款状态
            if (order.getStatus() != 1 && order.getStatus() != 3) {
                throw new BusinessException("当前订单状态不允许退票");
            }
            order.setStatus(5);
            log.info("同意退票：orderId={}, reason={}", orderId, reason);

            // 释放座位
            LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderItem::getOrderId, orderId);
            List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);

            for (OrderItem item : orderItems) {
                Seat seat = seatMapper.selectById(item.getSeatId());
                if (seat != null) {
                    seat.setStatus(0); // 释放为可选
                    seat.setLockedUserId(null);
                    seat.setLockTime(null);
                    seatMapper.updateById(seat);
                    log.info("退票释放座位：seatId={}, seatNumber={}", seat.getId(), seat.getSeatNumber());
                }
            }
            log.info("退票完成，已释放{}个座位", orderItems.size());
        } else {
            // 拒绝退票，恢复原状态
            if (order.getStatus() == 4) {
                order.setStatus(3);
                log.info("拒绝退票：orderId={}, reason={}", orderId, reason);
            }
        }

        int result = orderMapper.updateById(order);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createOrder(OrderCreateDTO dto, Long userId) {
        log.info("开始创建订单：userId={}, sessionId={}, seatCount={}",
                userId, dto.getSessionId(), dto.getSeats().size());

        // 1. 验证场次是否存在
        ShowSession session = sessionMapper.selectById(dto.getSessionId());
        if (session == null) {
            throw new BusinessException("场次不存在");
        }

        // 2. 查询座位信息并验证
        BigDecimal totalPrice = BigDecimal.ZERO;
        Map<Long, Seat> seatMap = new HashMap<>();

        for (OrderCreateDTO.SeatDTO seatDto : dto.getSeats()) {
            // 根据sessionId、rowNum、colNum查询座位
            LambdaQueryWrapper<Seat> seatWrapper = new LambdaQueryWrapper<>();
            seatWrapper.eq(Seat::getSessionId, dto.getSessionId())
                    .eq(Seat::getRowNum, seatDto.getRowNum())
                    .eq(Seat::getColNum, seatDto.getColNum());

            Seat seat = seatMapper.selectOne(seatWrapper);
            if (seat == null) {
                throw new BusinessException(
                        String.format("座位 %d排%d座 不存在", seatDto.getRowNum(), seatDto.getColNum()));
            }

            // 检查座位状态（0-可选，1-已锁定，2-已售出）
            // 简化逻辑：只允许选择可选座位，已锁定或已售出的都拒绝
            if (seat.getStatus() != 0) {
                String statusMsg = seat.getStatus() == 1 ? "已被锁定" : "已售出";
                throw new BusinessException(
                        String.format("座位 %d排%d座 %s，请重新选择", seatDto.getRowNum(), seatDto.getColNum(), statusMsg));
            }

            seatMap.put(seat.getId(), seat);
            totalPrice = totalPrice.add(seat.getPrice());
        }

        // 3. 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis();

        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSessionId(dto.getSessionId());
        order.setShowId(session.getShowId());
        order.setTheaterId(session.getTheaterId());
        order.setSeatCount(dto.getSeats().size());
        order.setTotalPrice(totalPrice);
        order.setStatus(0); // 待支付
        order.setExpireTime(LocalDateTime.now().plusMinutes(15)); // 15分钟过期

        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("订单创建失败");
        }

        log.info("订单创建成功：orderId={}, orderNo={}, totalPrice={}",
                order.getId(), orderNo, totalPrice);

        // 5. 创建订单明细并锁定座位
        for (Map.Entry<Long, Seat> entry : seatMap.entrySet()) {
            Seat seat = entry.getValue();

            // 创建订单明细
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setSeatId(seat.getId());
            orderItem.setSeatNumber(seat.getSeatNumber());
            orderItem.setPrice(seat.getPrice());
            orderItemMapper.insert(orderItem);

            // 锁定座位（状态改为1-已锁定）
            seat.setStatus(1);
            seat.setLockedUserId(userId);
            seat.setLockTime(LocalDateTime.now());
            seatMapper.updateById(seat);
            log.info("订单创建并锁定座位：seatId={}, seatNumber={}", seat.getId(), seat.getSeatNumber());
        }

        log.info("订单明细创建完成，座位已锁定：orderId={}, seatCount={}",
                order.getId(), seatMap.size());

        // 6. 返回订单信息
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderId", order.getId());
        resultMap.put("orderNo", orderNo);
        resultMap.put("totalPrice", totalPrice);
        resultMap.put("seatCount", dto.getSeats().size());
        resultMap.put("expireTime", order.getExpireTime());

        return resultMap;
    }
}
