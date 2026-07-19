<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand" @click="$router.push('/')">易拍 · 后台</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/admin/categories">
          <el-icon><Files /></el-icon><span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon><span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/tutorials">
          <el-icon><Document /></el-icon><span>教程管理</span>
        </el-menu-item>
      </el-menu>
      <div class="back">
        <el-button text @click="$router.push('/')">返回前台</el-button>
      </div>
    </aside>
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 220px;
  background: var(--ep-bg-card);
  border-right: 1px solid var(--ep-border);
  display: flex;
  flex-direction: column;
}
.brand {
  padding: 20px;
  font-size: 18px;
  font-weight: 700;
  color: var(--ep-primary-deep);
  cursor: pointer;
}
.sidebar :deep(.el-menu) {
  border-right: none;
  background: transparent;
  flex: 1;
}
.back {
  padding: 16px;
}
.admin-main {
  flex: 1;
  padding: 24px;
}
</style>
