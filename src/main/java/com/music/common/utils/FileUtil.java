package com.music.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 *
 * @author 黄晓倩
 */
@Slf4j
@Component
public class FileUtil {

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
     * 允许上传的图片格式
     */
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
    );

    /**
     * 允许上传的文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    /**
     * 上传文件到指定目录
     *
     * @param file     文件
     * @param category 分类目录（可选）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String category) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);

        // 检查文件格式
        if (!isImageFile(extension)) {
            throw new IllegalArgumentException("只支持上传图片文件");
        }

        try {
            // 生成新的文件名
            String newFileName = generateFileName(extension);

            // 构建保存路径
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = (category != null ? category + "/" : "") + datePath + "/" + newFileName;
            String fullPath = uploadPath + "/" + relativePath;

            // 确保目录存在
            Path filePath = Paths.get(fullPath);
            Files.createDirectories(filePath.getParent());

            // 保存文件
            file.transferTo(filePath.toFile());

            // 返回访问URL
            String accessUrlPath = accessUrl + "/" + relativePath;
            log.info("文件上传成功：{}", accessUrlPath);
            return accessUrlPath;

        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败");
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl.replace(accessUrl + "/", "");
            String fullPath = uploadPath + "/" + relativePath;

            File file = new File(fullPath);
            if (file.exists() && file.delete()) {
                log.info("文件删除成功：{}", fullPath);
                return true;
            }
            return false;

        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("文件格式不正确");
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 判断是否为图片文件
     *
     * @param extension 扩展名
     * @return 是否为图片
     */
    private boolean isImageFile(String extension) {
        return IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 生成新的文件名
     *
     * @param extension 扩展名
     * @return 新文件名
     */
    private String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
}
