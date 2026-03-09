package com.music.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.music.modules.admin.dto.*;
import com.music.modules.user.mapper.UserMapper;
import com.music.modules.admin.service.StatisticsService;
import com.music.modules.order.entity.Order;
import com.music.modules.order.mapper.OrderMapper;
import com.music.modules.show.entity.Show;
import com.music.modules.show.mapper.ShowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ShowMapper showMapper;

    @Override
    public UserStatisticsDTO getUserStatistics() {
        UserStatisticsDTO dto = new UserStatisticsDTO();

        // 总用户数
        Long totalUsers = userMapper.selectCount(null);
        dto.setTotalUsers(totalUsers);

        // 活跃用户数（7天内有登录记录）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        // TODO: 需要根据实际登录日志表查询
        Long activeUsers = totalUsers; // 临时处理
        dto.setActiveUsers(activeUsers);

        // 今日新增用户数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<com.music.modules.user.entity.User> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(com.music.modules.user.entity.User::getCreateTime, todayStart);
        Long todayNewUsers = userMapper.selectCount(todayWrapper);
        dto.setTodayNewUsers(todayNewUsers);

        // 近7天新增用户趋势
        List<Map<String, Object>> newUserTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<com.music.modules.user.entity.User> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(com.music.modules.user.entity.User::getCreateTime, dayStart)
                    .lt(com.music.modules.user.entity.User::getCreateTime, dayEnd);
            Long count = userMapper.selectCount(wrapper);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            item.put("count", count);
            newUserTrend.add(item);
        }
        dto.setNewUserTrend(newUserTrend);

        // 用户角色分布
        Map<String, Long> roleDistribution = new HashMap<>();
        roleDistribution.put("管理员", 1L);
        roleDistribution.put("普通用户", totalUsers - 1);
        dto.setRoleDistribution(roleDistribution);

        return dto;
    }

    @Override
    public OrderStatisticsDTO getOrderStatistics() {
        OrderStatisticsDTO dto = new OrderStatisticsDTO();

        // 总订单数
        Long totalOrders = orderMapper.selectCount(null);
        dto.setTotalOrders(totalOrders);

        // 待支付订单数
        LambdaQueryWrapper<Order> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(Order::getStatus, 0);
        Long unpaidOrders = orderMapper.selectCount(unpaidWrapper);
        dto.setUnpaidOrders(unpaidOrders);

        // 已完成订单数
        LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Order::getStatus, 2);
        Long completedOrders = orderMapper.selectCount(completedWrapper);
        dto.setCompletedOrders(completedOrders);

        // 总销售额（已完成的订单）
        // TODO: 需要根据实际订单表查询
        Double totalSales = 0.0;
        dto.setTotalSales(totalSales);

        // 今日销售额
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Order::getStatus, 2)
                .ge(Order::getCreateTime, todayStart);
        // TODO: 需要根据实际订单表查询
        Double todaySales = 0.0;
        dto.setTodaySales(todaySales);

        // 近7天销售额趋势
        List<Map<String, Object>> salesTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            // TODO: 需要根据实际订单表查询
            Double sales = 0.0;

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            item.put("sales", sales);
            salesTrend.add(item);
        }
        dto.setSalesTrend(salesTrend);

        // 订单状态分布
        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("待支付", unpaidOrders);
        statusDistribution.put("已支付", 0L);
        statusDistribution.put("已完成", completedOrders);
        statusDistribution.put("已取消", 0L);
        statusDistribution.put("已退款", 0L);
        dto.setStatusDistribution(statusDistribution);

        return dto;
    }

    @Override
    public ShowStatisticsDTO getShowStatistics() {
        ShowStatisticsDTO dto = new ShowStatisticsDTO();

        // 总剧目数
        Long totalShows = showMapper.selectCount(null);
        dto.setTotalShows(totalShows);

        // 在售剧目数（正在热映）
        LambdaQueryWrapper<Show> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Show::getStatus, 1); // 1-正在热映
        Long activeShows = showMapper.selectCount(activeWrapper);
        dto.setActiveShows(activeShows);

        // 今日上映剧目数（使用状态统计）
        LambdaQueryWrapper<Show> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Show::getStatus, 1); // 正在热映的剧目
        Long todayShows = showMapper.selectCount(todayWrapper);
        dto.setTodayShows(todayShows);

        // 热门剧目排行（按订单量）
        // TODO: 需要根据订单表统计
        List<Map<String, Object>> hotShows = new ArrayList<>();
        dto.setHotShows(hotShows);

        // 剧目分类统计
        // TODO: 需要根据实际剧目分类字段统计
        Map<String, Long> categoryStatistics = new HashMap<>();
        categoryStatistics.put("音乐剧", totalShows);
        dto.setCategoryStatistics(categoryStatistics);

        return dto;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 用户统计
        UserStatisticsDTO userStatistics = getUserStatistics();
        data.put("userStatistics", userStatistics);

        // 订单统计
        OrderStatisticsDTO orderStatistics = getOrderStatistics();
        data.put("orderStatistics", orderStatistics);

        // 剧目统计
        ShowStatisticsDTO showStatistics = getShowStatistics();
        data.put("showStatistics", showStatistics);

        // 系统信息
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("currentTime", LocalDateTime.now());
        systemInfo.put("serverTime", System.currentTimeMillis());
        data.put("systemInfo", systemInfo);

        return data;
    }

    @Override
    public Map<String, Object> getUserTrend(Integer days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<com.music.modules.user.entity.User> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(com.music.modules.user.entity.User::getCreateTime, dayStart)
                    .lt(com.music.modules.user.entity.User::getCreateTime, dayEnd);
            Long count = userMapper.selectCount(wrapper);

            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            counts.add(count);
        }

        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }

    @Override
    public Map<String, Object> getOrderTrend(Integer days) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Order::getCreateTime, dayStart)
                    .lt(Order::getCreateTime, dayEnd);
            Long count = orderMapper.selectCount(wrapper);

            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            counts.add(count);
        }

        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }

    @Override
    public Map<String, Object> getHotShows(Integer top) {
        Map<String, Object> result = new HashMap<>();

        // 获取所有剧目
        LambdaQueryWrapper<Show> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Show::getStatus, 1); // 正在热映
        wrapper.orderByDesc(Show::getRating);
        wrapper.last("LIMIT " + top);
        List<Show> shows = showMapper.selectList(wrapper);

        List<Map<String, Object>> hotShows = shows.stream().map(show -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", show.getId());
            item.put("name", show.getName());
            item.put("poster", show.getPoster());
            item.put("rating", show.getRating());
            item.put("type", show.getType());
            // TODO: 添加实际订单量统计
            item.put("orderCount", 0);
            return item;
        }).collect(Collectors.toList());

        result.put("hotShows", hotShows);
        return result;
    }

    @Override
    public Map<String, Object> getSalesStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 今日销售额
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Order::getStatus, 2) // 已完成
                .ge(Order::getCreateTime, todayStart);
        // TODO: 需要根据实际订单金额字段统计
        Double todaySales = 0.0;

        // 本月销售额
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LambdaQueryWrapper<Order> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(Order::getStatus, 2)
                .ge(Order::getCreateTime, monthStart);
        // TODO: 需要根据实际订单金额字段统计
        Double monthSales = 0.0;

        // 总销售额
        LambdaQueryWrapper<Order> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Order::getStatus, 2);
        // TODO: 需要根据实际订单金额字段统计
        Double totalSales = 0.0;

        result.put("todaySales", todaySales);
        result.put("monthSales", monthSales);
        result.put("totalSales", totalSales);

        // 近7天销售额趋势
        List<String> dates = new ArrayList<>();
        List<Double> sales = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            // TODO: 需要根据实际订单金额字段统计
            Double daySales = 0.0;

            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            sales.add(daySales);
        }

        result.put("dates", dates);
        result.put("sales", sales);

        return result;
    }
}
