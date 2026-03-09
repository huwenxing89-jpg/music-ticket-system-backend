package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.music.common.result.Result;
import com.music.modules.admin.dto.LoginLogQueryDTO;
import com.music.modules.admin.entity.LoginLog;
import com.music.modules.admin.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-登录日志控制器
 *
 * @author 黄晓倩
 */
@Tag(name = "管理端-登录日志")
@RestController
@RequestMapping("/api/admin/log/login")
@RequiredArgsConstructor
public class AdminLoginLogController {

    private final LoginLogService loginLogService;

    /**
     * 分页查询登录日志
     */
    @Operation(summary = "分页查询登录日志")
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<LoginLog>> getLoginLogPage(LoginLogQueryDTO dto) {
        IPage<LoginLog> page = loginLogService.getLoginLogPage(dto);
        return Result.success(page);
    }

    /**
     * 获取登录日志详情
     */
    @Operation(summary = "获取登录日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<LoginLog> getLoginLogById(@PathVariable Long id) {
        LoginLog log = loginLogService.getById(id);
        if (log == null) {
            return Result.fail("日志不存在");
        }
        return Result.success(log);
    }
}
