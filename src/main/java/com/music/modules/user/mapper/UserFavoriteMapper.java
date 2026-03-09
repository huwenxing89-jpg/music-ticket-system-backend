package com.music.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.modules.user.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏Mapper接口
 *
 * @author 黄晓倩
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
