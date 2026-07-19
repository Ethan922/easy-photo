## ADDED Requirements

### Requirement: 分类 Tab 浏览与筛选
系统 SHALL 以 Tab 形式展示一级分类，选中分类后展示其下维度，选中维度后展示该维度下的教程卡片列表。系统 MUST 支持按任一分类的维度独立筛选。

#### Scenario: 按维度筛选教程
- **WHEN** 用户选中某一级分类下的某个维度
- **THEN** 系统展示关联该维度且当前用户可见的教程卡片列表

#### Scenario: 卡片展示内容
- **WHEN** 系统展示教程卡片列表
- **THEN** 每张卡片 MUST 至少显示封面图与标题

### Requirement: 教程详情浏览
系统 SHALL 提供教程详情，展示元数据、富文本正文与作者信息，并展示点赞数、收藏数。

#### Scenario: 查看教程详情
- **WHEN** 用户打开一个对其可见的教程
- **THEN** 系统展示标题、封面、富文本正文、作者、点赞数与收藏数

### Requirement: 列表与详情的可见性过滤
系统 SHALL 在浏览列表与详情时应用可见性规则：游客与普通用户仅能看到 public 教程及本人的 private 教程；管理员可看到全部教程。

#### Scenario: 游客浏览列表
- **WHEN** 游客浏览教程列表
- **THEN** 系统仅返回 public 教程

#### Scenario: 用户浏览含本人 private
- **WHEN** 登录用户浏览列表
- **THEN** 系统返回 public 教程与该用户本人的 private 教程
