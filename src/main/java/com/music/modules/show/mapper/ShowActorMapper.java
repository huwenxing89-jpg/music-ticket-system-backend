package com.music.modules.show.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.modules.show.entity.ShowActor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 剧目演员关联Mapper
 *
 * @author 黄晓倩
 */
@Mapper
public interface ShowActorMapper extends BaseMapper<ShowActor> {
}
