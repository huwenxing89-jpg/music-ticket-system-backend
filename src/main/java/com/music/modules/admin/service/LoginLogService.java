package com.music.modules.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.admin.dto.LoginLogQueryDTO;
import com.music.modules.admin.entity.LoginLog;

/**
 * 登录日志服务接口
 *
 * @author 黄晓倩
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 分页查询登录日志
     *
     * @param dto 查询参数
     * @return 登录日志列表
     */
    IPage<LoginLog> getLoginLogPage(LoginLogQueryDTO dto);

    /**
     * 记录登录日志
     *
     * @param log 登录日志
     * @return 是否成功
     */
    boolean saveLoginLog(LoginLog log);
}
