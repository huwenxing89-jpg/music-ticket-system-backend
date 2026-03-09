package com.music.modules.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.cart.dto.AddCartDTO;
import com.music.modules.cart.entity.Cart;
import com.music.modules.cart.mapper.CartMapper;
import com.music.modules.cart.service.CartService;
import com.music.modules.seat.entity.Seat;
import com.music.modules.seat.mapper.SeatMapper;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final SeatMapper seatMapper;
    private final SessionMapper sessionMapper;
    private final ShowMapper showMapper;
    private final TheaterMapper theaterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToCart(AddCartDTO dto, Long userId) {
        // 检查座位是否存在且可选
        Seat seat = seatMapper.selectById(dto.getSeatId());
        if (seat == null) {
            throw new BusinessException("座位不存在");
        }
        if (!seat.getSessionId().equals(dto.getSessionId())) {
            throw new BusinessException("座位不属于该场次");
        }
        // 检查座位状态（简化：只检查是否已售出）
        // 购物车不检查锁定状态，因为锁定是临时的
        if (seat.getStatus() == 2) {
            throw new BusinessException("座位已售出");
        }

        // 检查是否已在购物车中
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getSeatId, dto.getSeatId());
        Cart existingCart = baseMapper.selectOne(wrapper);
        if (existingCart != null) {
            throw new BusinessException("该座位已在购物车中");
        }

        // 添加到购物车
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setSessionId(dto.getSessionId());
        cart.setSeatId(dto.getSeatId());
        cart.setSeatNumber(seat.getSeatNumber());
        cart.setPrice(seat.getPrice());

        int result = baseMapper.insert(cart);

        log.info("用户{}添加座位到购物车：sessionId={}, seatId={}", userId, dto.getSessionId(), dto.getSeatId());
        return result > 0;
    }

    @Override
    public List<Map<String, Object>> getUserCart(Long userId) {
        // 查询购物车列表
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime);
        List<Cart> cartList = baseMapper.selectList(wrapper);

        if (cartList.isEmpty()) {
            return new ArrayList<>();
        }

        // 按场次分组
        Map<Long, List<Cart>> groupedBySession = cartList.stream()
                .collect(Collectors.groupingBy(Cart::getSessionId));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<Long, List<Cart>> entry : groupedBySession.entrySet()) {
            Long sessionId = entry.getKey();
            List<Cart> carts = entry.getValue();

            // 查询场次信息
            ShowSession session = sessionMapper.selectById(sessionId);
            if (session == null) {
                continue;
            }

            // 查询剧目信息
            String showTitle = "未知剧目";
            String showCover = null;
            if (session.getShowId() != null) {
                Show show = showMapper.selectById(session.getShowId());
                if (show != null) {
                    showTitle = show.getName();
                    showCover = show.getPoster();
                }
            }

            // 查询剧院信息
            String theaterName = "未知剧院";
            if (session.getTheaterId() != null) {
                Theater theater = theaterMapper.selectById(session.getTheaterId());
                if (theater != null) {
                    theaterName = theater.getName();
                }
            }

            // 构建场次购物车数据
            Map<String, Object> sessionCart = new HashMap<>();
            sessionCart.put("sessionId", session.getId());
            sessionCart.put("showId", session.getShowId());
            sessionCart.put("showTitle", showTitle);
            sessionCart.put("showCover", showCover);
            sessionCart.put("theaterName", theaterName);
            sessionCart.put("showTime", session.getShowTime());

            // 座位列表
            List<Map<String, Object>> seats = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            for (Cart cart : carts) {
                Map<String, Object> seatInfo = new HashMap<>();
                seatInfo.put("cartId", cart.getId());
                seatInfo.put("seatId", cart.getSeatId());
                seatInfo.put("seatNumber", cart.getSeatNumber());
                seatInfo.put("price", cart.getPrice());
                seats.add(seatInfo);
                totalPrice = totalPrice.add(cart.getPrice());
            }

            sessionCart.put("seats", seats);
            sessionCart.put("totalPrice", totalPrice);
            sessionCart.put("seatCount", seats.size());

            result.add(sessionCart);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromCart(Long id, Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getId, id)
                .eq(Cart::getUserId, userId);

        int result = baseMapper.delete(wrapper);
        log.info("用户{}从购物车删除：cartId={}", userId, id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);

        int result = baseMapper.delete(wrapper);
        log.info("用户{}清空购物车：删除{}条记录", userId, result);
        return result >= 0;
    }
}
