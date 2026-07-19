<template>
  <div>
    <div class="head">
      <h2>用户管理</h2>
      <el-button type="primary" :icon="Plus" @click="addUser">新增用户</el-button>
    </div>

    <el-table v-loading="loading" :data="users" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'warning' : 'info'">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="200" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            v-if="row.role !== 'admin'"
            size="small"
            type="danger"
            :icon="Delete"
            @click="remove(row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageSize"
      class="pager"
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="current"
      @current-change="onPageChange"
      background
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { userApi } from '@/api'

const users = ref([])
const loading = ref(false)
const total = ref(0)
const current = ref(1)
const pageSize = 20

async function load() {
  loading.value = true
  try {
    const data = await userApi.list({ current: current.value, size: pageSize })
    users.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function addUser() {
  const { value: username } = await ElMessageBox.prompt('输入用户名（3-64位）', '新增用户', {
    inputPattern: /^.{3,64}$/, inputErrorMessage: '用户名需 3-64 位'
  })
  const { value: password } = await ElMessageBox.prompt('输入密码（至少6位）', '设置密码', {
    inputType: 'password', inputPattern: /^.{6,}$/, inputErrorMessage: '密码至少6位'
  })
  await userApi.create({ username: username.trim(), password })
  ElMessage.success('已新增')
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`删除用户「${row.username}」？`, '提示', { type: 'warning' })
  await userApi.remove(row.id)
  ElMessage.success('已删除')
  load()
}

function onPageChange(page) {
  current.value = page
  load()
}

onMounted(load)
</script>

<style scoped>
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.pager { margin-top: 20px; justify-content: center; }
</style>
