-- easy-photo 数据库初始化脚本
-- MySQL 8.0.x
-- 所有表采用逻辑删除（deleted：0=未删除，1=已删除）
-- 唯一性（category.name、dimension(category_id,name)）在应用层校验，仅普通索引

CREATE DATABASE IF NOT EXISTS easy_photo
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE easy_photo;

-- 用户
CREATE TABLE IF NOT EXISTS `user` (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  username    VARCHAR(64)  NOT NULL COMMENT '用户名（未删除范围内唯一，应用层校验）',
  password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 密文',
  role        VARCHAR(16)  NOT NULL DEFAULT 'user' COMMENT 'user / admin',
  deleted     TINYINT      NOT NULL DEFAULT 0,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- 一级分类
CREATE TABLE IF NOT EXISTS category (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(64)  NOT NULL COMMENT '分类名（未删除范围内全局唯一，应用层校验）',
  deleted     TINYINT      NOT NULL DEFAULT 0,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='一级分类';

-- 二级维度
CREATE TABLE IF NOT EXISTS dimension (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  category_id BIGINT       NOT NULL COMMENT '所属分类',
  name        VARCHAR(64)  NOT NULL COMMENT '维度名（同分类内未删除范围唯一，应用层校验）',
  deleted     TINYINT      NOT NULL DEFAULT 0,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_dimension_category (category_id),
  KEY idx_dimension_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='二级维度';

-- 教程
CREATE TABLE IF NOT EXISTS tutorial (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  title          VARCHAR(128)  NOT NULL COMMENT '标题',
  cover_url      VARCHAR(255)  NOT NULL COMMENT '封面图访问路径',
  content        LONGTEXT      NOT NULL COMMENT '富文本正文（HTML）',
  visibility     VARCHAR(16)   NOT NULL DEFAULT 'public' COMMENT 'public / private',
  author_id      BIGINT        NOT NULL COMMENT '作者用户 id',
  like_count     INT           NOT NULL DEFAULT 0 COMMENT '点赞数（冗余统计）',
  favorite_count INT           NOT NULL DEFAULT 0 COMMENT '收藏数（冗余统计）',
  deleted        TINYINT       NOT NULL DEFAULT 0,
  created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_tutorial_author (author_id),
  KEY idx_tutorial_visibility (visibility)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教程';

-- 教程-维度关系（多对多）
CREATE TABLE IF NOT EXISTS tutorial_dimension (
  id           BIGINT   NOT NULL AUTO_INCREMENT,
  tutorial_id  BIGINT   NOT NULL,
  dimension_id BIGINT   NOT NULL,
  deleted      TINYINT  NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_td_tutorial (tutorial_id),
  KEY idx_td_dimension (dimension_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教程-维度关系';

-- 收藏
CREATE TABLE IF NOT EXISTS tutorial_favorite (
  id          BIGINT    NOT NULL AUTO_INCREMENT,
  user_id     BIGINT    NOT NULL,
  tutorial_id BIGINT    NOT NULL,
  deleted     TINYINT   NOT NULL DEFAULT 0,
  created_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_fav_user (user_id),
  KEY idx_fav_tutorial (tutorial_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏';

-- 点赞
CREATE TABLE IF NOT EXISTS tutorial_like (
  id          BIGINT    NOT NULL AUTO_INCREMENT,
  user_id     BIGINT    NOT NULL,
  tutorial_id BIGINT    NOT NULL,
  deleted     TINYINT   NOT NULL DEFAULT 0,
  created_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_like_user (user_id),
  KEY idx_like_tutorial (tutorial_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞';
