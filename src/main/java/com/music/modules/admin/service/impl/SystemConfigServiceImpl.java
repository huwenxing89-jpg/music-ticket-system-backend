package com.music.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.common.exception.BusinessException;
import com.music.modules.admin.dto.SystemConfigUpdateDTO;
import com.music.modules.admin.entity.SystemConfig;
import com.music.modules.admin.mapper.SystemConfigMapper;
import com.music.modules.admin.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务实现
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    @Override
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public Map<String, String> getConfigMapByGroup(String configGroup) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigGroup, configGroup);
        List<SystemConfig> list = systemConfigMapper.selectList(wrapper);

        Map<String, String> map = new HashMap<>();
        for (SystemConfig config : list) {
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(SystemConfigUpdateDTO dto) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, dto.getConfigKey());
        SystemConfig config = systemConfigMapper.selectOne(wrapper);

        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        config.setConfigValue(dto.getConfigValue());
        int result = systemConfigMapper.updateById(config);
        log.info("更新系统配置：key={}, value={}", dto.getConfigKey(), dto.getConfigValue());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateConfig(Map<String, String> configMap) {
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();

            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, configKey);
            SystemConfig config = systemConfigMapper.selectOne(wrapper);

            if (config != null) {
                config.setConfigValue(configValue);
                systemConfigMapper.updateById(config);
            }
        }
        log.info("批量更新系统配置：{}", configMap);
        return true;
    }

    @Override
    public Map<String, Map<String, String>> getAllConfigByGroup() {
        List<SystemConfig> allConfigs = systemConfigMapper.selectList(null);
        Map<String, Map<String, String>> result = new HashMap<>();

        for (SystemConfig config : allConfigs) {
            String group = config.getConfigGroup();
            if (group == null || group.isEmpty()) {
                group = "other";
            }

            result.computeIfAbsent(group, k -> new HashMap<>())
                   .put(config.getConfigKey(), config.getConfigValue());
        }

        return result;
    }
}
