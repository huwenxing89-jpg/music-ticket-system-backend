# 音乐剧购票系统后端

基于 Spring Boot + MyBatis-Plus + Spring Security + Redis 的音乐剧购票系统后端项目。

## 技术栈

- Spring Boot 2.7.18
- Spring Security
- MyBatis-Plus 3.5.5
- MySQL 8.0+
- Redis 6.0+
- JWT 0.11.5
- Knife4j 4.1.0
- Hutool 5.8.23

## 项目结构

```
music-ticket-system-backend/
├── src/main/java/com/music/
│   ├── MusicTicketApplication.java       # 主启动类
│   ├── common/                            # 通用模块
│   │   ├── config/                        # 配置类
│   │   ├── constants/                     # 常量定义
│   │   ├── dto/                           # 通用DTO
│   │   ├── exception/                     # 异常处理
│   │   ├── result/                        # 统一响应
│   │   └── utils/                         # 工具类
│   ├── security/                          # 安全模块
│   │   ├── config/                        # 安全配置
│   │   └── JwtAuthenticationFilter.java   # JWT过滤器
│   ├── task/                              # 定时任务
│   └── modules/                           # 业务模块
│       ├── admin/                         # 管理员模块
│       ├── comment/                       # 评论模块
│       ├── order/                         # 订单模块
│       ├── seat/                          # 座位模块
│       ├── show/                          # 剧目模块
│       ├── theater/                       # 剧院模块
│       └── user/                          # 用户模块
└── src/main/resources/
    ├── application.yml                    # 主配置文件
    ├── application-dev.yml                # 开发环境配置
    ├── application-prod.yml               # 生产环境配置
    └── mapper/                            # MyBatis XML映射文件
```

## 核心功能模块

### 基础框架

1. **统一响应格式** - Result<T>类，包含code、message、data字段
2. **全局异常处理** - GlobalExceptionHandler，处理各种异常类型
3. **Spring Security配置** - JWT认证、密码编码、CORS跨域、接口权限控制
4. **JWT工具类** - 生成Token、验证Token、解析Token获取用户信息
5. **Redis配置** - RedisTemplate配置、序列化方式、RedisUtil工具类
6. **文件上传功能** - 配置文件上传大小限制、本地文件存储、返回文件访问URL
7. **MyBatis-Plus配置** - 分页插件、逻辑删除、自动填充
8. **定时任务配置** - 每5分钟取消超时未支付订单、每日凌晨统计数据、定时清理过期数据

### 管理端模块

1. **评论管理模块**（/api/admin/comment/*）
   - 评论列表（分页、搜索、筛选）
   - 评论审核（通过/拒绝）
   - 删除评论
   - 标记优质评论

2. **轮播图管理模块**（/api/admin/carousel/*）
   - 轮播图列表
   - 添加轮播图
   - 编辑轮播图
   - 删除轮播图
   - 设置排序

3. **公告管理模块**（/api/admin/announcement/*）
   - 公告列表
   - 发布公告
   - 编辑公告
   - 删除公告
   - 置顶公告

4. **系统配置模块**（/api/admin/config/*）
   - 系统配置列表
   - 更新配置
   - 网站基本信息
   - 支付配置

5. **操作日志模块**（/api/admin/log/operator/*）
   - 操作日志列表（分页）
   - 日志详情查询
   - 自动记录管理员操作

6. **登录日志模块**（/api/admin/log/login/*）
   - 登录日志列表（分页）
   - 登录记录查询
   - 自动记录登录行为

7. **数据统计模块**（/api/admin/statistics/*）
   - 用户统计（注册用户数、活跃用户数、新增用户趋势）
   - 订单统计（订单量、销售额、订单状态分布）
   - 剧目统计（热门剧目排行、上座率分析）

## 快速开始

### 1. 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库配置

创建数据库并导入SQL脚本：

```sql
CREATE DATABASE music_ticket_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 修改配置文件

修改 `application-dev.yml` 中的数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/music_ticket_system
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 4. 启动项目

```bash
mvn clean install
mvn spring-boot:run
```

### 5. 访问接口文档

启动成功后，访问 Knife4j 接口文档：

```
http://localhost:8080/doc.html
```

## 预置账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 系统管理员 |
| 测试用户 | user | 123456 | 普通用户 |

## API路径规范

- /api/admin/comment/* - 评论管理
- /api/admin/carousel/* - 轮播图管理
- /api/admin/announcement/* - 公告管理
- /api/admin/config/* - 系统配置
- /api/admin/log/operator/* - 操作日志
- /api/admin/log/login/* - 登录日志
- /api/admin/statistics/* - 数据统计

## 开发说明

### 统一响应格式

所有接口返回统一的JSON格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1709606400000
}
```

### 异常处理

使用 `BusinessException` 抛出业务异常：

```java
throw new BusinessException("用户不存在");
```

### 日志记录

使用 `@Slf4j` 注解记录日志：

```java
@Slf4j
@Service
public class UserServiceImpl {
    public void method() {
        log.info("信息日志");
        log.error("错误日志");
    }
}
```

## 作者

黄晓倩（学号：2205100223）

## 许可证

Apache License 2.0
