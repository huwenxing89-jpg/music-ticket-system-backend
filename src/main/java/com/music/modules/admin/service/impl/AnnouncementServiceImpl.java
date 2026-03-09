package com.music.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.admin.dto.AnnouncementSaveDTO;
import com.music.modules.admin.entity.Announcement;
import com.music.modules.admin.mapper.AnnouncementMapper;
import com.music.modules.admin.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> getActiveAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAnnouncement(AnnouncementSaveDTO dto) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(dto, announcement);
        // 默认为系统公告
        if (announcement.getType() == null) {
            announcement.setType(1);
        }
        // 默认启用
        if (announcement.getStatus() == null) {
            announcement.setStatus(1);
        }
        // 默认不置顶
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(0);
        }
        int result = announcementMapper.insert(announcement);
        log.info("新增公告：title={}", dto.getTitle());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAnnouncement(Long id, AnnouncementSaveDTO dto) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }

        Announcement updateAnnouncement = new Announcement();
        BeanUtils.copyProperties(dto, updateAnnouncement);
        updateAnnouncement.setId(id);
        int result = announcementMapper.updateById(updateAnnouncement);
        log.info("更新公告：id={}, title={}", id, dto.getTitle());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnnouncement(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }

        int result = announcementMapper.deleteById(id);
        log.info("删除公告：id={}", id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }

        announcement.setStatus(status);
        int result = announcementMapper.updateById(announcement);
        log.info("更新公告状态：id={}, status={}", id, status);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTop(Long id, Integer isTop) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }

        announcement.setIsTop(isTop);
        int result = announcementMapper.updateById(announcement);
        log.info("更新公告置顶状态：id={}, isTop={}", id, isTop);
        return result > 0;
    }
}
