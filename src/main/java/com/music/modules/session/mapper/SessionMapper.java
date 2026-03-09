package com.music.modules.session.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.modules.session.entity.ShowSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 场次Mapper接口
 *
 * @author 黄晓倩
 */
@Mapper
public interface SessionMapper extends BaseMapper<ShowSession> {

    /**
     * 查询场次座位销售情况
     *
     * @param sessionId 场次ID
     * @return 座位销售情况 [总座位数, 已售座位数, 已锁座位数]
     */
    @Select("SELECT COUNT(*) FROM `seat` WHERE theater_id = (SELECT theater_id FROM `session` WHERE id = #{sessionId})")
    int getTotalSeats(@Param("sessionId") Long sessionId);
}
