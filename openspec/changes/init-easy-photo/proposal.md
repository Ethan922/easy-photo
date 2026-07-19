## Why

易拍（easy-photo）是一款拍照教程分享应用，用户可按风格、姿势、色调等维度浏览、学习并分享拍照与修图技巧。目前项目仅有需求文档，尚无任何实现。本次变更建立从零到可用的初始版本，交付管理端与用户端的核心功能。

## What Changes

- 新增用户注册 / 登录能力，基于 JWT 鉴权，密码 BCrypt 加密存储；区分普通用户与管理员角色。
- 新增分类体系：两级结构（分类 + 维度，两张表），管理员可自定义增删；分类名全局唯一、同分类下维度名唯一；均为逻辑删除。
- 新增教程管理：富文本正文（图文混排），封面图，多维度标签（教程-维度多对多关系表），public / private 可见范围；作者自管 + 管理员全权。
- 新增用户端浏览：分类 Tab 筛选、教程卡片列表、教程详情。
- 新增互动能力：收藏、点赞（可取消）及收藏页。
- 新增管理端：分类管理、用户管理、教程管理（含 private）。
- 新增图片存储：图片存服务器本地磁盘，数据库仅存路径 / URL。
- 所有数据表统一采用逻辑删除（`deleted` 标记位）。

## Capabilities

### New Capabilities
- `user-auth`: 用户注册、登录、JWT 签发与校验、角色鉴权、密码加密。
- `category-management`: 分类与维度的增删查、唯一性约束、逻辑删除（管理员）。
- `tutorial-management`: 教程的创建、编辑、删除、富文本正文与维度关联、可见范围控制（作者自管 + 管理员全权）。
- `tutorial-browsing`: 分类 Tab 筛选、教程卡片列表、教程详情、可见性过滤。
- `engagement`: 收藏、点赞及取消、收藏页展示、统计计数。
- `user-management`: 管理员新增 / 删除用户。
- `image-storage`: 图片上传至本地磁盘、路径持久化与访问。

### Modified Capabilities
<!-- 无：项目为全新初始化，不存在既有 spec -->

## Impact

- 全新代码库：后端 Java 8 + Spring Boot + MySQL；前端 Vue3 + Element Plus。
- 数据库：新建 `easy_photo` 库及 user、category、dimension、tutorial、tutorial_dimension、favorite、like 等表（均含 `deleted`）。
- 依赖：Spring Boot、Spring Security/JWT 库、MyBatis 或 JPA、mysql-connector-j；前端 Vue3、Element Plus、Vue Router、Pinia、Axios、富文本编辑器。
- 部署：后端提供本地磁盘图片目录并对外暴露访问路径；前端通过代理访问后端 API。
