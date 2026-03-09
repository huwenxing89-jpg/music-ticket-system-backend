package com.music.modules.user.controller;

import com.music.common.result.Result;
import com.music.common.result.ResultCode;
import com.music.common.utils.JwtUtil;
import com.music.modules.user.dto.*;
import com.music.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Api(tags = "用户管理")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    @Value("${file.access-url:/uploads}")
    private String accessUrl;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result<Long> register(@Validated @RequestBody UserRegisterDTO dto) {
        Long userId = userService.register(dto);
        return Result.success(ResultCode.USER_REGISTER_SUCCESS.getMessage(), userId);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Result<LoginVO> login(@Validated @RequestBody UserLoginDTO dto) {
        LoginVO loginVO = userService.login(dto);
        return Result.success(ResultCode.USER_LOGIN_SUCCESS.getMessage(), loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取当前用户信息")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    @ApiOperation(value = "更新用户信息")
    public Result<Void> updateUserInfo(HttpServletRequest request,
                                       @Validated @RequestBody UserUpdateDTO dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @ApiOperation(value = "修改密码")
    public Result<Void> changePassword(HttpServletRequest request,
                                       @Validated @RequestBody UserChangePasswordDTO dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.changePassword(userId, dto);
        return Result.success();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.logout(token);
        return Result.success();
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    @ApiOperation(value = "刷新Token")
    public Result<String> refreshToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String newToken = jwtUtil.refreshToken(token);
        return Result.success(newToken);
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    @ApiOperation(value = "上传头像")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.fail("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                return Result.fail("只支持图片文件");
            }

            // 验证文件大小（5MB）
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.fail("图片大小不能超过5MB");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            Path filePath = Paths.get(uploadPath, filename);
            Files.write(filePath, file.getBytes());

            // 返回访问URL
            String fileUrl = accessUrl.startsWith("/") ? accessUrl + "/" + filename : "/" + accessUrl + "/" + filename;
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);

            log.info("头像上传成功: {}", fileUrl);
            return Result.success(result);

        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.fail("头像上传失败");
        }
    }
}
