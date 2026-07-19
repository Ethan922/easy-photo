<template>
  <div>
    <h2>教程管理</h2>
    <p class="tip">管理员可查看全部教程（含私密），并可删除任意教程。</p>

    <el-table v-loading="loading" :data="tutorials" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <img :src="row.coverUrl" class="thumb" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="authorName" label="作者" width="140" />
      <el-table-column label="可见性" width="100">
        <template #default="{ row }">
          <el-tag :type="row.visibility === 'public' ? 'success' : 'info'">
            {{ row.visibility === 'public' ? '公开' : '私密' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="统计" width="140">
        <template #default="{ row }">
          收藏 {{ row.favoriteCount }} / 赞 {{ row.likeCount }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/tutorials/${row.id}`)">查看</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="remove(row)">删除</el-button>
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
import { Delete } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { tutorialApi } from '@/api'

const tutorials = ref([])
const loading = ref(false)
const total = ref(0)
const current = ref(1)
const pageSize = 20

async function load() {
  loading.value = true
  try {
    // 管理员登录后，后端 list 返回全部教程（含 private）
    const data = await tutorialApi.list({ current: current.value, size: pageSize })
    tutorials.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`删除教程《${row.title}》？`, '提示', { type: 'warning' })
  await tutorialApi.remove(row.id)
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
.tip { color: var(--ep-text-light); margin-bottom: 16px; }
.thumb { width: 64px; height: 48px; object-fit: cover; border-radius: 4px; }
.pager { margin-top: 20px; justify-content: center; }
</style>
