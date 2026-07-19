<template>
  <div class="tutorial-card ep-card" @click="$router.push(`/tutorials/${tutorial.id}`)">
    <div class="cover">
      <img :src="tutorial.coverUrl" :alt="tutorial.title" />
      <el-tag v-if="tutorial.visibility === 'private'" class="badge" size="small" type="info">私密</el-tag>
    </div>
    <div class="body">
      <h3 class="title">{{ tutorial.title }}</h3>
      <div class="meta">
        <span class="author">{{ tutorial.authorName }}</span>
        <span class="stats">
          <el-icon><Star /></el-icon>{{ tutorial.favoriteCount }}
          <el-icon><Pointer /></el-icon>{{ tutorial.likeCount }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Star, Pointer } from '@element-plus/icons-vue'
defineProps({
  tutorial: { type: Object, required: true }
})
</script>

<style scoped>
.tutorial-card {
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}
.cover {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  background: var(--ep-primary-light-9, #fdf9ec);
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.badge { position: absolute; top: 8px; right: 8px; }
.body { padding: 12px 14px; }
.title {
  margin: 0 0 8px;
  font-size: 16px;
  color: var(--ep-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--ep-text-light);
  font-size: 13px;
}
.stats {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
