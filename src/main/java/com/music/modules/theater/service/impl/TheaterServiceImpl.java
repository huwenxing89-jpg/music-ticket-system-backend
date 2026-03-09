package com.music.modules.theater.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.modules.theater.entity.Theater;
import com.music.modules.theater.mapper.TheaterMapper;
import com.music.modules.theater.service.TheaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 剧院服务实现类
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TheaterServiceImpl extends ServiceImpl<TheaterMapper, Theater> implements TheaterService {

    /**
     * 分页查询剧院列表
     */
    @Override
    public Page<Theater> getTheaterList(Page<Theater> page, String name, String address) {
        LambdaQueryWrapper<Theater> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Theater::getName, name);
        }

        if (StringUtils.isNotBlank(address)) {
            wrapper.like(Theater::getAddress, address);
        }

        wrapper.orderByDesc(Theater::getCreateTime);

        return page(page, wrapper);
    }
}
