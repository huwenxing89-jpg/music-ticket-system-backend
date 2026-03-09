package com.music.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.admin.dto.CarouselSaveDTO;
import com.music.modules.admin.dto.CarouselSortDTO;
import com.music.modules.admin.entity.Carousel;

import java.util.List;

/**
 * 轮播图服务接口
 *
 * @author 黄晓倩
 */
public interface CarouselService extends IService<Carousel> {

    /**
     * 获取所有启用的轮播图（按排序）
     *
     * @return 轮播图列表
     */
    List<Carousel> getActiveCarousels();

    /**
     * 保存轮播图（新增或更新）
     *
     * @param dto 保存参数
     * @return 是否成功
     */
    boolean saveCarousel(CarouselSaveDTO dto);

    /**
     * 更新轮播图
     *
     * @param id  轮播图ID
     * @param dto 保存参数
     * @return 是否成功
     */
    boolean updateCarousel(Long id, CarouselSaveDTO dto);

    /**
     * 删除轮播图
     *
     * @param id 轮播图ID
     * @return 是否成功
     */
    boolean deleteCarousel(Long id);

    /**
     * 启用/禁用轮播图
     *
     * @param id     轮播图ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 批量更新轮播图排序
     *
     * @param sortList 排序列表
     * @return 是否成功
     */
    boolean updateSort(List<CarouselSortDTO> sortList);
}
