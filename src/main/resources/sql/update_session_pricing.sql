-- 为session表设置票价数据（从show表复制）
USE music_ticket_system;

UPDATE session s
INNER JOIN `show` sh ON s.show_id = sh.id
SET
    s.vip_price = sh.vip_price,
    s.normal_price = sh.normal_price,
    s.student_price = sh.student_price,
    s.discount_price = sh.discount_price
WHERE s.vip_price IS NULL OR s.normal_price IS NULL;

-- 验证更新结果
SELECT id, show_id, vip_price, normal_price, student_price, discount_price
FROM session
LIMIT 5;

SELECT '✅ session表票价更新完成！' AS result;
