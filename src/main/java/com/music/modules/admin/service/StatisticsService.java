package com.music.modules.admin.service;

import com.music.modules.admin.dto.OrderStatisticsDTO;
import com.music.modules.admin.dto.ShowStatisticsDTO;
import com.music.modules.admin.dto.UserStatisticsDTO;

/**
 * 数据统计服务接口
 *
 * @author 黄晓倩
 */
public interface StatisticsService {

    /**
     * 获取用户统计数据
     *
     * @return 用户统计数据
     */
    UserStatisticsDTO getUserStatistics();

    /**
     * 获取订单统计数据
     *
     * @return 订单统计数据
     */
    OrderStatisticsDTO getOrderStatistics();

    /**
     * 获取剧目统计数据
     *
     * @return 剧目统计数据
     */
    ShowStatisticsDTO getShowStatistics();

    /**
     * 获取仪表盘数据（汇总）
     *
     * @return 仪表盘数据
     */
    java.util.Map<String, Object> getDashboardData();

    /**
     * 获取用户注册趋势
     *
     * @param days 天数
     * @return 用户注册趋势数据
     */
    java.util.Map<String, Object> getUserTrend(Integer days);

    /**
     * 获取订单趋势
     *
     * @param days 天数
     * @return 订单趋势数据
     */
    java.util.Map<String, Object> getOrderTrend(Integer days);

    /**
     * 获取热门剧目排行
     *
     * @param top 返回数量
     * @return 热门剧目数据
     */
    java.util.Map<String, Object> getHotShows(Integer top);

    /**
     * 获取销售额统计
     *
     * @return 销售额统计数据
     */
    java.util.Map<String, Object> getSalesStatistics();
}
