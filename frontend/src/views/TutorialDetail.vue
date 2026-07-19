<template>
  <div class="ep-page" v-loading="loading">
    <div v-if="tutorial" class="detail ep-card">
      <img class="cover" :src="tutorial.coverUrl" :alt="tutorial.title" />
      <div class="content-wrap">
        <div class="head">
          <h1>{{ tutorial.title }}</h1>
          <div class="actions" v-if="auth.isLoggedIn">
            <el-button
              :type="tutorial.liked ? 'primary' : 'default'"
              :icon="Pointer"
              round
              @click="toggleLike"
            >{{ tutorial.likeCount }}</el-button>
            <el-button
              :type="tutorial.favorited ? 'warning' : 'default'"
              :icon="Star"
              round
              @click="toggleFavorite"
            >{{ tutorial.favoriteCount }}</el-button>
            <el-button
              v-if="canEdit"
              :icon="Edit"
              round
              @click="$router.push(`/tutorials/${tutorial.id}/edit`)"
            >编辑</el-button>
          </div>
        </div>
        <div class="tags">
          <span class="author">作者：{{ tutorial.authorName }}</span>
          <el-tag v-for="d in tutorial.dimensions" :key="d.id" class="dim" effect="light">
            {{ d.categoryName }} · {{ d.name }}
          </el-tag>
        </div>
        <!-- 富文本正文（后端已清洗） -->
        <div class="rich" v-html="tutorial.content"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Star, Pointer, Edit } from '@element-plus/icons-vue'
import { tutorialApi, engagementApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const auth = useAuthStore()
const tutorial = ref(null)
const loading = ref(false)

const canEdit = computed(() =>
  auth.isLoggedIn && tutorial.value &&
  (auth.isAdmin || auth.userId === tutorial.value.authorId)
)

async function load() {
  loading.value = true
  try {
    tutorial.value = await tutorialApi.detail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function toggleLike() {
  if (tutorial.value.liked) {
    await engagementApi.unlike(tutorial.value.id)
    tutorial.value.liked = false
    tutorial.value.likeCount--
  } else {
    await engagementApi.like(tutorial.value.id)
    tutorial.value.liked = true
    tutorial.value.likeCount++
  }
}

async function toggleFavorite() {
  if (tutorial.value.favorited) {
    await engagementApi.unfavorite(tutorial.value.id)
    tutorial.value.favorited = false
    tutorial.value.favoriteCount--
  } else {
    await engagementApi.favorite(tutorial.value.id)
    tutorial.value.favorited = true
    tutorial.value.favoriteCount++
  }
}

onMounted(load)
</script>

<style scoped>
.detail { overflow: hidden; }
.cover {
  width: 100%;
  max-height: 420px;
  object-fit: cover;
}
.content-wrap { padding: 28px; }
.head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}
.head h1 { margin: 0; color: var(--ep-text); }
.actions { display: flex; gap: 8px; flex-shrink: 0; }
.tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin: 16px 0 24px;
}
.author { color: var(--ep-text-light); }
.rich {
  line-height: 1.8;
  color: var(--ep-text);
}
.rich :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 12px 0;
}
</style>
