package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.music.common.result.Result;
import com.music.modules.admin.dto.OperatorLogQueryDTO;
import com.music.modules.admin.entity.OperatorLog;
import com.music.modules.admin.service.OperatorLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-操作日志控制器
 *
 * @author 黄晓倩
 */
@Tag(name = "管理端-操作日志")
@RestController
@RequestMapping("/api/admin/log/operator")
@RequiredArgsConstructor
public class AdminOperatorLogController {

    private final OperatorLogService operatorLogService;

    /**
     * 分页查询操作日志
     */
    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<OperatorLog>> getOperatorLogPage(OperatorLogQueryDTO dto) {
        IPage<OperatorLog> page = operatorLogService.getOperatorLogPage(dto);
        return Result.success(page);
    }

    /**
     * 获取操作日志详情
     */
    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<OperatorLog> getOperatorLogById(@PathVariable Long id) {
        OperatorLog log = operatorLogService.getById(id);
        if (log == null) {
            return Result.fail("日志不存在");
        }
        return Result.success(log);
    }
}
