package com.music.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.admin.dto.CarouselSaveDTO;
import com.music.modules.admin.dto.CarouselSortDTO;
import com.music.modules.admin.entity.Carousel;
import com.music.modules.admin.mapper.CarouselMapper;
import com.music.modules.admin.service.CarouselService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮播图服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    private final CarouselMapper carouselMapper;

    @Override
    public List<Carousel> getActiveCarousels() {
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Carousel::getStatus, 1)
                .orderByAsc(Carousel::getSort);
        return carouselMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCarousel(CarouselSaveDTO dto) {
        Carousel carousel = new Carousel();
        BeanUtils.copyProperties(dto, carousel);
        // 默认排序为0
        if (carousel.getSort() == null) {
            carousel.setSort(0);
        }
        // 默认启用
        if (carousel.getStatus() == null) {
            carousel.setStatus(1);
        }
        int result = carouselMapper.insert(carousel);
        log.info("新增轮播图：title={}", dto.getTitle());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCarousel(Long id, CarouselSaveDTO dto) {
        Carousel carousel = carouselMapper.selectById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }

        Carousel updateCarousel = new Carousel();
        BeanUtils.copyProperties(dto, updateCarousel);
        updateCarousel.setId(id);
        int result = carouselMapper.updateById(updateCarousel);
        log.info("更新轮播图：id={}, title={}", id, dto.getTitle());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCarousel(Long id) {
        Carousel carousel = carouselMapper.selectById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }

        int result = carouselMapper.deleteById(id);
        log.info("删除轮播图：id={}", id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Carousel carousel = carouselMapper.selectById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }

        carousel.setStatus(status);
        int result = carouselMapper.updateById(carousel);
        log.info("更新轮播图状态：id={}, status={}", id, status);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSort(List<CarouselSortDTO> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return true;
        }

        // 批量更新排序
        List<Carousel> carousels = sortList.stream().map(dto -> {
            Carousel carousel = new Carousel();
            carousel.setId(dto.getId());
            carousel.setSort(dto.getSort());
            return carousel;
        }).collect(Collectors.toList());

        // 使用批量更新
        carousels.forEach(carouselMapper::updateById);
        log.info("批量更新轮播图排序，数量：{}", sortList.size());
        return true;
    }
}
