package com.music.modules.admin.controller;

import com.music.common.result.Result;
import com.music.modules.admin.entity.Carousel;
import com.music.modules.admin.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图控制器（用户端）
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
@Tag(name = "轮播图管理（用户端）")
public class CarouselController {

    private final CarouselService carouselService;

    /**
     * 获取所有启用的轮播图（按排序）
     */
    @GetMapping("/active")
    @Operation(summary = "获取所有启用的轮播图")
    public Result<List<Carousel>> getActiveCarousels() {
        log.info("获取启用的轮播图列表");
        List<Carousel> list = carouselService.getActiveCarousels();
        return Result.success(list);
    }
}
