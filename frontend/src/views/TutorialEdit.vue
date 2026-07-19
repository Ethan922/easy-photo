<template>
  <div class="ep-page">
    <div class="editor-card ep-card">
      <h2>{{ isEdit ? '编辑教程' : '发布教程' }}</h2>
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="给教程起个标题" maxlength="128" show-word-limit />
        </el-form-item>

        <el-form-item label="封面图">
          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="uploadCover"
            accept="image/*"
          >
            <div class="cover-upload">
              <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
              <div v-else class="cover-placeholder">
                <el-icon><Plus /></el-icon><span>上传封面</span>
              </div>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="分类维度">
          <div class="dim-picker">
            <div v-for="cat in categories" :key="cat.id" class="dim-group">
              <span class="cat-name">{{ cat.name }}：</span>
              <el-check-tag
                v-for="dim in cat.dimensions"
                :key="dim.id"
                :checked="form.dimensionIds.includes(dim.id)"
                class="dim-check"
                @change="toggleDim(dim.id)"
              >{{ dim.name }}</el-check-tag>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="可见范围">
          <el-radio-group v-model="form.visibility">
            <el-radio value="public">公开</el-radio>
            <el-radio value="private">私密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="正文">
          <div class="rich-editor">
            <Toolbar :editor="editorRef" :defaultConfig="{}" mode="default" />
            <Editor
              v-model="form.content"
              :defaultConfig="editorConfig"
              mode="default"
              style="height: 360px; overflow-y: hidden;"
              @onCreated="onEditorCreated"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="submit">
            {{ isEdit ? '保存修改' : '发布' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, shallowRef, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import { categoryApi, tutorialApi, imageApi } from '@/api'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)

const categories = ref([])
const saving = ref(false)
const form = reactive({
  title: '',
  coverUrl: '',
  content: '',
  visibility: 'public',
  dimensionIds: []
})

const editorRef = shallowRef()
const editorConfig = {
  placeholder: '请输入教程正文，可插入图片……',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        const fd = new FormData()
        fd.append('file', file)
        const data = await imageApi.upload(fd)
        insertFn(data.url, '', data.url)
      }
    }
  }
}

function onEditorCreated(editor) {
  editorRef.value = editor
}

function toggleDim(id) {
  const idx = form.dimensionIds.indexOf(id)
  if (idx >= 0) form.dimensionIds.splice(idx, 1)
  else form.dimensionIds.push(id)
}

function beforeUpload(file) {
  const ok = file.type.startsWith('image/')
  if (!ok) ElMessage.error('只能上传图片')
  return ok
}

async function uploadCover(options) {
  const fd = new FormData()
  fd.append('file', options.file)
  const data = await imageApi.upload(fd)
  form.coverUrl = data.url
  ElMessage.success('封面上传成功')
}

async function submit() {
  if (!form.title) return ElMessage.warning('请输入标题')
  if (!form.coverUrl) return ElMessage.warning('请上传封面')
  if (!form.dimensionIds.length) return ElMessage.warning('至少选择一个维度')
  if (!form.content || form.content === '<p><br></p>') return ElMessage.warning('请输入正文')

  saving.value = true
  try {
    if (isEdit.value) {
      await tutorialApi.update(route.params.id, form)
      ElMessage.success('保存成功')
      router.push(`/tutorials/${route.params.id}`)
    } else {
      const data = await tutorialApi.create(form)
      ElMessage.success('发布成功')
      router.push(`/tutorials/${data.id}`)
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  categories.value = await categoryApi.list()
  if (isEdit.value) {
    const t = await tutorialApi.detail(route.params.id)
    form.title = t.title
    form.coverUrl = t.coverUrl
    form.content = t.content
    form.visibility = t.visibility
    form.dimensionIds = t.dimensions.map((d) => d.id)
  }
})

onBeforeUnmount(() => {
  editorRef.value?.destroy()
})
</script>

<style scoped>
.editor-card { padding: 28px; }
.editor-card h2 { margin-top: 0; color: var(--ep-primary-deep); }
.cover-upload { cursor: pointer; }
.cover-preview { width: 200px; height: 150px; object-fit: cover; border-radius: 8px; }
.cover-placeholder {
  width: 200px; height: 150px;
  border: 1px dashed var(--ep-border);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--ep-text-light);
}
.dim-picker { display: flex; flex-direction: column; gap: 12px; width: 100%; }
.dim-group { display: flex; flex-wrap: wrap; align-items: center; gap: 8px; }
.cat-name { color: var(--ep-text-light); min-width: 48px; }
.dim-check { cursor: pointer; }
.rich-editor {
  border: 1px solid var(--ep-border);
  border-radius: 8px;
  width: 100%;
  overflow: hidden;
}
.rich-editor :deep(.w-e-toolbar) { border-bottom: 1px solid var(--ep-border); }
</style>
