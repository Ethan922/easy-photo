<template>
  <div class="auth-page">
    <div class="auth-card ep-card">
      <h2>登录易拍</h2>
      <el-form :model="form" @submit.prevent>
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" size="large" class="full" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <p class="hint">还没有账号？<router-link to="/register">去注册</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

async function submit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await authApi.login(form)
    auth.setAuth(data)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--ep-bg) 0%, var(--ep-primary-light-9, #fdf9ec) 100%);
}
.auth-card {
  width: 380px;
  padding: 36px;
}
.auth-card h2 {
  text-align: center;
  color: var(--ep-primary-deep);
  margin-top: 0;
}
.full { width: 100%; }
.hint { text-align: center; margin-top: 16px; color: var(--ep-text-light); }
</style>
