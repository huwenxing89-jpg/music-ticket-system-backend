package com.music.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.modules.admin.dto.SystemConfigUpdateDTO;
import com.music.modules.admin.entity.SystemConfig;

import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author 黄晓倩
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置分组获取配置Map
     *
     * @param configGroup 配置分组
     * @return 配置Map
     */
    Map<String, String> getConfigMapByGroup(String configGroup);

    /**
     * 更新配置
     *
     * @param dto 更新参数
     * @return 是否成功
     */
    boolean updateConfig(SystemConfigUpdateDTO dto);

    /**
     * 批量更新配置
     *
     * @param configMap 配置Map (key为配置键，value为配置值)
     * @return 是否成功
     */
    boolean batchUpdateConfig(Map<String, String> configMap);

    /**
     * 获取所有配置（按分组返回）
     *
     * @return 配置Map (key为分组名，value为该分组的配置Map)
     */
    Map<String, Map<String, String>> getAllConfigByGroup();
}
