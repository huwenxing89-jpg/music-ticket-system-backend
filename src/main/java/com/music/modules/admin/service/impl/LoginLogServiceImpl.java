package com.music.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.modules.admin.dto.LoginLogQueryDTO;
import com.music.modules.admin.entity.LoginLog;
import com.music.modules.admin.mapper.LoginLogMapper;
import com.music.modules.admin.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Override
    public IPage<LoginLog> getLoginLogPage(LoginLogQueryDTO dto) {
        Page<LoginLog> page = new Page<>(dto.getCurrent(), dto.getSize());
        return loginLogMapper.selectLoginLogPage(
                page,
                dto.getUsername(),
                dto.getStatus(),
                dto.getStartTime(),
                dto.getEndTime()
        );
    }

    @Override
    public boolean saveLoginLog(LoginLog loginLog) {
        int result = loginLogMapper.insert(loginLog);
        log.debug("保存登录日志：username={}, status={}", loginLog.getUsername(), loginLog.getStatus());
        return result > 0;
    }
}
