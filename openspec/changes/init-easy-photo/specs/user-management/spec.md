## ADDED Requirements

### Requirement: 管理员管理用户
系统 SHALL 允许管理员新增和删除用户。删除采用逻辑删除。新增用户时用户名 MUST 全局唯一（未删除范围内），密码 MUST 加密存储。

#### Scenario: 新增用户
- **WHEN** 管理员提交未被占用的用户名和密码新增用户
- **THEN** 系统创建该用户，密码以 BCrypt 密文存储

#### Scenario: 删除用户
- **WHEN** 管理员删除某个用户
- **THEN** 系统将该用户标记为已删除，默认查询不再返回

#### Scenario: 非管理员访问用户管理
- **WHEN** 普通用户尝试新增或删除用户
- **THEN** 系统返回禁止访问错误
