<template>
  <div class="article-write px-6 pb-20">
    <Header
      :isLogin="auth.isLoggedIn"
      :user="auth.user"
      @go-community="router.push('/community')"
      @go-meals="router.push('/meals')"
      @go-videos="router.push('/videos')"
      @go-profile="router.push('/profile')"
      @logout="logout"
      @go-login="router.push('/login')"
      @go-register="router.push('/register')"
    />

    <div class="max-w-3xl mx-auto mt-10">
      <h1 class="text-2xl font-bold mb-6">{{ boardName }}</h1>

      <input
        v-model="title"
        type="text"
        placeholder="제목을 입력하세요"
        class="w-full border p-3 rounded mb-4 text-lg"
      />

      <textarea
        v-model="content"
        placeholder="내용을 입력하세요"
        class="w-full border p-3 rounded h-60 mb-4"
      ></textarea>

      <div class="mb-6">
        <label class="block mb-2 font-semibold">파일 선택</label>
        <input
          type="file"
          accept="image/*"
          multiple
          @change="handleFileChange"
        />
        <div class="text-sm text-gray-500 mt-1">
          선택된 파일: {{ fileNames.join(', ') || '없음' }}
        </div>

        <!-- 이미지 미리보기 -->
        <div class="grid grid-cols-3 gap-4 mt-4" v-if="previewUrls.length">
            <div v-for="(url, i) in previewUrls" :key="i">
            <img :src="url" class="w-full h-32 object-cover rounded shadow" />
            </div>
        </div>
      </div>

      <div class="text-right">
        <button
          class="px-6 py-2 bg-red-600 text-white rounded hover:bg-red-700"
          @click="submit"
        >
          저장하기
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

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const boardId = Number(route.query.boardId) || null
const boardName = ref('게시판')
const title = ref('')
const content = ref('')
const files = ref([])

const fileNames = ref([])
const previewUrls = ref([]) // 미리보기용 URL 저장

const fetchBoardName = async () => {
  if (!boardId) return
  const res = await axios.get(`/api/boards/${boardId}`)
  boardName.value = res.data.data.name
}

const handleFileChange = (event) => {
  files.value = Array.from(event.target.files)
  fileNames.value = files.value.map((file) => file.name)
  // 미리보기 URL 생성
  previewUrls.value = files.value.map(file => URL.createObjectURL(file))
}

const submit = async () => {
  if (!title.value || !content.value) {
    alert('제목과 내용을 입력해주세요!')
    return
  }

  const articleData = {
    title: title.value,
    content: content.value,
    boardId: boardId
  }

  const formData = new FormData()
  formData.append('article', JSON.stringify(articleData))
  files.value.forEach((file) => {
    formData.append('files', file)
  })

  try {
    const res = await axios.post('/api/articles', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${auth.accessToken}`
      }
    })

    alert('게시글이 성공적으로 등록되었습니다!')
    // 생성된 articleId를 응답에서 추출하고 상세 페이지로 이동
    const articleId = res.data.data
    router.push(`/articles/${articleId}`)
  } catch (err) {
    console.error('게시글 등록 실패:', err)
    alert('게시글 등록 중 오류가 발생했습니다.')
  }
}

const logout = () => {
  auth.logout()
  router.push('/')
}

onMounted(fetchBoardName)

onUnmounted(() => {
  previewUrls.value.forEach(url => URL.revokeObjectURL(url))
})

</script>
