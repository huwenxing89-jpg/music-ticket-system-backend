package com.music.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.music.modules.order.mapper.OrderMapper;
import com.music.modules.order.entity.Order;
import com.music.modules.order.entity.OrderItem;
import com.music.modules.order.mapper.OrderItemMapper;
import com.music.modules.seat.entity.Seat;
import com.music.modules.seat.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务
 *
 * @author 黄晓倩
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SeatMapper seatMapper;

    /**
     * 每5分钟取消超时未支付订单
     * Cron表达式: 秒 分 时 日 月 周
     * 0 star/5 * * * * 表示每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional(rollbackFor = Exception.class)
    public void cancelExpiredOrders() {
        log.info("开始执行取消超时订单任务，时间：{}", LocalDateTime.now());

        try {
            // 查询15分钟前未支付的订单
            LocalDateTime timeout = LocalDateTime.now().minusMinutes(15);
            List<Order> expiredOrders = orderMapper.selectExpiredOrders(timeout);

            if (expiredOrders.isEmpty()) {
                log.info("没有需要取消的超时订单");
                return;
            }

            // 批量取消订单并释放座位
            for (Order order : expiredOrders) {
                // 更新订单状态为已取消(2)
                order.setStatus(2);
                order.setUpdateTime(LocalDateTime.now());
                orderMapper.updateById(order);

                // 释放座位锁定
                LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
                itemWrapper.eq(OrderItem::getOrderId, order.getId());
                List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

                for (OrderItem item : orderItems) {
                    Seat seat = seatMapper.selectById(item.getSeatId());
                    if (seat != null && seat.getStatus() == 1) {
                        seat.setStatus(0); // 释放为可选
                        seat.setLockedUserId(null);
                        seat.setLockTime(null);
                        seatMapper.updateById(seat);
                        log.info("超时取消释放座位：seatId={}, seatNumber={}", seat.getId(), seat.getSeatNumber());
                    }
                }

                log.info("订单{}已超时取消，已释放{}个座位", order.getOrderNo(), orderItems.size());
            }

            log.info("取消超时订单任务完成，共取消{}个订单", expiredOrders.size());

        } catch (Exception e) {
            log.error("取消超时订单任务执行失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 每日凌晨1点统计数据
     * 0 0 1 * * * 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void dailyStatistics() {
        log.info("开始执行每日统计任务，时间：{}", LocalDateTime.now());

        try {
            // TODO: 实现统计逻辑
            // 1. 统计新增用户数
            // 2. 统计订单量
            // 3. 统计销售额
            // 4. 统计热门剧目
            // 5. 保存统计数据

            log.info("每日统计任务完成");

        } catch (Exception e) {
            log.error("每日统计任务执行失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 每周日凌晨2点清理过期数据
     * 0 0 2 * * 1 表示每周日凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * 1")
    public void cleanExpiredData() {
        log.info("开始执行清理过期数据任务，时间：{}", LocalDateTime.now());

        try {
            // TODO: 实现清理逻辑
            // 1. 清理30天前的操作日志
            // 2. 清理90天前的登录日志
            // 3. 清理已删除的评论
            // 4. 清理过期的验证码

            log.info("清理过期数据任务完成");

        } catch (Exception e) {
            log.error("清理过期数据任务执行失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 每小时刷新热门剧目缓存
     * 0 0 * * * * 表示每小时执行
     */
    @Scheduled(cron = "0 0 * * * *")
    public void refreshHotShowsCache() {
        log.info("开始执行刷新热门剧目缓存任务，时间：{}", LocalDateTime.now());

        try {
            // TODO: 实现缓存刷新逻辑
            // 1. 查询热门剧目
            // 2. 更新Redis缓存

            log.info("刷新热门剧目缓存任务完成");

        } catch (Exception e) {
            log.error("刷新热门剧目缓存任务执行失败：{}", e.getMessage(), e);
        }
    }
}
