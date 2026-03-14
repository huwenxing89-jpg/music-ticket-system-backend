package com.music.modules.show.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.show.dto.ActorVO;
import com.music.modules.show.dto.SessionVO;
import com.music.modules.show.entity.Actor;
import com.music.modules.show.entity.Show;
import com.music.modules.show.entity.ShowActor;
import com.music.modules.show.mapper.ActorMapper;
import com.music.modules.show.mapper.ShowActorMapper;
import com.music.modules.show.mapper.ShowMapper;
import com.music.modules.show.service.ShowService;
import com.music.modules.session.entity.ShowSession;
import com.music.modules.session.mapper.SessionMapper;
import com.music.modules.theater.entity.Theater;
import com.music.modules.theater.mapper.TheaterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 剧目服务实现类
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShowServiceImpl extends ServiceImpl<ShowMapper, Show> implements ShowService {

    private final ShowActorMapper showActorMapper;
    private final ActorMapper actorMapper;
    private final SessionMapper sessionMapper;
    private final TheaterMapper theaterMapper;

    @Override
    public Page<Show> getShowList(Page<Show> page, String keyword, Integer type, Integer status,
                                   String category, String sort, BigDecimal minPrice, BigDecimal maxPrice) {
        LambdaQueryWrapper<Show> wrapper = new LambdaQueryWrapper<>();

        // 搜索关键词（剧目名称）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Show::getName, keyword);
        }

        // 剧目类型筛选（整数类型）
        if (type != null) {
            wrapper.eq(Show::getType, type);
        }

        // 剧目状态筛选
        if (status != null) {
            wrapper.eq(Show::getStatus, status);
        }

        // 注意：category字段不在数据库中，无法进行SQL筛选
        // 如果需要category筛选，需要在内存中进行处理

        // 注意：价格筛选在内存中进行，因为minPrice/maxPrice是计算字段
        // 这里不进行SQL层面的价格筛选

        // 排序处理
        if (sort != null && !sort.trim().isEmpty()) {
            switch (sort) {
                case "price_asc":
                    // 按最低价格升序排序（使用normalPrice作为参考）
                    wrapper.orderByAsc(Show::getNormalPrice);
                    break;
                case "price_desc":
                    // 按最低价格降序排序（使用normalPrice作为参考）
                    wrapper.orderByDesc(Show::getNormalPrice);
                    break;
                case "rating_desc":
                    wrapper.orderByDesc(Show::getRating);
                    break;
                case "createTime_desc":
                    wrapper.orderByDesc(Show::getCreateTime);
                    break;
                default:
                    // 默认按创建时间降序
                    wrapper.orderByDesc(Show::getCreateTime);
                    break;
            }
        } else {
            // 默认按创建时间降序排序
            wrapper.orderByDesc(Show::getCreateTime);
        }

        // 价格范围筛选需要在内存中进行，因为minPrice/maxPrice是计算字段
        // 先查询所有符合其他条件的剧目
        boolean hasPriceFilter = (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) ||
                                 (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0);

        if (hasPriceFilter) {
            // 有价格筛选时，查询所有符合其他条件的剧目
            List<Show> list = list(wrapper);

            // 计算票价范围
            for (Show show : list) {
                calculatePriceRange(show);
            }

            // 进行价格筛选
            // 筛选逻辑：找到剧目价格范围与用户设置范围有交集的剧目
            // 即：剧目的最低价 <= 用户最高价 AND 剧目的最高价 >= 用户最低价
            List<Show> filteredRecords = list.stream()
                .filter(show -> {
                    BigDecimal showMinPrice = show.getMinPrice();
                    BigDecimal showMaxPrice = show.getMaxPrice();

                    // 价格范围是否有交集
                    boolean hasOverlap = true;

                    if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
                        // 用户设置了最低价，检查剧目最高价是否 >= 最低价
                        hasOverlap = hasOverlap && (showMaxPrice != null && showMaxPrice.compareTo(minPrice) >= 0);
                    }
                    if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
                        // 用户设置了最高价，检查剧目最低价是否 <= 最高价
                        hasOverlap = hasOverlap && (showMinPrice != null && showMinPrice.compareTo(maxPrice) <= 0);
                    }

                    return hasOverlap;
                })
                .collect(java.util.stream.Collectors.toList());

            // 进行排序（需要在筛选后重新排序）
            if (sort != null && !sort.trim().isEmpty()) {
                sortRecords(filteredRecords, sort);
            }

            // 手动分页
            long pageNumLong = page.getCurrent();
            long pageSizeLong = page.getSize();
            int pageNum = pageNumLong > 0 ? (int) pageNumLong : 1;
            int pageSize = pageSizeLong > 0 ? (int) pageSizeLong : 10;
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, filteredRecords.size());

            List<Show> pagedRecords = fromIndex < filteredRecords.size()
                ? filteredRecords.subList(fromIndex, toIndex)
                : new ArrayList<>();

            // 构建分页结果
            Page<Show> result = new Page<>(pageNum, pageSize, filteredRecords.size());
            result.setRecords(pagedRecords);
            return result;
        } else {
            // 没有价格筛选时，直接使用SQL分页
            Page<Show> result = page(page, wrapper);

            // 计算票价范围
            List<Show> records = result.getRecords();
            for (Show show : records) {
                calculatePriceRange(show);
            }

            return result;
        }
    }

    /**
     * 对剧目列表进行排序
     */
    private void sortRecords(List<Show> records, String sort) {
        switch (sort) {
            case "price_asc":
                records.sort((a, b) -> {
                    BigDecimal aPrice = a.getMinPrice() != null ? a.getMinPrice() : new BigDecimal("999999");
                    BigDecimal bPrice = b.getMinPrice() != null ? b.getMinPrice() : new BigDecimal("999999");
                    return aPrice.compareTo(bPrice);
                });
                break;
            case "price_desc":
                records.sort((a, b) -> {
                    BigDecimal aPrice = a.getMaxPrice() != null ? a.getMaxPrice() : BigDecimal.ZERO;
                    BigDecimal bPrice = b.getMaxPrice() != null ? b.getMaxPrice() : BigDecimal.ZERO;
                    return bPrice.compareTo(aPrice);
                });
                break;
            case "rating_desc":
                records.sort((a, b) -> {
                    BigDecimal aRating = a.getRating() != null ? a.getRating() : BigDecimal.ZERO;
                    BigDecimal bRating = b.getRating() != null ? b.getRating() : BigDecimal.ZERO;
                    return bRating.compareTo(aRating);
                });
                break;
            case "createTime_desc":
                records.sort((a, b) -> {
                    if (a.getCreateTime() == null) return 1;
                    if (b.getCreateTime() == null) return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                });
                break;
            default:
                // 默认按创建时间降序
                records.sort((a, b) -> {
                    if (a.getCreateTime() == null) return 1;
                    if (b.getCreateTime() == null) return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                });
                break;
        }
    }

    @Override
    public Show getShowDetail(Long id) {
        Show show = getById(id);
        if (show == null) {
            throw new BusinessException(404, "剧目不存在");
        }
        // 计算票价范围
        calculatePriceRange(show);

        try {
            // 查询场次信息
            LambdaQueryWrapper<ShowSession> sessionWrapper = new LambdaQueryWrapper<>();
            sessionWrapper.eq(ShowSession::getShowId, id)
                    .eq(ShowSession::getStatus, 0) // 0-未开始
                    .ge(ShowSession::getShowTime, java.time.LocalDateTime.now()) // 只查询未来的场次
                    .orderByAsc(ShowSession::getShowTime);
            List<ShowSession> sessionList = sessionMapper.selectList(sessionWrapper);

            if (!sessionList.isEmpty()) {
                // 获取剧院ID列表
                List<Long> theaterIds = sessionList.stream()
                        .map(ShowSession::getTheaterId)
                        .filter(theaterId -> theaterId != null)
                        .distinct()
                        .collect(Collectors.toList());

                // 查询剧院信息
                Map<Long, Theater> theaterMap = java.util.Collections.emptyMap();
                if (!theaterIds.isEmpty()) {
                    try {
                        LambdaQueryWrapper<Theater> theaterWrapper = new LambdaQueryWrapper<>();
                        theaterWrapper.in(Theater::getId, theaterIds);
                        List<Theater> theaters = theaterMapper.selectList(theaterWrapper);
                        theaterMap = theaters.stream().collect(Collectors.toMap(Theater::getId, t -> t));
                    } catch (Exception e) {
                        log.warn("查询剧院信息失败：{}", e.getMessage());
                    }
                }

                // 构建场次VO列表
                Map<Long, Theater> finalTheaterMap = theaterMap;
                List<SessionVO> sessionVOList = sessionList.stream().map(session -> {
                    SessionVO vo = new SessionVO();
                    vo.setId(session.getId());
                    vo.setShowTime(session.getShowTime());
                    vo.setDuration(session.getDuration());

                    // 设置详细价格：优先使用场次价格，为空时使用剧目价格
                    vo.setVipPrice(session.getVipPrice() != null ? session.getVipPrice() : show.getVipPrice());
                    vo.setNormalPrice(session.getNormalPrice() != null ? session.getNormalPrice() : show.getNormalPrice());
                    vo.setStudentPrice(session.getStudentPrice() != null ? session.getStudentPrice() : show.getStudentPrice());
                    vo.setDiscountPrice(session.getDiscountPrice() != null ? session.getDiscountPrice() : show.getDiscountPrice());

                    // 计算场次最低票价：优先使用场次价格，为空时使用剧目价格
                    Integer minPrice = calculateSessionMinPrice(session, show);
                    vo.setPrice(minPrice);

                    vo.setTheaterId(session.getTheaterId());
                    Theater theater = finalTheaterMap.get(session.getTheaterId());
                    vo.setTheaterName(theater != null ? theater.getName() : "未知剧院");
                    Integer totalSeats = session.getTotalSeats();
                    Integer soldSeats = session.getSoldSeats();
                    vo.setAvailableSeats((totalSeats != null ? totalSeats : 0) - (soldSeats != null ? soldSeats : 0));
                    return vo;
                }).collect(Collectors.toList());
                show.setSessions(sessionVOList);
            } else {
                show.setSessions(new ArrayList<>());
            }
        } catch (Exception e) {
            log.error("查询场次信息失败：showId={}, error={}", id, e.getMessage());
            show.setSessions(new ArrayList<>());
        }

        try {
            // 查询演员信息
            LambdaQueryWrapper<ShowActor> actorWrapper = new LambdaQueryWrapper<>();
            actorWrapper.eq(ShowActor::getShowId, id);
            List<ShowActor> showActorList = showActorMapper.selectList(actorWrapper);

            if (!showActorList.isEmpty()) {
                List<Long> actorIds = showActorList.stream()
                        .map(ShowActor::getActorId)
                        .filter(actorId -> actorId != null)
                        .distinct()
                        .collect(Collectors.toList());

                if (!actorIds.isEmpty()) {
                    try {
                        LambdaQueryWrapper<Actor> wrapper = new LambdaQueryWrapper<>();
                        wrapper.in(Actor::getId, actorIds);
                        List<Actor> actorList = actorMapper.selectList(wrapper);
                        Map<Long, Actor> actorMap = actorList.stream().collect(Collectors.toMap(Actor::getId, a -> a));

                        List<ActorVO> actorVOList = showActorList.stream().map(showActor -> {
                            ActorVO vo = new ActorVO();
                            Actor actor = actorMap.get(showActor.getActorId());
                            if (actor != null) {
                                vo.setId(actor.getId());
                                vo.setName(actor.getName());
                                vo.setAvatar(actor.getAvatar());
                                vo.setIntroduction(actor.getIntroduction());
                            }
                            vo.setRoleName(showActor.getRoleName());
                            vo.setIsMain(showActor.getIsMain());
                            return vo;
                        }).collect(Collectors.toList());
                        show.setActors(actorVOList);
                    } catch (Exception e) {
                        log.warn("查询演员详细信息失败：{}", e.getMessage());
                        show.setActors(new ArrayList<>());
                    }
                }
            } else {
                show.setActors(new ArrayList<>());
            }
        } catch (Exception e) {
            log.error("查询演员信息失败：showId={}, error={}", id, e.getMessage());
            show.setActors(new ArrayList<>());
        }

        return show;
    }

    /**
     * 计算票价范围
     */
    private void calculatePriceRange(Show show) {
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        // 获取所有票价
        if (show.getVipPrice() != null && show.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (minPrice == null || show.getVipPrice().compareTo(minPrice) < 0) {
                minPrice = show.getVipPrice();
            }
            if (maxPrice == null || show.getVipPrice().compareTo(maxPrice) > 0) {
                maxPrice = show.getVipPrice();
            }
        }
        if (show.getNormalPrice() != null && show.getNormalPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (minPrice == null || show.getNormalPrice().compareTo(minPrice) < 0) {
                minPrice = show.getNormalPrice();
            }
            if (maxPrice == null || show.getNormalPrice().compareTo(maxPrice) > 0) {
                maxPrice = show.getNormalPrice();
            }
        }
        if (show.getStudentPrice() != null && show.getStudentPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (minPrice == null || show.getStudentPrice().compareTo(minPrice) < 0) {
                minPrice = show.getStudentPrice();
            }
            if (maxPrice == null || show.getStudentPrice().compareTo(maxPrice) > 0) {
                maxPrice = show.getStudentPrice();
            }
        }
        if (show.getDiscountPrice() != null && show.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
            if (minPrice == null || show.getDiscountPrice().compareTo(minPrice) < 0) {
                minPrice = show.getDiscountPrice();
            }
            if (maxPrice == null || show.getDiscountPrice().compareTo(maxPrice) > 0) {
                maxPrice = show.getDiscountPrice();
            }
        }

        show.setMinPrice(minPrice);
        show.setMaxPrice(maxPrice);
    }

    /**
     * 计算场次最低票价
     * 优先使用场次价格，为空时使用剧目价格
     */
    private Integer calculateSessionMinPrice(ShowSession session, Show show) {
        Integer minPrice = null;
        log.info("计算场次票价 - sessionId: {}, vipPrice: {}, normalPrice: {}, studentPrice: {}, discountPrice: {}",
            session.getId(), session.getVipPrice(), session.getNormalPrice(),
            session.getStudentPrice(), session.getDiscountPrice());

        // 优先使用场次价格，为空时使用剧目价格
        BigDecimal vipPrice = session.getVipPrice() != null ? session.getVipPrice() : show.getVipPrice();
        BigDecimal normalPrice = session.getNormalPrice() != null ? session.getNormalPrice() : show.getNormalPrice();
        BigDecimal studentPrice = session.getStudentPrice() != null ? session.getStudentPrice() : show.getStudentPrice();
        BigDecimal discountPrice = session.getDiscountPrice() != null ? session.getDiscountPrice() : show.getDiscountPrice();

        // 获取票价，找到最低价
        if (vipPrice != null && vipPrice.compareTo(BigDecimal.ZERO) > 0) {
            int price = vipPrice.intValue();
            if (minPrice == null || price < minPrice) {
                minPrice = price;
            }
        }
        if (normalPrice != null && normalPrice.compareTo(BigDecimal.ZERO) > 0) {
            int price = normalPrice.intValue();
            if (minPrice == null || price < minPrice) {
                minPrice = price;
            }
        }
        if (studentPrice != null && studentPrice.compareTo(BigDecimal.ZERO) > 0) {
            int price = studentPrice.intValue();
            if (minPrice == null || price < minPrice) {
                minPrice = price;
            }
        }
        if (discountPrice != null && discountPrice.compareTo(BigDecimal.ZERO) > 0) {
            int price = discountPrice.intValue();
            if (minPrice == null || price < minPrice) {
                minPrice = price;
            }
        }

        log.info("场次计算结果 - sessionId: {}, minPrice: {}", session.getId(), minPrice);
        return minPrice;
    }
}
