package com.music.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.modules.admin.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 登录日志Mapper
 *
 * @author 黄晓倩
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 分页查询登录日志（带搜索条件）
     *
     * @param page     分页对象
     * @param username 用户名（可选）
     * @param status   登录状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 登录日志列表
     */
    IPage<LoginLog> selectLoginLogPage(Page<LoginLog> page,
                                        @Param("username") String username,
                                        @Param("status") Integer status,
                                        @Param("startTime") String startTime,
                                        @Param("endTime") String endTime);
}
