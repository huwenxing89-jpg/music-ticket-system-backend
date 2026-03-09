package com.music.modules.show.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.show.entity.Show;

import java.math.BigDecimal;

/**
 * 剧目服务接口
 *
 * @author 黄晓倩
 */
public interface ShowService extends IService<Show> {

    /**
     * 分页查询剧目列表
     *
     * @param page    分页参数
     * @param keyword 搜索关键词（剧目名称）
     * @param type    剧目类型
     * @param status  剧目状态
     * @param category 剧目类别(字符串)
     * @param sort    排序方式
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 剧目分页列表
     */
    Page<Show> getShowList(Page<Show> page, String keyword, Integer type, Integer status,
                           String category, String sort, BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 获取剧目详情
     *
     * @param id 剧目ID
     * @return 剧目详情
     */
    Show getShowDetail(Long id);
}
