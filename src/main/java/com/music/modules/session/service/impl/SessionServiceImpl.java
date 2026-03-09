package com.music.modules.session.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.session.entity.ShowSession;
import com.music.modules.session.mapper.SessionMapper;
import com.music.modules.session.service.SessionService;
import com.music.modules.show.entity.Show;
import com.music.modules.show.mapper.ShowMapper;
import com.music.modules.theater.entity.Theater;
import com.music.modules.theater.mapper.TheaterMapper;
import com.music.modules.seat.entity.Seat;
import com.music.modules.seat.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 场次服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl extends ServiceImpl<SessionMapper, ShowSession> implements SessionService {

    private final SessionMapper sessionMapper;
    private final ShowMapper showMapper;
    private final TheaterMapper theaterMapper;
    private final SeatMapper seatMapper;

    @Override
    public IPage<ShowSession> getSessionList(Page<ShowSession> page, Long showId, Long theaterId, Integer status) {
        LambdaQueryWrapper<ShowSession> wrapper = new LambdaQueryWrapper<>();

        if (showId != null) {
            wrapper.eq(ShowSession::getShowId, showId);
        }
        if (theaterId != null) {
            wrapper.eq(ShowSession::getTheaterId, theaterId);
        }
        if (status != null) {
            wrapper.eq(ShowSession::getStatus, status);
        }

        wrapper.orderByAsc(ShowSession::getShowTime);

        IPage<ShowSession> result = sessionMapper.selectPage(page, wrapper);

        // 填充剧目名称和剧院名称
        List<ShowSession> records = result.getRecords();
        if (!records.isEmpty()) {
            // 获取所有剧目ID和剧院ID
            List<Long> showIds = records.stream()
                    .map(ShowSession::getShowId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            List<Long> theaterIds = records.stream()
                    .map(ShowSession::getTheaterId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());

            // 查询剧目信息
            Map<Long, Show> showMap = java.util.Collections.emptyMap();
            if (!showIds.isEmpty()) {
                try {
                    List<Show> shows = showMapper.selectBatchIds(showIds);
                    showMap = shows.stream().collect(Collectors.toMap(Show::getId, s -> s));
                } catch (Exception e) {
                    log.warn("查询剧目信息失败：{}", e.getMessage());
                }
            }

            // 查询剧院信息
            Map<Long, Theater> theaterMap = java.util.Collections.emptyMap();
            if (!theaterIds.isEmpty()) {
                try {
                    List<Theater> theaters = theaterMapper.selectBatchIds(theaterIds);
                    theaterMap = theaters.stream().collect(Collectors.toMap(Theater::getId, t -> t));
                } catch (Exception e) {
                    log.warn("查询剧院信息失败：{}", e.getMessage());
                }
            }

            // 填充名称和价格
            Map<Long, Show> finalShowMap = showMap;
            Map<Long, Theater> finalTheaterMap = theaterMap;
            records.forEach(session -> {
                Show show = finalShowMap.get(session.getShowId());
                session.setShowTitle(show != null ? show.getName() : "未知剧目");

                // 如果场次没有设置价格，从剧目中获取
                if (show != null) {
                    log.info("场次{}继承剧目{}价格 - 场次原价格: vip={}, normal={}, student={}, discount={}",
                        session.getId(), show.getId(), session.getVipPrice(), session.getNormalPrice(),
                        session.getStudentPrice(), session.getDiscountPrice());
                    log.info("剧目{}价格 - vip={}, normal={}, student={}, discount={}",
                        show.getId(), show.getVipPrice(), show.getNormalPrice(),
                        show.getStudentPrice(), show.getDiscountPrice());

                    if (session.getVipPrice() == null) {
                        session.setVipPrice(show.getVipPrice());
                    }
                    if (session.getNormalPrice() == null) {
                        session.setNormalPrice(show.getNormalPrice());
                    }
                    if (session.getStudentPrice() == null) {
                        session.setStudentPrice(show.getStudentPrice());
                    }
                    if (session.getDiscountPrice() == null) {
                        session.setDiscountPrice(show.getDiscountPrice());
                    }

                    log.info("场次{}继承后价格 - vip={}, normal={}, student={}, discount={}",
                        session.getId(), session.getVipPrice(), session.getNormalPrice(),
                        session.getStudentPrice(), session.getDiscountPrice());
                } else {
                    log.warn("场次{}对应的剧目{}未找到", session.getId(), session.getShowId());
                }

                Theater theater = finalTheaterMap.get(session.getTheaterId());
                session.setTheaterName(theater != null ? theater.getName() : "未知剧院");
            });
        }

        return result;
    }

    @Override
    public ShowSession getSessionDetail(Long id) {
        ShowSession session = sessionMapper.selectById(id);
        if (session == null) {
            throw new BusinessException("场次不存在");
        }
        return session;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSession(ShowSession session) {
        // 初始化已售座位数为0
        session.setSoldSeats(0);
        if (session.getStatus() == null) {
            session.setStatus(0);
        }
        int result = sessionMapper.insert(session);
        log.info("创建场次：showId={}, showTime={}", session.getShowId(), session.getShowTime());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSession(Long id, ShowSession session) {
        ShowSession existSession = sessionMapper.selectById(id);
        if (existSession == null) {
            throw new BusinessException("场次不存在");
        }

        // 不允许修改已售出座位的场次
        if (existSession.getSoldSeats() > 0) {
            throw new BusinessException("该场次已有售出座位，不允许修改");
        }

        session.setId(id);
        int result = sessionMapper.updateById(session);
        log.info("更新场次：id={}", id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSession(Long id, String reason) {
        ShowSession session = sessionMapper.selectById(id);
        if (session == null) {
            throw new BusinessException("场次不存在");
        }

        // 已取消或已结束的场次不能取消
        if (session.getStatus() == 2 || session.getStatus() == 3) {
            throw new BusinessException("该场次不能取消");
        }

        // 更新状态为已取消
        session.setStatus(3);
        session.setCancelReason(reason);
        int result = sessionMapper.updateById(session);
        log.info("取消场次：id={}, reason={}", id, reason);
        return result > 0;
    }

    @Override
    public Map<String, Object> getSessionSeatStatus(Long sessionId) {
        ShowSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("场次不存在");
        }

        // 查询该场次的所有座位
        LambdaQueryWrapper<Seat> seatWrapper = new LambdaQueryWrapper<>();
        seatWrapper.eq(Seat::getSessionId, sessionId);
        seatWrapper.orderByAsc(Seat::getRowNum, Seat::getColNum);
        List<Seat> seats = seatMapper.selectList(seatWrapper);

        // 计算座位统计
        int totalSeats = seats.size();
        int soldSeats = (int) seats.stream().filter(s -> s.getStatus() == 2).count();
        int lockedSeats = (int) seats.stream().filter(s -> s.getStatus() == 1).count();
        int availableSeats = (int) seats.stream().filter(s -> s.getStatus() == 0).count();

        Map<String, Object> result = new HashMap<>();
        result.put("seats", seats);
        result.put("totalSeats", totalSeats);
        result.put("soldSeats", soldSeats);
        result.put("availableSeats", availableSeats);
        result.put("lockedSeats", lockedSeats);

        return result;
    }
}
