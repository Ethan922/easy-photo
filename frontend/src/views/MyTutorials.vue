<template>
  <div class="ep-page">
    <div class="head">
      <h2>我的教程</h2>
      <el-button type="primary" :icon="Plus" @click="$router.push('/tutorials/new')">发布教程</el-button>
    </div>
    <div v-loading="loading" class="grid">
      <div v-for="t in tutorials" :key="t.id" class="item">
        <TutorialCard :tutorial="t" />
        <div class="ops">
          <el-button size="small" :icon="Edit" @click="$router.push(`/tutorials/${t.id}/edit`)">编辑</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="remove(t)">删除</el-button>
        </div>
      </div>
    </div>
    <el-empty v-if="!loading && !tutorials.length" description="还没有发布教程" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { tutorialApi } from '@/api'
import { useAuthStore } from '@/stores/auth'
import TutorialCard from '@/components/TutorialCard.vue'

const auth = useAuthStore()
const tutorials = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await tutorialApi.list({ authorId: auth.userId, current: 1, size: 100 })
    tutorials.value = data.records
  } finally {
    loading.value = false
  }
}

async function remove(t) {
  await ElMessageBox.confirm(`确定删除《${t.title}》？`, '提示', { type: 'warning' })
  await tutorialApi.remove(t.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.head { display: flex; justify-content: space-between; align-items: center; }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  min-height: 200px;
}
.item { display: flex; flex-direction: column; gap: 8px; }
.ops { display: flex; gap: 8px; justify-content: center; }
</style>
