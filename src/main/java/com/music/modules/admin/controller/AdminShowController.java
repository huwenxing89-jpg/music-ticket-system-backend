package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.show.entity.Show;
import com.music.modules.show.service.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理员-剧目管理控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/show")
@RequiredArgsConstructor
@Tag(name = "管理员-剧目管理")
public class AdminShowController {

    private final ShowService showService;

    @Value("${file.upload-path:D:/upload/music-ticket-system}")
    private String uploadPath;

    @Value("${file.access-url:/files}")
    private String accessUrl;

    /**
     * 分页查询剧目列表（管理员）
     */
    @GetMapping
    @Operation(summary = "分页查询剧目列表")
    public Result<Page<Show>> getShowList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "剧目名称") @RequestParam(required = false) String name,
            @Parameter(description = "剧目类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "剧目状态") @RequestParam(required = false) Integer status) {

        log.info("管理员查询剧目列表：page={}, size={}, name={}, type={}, status={}", page, size, name, type, status);

        Page<Show> pageParam = new Page<>(page, size);
        Page<Show> result = showService.getShowList(pageParam, name, type, status, null, null, null, null);

        return Result.success(result);
    }

    /**
     * 添加剧目
     */
    @PostMapping
    @Operation(summary = "添加剧目")
    public Result<Void> addShow(@RequestBody Show show) {
        log.info("添加剧目：{}", show.getName());
        showService.save(show);
        return Result.success();
    }

    /**
     * 更新剧目
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新剧目")
    public Result<Void> updateShow(
            @Parameter(description = "剧目ID") @PathVariable Long id,
            @RequestBody Show show) {
        log.info("更新剧目：id={}", id);
        show.setId(id);
        showService.updateById(show);
        return Result.success();
    }

    /**
     * 删除剧目
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除剧目")
    public Result<Void> deleteShow(@Parameter(description = "剧目ID") @PathVariable Long id) {
        log.info("删除剧目：id={}", id);
        showService.removeById(id);
        return Result.success();
    }

    /**
     * 获取剧目详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取剧目详情")
    public Result<Show> getShowDetail(@Parameter(description = "剧目ID") @PathVariable Long id) {
        log.info("查询剧目详情：id={}", id);
        Show show = showService.getShowDetail(id);
        return Result.success(show);
    }

    /**
     * 上传剧目海报
     */
    @PostMapping("/upload")
    @Operation(summary = "上传剧目海报")
    public Result<Map<String, String>> uploadPoster(@RequestParam("file") MultipartFile file) {
        log.info("上传文件：{}, 大小：{}", file.getOriginalFilename(), file.getSize());

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
            Path uploadDir = Paths.get(uploadPath, "shows");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            // 构建返回的URL
            String fileUrl = accessUrl + "/shows/" + filename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);

            log.info("文件上传成功：{}", fileUrl);
            return Result.success(result);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新剧目票价
     */
    @PutMapping("/{id}/price")
    @Operation(summary = "更新剧目票价")
    public Result<Void> updateShowPrice(
            @Parameter(description = "剧目ID") @PathVariable Long id,
            @RequestBody Map<String, Object> priceData) {
        log.info("更新剧目票价：id={}", id);

        Show show = showService.getById(id);
        if (show == null) {
            return Result.fail("剧目不存在");
        }

        // 获取票价数据
        Object vipPrice = priceData.get("vipPrice");
        Object normalPrice = priceData.get("normalPrice");
        Object studentPrice = priceData.get("studentPrice");
        Object discountPrice = priceData.get("discountPrice");

        // 更新票价
        if (vipPrice != null) {
            show.setVipPrice(new BigDecimal(vipPrice.toString()));
        }
        if (normalPrice != null) {
            show.setNormalPrice(new BigDecimal(normalPrice.toString()));
        }
        if (studentPrice != null) {
            show.setStudentPrice(new BigDecimal(studentPrice.toString()));
        }
        if (discountPrice != null) {
            show.setDiscountPrice(new BigDecimal(discountPrice.toString()));
        }

        showService.updateById(show);
        return Result.success();
    }
}
