-- =====================================================
-- 修复show表结构，使其与Show实体类匹配
-- =====================================================
-- 添加票价字段和修改字段名

USE music_ticket_system;

-- 添加票价字段
ALTER TABLE `show`
ADD COLUMN `vip_price` DECIMAL(10,2) NULL COMMENT 'VIP票价格' AFTER `status`,
ADD COLUMN `normal_price` DECIMAL(10,2) NULL COMMENT '普通票价格' AFTER `vip_price`,
ADD COLUMN `student_price` DECIMAL(10,2) NULL COMMENT '学生票价格' AFTER `normal_price`,
ADD COLUMN `discount_price` DECIMAL(10,2) NULL COMMENT '优惠票价格' AFTER `student_price`;

-- 添加其他缺失字段
ALTER TABLE `show`
ADD COLUMN `type` INT NULL COMMENT '剧目类型：0-原创，1-引进，2-经典' AFTER `discount_price`,
ADD COLUMN `rating` DECIMAL(3,2) NULL COMMENT '评分（0-5分）' AFTER `type`;

-- 为现有剧目设置默认票价
UPDATE `show` SET
    vip_price = 680,
    normal_price = 380,
    student_price = 180,
    discount_price = 280
WHERE vip_price IS NULL OR normal_price IS NULL;

-- 添加测试图片路径（如果cover_image为空）
UPDATE `show` SET
    cover_image = '/uploads/shows/default-poster.jpg'
WHERE cover_image IS NULL OR cover_image = '';

SELECT '✅ show表结构修复完成！' AS result;
