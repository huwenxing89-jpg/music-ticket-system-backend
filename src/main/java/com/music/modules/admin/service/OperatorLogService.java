package com.music.modules.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.admin.dto.OperatorLogQueryDTO;
import com.music.modules.admin.entity.OperatorLog;

/**
 * 操作日志服务接口
 *
 * @author 黄晓倩
 */
public interface OperatorLogService extends IService<OperatorLog> {

    /**
     * 分页查询操作日志
     *
     * @param dto 查询参数
     * @return 操作日志列表
     */
    IPage<OperatorLog> getOperatorLogPage(OperatorLogQueryDTO dto);

    /**
     * 记录操作日志
     *
     * @param log 操作日志
     * @return 是否成功
     */
    boolean saveOperatorLog(OperatorLog log);
}
