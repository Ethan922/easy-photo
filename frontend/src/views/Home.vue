<template>
  <div class="ep-page">
    <!-- 分类 Tab -->
    <el-tabs v-model="activeCategory" @tab-change="onCategoryChange">
      <el-tab-pane
        v-for="cat in categories"
        :key="cat.id"
        :label="cat.name"
        :name="String(cat.id)"
      />
    </el-tabs>

    <!-- 维度筛选 -->
    <div class="dimensions" v-if="currentDimensions.length">
      <el-tag
        :effect="selectedDimension === null ? 'dark' : 'plain'"
        class="dim-tag"
        @click="selectDimension(null)"
      >全部</el-tag>
      <el-tag
        v-for="dim in currentDimensions"
        :key="dim.id"
        :effect="selectedDimension === dim.id ? 'dark' : 'plain'"
        class="dim-tag"
        @click="selectDimension(dim.id)"
      >{{ dim.name }}</el-tag>
    </div>

    <!-- 教程卡片列表 -->
    <div v-loading="loading" class="grid">
      <TutorialCard v-for="t in tutorials" :key="t.id" :tutorial="t" />
    </div>
    <el-empty v-if="!loading && !tutorials.length" description="暂无教程" />

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
import { ref, computed, onMounted } from 'vue'
import { categoryApi, tutorialApi } from '@/api'
import TutorialCard from '@/components/TutorialCard.vue'

const categories = ref([])
const activeCategory = ref('')
const selectedDimension = ref(null)
const tutorials = ref([])
const loading = ref(false)
const total = ref(0)
const current = ref(1)
const pageSize = 12

const currentDimensions = computed(() => {
  const cat = categories.value.find((c) => String(c.id) === activeCategory.value)
  return cat ? cat.dimensions : []
})

async function loadCategories() {
  categories.value = await categoryApi.list()
  if (categories.value.length) {
    activeCategory.value = String(categories.value[0].id)
  }
}

async function loadTutorials() {
  loading.value = true
  try {
    const params = { current: current.value, size: pageSize }
    if (selectedDimension.value !== null) {
      params.dimensionId = selectedDimension.value
    }
    const data = await tutorialApi.list(params)
    tutorials.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onCategoryChange() {
  selectedDimension.value = null
  current.value = 1
  loadTutorials()
}

function selectDimension(id) {
  selectedDimension.value = id
  current.value = 1
  loadTutorials()
}

function onPageChange(page) {
  current.value = page
  loadTutorials()
}

onMounted(async () => {
  await loadCategories()
  await loadTutorials()
})
</script>

<style scoped>
.dimensions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 12px 0 24px;
}
.dim-tag { cursor: pointer; }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  min-height: 200px;
}
.pager {
  margin-top: 30px;
  justify-content: center;
}
</style>
