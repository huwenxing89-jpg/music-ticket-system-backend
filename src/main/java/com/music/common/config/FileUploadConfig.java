package com.music.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 文件上传配置
 *
 * @author 黄晓倩
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    /**
     * 文件上传路径
     */
    @Value("${file.upload-path:/upload}")
    private String uploadPath;

    /**
     * 文件访问URL前缀
     */
    @Value("${file.access-url:/files}")
    private String accessUrl;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 配置主要文件访问映射（/uploads）
        registry.addResourceHandler(accessUrl + "/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // 同时支持 /files 路径（兼容旧数据）
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
