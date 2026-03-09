package com.music.modules.show.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.common.utils.JwtUtil;
import com.music.modules.show.dto.FavoriteVO;
import com.music.modules.show.entity.Show;
import com.music.modules.show.service.ShowService;
import com.music.modules.user.entity.UserFavorite;
import com.music.modules.user.mapper.UserFavoriteMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 剧目控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/show")
@RequiredArgsConstructor
@Tag(name = "剧目管理")
public class ShowController {

    private final ShowService showService;
    private final UserFavoriteMapper userFavoriteMapper;
    private final JwtUtil jwtUtil;

    /**
     * 分页查询剧目列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询剧目列表")
    public Result<Page<Show>> getShowList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "剧目类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "剧目状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "剧目类别(字符串)") @RequestParam(required = false) String category,
            @Parameter(description = "排序方式") @RequestParam(required = false) String sort,
            @Parameter(description = "最低价格") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) BigDecimal maxPrice) {

        log.info("查询剧目列表：current={}, size={}, keyword={}, type={}, status={}, category={}, sort={}, minPrice={}, maxPrice={}",
                current, size, keyword, type, status, category, sort, minPrice, maxPrice);

        Page<Show> page = new Page<>(current, size);
        Page<Show> result = showService.getShowList(page, keyword, type, status, category, sort, minPrice, maxPrice);

        return Result.success(result);
    }

    /**
     * 获取剧目详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取剧目详情")
    public Result<Show> getShowDetail(
            @Parameter(description = "剧目ID") @PathVariable Long id) {

        log.info("查询剧目详情：id={}", id);

        Show show = showService.getShowDetail(id);
        return Result.success(show);
    }

    /**
     * 获取热门剧目
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门剧目")
    public Result<Page<Show>> getHotShows(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {

        log.info("查询热门剧目：limit={}", limit);

        Page<Show> page = new Page<>(1, limit);
        Page<Show> result = showService.getShowList(page, null, null, 1, null, null, null, null); // 只返回正在上映的剧目

        return Result.success(result);
    }

    /**
     * 获取推荐剧目
     */
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐剧目")
    public Result<Page<Show>> getRecommendShows(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {

        log.info("查询推荐剧目：limit={}", limit);

        Page<Show> page = new Page<>(1, limit);
        Page<Show> result = showService.getShowList(page, null, null, 1, null, null, null, null); // 返回正在上映的剧目作为推荐

        return Result.success(result);
    }

    /**
     * 添加收藏
     */
    @PostMapping("/favorite")
    @Operation(summary = "添加收藏")
    public Result<Void> favoriteShow(HttpServletRequest request,
                                     @Parameter(description = "剧目ID") @RequestParam Long showId) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 检查是否已收藏
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId).eq(UserFavorite::getShowId, showId);
        UserFavorite existFavorite = userFavoriteMapper.selectOne(wrapper);

        if (existFavorite != null) {
            return Result.fail("已经收藏过了");
        }

        // 添加收藏
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setShowId(showId);
        userFavoriteMapper.insert(favorite);

        log.info("用户{}收藏剧目{}", userId, showId);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite")
    @Operation(summary = "取消收藏")
    public Result<Void> unfavoriteShow(HttpServletRequest request,
                                       @Parameter(description = "剧目ID") @RequestParam Long showId) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 删除收藏
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId).eq(UserFavorite::getShowId, showId);
        userFavoriteMapper.delete(wrapper);

        log.info("用户{}取消收藏剧目{}", userId, showId);
        return Result.success();
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/favorite/list")
    @Operation(summary = "获取收藏列表")
    public Result<Page<FavoriteVO>> getFavoriteList(HttpServletRequest request,
                                                     @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
                                                     @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 分页查询收藏列表
        Page<UserFavorite> page = new Page<>(current, size);
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId).orderByDesc(UserFavorite::getCreateTime);
        Page<UserFavorite> favoritePage = userFavoriteMapper.selectPage(page, wrapper);

        // 获取收藏的剧目ID列表
        List<Long> showIds = favoritePage.getRecords().stream()
                .map(UserFavorite::getShowId)
                .collect(Collectors.toList());

        // 查询剧目详情
        List<FavoriteVO> favoriteVOList = new ArrayList<>();
        if (!showIds.isEmpty()) {
            LambdaQueryWrapper<Show> showWrapper = new LambdaQueryWrapper<>();
            showWrapper.in(Show::getId, showIds);
            List<Show> shows = showService.list(showWrapper);

            // 创建showId -> Show的映射
            Map<Long, Show> showMap = shows.stream()
                    .collect(Collectors.toMap(Show::getId, s -> s));

            // 组装FavoriteVO
            for (UserFavorite favorite : favoritePage.getRecords()) {
                Show show = showMap.get(favorite.getShowId());
                if (show != null) {
                    FavoriteVO vo = new FavoriteVO();
                    vo.setId(favorite.getId());
                    vo.setUserId(favorite.getUserId());
                    vo.setShowId(favorite.getShowId());
                    vo.setCreateTime(favorite.getCreateTime());

                    // 设置剧目信息
                    vo.setShowTitle(show.getName());
                    vo.setPoster(show.getPoster());
                    vo.setDescription(show.getDescription());
                    vo.setDuration(show.getDuration());
                    vo.setType(show.getType());
                    vo.setStatus(show.getStatus());
                    vo.setRating(show.getRating());
                    vo.setMinPrice(show.getMinPrice());
                    vo.setMaxPrice(show.getMaxPrice());

                    favoriteVOList.add(vo);
                }
            }
        }

        // 组装分页结果
        Page<FavoriteVO> result = new Page<>(current, size, favoritePage.getTotal());
        result.setRecords(favoriteVOList);

        log.info("查询用户{}的收藏列表", userId);
        return Result.success(result);
    }
}
