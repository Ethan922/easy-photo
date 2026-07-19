# image-storage

## Purpose

定义图片上传至服务器本地磁盘、数据库仅存路径、以及图片访问的行为。

## Requirements

### Requirement: 图片上传至本地磁盘
系统 SHALL 接收登录用户上传的图片（封面及正文内嵌图），MUST 将图片文件存储于服务器本地磁盘，数据库仅保存相对路径 / 访问 URL，MUST NOT 将图片二进制存入数据库。

#### Scenario: 上传图片
- **WHEN** 登录用户上传一张图片
- **THEN** 系统将文件写入本地磁盘并返回其访问路径 / URL

#### Scenario: 数据库仅存路径
- **WHEN** 图片上传成功后教程保存
- **THEN** 数据库中记录的是图片路径 / URL 而非二进制内容

### Requirement: 图片访问
系统 SHALL 通过访问路径 / URL 提供已上传图片的读取。

#### Scenario: 访问已上传图片
- **WHEN** 客户端请求某图片的访问路径
- **THEN** 系统返回对应的图片内容
