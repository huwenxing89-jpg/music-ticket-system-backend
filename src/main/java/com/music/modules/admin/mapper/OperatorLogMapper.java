package com.music.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.modules.admin.entity.OperatorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 操作日志Mapper
 *
 * @author 黄晓倩
 */
@Mapper
public interface OperatorLogMapper extends BaseMapper<OperatorLog> {

    /**
     * 分页查询操作日志（带搜索条件）
     *
     * @param page         分页对象
     * @param username     用户名（可选）
     * @param module       模块（可选）
     * @param operationType 操作类型（可选）
     * @param startTime    开始时间（可选）
     * @param endTime      结束时间（可选）
     * @return 操作日志列表
     */
    IPage<OperatorLog> selectOperatorLogPage(Page<OperatorLog> page,
                                              @Param("username") String username,
                                              @Param("module") String module,
                                              @Param("operationType") Integer operationType,
                                              @Param("startTime") String startTime,
                                              @Param("endTime") String endTime);
}
