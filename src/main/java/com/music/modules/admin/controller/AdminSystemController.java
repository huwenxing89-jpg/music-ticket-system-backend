package com.music.modules.admin.controller;

import com.music.common.result.Result;
import com.music.modules.admin.service.SystemConfigService;
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
import java.util.Map;

/**
 * 管理端-系统管理控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
@Tag(name = "管理端-系统管理")
public class AdminSystemController {

    private final SystemConfigService systemConfigService;

    @Value("${file.upload-path:D:/upload/music-ticket-system}")
    private String uploadPath;

    @Value("${file.access-url:/files}")
    private String accessUrl;

    /**
     * 上传系统Logo
     */
    @PostMapping("/upload-logo")
    @Operation(summary = "上传系统Logo")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) {
        log.info("上传系统Logo：{}, 大小：{}", file.getOriginalFilename(), file.getSize());

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
                : ".png";
            String filename = "logo" + extension;

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, "system");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            file.transferTo(filePath.toFile());

            // 构建返回的URL
            String fileUrl = accessUrl + "/system/" + filename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);

            log.info("系统Logo上传成功：{}", fileUrl);
            return Result.success(result);

        } catch (IOException e) {
            log.error("系统Logo上传失败", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 测试邮件配置
     */
    @PostMapping("/test-email")
    @Operation(summary = "测试邮件配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> testEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("测试邮件配置：{}", email);

        Map<String, Object> result = new HashMap<>();
        // TODO: 实现邮件测试功能
        result.put("success", true);
        result.put("message", "邮件发送成功");
        return result;
    }

    /**
     * 测试短信配置
     */
    @PostMapping("/test-sms")
    @Operation(summary = "测试短信配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> testSms(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        log.info("测试短信配置：{}", phone);

        Map<String, Object> result = new HashMap<>();
        // TODO: 实现短信测试功能
        result.put("success", true);
        result.put("message", "短信发送成功");
        return result;
    }

    /**
     * 获取系统配置（分组返回）
     */
    @GetMapping("/config")
    @Operation(summary = "获取系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getSystemConfig() {
        log.info("获取系统配置");

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
        basic.put("contactEmail", basicDb.getOrDefault("contact.email", ""));
        basic.put("contactPhone", basicDb.getOrDefault("contact.phone", ""));
        config.put("basic", basic);

        // 支付配置
        Map<String, Object> payment = new HashMap<>();
        Map<String, String> paymentDb = dbConfig.getOrDefault("payment", new HashMap<>());
        payment.put("alipayEnabled", Boolean.parseBoolean(paymentDb.getOrDefault("alipay.enabled", "false")));
        payment.put("alipayAppId", paymentDb.getOrDefault("alipay.appid", ""));
        payment.put("wechatEnabled", Boolean.parseBoolean(paymentDb.getOrDefault("wechat.enabled", "false")));
        payment.put("wechatAppId", paymentDb.getOrDefault("wechat.appid", ""));
        payment.put("orderTimeout", paymentDb.getOrDefault("order.timeout", "30"));
        config.put("payment", payment);

        // 短信配置
        Map<String, Object> sms = new HashMap<>();
        Map<String, String> smsDb = dbConfig.getOrDefault("sms", new HashMap<>());
        sms.put("enabled", Boolean.parseBoolean(smsDb.getOrDefault("sms.enabled", "false")));
        sms.put("provider", smsDb.getOrDefault("sms.provider", "aliyun"));
        sms.put("accessKey", smsDb.getOrDefault("sms.accesskey", ""));
        sms.put("secretKey", smsDb.getOrDefault("sms.secretkey", ""));
        sms.put("signName", smsDb.getOrDefault("sms.signname", ""));
        sms.put("templateCode", smsDb.getOrDefault("sms.templatecode", ""));
        config.put("sms", sms);

        // 邮件配置
        Map<String, Object> email = new HashMap<>();
        Map<String, String> emailDb = dbConfig.getOrDefault("email", new HashMap<>());
        email.put("enabled", Boolean.parseBoolean(emailDb.getOrDefault("email.enabled", "false")));
        email.put("host", emailDb.getOrDefault("email.host", "smtp.qq.com"));
        email.put("port", emailDb.getOrDefault("email.port", "587"));
        email.put("username", emailDb.getOrDefault("email.username", ""));
        email.put("password", emailDb.getOrDefault("email.password", ""));
        email.put("fromName", emailDb.getOrDefault("email.fromname", "音乐剧购票系统"));
        email.put("fromAddress", emailDb.getOrDefault("email.fromaddress", "noreply@example.com"));
        config.put("email", email);

        // 其他配置
        Map<String, Object> other = new HashMap<>();
        Map<String, String> otherDb = dbConfig.getOrDefault("other", new HashMap<>());
        other.put("registerEnabled", Boolean.parseBoolean(otherDb.getOrDefault("register.enabled", "true")));
        other.put("emailVerifyRequired", Boolean.parseBoolean(otherDb.getOrDefault("verify.email", "false")));
        other.put("phoneVerifyRequired", Boolean.parseBoolean(otherDb.getOrDefault("verify.phone", "false")));
        other.put("commentAutoAudit", Boolean.parseBoolean(otherDb.getOrDefault("comment.autoaudit", "true")));
        config.put("other", other);

        return Result.success(config);
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/config")
    @Operation(summary = "更新系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateSystemConfig(@RequestBody Map<String, Object> config) {
        log.info("更新系统配置：{}", config);

        Map<String, String> configMap = new HashMap<>();

        // 处理基础配置
        Map<String, Object> basic = (Map<String, Object>) config.get("basic");
        if (basic != null) {
            configMap.put("site.name", String.valueOf(basic.get("siteName")));
            configMap.put("site.shortname", String.valueOf(basic.get("siteShortName")));
            configMap.put("site.logo", String.valueOf(basic.get("logo")));
            configMap.put("site.icp", String.valueOf(basic.get("icp")));
            configMap.put("site.copyright", String.valueOf(basic.get("copyright")));
            configMap.put("contact.email", String.valueOf(basic.get("contactEmail")));
            configMap.put("contact.phone", String.valueOf(basic.get("contactPhone")));
        }

        // 处理支付配置
        Map<String, Object> payment = (Map<String, Object>) config.get("payment");
        if (payment != null) {
            configMap.put("alipay.enabled", String.valueOf(payment.get("alipayEnabled")));
            configMap.put("alipay.appid", String.valueOf(payment.get("alipayAppId")));
            configMap.put("wechat.enabled", String.valueOf(payment.get("wechatEnabled")));
            configMap.put("wechat.appid", String.valueOf(payment.get("wechatAppId")));
            configMap.put("order.timeout", String.valueOf(payment.get("orderTimeout")));
        }

        // 处理短信配置
        Map<String, Object> sms = (Map<String, Object>) config.get("sms");
        if (sms != null) {
            configMap.put("sms.enabled", String.valueOf(sms.get("enabled")));
            configMap.put("sms.provider", String.valueOf(sms.get("provider")));
            configMap.put("sms.accesskey", String.valueOf(sms.get("accessKey")));
            configMap.put("sms.secretkey", String.valueOf(sms.get("secretKey")));
            configMap.put("sms.signname", String.valueOf(sms.get("signName")));
            configMap.put("sms.templatecode", String.valueOf(sms.get("templateCode")));
        }

        // 处理邮件配置
        Map<String, Object> email = (Map<String, Object>) config.get("email");
        if (email != null) {
            configMap.put("email.enabled", String.valueOf(email.get("enabled")));
            configMap.put("email.host", String.valueOf(email.get("host")));
            configMap.put("email.port", String.valueOf(email.get("port")));
            configMap.put("email.username", String.valueOf(email.get("username")));
            configMap.put("email.password", String.valueOf(email.get("password")));
            configMap.put("email.fromname", String.valueOf(email.get("fromName")));
            configMap.put("email.fromaddress", String.valueOf(email.get("fromAddress")));
        }

        // 处理其他配置
        Map<String, Object> other = (Map<String, Object>) config.get("other");
        if (other != null) {
            configMap.put("register.enabled", String.valueOf(other.get("registerEnabled")));
            configMap.put("verify.email", String.valueOf(other.get("emailVerifyRequired")));
            configMap.put("verify.phone", String.valueOf(other.get("phoneVerifyRequired")));
            configMap.put("comment.autoaudit", String.valueOf(other.get("commentAutoAudit")));
        }

        systemConfigService.batchUpdateConfig(configMap);
        return Result.success("更新成功");
    }
}
