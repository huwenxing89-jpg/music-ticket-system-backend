package com.music.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j接口文档配置
 *
 * @author 黄晓倩
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("音乐剧购票系统API文档")
                        .version("1.0.0")
                        .description("基于SpringBoot+Vue的音乐剧购票系统后端接口文档")
                        .contact(new Contact()
                                .name("黄晓倩")
                                .email("2205100223@example.com")
                                .url("https://github.com/huangxiaoqian"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
