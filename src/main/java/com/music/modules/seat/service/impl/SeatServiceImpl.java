package com.music.modules.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.seat.entity.Seat;
import com.music.modules.seat.mapper.SeatMapper;
import com.music.modules.seat.service.SeatService;
import com.music.modules.session.entity.ShowSession;
import com.music.modules.session.mapper.SessionMapper;
import com.music.modules.show.entity.Show;
import com.music.modules.show.mapper.ShowMapper;
import com.music.modules.theater.entity.Theater;
import com.music.modules.theater.mapper.TheaterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 座位服务实现类
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    private final SessionMapper sessionMapper;
    private final TheaterMapper theaterMapper;
    private final ShowMapper showMapper;

    @Override
    public Map<String, Object> getSessionSeatStatus(Long sessionId) {
        // 查询场次信息
        ShowSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException("场次不存在");
        }

        // 查询剧院信息
        Theater theater = null;
        if (session.getTheaterId() != null) {
            try {
                theater = theaterMapper.selectById(session.getTheaterId());
            } catch (Exception e) {
                log.warn("查询剧院信息失败：{}", e.getMessage());
            }
            if (theater != null) {
                session.setTheaterName(theater.getName());
            } else {
                session.setTheaterName("未知剧院");
            }
        } else {
            session.setTheaterName("未知剧院");
        }

        // 查询剧目信息
        if (session.getShowId() != null) {
            try {
                Show show = showMapper.selectById(session.getShowId());
                if (show != null) {
                    session.setShowTitle(show.getName());
                } else {
                    session.setShowTitle("未知剧目");
                }
            } catch (Exception e) {
                log.warn("查询剧目信息失败：{}", e.getMessage());
                session.setShowTitle("未知剧目");
            }
        } else {
            session.setShowTitle("未知剧目");
        }

        // 查询座位列表
        List<Seat> seats = new ArrayList<>();
        try {
            LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Seat::getSessionId, sessionId);
            seats = baseMapper.selectList(wrapper);

            // 检查座位数量是否匹配场次配置
            int expectedSeats = session.getTotalSeats() != null && session.getTotalSeats() > 0
                ? session.getTotalSeats() : 100;

            boolean needRegenerate = false;

            if (seats.isEmpty()) {
                // 首次生成座位
                log.info("场次{}暂无座位数据，自动生成座位图", sessionId);
                needRegenerate = true;
            } else if (seats.size() != expectedSeats) {
                // 座位数量不匹配，检查是否有已售出座位
                long soldCount = seats.stream().filter(s -> s.getStatus() == 2).count();

                if (soldCount > 0) {
                    log.warn("场次{}座位配置不匹配：当前{}个座位，配置{}个座位，已有{}个已售出，无法重新生成",
                        sessionId, seats.size(), expectedSeats, soldCount);
                    // 不重新生成，使用现有座位数据
                } else {
                    log.info("场次{}座位配置已改变：当前{}个座位，新配置{}个座位，重新生成座位图",
                        sessionId, seats.size(), expectedSeats);

                    // 删除旧座位数据
                    LambdaQueryWrapper<Seat> deleteWrapper = new LambdaQueryWrapper<>();
                    deleteWrapper.eq(Seat::getSessionId, sessionId);
                    baseMapper.delete(deleteWrapper);
                    seats.clear();
                    needRegenerate = true;
                }
            }

            if (needRegenerate) {
                seats = generateSeats(session);
            }
        } catch (Exception e) {
            log.warn("查询座位数据失败：sessionId={}, error={}", sessionId, e.getMessage());
            // 自动生成座位图作为回退
            seats = generateSeats(session);
        }

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("session", session);
        result.put("seats", seats);

        return result;
    }

    /**
     * 根据场次信息自动生成座位图
     * 票价优先级：场次票价 > 剧目票价 > 系统默认
     * 座位布局：剧院配置 > 智能计算
     */
    private List<Seat> generateSeats(ShowSession session) {
        List<Seat> generatedSeats = new ArrayList<>();

        if (session.getTotalSeats() == null || session.getTotalSeats() <= 0) {
            session.setTotalSeats(100);
        }

        // ============== 第一步：获取票价配置 ==============
        // 优先级：场次覆盖 > 剧目票价 > 系统默认
        Map<Integer, BigDecimal> priceMap = getPricingConfiguration(session);

        // ============== 第二步：获取座位布局配置 ==============
        TheaterLayoutConfig layoutConfig = getTheaterLayoutConfig(session);

        // ============== 第三步：生成座位 ==============
        generatedSeats = generateSeatsByLayout(session, priceMap, layoutConfig);

        // ============== 第四步：批量保存到数据库 ==============
        saveGeneratedSeats(session.getId(), generatedSeats);

        return generatedSeats;
    }

    /**
     * 获取票价配置
     * 优先级：场次票价覆盖 > 剧目票价 > 系统默认
     */
    private Map<Integer, BigDecimal> getPricingConfiguration(ShowSession session) {
        Map<Integer, BigDecimal> priceMap = new HashMap<>();

        // 系统默认票价
        BigDecimal defaultVipPrice = new BigDecimal("680");
        BigDecimal defaultNormalPrice = new BigDecimal("380");
        BigDecimal defaultStudentPrice = new BigDecimal("180");
        BigDecimal defaultDiscountPrice = new BigDecimal("280");

        // 从剧目获取基础票价
        Show show = null;
        if (session.getShowId() != null) {
            try {
                show = showMapper.selectById(session.getShowId());
                if (show != null) {
                    defaultVipPrice = show.getVipPrice() != null ? show.getVipPrice() : defaultVipPrice;
                    defaultNormalPrice = show.getNormalPrice() != null ? show.getNormalPrice() : defaultNormalPrice;
                    defaultStudentPrice = show.getStudentPrice() != null ? show.getStudentPrice() : defaultStudentPrice;
                    defaultDiscountPrice = show.getDiscountPrice() != null ? show.getDiscountPrice() : defaultDiscountPrice;
                }
            } catch (Exception e) {
                log.warn("查询剧目票价失败：showId={}, error={}", session.getShowId(), e.getMessage());
            }
        }

        // 场次票价覆盖（优先级最高）
        BigDecimal vipPrice = session.getVipPrice() != null ? session.getVipPrice() : defaultVipPrice;
        BigDecimal normalPrice = session.getNormalPrice() != null ? session.getNormalPrice() : defaultNormalPrice;
        BigDecimal studentPrice = session.getStudentPrice() != null ? session.getStudentPrice() : defaultStudentPrice;
        BigDecimal discountPrice = session.getDiscountPrice() != null ? session.getDiscountPrice() : defaultDiscountPrice;

        // 解析场次price_types（兼容旧格式）
        if (session.getPriceTypes() != null && !session.getPriceTypes().isEmpty()) {
            Map<Integer, BigDecimal> parsedPrices = parsePriceTypes(session.getPriceTypes());
            if (!parsedPrices.isEmpty()) {
                vipPrice = parsedPrices.getOrDefault(1, vipPrice);
                normalPrice = parsedPrices.getOrDefault(0, normalPrice);
                studentPrice = parsedPrices.getOrDefault(2, studentPrice);
            }
        }

        // 构建票价映射：座位类型 -> 价格
        // 0-普通座，1-VIP座，2-学生票，3-优惠票
        priceMap.put(0, normalPrice);
        priceMap.put(1, vipPrice);
        priceMap.put(2, studentPrice);
        priceMap.put(3, discountPrice);

        log.info("票价配置获取成功：sessionId={}, vip={}, normal={}, student={}, discount={}",
                session.getId(), vipPrice, normalPrice, studentPrice, discountPrice);

        return priceMap;
    }

    /**
     * 解析票价类型字符串（兼容旧格式）
     * 支持格式：VIP:680,普通:380 或 vip,normal
     */
    private Map<Integer, BigDecimal> parsePriceTypes(String priceTypes) {
        Map<Integer, BigDecimal> priceMap = new HashMap<>();

        try {
            String[] types = priceTypes.split(",");
            for (String type : types) {
                type = type.trim();
                String[] parts = type.split(":");
                String typeName = parts[0].trim();
                BigDecimal price = null;

                if (parts.length == 2) {
                    price = new BigDecimal(parts[1].trim());
                }

                // 映射到座位类型
                if (typeName.contains("VIP") || typeName.contains("vip")) {
                    if (price != null) priceMap.put(1, price);
                } else if (typeName.contains("学生") || typeName.contains("优惠")) {
                    if (price != null) priceMap.put(2, price);
                } else {
                    if (price != null) priceMap.put(0, price);
                }
            }
        } catch (Exception e) {
            log.warn("解析票价类型失败：{}", priceTypes);
        }

        return priceMap;
    }

    /**
     * 座位布局配置内部类
     */
    private static class TheaterLayoutConfig {
        int totalRows;
        int seatsPerRow;
        int[] vipRows;
        int[] studentRows;
        int[] discountRows;

        TheaterLayoutConfig() {
            this.vipRows = new int[0];
            this.studentRows = new int[0];
            this.discountRows = new int[0];
        }
    }

    /**
     * 获取剧院座位布局配置
     */
    private TheaterLayoutConfig getTheaterLayoutConfig(ShowSession session) {
        TheaterLayoutConfig config = new TheaterLayoutConfig();

        // 从剧院获取布局配置
        if (session.getTheaterId() != null) {
            try {
                Theater theater = theaterMapper.selectById(session.getTheaterId());
                if (theater != null) {
                    // 解析VIP排数配置
                    if (theater.getVipRows() != null && !theater.getVipRows().isEmpty()) {
                        config.vipRows = parseRowsConfig(theater.getVipRows(), config.totalRows);
                    }
                    // 解析学生排数配置
                    if (theater.getStudentRows() != null && !theater.getStudentRows().isEmpty()) {
                        config.studentRows = parseRowsConfig(theater.getStudentRows(), config.totalRows);
                    }

                    log.info("从剧院获取布局配置：theaterId={}, vipRows={}, studentRows={}",
                            theater.getId(), theater.getVipRows(), theater.getStudentRows());
                }
            } catch (Exception e) {
                log.warn("查询剧院布局配置失败：theaterId={}, error={}", session.getTheaterId(), e.getMessage());
            }
        }

        // 智能计算座位布局（如果没有配置或配置为空）
        int totalSeats = session.getTotalSeats();
        config.seatsPerRow = (int) Math.ceil(Math.sqrt(totalSeats));
        config.totalRows = (int) Math.ceil((double) totalSeats / config.seatsPerRow);

        // 如果没有配置VIP排数，使用默认：前3排
        if (config.vipRows.length == 0) {
            int vipRowCount = Math.min(3, config.totalRows);
            config.vipRows = new int[vipRowCount];
            for (int i = 0; i < vipRowCount; i++) {
                config.vipRows[i] = i + 1;
            }
        }

        // 如果没有配置学生排数，使用默认：最后2排
        if (config.studentRows.length == 0) {
            int studentRowCount = Math.min(2, config.totalRows);
            config.studentRows = new int[studentRowCount];
            for (int i = 0; i < studentRowCount; i++) {
                config.studentRows[i] = config.totalRows - studentRowCount + i + 1;
            }
        }

        log.info("座位布局配置：totalSeats={}, totalRows={}, seatsPerRow={}, vipRows={}, studentRows={}",
                totalSeats, config.totalRows, config.seatsPerRow,
                java.util.Arrays.toString(config.vipRows),
                java.util.Arrays.toString(config.studentRows));

        return config;
    }

    /**
     * 解析排数配置字符串
     * 支持格式："1-3", "1-3,8-10", "倒数2排"
     */
    private int[] parseRowsConfig(String rowsConfig, int totalRows) {
        java.util.List<Integer> rows = new java.util.ArrayList<>();

        if (rowsConfig.contains("倒数")) {
            // 处理"倒数2排"格式
            String numStr = rowsConfig.replaceAll("[^0-9]", "");
            int count = Integer.parseInt(numStr);
            for (int i = 0; i < count; i++) {
                rows.add(totalRows - count + i + 1);
            }
        } else {
            // 处理"1-3"或"1-3,8-10"格式
            String[] parts = rowsConfig.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.contains("-")) {
                    String[] range = part.split("-");
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int i = start; i <= end; i++) {
                        rows.add(i);
                    }
                } else {
                    rows.add(Integer.parseInt(part));
                }
            }
        }

        return rows.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 根据布局配置生成座位
     */
    private List<Seat> generateSeatsByLayout(ShowSession session,
                                             Map<Integer, BigDecimal> priceMap,
                                             TheaterLayoutConfig layoutConfig) {
        List<Seat> generatedSeats = new ArrayList<>();

        for (int row = 1; row <= layoutConfig.totalRows; row++) {
            // 计算当前行的座位数
            int seatsInThisRow = layoutConfig.seatsPerRow;
            if (row == layoutConfig.totalRows) {
                // 最后一行可能不满
                seatsInThisRow = session.getTotalSeats() - (layoutConfig.totalRows - 1) * layoutConfig.seatsPerRow;
            }

            for (int col = 1; col <= seatsInThisRow; col++) {
                Seat seat = new Seat();
                seat.setSessionId(session.getId());
                seat.setRowNum(row);
                seat.setColNum(col);
                seat.setSeatNumber(row + "排" + col + "座");

                // 确定座位类型和价格
                Integer seatType = determineSeatType(row, layoutConfig);
                BigDecimal price = priceMap.getOrDefault(seatType, priceMap.get(0));

                seat.setSeatType(seatType);
                seat.setPrice(price);
                seat.setStatus(0); // 0-可选

                generatedSeats.add(seat);
            }

            if (generatedSeats.size() >= session.getTotalSeats()) {
                break;
            }
        }

        log.info("座位生成完成：sessionId={}, 总座位数={}, VIP={}, 普通={}, 学生={}",
                session.getId(), generatedSeats.size(),
                generatedSeats.stream().filter(s -> s.getSeatType() == 1).count(),
                generatedSeats.stream().filter(s -> s.getSeatType() == 0).count(),
                generatedSeats.stream().filter(s -> s.getSeatType() == 2).count());

        return generatedSeats;
    }

    /**
     * 根据行号确定座位类型
     */
    private Integer determineSeatType(int row, TheaterLayoutConfig layoutConfig) {
        // 检查是否是VIP排
        for (int vipRow : layoutConfig.vipRows) {
            if (row == vipRow) {
                return 1; // VIP座
            }
        }

        // 检查是否是学生排
        for (int studentRow : layoutConfig.studentRows) {
            if (row == studentRow) {
                return 2; // 学生票
            }
        }

        // 检查是否是优惠排
        for (int discountRow : layoutConfig.discountRows) {
            if (row == discountRow) {
                return 3; // 优惠票
            }
        }

        return 0; // 普通座
    }

    /**
     * 批量保存生成的座位到数据库
     */
    private void saveGeneratedSeats(Long sessionId, List<Seat> generatedSeats) {
        try {
            for (Seat seat : generatedSeats) {
                seat.setId(null); // 清除ID，让数据库自动生成
                baseMapper.insert(seat);
            }
            log.info("座位数据已保存到数据库：sessionId={}, 座位数={}", sessionId, generatedSeats.size());
        } catch (Exception e) {
            log.error("保存座位数据到数据库失败：sessionId={}, error={}", sessionId, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockSeats(Long sessionId, List<Long> seatIds, Long userId) {
        if (seatIds == null || seatIds.isEmpty()) {
            return true;
        }

        // 检查座位是否存在且可选
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getSessionId, sessionId)
                .in(Seat::getId, seatIds)
                .eq(Seat::getStatus, 0); // 只能锁定可选座位

        List<Seat> seats = baseMapper.selectList(wrapper);
        if (seats.size() != seatIds.size()) {
            throw new BusinessException("部分座位已被售出或锁定");
        }

        // 锁定座位
        LocalDateTime now = LocalDateTime.now();
        for (Seat seat : seats) {
            seat.setStatus(1); // 已锁定
            seat.setLockedUserId(userId);
            seat.setLockTime(now);
            baseMapper.updateById(seat);
        }

        log.info("用户{}锁定场次{}的{}个座位", userId, sessionId, seatIds.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockSeats(List<Long> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            return true;
        }

        // 释放座位
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Seat::getId, seatIds);
        List<Seat> seats = baseMapper.selectList(wrapper);

        for (Seat seat : seats) {
            if (seat.getStatus() == 1) { // 只释放已锁定的座位
                seat.setStatus(0); // 设为可选
                seat.setLockedUserId(null);
                seat.setLockTime(null);
                baseMapper.updateById(seat);
            }
        }

        log.info("释放{}个座位", seatIds.size());
        return true;
    }
}
