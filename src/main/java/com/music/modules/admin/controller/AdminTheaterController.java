package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.theater.entity.Theater;
import com.music.modules.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理员-剧院管理控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/theater")
@RequiredArgsConstructor
@Tag(name = "管理员-剧院管理")
public class AdminTheaterController {

    private final TheaterService theaterService;

    @Value("${file.upload-path:D:/upload/music-ticket-system}")
    private String uploadPath;

    @Value("${file.access-url:/files}")
    private String accessUrl;

    /**
     * 分页查询剧院列表
     */
    @GetMapping
    @Operation(summary = "分页查询剧院列表")
    public Result<Page<Theater>> getTheaterList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "剧院名称") @RequestParam(required = false) String name) {

        log.info("管理员查询剧院列表：page={}, size={}, name={}", page, size, name);
        Page<Theater> pageParam = new Page<>(page, size);
        Page<Theater> result = theaterService.getTheaterList(pageParam, name, null);
        return Result.success(result);
    }

    /**
     * 获取剧院详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取剧院详情")
    public Result<Theater> getTheaterDetail(@Parameter(description = "剧院ID") @PathVariable Long id) {
        log.info("查询剧院详情：id={}", id);
        Theater theater = theaterService.getById(id);
        return Result.success(theater);
    }

    /**
     * 创建剧院
     */
    @PostMapping
    @Operation(summary = "创建剧院")
    public Result<Void> createTheater(@RequestBody Theater theater) {
        log.info("创建剧院：{}", theater.getName());
        theaterService.save(theater);
        return Result.success();
    }

    /**
     * 更新剧院
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新剧院")
    public Result<Void> updateTheater(
            @Parameter(description = "剧院ID") @PathVariable Long id,
            @RequestBody Theater theater) {
        log.info("更新剧院：id={}", id);
        theater.setId(id);
        theaterService.updateById(theater);
        return Result.success();
    }

    /**
     * 删除剧院
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除剧院")
    public Result<Void> deleteTheater(@Parameter(description = "剧院ID") @PathVariable Long id) {
        log.info("删除剧院：id={}", id);
        theaterService.removeById(id);
        return Result.success();
    }

    /**
     * 获取座位模板列表
     */
    @GetMapping("/seat-templates")
    @Operation(summary = "获取座位模板列表")
    public Result<Map<String, Object>> getSeatTemplates() {
        log.info("获取座位模板列表");
        // TODO: 返回预设的座位模板
        Map<String, Object> templates = new HashMap<>();
        templates.put("small", "小型剧院（100座）");
        templates.put("medium", "中型剧院（300座）");
        templates.put("large", "大型剧院（800座）");
        return Result.success(templates);
    }

    /**
     * 保存座位图配置
     */
    @PostMapping("/{id}/seat-config")
    @Operation(summary = "保存座位图配置")
    public Result<Void> saveSeatConfig(
            @Parameter(description = "剧院ID") @PathVariable Long id,
            @RequestBody Map<String, Object> config) {
        log.info("保存座位图配置：id={}, config={}", id, config);
        // TODO: 实现座位图配置保存
        return Result.success();
    }

    /**
     * 上传剧院图片
     */
    @PostMapping("/upload")
    @Operation(summary = "上传剧院图片")
    public Result<Map<String, String>> uploadTheaterImage(@RequestParam("file") MultipartFile file) {
        log.info("上传剧院图片：{}, 大小：{}", file.getOriginalFilename(), file.getSize());

        try {
            // 验证文件是否存在
            if (file == null || file.isEmpty()) {
                log.warn("上传文件为空");
                return Result.fail("请选择要上传的文件");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            log.info("文件ContentType: {}", contentType);
            if (contentType == null || (!contentType.startsWith("image/") &&
                !contentType.equals("application/octet-stream"))) {
                log.warn("不支持的文件类型: {}", contentType);
                return Result.fail("只支持图片文件");
            }

            // 验证文件大小（5MB）
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                log.warn("文件大小超出限制: {} bytes", file.getSize());
                return Result.fail("文件大小不能超过5MB");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            log.info("上传路径: {}, 文件名: {}", uploadPath, filename);

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, "theaters");
            if (!Files.exists(uploadDir)) {
                log.info("创建上传目录: {}", uploadDir);
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            log.info("保存文件到: {}", filePath);
            file.transferTo(filePath.toFile());

            // 构建返回的URL
            String fileUrl = accessUrl + "/theaters/" + filename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);

            log.info("剧院图片上传成功：{}", fileUrl);
            return Result.success(result);

        } catch (IOException e) {
            log.error("剧院图片上传失败 - IO异常", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("剧院图片上传失败 - 系统异常", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }
}
