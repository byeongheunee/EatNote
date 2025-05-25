<template>
  <div class="article-edit px-6 pb-20">
    <Header />

    <div class="max-w-3xl mx-auto mt-10">
      <!-- 🔹 목록 이동 버튼 -->
      <div class="flex justify-end mb-4">
        <button @click="goToBoard" class="text-sm text-blue-600 hover:underline">
          ← 목록으로 이동하기
        </button>
      </div>

      <h1 class="text-2xl font-bold mb-2">{{ isEditMode ? '게시글 수정' : '게시글 작성' }}</h1>

      <!-- 제목 입력란 + 글자 수 표시 -->
      <div class="mb-4">
      <!-- 글자 수 -->
      <div class="text-sm text-gray-500 text-right mb-1">
          {{ title.length }}/200자
      </div>
      <!-- 제목 input -->
      <input v-model="title" type="text" placeholder="제목을 입력하세요" class="w-full border p-3 rounded text-lg" maxlength="200"/>
      </div>
      <textarea v-model="content" placeholder="내용을 입력하세요" class="w-full border p-3 rounded h-60 mb-4"></textarea>

      <!-- 기존 이미지 목록 -->
      <div v-if="existingImages.length" class="grid grid-cols-3 gap-4 mb-6">
        <div v-for="(img, i) in existingImages" :key="img.fileId" class="relative group">
          <img :src="getImageUrl(img.filePath)" class="w-full h-32 object-cover rounded shadow" />
          <button
            class="absolute top-1 right-1 bg-red-600 text-white text-xs px-2 py-1 rounded opacity-70 group-hover:opacity-100"
            @click="removeExistingImage(i)">✕</button>
        </div>
      </div>

      <!-- 새 파일 업로드 -->
      <div class="mb-6">
        <label class="block mb-2 font-semibold">파일 추가</label>
        <input type="file" multiple accept="image/*" @change="handleFileChange" />
        <div class="text-sm text-gray-500 mt-1">선택된 파일: {{ fileNames.join(', ') || '없음' }}</div>
      </div>

      <!-- 새 이미지 미리보기 -->
      <div v-if="previewUrls.length" class="grid grid-cols-3 gap-4 mt-4">
        <img v-for="(url, i) in previewUrls" :key="i" :src="url" class="w-full h-32 object-cover rounded shadow" />
      </div>

      <div class="text-right mt-6">
        <button class="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700" @click="submit">{{ isEditMode ?
          "수정완료" : "작성 완료" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const toast = useToast()
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const articleId = Number(route.params.articleId)
const isEditMode = !isNaN(articleId)
const boardId = Number(route.query.boardId)

const title = ref('')
const content = ref('')
const files = ref([])
const fileNames = ref([])
const previewUrls = ref([])
const existingImages = ref([])
const deletedFileIds = ref([])

const getImageUrl = (path) => `http://localhost:8080${path}`


const goToBoard = () => {
  router.push(`/community/${boardId}`)
}

const fetchArticle = async () => {
  const res = await axios.get(`/api/articles/${articleId}`, {
    headers: { Authorization: `Bearer ${auth.accessToken}` }
  })
  const data = res.data.data
  title.value = data.title
  content.value = data.content
  existingImages.value = data.attachments || []
}

const removeExistingImage = (index) => {
  const removed = existingImages.value.splice(index, 1)[0]
  deletedFileIds.value.push(removed.fileId)
}

const handleFileChange = (event) => {
  const selectedFiles = Array.from(event.target.files)

  const totalImages = existingImages.value.length + selectedFiles.length
  if (totalImages > 5) {
    toast.warning('첨부 이미지는 최대 5개까지만 업로드할 수 있습니다.')
    return
  }

  files.value = selectedFiles
  fileNames.value = files.value.map(file => file.name)
  previewUrls.value = files.value.map(file => URL.createObjectURL(file))
}

const submit = async () => {
  if (!title.value || !content.value) {
    // alert('제목과 내용을 입력해주세요!')
    toast.warning('제목과 내용을 입력해주세요!')
    return
  }

  const articleData = isEditMode
    ? {
      title: title.value,
      content: content.value,
      deleteFileIds: deletedFileIds.value
    }
    : {
      title: title.value,
      content: content.value,
      boardId: boardId
    }

  const formData = new FormData()
  formData.append('article', JSON.stringify(articleData))
  files.value.forEach(file => formData.append('files', file))

  const method = isEditMode ? 'put' : 'post'
  const url = isEditMode
    ? `/api/articles/${articleId}`
    : `/api/articles?boardId=${boardId}`

  try {
    const res = await axios({
      method,
      url,
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${auth.accessToken}`
      }
    })

    const newArticleId = isEditMode ? articleId : res.data.data
    router.replace(`/community/${boardId}/article/${newArticleId}`)
  } catch (e) {
    console.error('게시글 저장 실패:', e)
    // alert(isEditMode ? '수정 중 오류가 발생했습니다.' : '작성 중 오류가 발생했습니다.')
    toast.error(isEditMode ? '수정 중 오류가 발생했습니다.' : '작성 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  if (isEditMode) {
    fetchArticle()
  }
})

onUnmounted(() => {
  previewUrls.value.forEach(url => URL.revokeObjectURL(url))
})
</script>
