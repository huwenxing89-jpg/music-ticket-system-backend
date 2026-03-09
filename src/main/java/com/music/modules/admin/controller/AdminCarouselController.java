package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.admin.dto.CarouselSaveDTO;
import com.music.modules.admin.dto.CarouselSortDTO;
import com.music.modules.admin.entity.Carousel;
import com.music.modules.admin.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 管理端-轮播图控制器
 *
 * @author 黄晓倩
 */
@Tag(name = "管理端-轮播图管理")
@Slf4j
@RestController
@RequestMapping("/api/admin/carousel")
@RequiredArgsConstructor
public class AdminCarouselController {

    private final CarouselService carouselService;

    @Value("${file.upload-path:D:/upload/music-ticket-system}")
    private String uploadPath;

    @Value("${file.access-url:/files}")
    private String accessUrl;

    /**
     * 获取所有轮播图列表（不分页）
     */
    @Operation(summary = "获取所有轮播图列表")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Carousel>> getCarouselList() {
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Carousel::getSort);
        List<Carousel> list = carouselService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 分页查询轮播图列表
     */
    @Operation(summary = "分页查询轮播图列表")
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Carousel>> getCarouselPage(@RequestParam(defaultValue = "1") Integer current,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        Page<Carousel> page = new Page<>(current, size);
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Carousel::getSort);
        Page<Carousel> result = carouselService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的轮播图（用于前端展示）
     */
    @Operation(summary = "获取所有启用的轮播图")
    @GetMapping("/active")
    public Result<List<Carousel>> getActiveCarousels() {
        List<Carousel> list = carouselService.getActiveCarousels();
        return Result.success(list);
    }

    /**
     * 获取轮播图详情
     */
    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Carousel> getCarouselById(@PathVariable Long id) {
        Carousel carousel = carouselService.getById(id);
        if (carousel == null) {
            return Result.fail("轮播图不存在");
        }
        return Result.success(carousel);
    }

    /**
     * 新增轮播图
     */
    @Operation(summary = "新增轮播图")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveCarousel(@RequestBody CarouselSaveDTO dto) {
        carouselService.saveCarousel(dto);
        return Result.success("新增成功");
    }

    /**
     * 更新轮播图
     */
    @Operation(summary = "更新轮播图")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateCarousel(@PathVariable Long id, @RequestBody CarouselSaveDTO dto) {
        carouselService.updateCarousel(id, dto);
        return Result.success("更新成功");
    }

    /**
     * 删除轮播图
     */
    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCarousel(@PathVariable Long id) {
        carouselService.deleteCarousel(id);
        return Result.success("删除成功");
    }

    /**
     * 启用/禁用轮播图
     */
    @Operation(summary = "启用/禁用轮播图")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        carouselService.updateStatus(id, status);
        return Result.success("操作成功");
    }

    /**
     * 批量更新轮播图排序
     */
    @Operation(summary = "批量更新轮播图排序")
    @PutMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateSort(@RequestBody List<CarouselSortDTO> sortList) {
        carouselService.updateSort(sortList);
        return Result.success("排序更新成功");
    }

    /**
     * 上传轮播图图片
     */
    @PostMapping("/upload")
    @Operation(summary = "上传轮播图图片")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, String>> uploadCarouselImage(@RequestParam("file") MultipartFile file) {
        log.info("上传轮播图图片：{}, 大小：{}", file.getOriginalFilename(), file.getSize());

        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") &&
                !contentType.equals("application/octet-stream"))) {
                return Result.fail("只支持图片文件");
            }

            // 验证文件大小（5MB）
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.fail("文件大小不能超过5MB");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, "carousels");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            // 构建返回的URL
            String fileUrl = accessUrl + "/carousels/" + filename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);

            log.info("轮播图图片上传成功：{}", fileUrl);
            return Result.success(result);

        } catch (IOException e) {
            log.error("轮播图图片上传失败", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }
}
