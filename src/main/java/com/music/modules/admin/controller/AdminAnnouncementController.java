package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.modules.admin.dto.AnnouncementSaveDTO;
import com.music.modules.admin.entity.Announcement;
import com.music.modules.admin.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端-公告控制器
 *
 * @author 黄晓倩
 */
@Tag(name = "管理端-公告管理")
@RestController
@RequestMapping("/api/admin/announcement")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 分页查询公告列表
     */
    @Operation(summary = "分页查询公告列表")
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Announcement>> getAnnouncementPage(@RequestParam(defaultValue = "1") Integer current,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        Page<Announcement> page = new Page<>(current, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        Page<Announcement> result = announcementService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 查询公告列表（兼容前端）
     */
    @Operation(summary = "查询公告列表")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Announcement>> getAnnouncementList(@RequestParam(defaultValue = "1") Integer current,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        Page<Announcement> page = new Page<>(current, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        Page<Announcement> result = announcementService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的公告（用于前端展示）
     */
    @Operation(summary = "获取所有启用的公告")
    @GetMapping("/active")
    public Result<List<Announcement>> getActiveAnnouncements() {
        List<Announcement> list = announcementService.getActiveAnnouncements();
        return Result.success(list);
    }

    /**
     * 获取公告详情
     */
    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.fail("公告不存在");
        }
        return Result.success(announcement);
    }

    /**
     * 新增公告
     */
    @Operation(summary = "新增公告")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveAnnouncement(@RequestBody AnnouncementSaveDTO dto) {
        announcementService.saveAnnouncement(dto);
        return Result.success("新增成功");
    }

    /**
     * 更新公告
     */
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementSaveDTO dto) {
        announcementService.updateAnnouncement(id, dto);
        return Result.success("更新成功");
    }

    /**
     * 删除公告
     */
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success("删除成功");
    }

    /**
     * 启用/禁用公告
     */
    @Operation(summary = "启用/禁用公告")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success("操作成功");
    }

    /**
     * 置顶/取消置顶公告
     */
    @Operation(summary = "置顶/取消置顶公告")
    @PutMapping("/{id}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateTop(@PathVariable Long id, @RequestParam Integer isTop) {
        announcementService.updateTop(id, isTop);
        return Result.success("操作成功");
    }
}
