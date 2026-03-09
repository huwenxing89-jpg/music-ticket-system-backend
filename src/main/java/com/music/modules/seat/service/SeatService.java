package com.music.modules.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.seat.entity.Seat;

import java.util.List;
import java.util.Map;

/**
 * 座位服务接口
 *
 * @author 黄晓倩
 */
public interface SeatService extends IService<Seat> {

    /**
     * 获取场次座位状态
     *
     * @param sessionId 场次ID
     * @return 包含场次信息和座位列表的数据
     */
    Map<String, Object> getSessionSeatStatus(Long sessionId);

    /**
     * 锁定座位
     *
     * @param sessionId 场次ID
     * @param seatIds   座位ID列表
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean lockSeats(Long sessionId, List<Long> seatIds, Long userId);

    /**
     * 释放座位
     *
     * @param seatIds 座位ID列表
     * @return 是否成功
     */
    boolean unlockSeats(List<Long> seatIds);
}
