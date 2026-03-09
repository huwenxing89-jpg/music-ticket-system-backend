-- 为session表添加票价字段
USE music_ticket_system;

-- 添加票价字段
ALTER TABLE `session`
ADD COLUMN `vip_price` DECIMAL(10,2) NULL COMMENT 'VIP票价（覆盖剧目票价）' AFTER `price`,
ADD COLUMN `normal_price` DECIMAL(10,2) NULL COMMENT '普通票价（覆盖剧目票价）' AFTER `vip_price`,
ADD COLUMN `student_price` DECIMAL(10,2) NULL COMMENT '学生票价（覆盖剧目票价）' AFTER `normal_price`,
ADD COLUMN `discount_price` DECIMAL(10,2) NULL COMMENT '优惠票价（覆盖剧目票价）' AFTER `student_price`,
ADD COLUMN `price_types` VARCHAR(100) NULL COMMENT '票价类型（vip,normal,student,discount）' AFTER `discount_price`;

-- 添加show_time和duration字段（如果还没有）
ALTER TABLE `session`
ADD COLUMN `show_time` DATETIME NULL COMMENT '演出时间' AFTER `theater_id`,
ADD COLUMN `duration` INT NULL COMMENT '时长（分钟）' AFTER `show_time`;

-- 添加totalSeats和soldSeats字段（如果还没有）
ALTER TABLE `session`
ADD COLUMN `total_seats` INT NULL DEFAULT 0 COMMENT '总座位数' AFTER `discount_price`,
ADD COLUMN `sold_seats` INT NULL DEFAULT 0 COMMENT '已售座位数' AFTER `total_seats`;

-- 添加cancel_reason字段（如果还没有）
ALTER TABLE `session`
ADD COLUMN `cancel_reason` VARCHAR(255) NULL COMMENT '取消原因' AFTER `sold_seats`;

-- 从剧目表复制价格到场次表（为null的场次）
UPDATE `session` s
INNER JOIN `show` sh ON s.show_id = sh.id
SET
    s.vip_price = sh.vip_price,
    s.normal_price = sh.normal_price,
    s.student_price = sh.student_price,
    s.discount_price = sh.discount_price
WHERE s.vip_price IS NULL OR s.normal_price IS NULL;

-- 验证结果
SELECT id, show_id, show_time, vip_price, normal_price, student_price, discount_price, price_types
FROM `session`
LIMIT 5;

SELECT '✅ session表票价字段添加完成！' AS result;
