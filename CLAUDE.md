# CLAUDE.md

本文件为跨会话共享的项目上下文。

## 项目简介

易拍（easy-photo）：拍照教程分享应用。详见 `docs/requirements.md`。

- 前端：Vue3 + Element Plus（主色调淡黄 + 莫兰迪/米白色系）
- 后端：Java 8 + Spring Boot + MySQL，认证使用 JWT
- 图片存储：服务器本地磁盘，数据库仅存路径/URL
- 所有表采用逻辑删除（`deleted` 标记位）

## 数据库连接

- 地址：`home.com:3306`（hosts 映射 `192.168.2.2`）
- 用户名：`root`
- 密码：`123456`
- 数据库名：待建（建库建表可按需求文档的数据模型自行创建）
- MySQL 版本：8.0.46
- mysql 客户端已加入系统 PATH，可直接使用 `mysql` 命令
- 连接示例：`mysql -h192.168.2.2 -P3306 -uroot -p123456`

> 已验证连接可用（2026-07-19）。现有库：information_schema、lan_file_storage、mysql、performance_schema、sys。

## Git

- 远程：`https://github.com/Ethan922/easy-photo.git`
- 分支：`master`（主）、`develop`（开发）
- 代理：使用 Clash，git 已配置 `http(s).proxy = http://127.0.0.1:7897`

## 分支约定

- 功能开发在 `develop` 分支进行。
- 仅在用户明确要求时提交/推送。
