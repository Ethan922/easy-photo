<template>
  <div>
    <div class="head">
      <h2>分类管理</h2>
      <el-button type="primary" :icon="Plus" @click="addCategory">新增分类</el-button>
    </div>

    <div v-loading="loading" class="cat-list">
      <div v-for="cat in categories" :key="cat.id" class="cat-block ep-card">
        <div class="cat-head">
          <span class="cat-name">{{ cat.name }}</span>
          <div>
            <el-button size="small" :icon="Plus" @click="addDimension(cat)">维度</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="removeCategory(cat)">删除分类</el-button>
          </div>
        </div>
        <div class="dims">
          <el-tag
            v-for="dim in cat.dimensions"
            :key="dim.id"
            closable
            class="dim"
            @close="removeDimension(dim)"
          >{{ dim.name }}</el-tag>
          <span v-if="!cat.dimensions.length" class="empty">暂无维度</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { categoryApi } from '@/api'

const categories = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    categories.value = await categoryApi.list()
  } finally {
    loading.value = false
  }
}

async function addCategory() {
  const { value } = await ElMessageBox.prompt('输入分类名称', '新增分类', {
    inputPattern: /\S+/, inputErrorMessage: '名称不能为空'
  })
  await categoryApi.createCategory(value.trim())
  ElMessage.success('已新增')
  load()
}

async function removeCategory(cat) {
  await ElMessageBox.confirm(`删除分类「${cat.name}」及其所有维度？`, '提示', { type: 'warning' })
  await categoryApi.deleteCategory(cat.id)
  ElMessage.success('已删除')
  load()
}

async function addDimension(cat) {
  const { value } = await ElMessageBox.prompt(`在「${cat.name}」下新增维度`, '新增维度', {
    inputPattern: /\S+/, inputErrorMessage: '名称不能为空'
  })
  await categoryApi.createDimension(cat.id, value.trim())
  ElMessage.success('已新增')
  load()
}

async function removeDimension(dim) {
  await ElMessageBox.confirm(`删除维度「${dim.name}」？`, '提示', { type: 'warning' })
  await categoryApi.deleteDimension(dim.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.cat-list { display: flex; flex-direction: column; gap: 16px; }
.cat-block { padding: 16px 20px; }
.cat-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.cat-name { font-size: 17px; font-weight: 600; color: var(--ep-primary-deep); }
.dims { display: flex; flex-wrap: wrap; gap: 8px; }
.empty { color: var(--ep-text-light); font-size: 13px; }
</style>
