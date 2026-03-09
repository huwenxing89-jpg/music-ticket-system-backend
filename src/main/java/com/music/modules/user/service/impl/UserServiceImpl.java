package com.music.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.constants.SystemConstants;
import com.music.common.result.ResultCode;
import com.music.common.exception.BusinessException;
import com.music.common.utils.JwtUtil;
import com.music.modules.user.dto.*;
import com.music.modules.user.entity.User;
import com.music.modules.user.mapper.UserMapper;
import com.music.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类（修复版 - 移除Redis依赖）
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO dto) {
        // 验证确认密码
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR.getCode(), "两次密码不一致");
        }

        // 检查用户名是否已存在
        if (getByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_EXIST);
        }

        // 检查手机号是否已注册
        if (dto.getPhone() != null && getByPhone(dto.getPhone()) != null) {
            throw new BusinessException(ResultCode.ERROR.getCode(), "手机号已被注册");
        }

        // 检查邮箱是否已注册
        if (dto.getEmail() != null && getByEmail(dto.getEmail()) != null) {
            throw new BusinessException(ResultCode.ERROR.getCode(), "邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("user"); // 默认为普通用户
        user.setStatus(0); // 0-正常

        int result = userMapper.insert(user);
        log.info("用户注册成功：username={}", dto.getUsername());
        return user.getId();
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        // 查找用户（支持用户名、手机号、邮箱登录）
        User user = findByUsernameOrPhoneOrEmail(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_DISABLE);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(buildUserVO(user));

        log.info("用户登录成功：username={}", user.getUsername());
        return loginVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        return buildUserVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        // 更新用户信息
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            // 检查手机号是否被其他用户使用
            User existingUser = getByPhone(dto.getPhone());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException(ResultCode.ERROR.getCode(), "手机号已被其他用户使用");
            }
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            User existingUser = getByEmail(dto.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException(ResultCode.ERROR.getCode(), "邮箱已被其他用户使用");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }

        int result = userMapper.updateById(user);
        log.info("更新用户信息成功：userId={}", userId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(Long userId, UserChangePasswordDTO dto) {
        // 验证确认密码
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR.getCode(), "两次密码不一致");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }

        // 验证原密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR.getCode(), "原密码错误");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        int result = userMapper.updateById(updateUser);

        log.info("用户修改密码成功：userId={}", userId);
        return result > 0;
    }

    @Override
    public Boolean logout(String token) {
        try {
            // TODO: 实现Token黑名单或清除缓存
            log.info("用户登出成功");
            return true;
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return false;
        }
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User getByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 根据用户名、手机号或邮箱查找用户
     *
     * @param username 用户名/手机号/邮箱
     * @return 用户实体
     */
    private User findByUsernameOrPhoneOrEmail(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .or().eq(User::getPhone, username)
                .or().eq(User::getEmail, username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 构建用户VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO buildUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 不返回密码
        return userVO;
    }

    /**
     * 分页查询用户列表（管理员）
     */
    @Override
    public Page<User> getUserList(Page<User> page, String username, String role, Integer status,
                                  String phone, String startDate, String endDate) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(User::getUsername, username);
        }

        if (role != null && !role.trim().isEmpty()) {
            wrapper.eq(User::getRole, role);
        }

        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        // 手机号搜索
        if (phone != null && !phone.trim().isEmpty()) {
            wrapper.like(User::getPhone, phone);
        }

        // 日期范围搜索（按创建时间）
        if (startDate != null && !startDate.trim().isEmpty()) {
            wrapper.ge(User::getCreateTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            wrapper.le(User::getCreateTime, endDate + " 23:59:59");
        }

        wrapper.orderByDesc(User::getCreateTime);

        return page(page, wrapper);
    }
}
