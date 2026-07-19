<template>
  <div class="auth-page">
    <div class="auth-card ep-card">
      <h2>注册易拍</h2>
      <el-form :model="form" @submit.prevent>
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名（3-64位）" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" size="large" :prefix-icon="Lock" show-password @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" size="large" class="full" :loading="loading" @click="submit">注册并登录</el-button>
      </el-form>
      <p class="hint">已有账号？<router-link to="/login">去登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()

async function submit() {
  if (form.username.length < 3) {
    ElMessage.warning('用户名至少3位')
    return
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码至少6位')
    return
  }
  loading.value = true
  try {
    const data = await authApi.register(form)
    auth.setAuth(data)
    ElMessage.success('注册成功')
    router.push('/')
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
  background: linear-gradient(135deg, var(--ep-bg) 0%, #fdf9ec 100%);
}
.auth-card { width: 380px; padding: 36px; }
.auth-card h2 { text-align: center; color: var(--ep-primary-deep); margin-top: 0; }
.full { width: 100%; }
.hint { text-align: center; margin-top: 16px; color: var(--ep-text-light); }
</style>
