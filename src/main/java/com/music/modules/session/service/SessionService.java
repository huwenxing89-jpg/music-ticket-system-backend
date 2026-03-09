package com.music.modules.session.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.session.entity.ShowSession;

import java.util.Map;

/**
 * 场次服务接口
 *
 * @author 黄晓倩
 */
public interface SessionService extends IService<ShowSession> {

    /**
     * 分页查询场次列表
     *
     * @param page      分页对象
     * @param showId    剧目ID
     * @param theaterId 剧院ID
     * @param status    状态
     * @return 场次列表
     */
    IPage<ShowSession> getSessionList(Page<ShowSession> page, Long showId, Long theaterId, Integer status);

    /**
     * 获取场次详情
     *
     * @param id 场次ID
     * @return 场次详情
     */
    ShowSession getSessionDetail(Long id);

    /**
     * 创建场次
     *
     * @param session 场次信息
     * @return 是否成功
     */
    boolean createSession(ShowSession session);

    /**
     * 更新场次
     *
     * @param id      场次ID
     * @param session 场次信息
     * @return 是否成功
     */
    boolean updateSession(Long id, ShowSession session);

    /**
     * 取消场次
     *
     * @param id     场次ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelSession(Long id, String reason);

    /**
     * 获取场次座位销售情况
     *
     * @param sessionId 场次ID
     * @return 座位销售情况
     */
    Map<String, Object> getSessionSeatStatus(Long sessionId);
}
