package com.music.modules.theater.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.theater.entity.Theater;

/**
 * 剧院服务接口
 *
 * @author 黄晓倩
 */
public interface TheaterService extends IService<Theater> {

    /**
     * 分页查询剧院列表
     *
     * @param page    分页参数
     * @param name    剧院名称（可选）
     * @param address  地址（可选）
     * @return 分页结果
     */
    Page<Theater> getTheaterList(Page<Theater> page, String name, String address);
}
