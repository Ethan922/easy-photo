<template>
  <div class="ep-page">
    <h2>我的收藏</h2>
    <div v-loading="loading" class="grid">
      <TutorialCard v-for="t in tutorials" :key="t.id" :tutorial="t" />
    </div>
    <el-empty v-if="!loading && !tutorials.length" description="还没有收藏任何教程" />
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
import { engagementApi } from '@/api'
import TutorialCard from '@/components/TutorialCard.vue'

const tutorials = ref([])
const loading = ref(false)
const total = ref(0)
const current = ref(1)
const pageSize = 12

async function load() {
  loading.value = true
  try {
    const data = await engagementApi.myFavorites({ current: current.value, size: pageSize })
    tutorials.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  current.value = page
  load()
}

onMounted(load)
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  min-height: 200px;
}
.pager { margin-top: 30px; justify-content: center; }
</style>
