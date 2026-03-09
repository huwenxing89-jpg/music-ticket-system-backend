package com.music.modules.cart.controller;

import com.music.common.result.Result;
import com.music.common.utils.JwtUtil;
import com.music.modules.cart.dto.AddCartDTO;
import com.music.modules.cart.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Api(tags = "购物车管理")
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    @ApiOperation("添加到购物车")
    @PostMapping("/add")
    public Result<?> addToCart(@Valid @RequestBody AddCartDTO dto, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        boolean success = cartService.addToCart(dto, userId);
        return Result.success(success);
    }

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getCartList(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        List<Map<String, Object>> cartList = cartService.getUserCart(userId);
        return Result.success(cartList);
    }

    @ApiOperation("从购物车删除")
    @DeleteMapping("/{id}")
    public Result<?> removeFromCart(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        boolean success = cartService.removeFromCart(id, userId);
        return Result.success(success);
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clear")
    public Result<?> clearCart(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        boolean success = cartService.clearCart(userId);
        return Result.success(success);
    }

    /**
     * 从JWT Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无法获取用户信息，请重新登录");
        }
        return userId;
    }
}
