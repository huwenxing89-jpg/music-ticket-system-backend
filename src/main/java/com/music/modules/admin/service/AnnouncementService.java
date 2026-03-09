package com.music.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.admin.dto.AnnouncementSaveDTO;
import com.music.modules.admin.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 *
 * @author 黄晓倩
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 获取所有启用的公告（置顶在前）
     *
     * @return 公告列表
     */
    List<Announcement> getActiveAnnouncements();

    /**
     * 保存公告（新增或更新）
     *
     * @param dto 保存参数
     * @return 是否成功
     */
    boolean saveAnnouncement(AnnouncementSaveDTO dto);

    /**
     * 更新公告
     *
     * @param id  公告ID
     * @param dto 保存参数
     * @return 是否成功
     */
    boolean updateAnnouncement(Long id, AnnouncementSaveDTO dto);

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 是否成功
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 启用/禁用公告
     *
     * @param id     公告ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 置顶/取消置顶公告
     *
     * @param id    公告ID
     * @param isTop 是否置顶
     * @return 是否成功
     */
    boolean updateTop(Long id, Integer isTop);
}
