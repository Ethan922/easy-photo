## Context

易拍为全新项目，无既有代码。技术栈已在需求文档中确定：后端 Java 8 + Spring Boot + MySQL（8.0.46，`home.com:3306`），前端 Vue3 + Element Plus。认证用 JWT，图片存本地磁盘，所有表逻辑删除。本设计确定架构分层、数据模型、鉴权与图片存储方案，为实现提供依据。

## Goals / Non-Goals

**Goals:**
- 建立前后端分离的可运行初始版本，覆盖用户端与管理端核心功能。
- 确定数据库 schema（7 张表，均含 `deleted`）与唯一性约束策略。
- 明确 JWT 鉴权、角色授权、密码加密方案。
- 明确本地磁盘图片存储与访问方案。

**Non-Goals:**
- 不做第三方/社交登录、邮箱验证、找回密码。
- 不做对象存储/CDN；不做全文搜索、推荐、评论。
- 不做分页外的高级检索与缓存优化（后续迭代）。

## Decisions

### 分层架构
后端采用 Controller → Service → Mapper(DAO) 分层，DTO 与实体分离。选用 **MyBatis-Plus**：其内置逻辑删除（`@TableLogic`）、分页插件与条件构造器，契合本项目「全表逻辑删除 + 简单 CRUD」的需求，比 JPA 更贴合 SQL 直觉。备选 Spring Data JPA 被否，因逻辑删除与动态查询处理更繁琐。

### 数据模型
7 张表：`user`、`category`、`dimension`、`tutorial`、`tutorial_dimension`、`tutorial_favorite`、`tutorial_like`（`favorite`、`like` 统一加 `tutorial_` 前缀，其中 `like` 亦为 MySQL 关键字，前缀命名一并规避）。教程与维度多对多经 `tutorial_dimension`。所有表含 `deleted TINYINT DEFAULT 0`。
- 唯一性：`category.name`、`dimension(category_id, name)` 仅对未删除记录唯一。MySQL 不支持带条件的唯一索引，故采用**应用层校验 + 普通索引**，不使用数据库唯一约束，避免与软删除历史记录冲突。
- `tutorial` 冗余 `like_count`、`favorite_count` 统计字段，互动时增量维护。

### 认证与授权
Spring Security + 自定义 JWT 过滤器。登录签发 JWT（含 userId、role、过期时间），后续请求经 `Authorization: Bearer <token>` 校验。密码 BCrypt 加密。基于角色（`user` / `admin`）做方法级授权（`@PreAuthorize` 或过滤器判定），管理端接口限 `admin`。

### 图片存储
上传接口将文件写入配置的本地目录（如 `${app.upload-dir}`），按日期/UUID 生成文件名，返回相对访问 URL（如 `/uploads/2026/07/xxx.jpg`）。Spring 静态资源映射该目录对外提供读取。富文本正文中的图片同走此上传接口，正文存返回的 URL。

### 富文本
正文以 HTML 字符串存 `tutorial.content`（`LONGTEXT`）。前端用富文本编辑器（如 wangEditor），图片经上传接口获得 URL 后插入。后端对 HTML 做基础清洗（防 XSS）。

### 前端结构
Vue3 + Vite + Vue Router + Pinia + Axios。Element Plus 组件库，主题定制为淡黄 + 莫兰迪/米白色系。区分用户端路由与管理端路由，路由守卫按登录态与角色控制访问。

## Risks / Trade-offs

- **软删除下的唯一性靠应用层校验** → 并发下可能产生重复；对 `name` 加普通索引并在 Service 层查重，必要时用数据库层「未删除部分唯一」的替代（如生成列）后续再优化。
- **XSS（富文本存 HTML）** → 后端存储前用白名单清洗（如 jsoup），前端渲染受控。
- **本地磁盘存图不利于水平扩展/备份** → 当前单机可接受；目录与访问路径通过配置隔离，后续可替换为对象存储。
- **统计字段与关系表可能不一致** → 增减在同一事务内完成；提供校对手段兜底。
- **表名统一前缀** → `favorite`、`like` 统一命名为 `tutorial_favorite`、`tutorial_like`，同时规避 `like` 关键字冲突。

## Migration Plan

1. 建库 `easy_photo`，按上述 schema 建 7 张表及索引（初始化 SQL 脚本纳入仓库）。
2. 预置管理员账号与初始分类（风格/姿势/色调）及示例维度（可选 seed 脚本）。
3. 后端提供本地图片目录并配置静态资源映射；前端配置 API 代理。
4. 回滚：初始版本无存量数据，回滚即重建库或删表重来。

## Open Questions

- 教程列表分页与排序默认规则（按时间倒序，页大小待定）。
- 富文本清洗白名单的具体标签/属性范围。
- 管理员账号的初始化方式（seed 脚本写死 or 首次启动生成）。
