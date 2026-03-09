package com.music.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置
 *
 * @author 黄晓倩
 */
@Configuration
@EnableScheduling
public class TaskConfig {
    // 定时任务配置类，启用Spring Task调度功能
}
