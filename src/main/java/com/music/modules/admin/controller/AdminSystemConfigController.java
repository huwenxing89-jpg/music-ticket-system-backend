package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.admin.dto.SystemConfigUpdateDTO;
import com.music.modules.admin.entity.SystemConfig;
import com.music.modules.admin.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端-系统配置控制器
 *
 * @author 黄晓倩
 */
@Tag(name = "管理端-系统配置")
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    /**
     * 分页查询系统配置列表
     */
    @Operation(summary = "分页查询系统配置列表")
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<SystemConfig>> getSystemConfigPage(@RequestParam(defaultValue = "1") Integer current,
                                                           @RequestParam(defaultValue = "10") Integer size,
                                                           @RequestParam(required = false) String configGroup) {
        Page<SystemConfig> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        if (configGroup != null && !configGroup.isEmpty()) {
            wrapper.eq(SystemConfig::getConfigGroup, configGroup);
        }
        wrapper.orderByAsc(SystemConfig::getConfigGroup)
                .orderByAsc(SystemConfig::getId);
        Page<SystemConfig> result = systemConfigService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 根据分组获取系统配置Map
     */
    @Operation(summary = "根据分组获取系统配置Map")
    @GetMapping("/group/{configGroup}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, String>> getConfigMapByGroup(@PathVariable String configGroup) {
        Map<String, String> map = systemConfigService.getConfigMapByGroup(configGroup);
        return Result.success(map);
    }

    /**
     * 获取所有配置分组
     */
    @Operation(summary = "获取所有配置分组")
    @GetMapping("/groups")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<String>> getConfigGroups() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SystemConfig::getConfigGroup)
                .groupBy(SystemConfig::getConfigGroup);
        List<SystemConfig> list = systemConfigService.list(wrapper);
        List<String> groups = list.stream()
                .map(SystemConfig::getConfigGroup)
                .collect(java.util.stream.Collectors.toList());
        return Result.success(groups);
    }

    /**
     * 根据配置键获取配置值
     */
    @Operation(summary = "根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    /**
     * 更新系统配置
     */
    @Operation(summary = "更新系统配置")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateConfig(@RequestBody SystemConfigUpdateDTO dto) {
        systemConfigService.updateConfig(dto);
        return Result.success("更新成功");
    }
}
