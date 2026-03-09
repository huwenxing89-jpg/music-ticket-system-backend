package com.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 音乐剧购票系统主启动类
 *
 * @author 黄晓倩
 * @since 2024-03-05
 */
@SpringBootApplication
public class MusicTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicTicketApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("音乐剧购票系统启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/doc.html");
        System.out.println("========================================\n");
    }
}
