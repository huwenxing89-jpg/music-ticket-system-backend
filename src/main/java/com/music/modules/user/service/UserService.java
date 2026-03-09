package com.music.modules.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.user.dto.*;
import com.music.modules.user.entity.User;

/**
 * 用户服务接口
 *
 * @author 黄晓倩
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param dto 注册信息
     * @return 用户ID
     */
    Long register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return 登录响应（包含Token和用户信息）
     */
    LoginVO login(UserLoginDTO dto);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto 更新信息
     * @return 是否成功
     */
    Boolean updateUserInfo(Long userId, UserUpdateDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto 密码信息
     * @return 是否成功
     */
    Boolean changePassword(Long userId, UserChangePasswordDTO dto);

    /**
     * 用户登出
     *
     * @param token Token
     * @return 是否成功
     */
    Boolean logout(String token);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);

    /**
     * 根据手机号查找用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    User getByPhone(String phone);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    User getByEmail(String email);

    /**
     * 分页查询用户列表（管理员）
     *
     * @param page    分页参数
     * @param username 用户名
     * @param role    角色
     * @param status  状态
     * @param phone   手机号
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分页结果
     */
    Page<User> getUserList(Page<User> page, String username, String role, Integer status, String phone, String startDate, String endDate);
}
