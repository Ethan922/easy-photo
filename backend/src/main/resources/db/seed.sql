-- easy-photo 初始化数据
-- 依赖 schema.sql 已执行

USE easy_photo;

-- 说明：初始管理员账号（admin / admin123）由后端启动时的 DataInitializer 用
-- BCryptPasswordEncoder 生成密文后插入，避免脚本中硬编码不可验证的哈希。

-- 预置一级分类：风格 / 姿势 / 色调
INSERT INTO category (name)
SELECT '风格' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '风格' AND deleted = 0);
INSERT INTO category (name)
SELECT '姿势' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '姿势' AND deleted = 0);
INSERT INTO category (name)
SELECT '色调' WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '色调' AND deleted = 0);

-- 示例维度
INSERT INTO dimension (category_id, name)
SELECT c.id, d.name
FROM (SELECT id FROM category WHERE name = '风格' AND deleted = 0 LIMIT 1) c
JOIN (SELECT '生活风' AS name UNION SELECT '甜美风' UNION SELECT '忧郁风' UNION SELECT '自然风') d
WHERE NOT EXISTS (
  SELECT 1 FROM dimension x WHERE x.category_id = c.id AND x.name = d.name AND x.deleted = 0
);

INSERT INTO dimension (category_id, name)
SELECT c.id, d.name
FROM (SELECT id FROM category WHERE name = '姿势' AND deleted = 0 LIMIT 1) c
JOIN (SELECT '站立' AS name UNION SELECT '坐姿' UNION SELECT '半身' UNION SELECT '蹲姿') d
WHERE NOT EXISTS (
  SELECT 1 FROM dimension x WHERE x.category_id = c.id AND x.name = d.name AND x.deleted = 0
);

INSERT INTO dimension (category_id, name)
SELECT c.id, d.name
FROM (SELECT id FROM category WHERE name = '色调' AND deleted = 0 LIMIT 1) c
JOIN (SELECT '冷色调' AS name UNION SELECT '暖色调' UNION SELECT '绿色' UNION SELECT '蓝色') d
WHERE NOT EXISTS (
  SELECT 1 FROM dimension x WHERE x.category_id = c.id AND x.name = d.name AND x.deleted = 0
);
