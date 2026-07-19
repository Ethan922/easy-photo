<template>
  <div class="user-layout">
    <header class="app-header">
      <div class="header-inner">
        <div class="brand" @click="$router.push('/')">
          <el-icon><Camera /></el-icon>
          <span>易拍</span>
        </div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <template v-if="auth.isLoggedIn">
            <router-link to="/favorites">我的收藏</router-link>
            <router-link to="/my-tutorials">我的教程</router-link>
            <router-link to="/tutorials/new">发布教程</router-link>
            <router-link v-if="auth.isAdmin" to="/admin">管理后台</router-link>
            <el-dropdown @command="onCommand">
              <span class="user-chip">{{ auth.username }} <el-icon><ArrowDown /></el-icon></span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
        </nav>
      </div>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

function onCommand(cmd) {
  if (cmd === 'logout') {
    auth.logout()
    router.push('/')
  }
}
</script>

<style scoped>
.app-header {
  background: var(--ep-bg-card);
  border-bottom: 1px solid var(--ep-border);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 700;
  color: var(--ep-primary-deep);
  cursor: pointer;
}
.nav {
  display: flex;
  align-items: center;
  gap: 18px;
}
.nav a {
  color: var(--ep-text);
  text-decoration: none;
  font-size: 15px;
}
.nav a.router-link-exact-active {
  color: var(--ep-primary-deep);
  font-weight: 600;
}
.user-chip {
  cursor: pointer;
  color: var(--ep-text);
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
