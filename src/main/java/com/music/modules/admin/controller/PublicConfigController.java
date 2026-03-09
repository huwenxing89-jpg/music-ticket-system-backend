package com.music.modules.admin.controller;

import com.music.common.result.Result;
import com.music.modules.admin.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共配置控制器（无需认证）
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "公共配置")
public class PublicConfigController {

    private final SystemConfigService systemConfigService;

    /**
     * 获取公共系统配置（无需认证）
     * 只返回用户端需要的基础信息，不包含敏感配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取公共系统配置")
    public Result<Map<String, Object>> getPublicConfig() {
        log.info("获取公共系统配置");

        Map<String, Object> config = new HashMap<>();

        // 从数据库获取配置
        Map<String, Map<String, String>> dbConfig = systemConfigService.getAllConfigByGroup();

        // 基础配置
        Map<String, Object> basic = new HashMap<>();
        Map<String, String> basicDb = dbConfig.getOrDefault("basic", new HashMap<>());
        basic.put("siteName", basicDb.getOrDefault("site.name", "音乐剧购票系统"));
        basic.put("siteShortName", basicDb.getOrDefault("site.shortname", "音乐剧票务"));
        basic.put("logo", basicDb.getOrDefault("site.logo", "/files/system/logo.png"));
        basic.put("icp", basicDb.getOrDefault("site.icp", ""));
        basic.put("copyright", basicDb.getOrDefault("site.copyright", "© 2024 音乐剧购票系统"));
        basic.put("contactEmail", basicDb.getOrDefault("contact.email", "service@musical.com"));
        basic.put("contactPhone", basicDb.getOrDefault("contact.phone", "400-888-8888"));
        config.put("basic", basic);

        // 其他配置（用户端需要的）
        Map<String, Object> other = new HashMap<>();
        Map<String, String> otherDb = dbConfig.getOrDefault("other", new HashMap<>());
        other.put("registerEnabled", Boolean.parseBoolean(otherDb.getOrDefault("register.enabled", "true")));
        config.put("other", other);

        return Result.success(config);
    }
}
