package com.music.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.common.result.Result;
import com.music.common.result.ResultCode;
import com.music.modules.user.entity.User;
import com.music.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员-用户管理控制器
 *
 * @author 黄晓倩
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "管理员-用户管理")
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @Operation(summary = "分页查询用户列表")
    public Result<Page<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {

        log.info("管理员查询用户列表：page={}, size={}, username={}, role={}, status={}, phone={}, startDate={}, endDate={}",
            page, size, username, role, status, phone, startDate, endDate);

        Page<User> pageParam = new Page<>(page, size);
        Page<User> result = userService.getUserList(pageParam, username, role, status, phone, startDate, endDate);

        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<User> getUserDetail(@Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("查询用户详情：id={}", id);
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody User user) {
        log.info("更新用户信息：id={}", id);
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 封禁用户
     */
    @PostMapping("/{id}/ban")
    @Operation(summary = "封禁用户")
    public Result<Void> banUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody(required = false) String reason) {
        log.info("封禁用户：id={}, reason={}", id, reason);
        User user = new User();
        user.setId(id);
        user.setStatus(1); // 1-封禁（与UserService登录逻辑一致）
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 解封用户
     */
    @PostMapping("/{id}/unban")
    @Operation(summary = "解封用户")
    public Result<Void> unbanUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("解封用户：id={}", id);
        User user = new User();
        user.setId(id);
        user.setStatus(0); // 0-正常（与UserService登录逻辑一致）
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 添加用户
     */
    @PostMapping
    @Operation(summary = "添加用户")
    public Result<Void> addUser(@RequestBody User user) {
        log.info("添加用户：username={}", user.getUsername());

        // 验证密码
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.fail(ResultCode.ERROR.getCode(), "密码不能为空");
        }

        try {
            // 检查用户名是否已存在（包括已删除的用户）
            User existingUser = getByUsernameIncludeDeleted(user.getUsername());
            if (existingUser != null) {
                if (existingUser.getDeleted() == 0) {
                    // 用户存在且未删除
                    return Result.fail(ResultCode.USER_ACCOUNT_EXIST);
                } else {
                    // 用户已被软删除，需要物理删除后才能重新创建
                    log.info("用户名{}已被软删除，将物理删除旧记录后重新创建", user.getUsername());
                    userService.removeById(existingUser.getId(), false); // 物理删除
                }
            }

            // 检查手机号是否已注册（同样需要检查已删除的）
            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                User existingPhone = getByPhoneIncludeDeleted(user.getPhone());
                if (existingPhone != null && existingPhone.getDeleted() == 0) {
                    return Result.fail(ResultCode.ERROR.getCode(), "手机号已被注册");
                }
            }

            // 检查邮箱是否已注册
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                User existingEmail = getByEmailIncludeDeleted(user.getEmail());
                if (existingEmail != null && existingEmail.getDeleted() == 0) {
                    return Result.fail(ResultCode.ERROR.getCode(), "邮箱已被注册");
                }
            }

            // 设置默认值
            if (user.getRole() == null) {
                user.setRole("user");
            }
            if (user.getStatus() == null) {
                user.setStatus(0); // 0-正常
            }
            if (user.getGender() == null) {
                user.setGender(0); // 0-未知
            }

            // 使用BCrypt加密密码
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            boolean saved = userService.save(user);
            if (saved) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.SYSTEM_ERROR);
            }
        } catch (DuplicateKeyException e) {
            log.error("添加用户失败，数据冲突：username={}", user.getUsername(), e);
            return Result.fail(ResultCode.USER_ACCOUNT_EXIST);
        } catch (Exception e) {
            log.error("添加用户失败：username={}", user.getUsername(), e);
            return Result.fail(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 查询用户（包括已删除的）- 用于唯一性检查
     */
    private User getByUsernameIncludeDeleted(String username) {
        return userService.getBaseMapper().selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("LIMIT 1")
        );
    }

    /**
     * 通过手机号查询用户（包括已删除的）
     */
    private User getByPhoneIncludeDeleted(String phone) {
        return userService.getBaseMapper().selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .last("LIMIT 1")
        );
    }

    /**
     * 通过邮箱查询用户（包括已删除的）
     */
    private User getByEmailIncludeDeleted(String email) {
        return userService.getBaseMapper().selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .last("LIMIT 1")
        );
    }

    /**
     * 删除用户（软删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("删除用户：id={}", id);
        userService.removeById(id);
        return Result.success();
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置用户密码")
    public Result<Void> resetUserPassword(@Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("重置用户密码：id={}", id);
        // TODO: 实现重置密码功能
        return Result.success();
    }
}
