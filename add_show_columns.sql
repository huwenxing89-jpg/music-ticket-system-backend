-- 为show表添加新字段
ALTER TABLE `show` 
ADD COLUMN `language` VARCHAR(50) NULL COMMENT '语言（如：中文、英文等）' AFTER `duration`,
ADD COLUMN `director` VARCHAR(100) NULL COMMENT '导演' AFTER `language`,
ADD COLUMN `playwright` VARCHAR(100) NULL COMMENT '编剧' AFTER `director`,
ADD COLUMN `premiere_date` DATE NULL COMMENT '首演时间' AFTER `playwright`;
