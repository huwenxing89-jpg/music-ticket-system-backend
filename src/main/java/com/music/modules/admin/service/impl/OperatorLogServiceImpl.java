package com.music.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.modules.admin.dto.OperatorLogQueryDTO;
import com.music.modules.admin.entity.OperatorLog;
import com.music.modules.admin.mapper.OperatorLogMapper;
import com.music.modules.admin.service.OperatorLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperatorLogServiceImpl extends ServiceImpl<OperatorLogMapper, OperatorLog> implements OperatorLogService {

    private final OperatorLogMapper operatorLogMapper;

    @Override
    public IPage<OperatorLog> getOperatorLogPage(OperatorLogQueryDTO dto) {
        Page<OperatorLog> page = new Page<>(dto.getCurrent(), dto.getSize());
        return operatorLogMapper.selectOperatorLogPage(
                page,
                dto.getUsername(),
                dto.getModule(),
                dto.getOperationType(),
                dto.getStartTime(),
                dto.getEndTime()
        );
    }

    @Override
    public boolean saveOperatorLog(OperatorLog operatorLog) {
        int result = operatorLogMapper.insert(operatorLog);
        log.debug("保存操作日志：module={}, description={}", operatorLog.getModule(), operatorLog.getDescription());
        return result > 0;
    }
}
