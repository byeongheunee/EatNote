<template>
  <div class="community-view px-6 pb-10">
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

    <h1 class="text-3xl font-bold text-center mt-10 mb-6">게시판</h1>

    <!-- 게시판 슬라이더 -->
    <BoardSlider
      :boards="boards"
      :selectedBoardId="selectedBoardId"
      @selectBoard="selectBoard"
    />

    <!-- 게시글 상세 보기 -->
    <ArticleDetail
      v-if="selectedArticleId"
      :articleId="selectedArticleId"
      @close="selectedArticleId = null"
    />

    <!-- 게시글 목록 -->
    <div v-else>
      <div class="text-right mb-4" v-if="canWriteArticle">
        <button
          class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
          @click="goToWrite"
        >
          ✏️ 게시글 작성
        </button>
      </div>
      <ArticleList :articles="articles" @open-detail="selectedArticleId = $event" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import Header from '@/components/common/Header.vue'
import BoardSlider from '@/components/BoardSlider.vue'
import ArticleList from '@/components/ArticleList.vue'
import ArticleDetail from '@/components/ArticleDetail.vue'
import { useAuthStore } from '@/stores/auth'


const router = useRouter()

// 로그인 사용자 유형 가져오기
const auth = useAuthStore()
const userType = computed(() => auth.user?.userType) // 0, 1, 2

const boards = ref([])
const articles = ref([])
const selectedBoardId = ref(null)
const selectedArticleId = ref(null)

// 현재 선택된 게시판 정보 계산
const selectedBoard = computed(() => {
  const found = boards.value.find((board) => board.boardId === selectedBoardId.value)
  console.log('선택된 게시판:', found)
  return found
})

// 게시글 작성 가능 여부 판단
const canWriteArticle = computed(() => {
  if (!auth.isLoggedIn) {
    console.log('로그인 안 됨')
    return false
  }
  if (!selectedBoard.value) {
    console.log('선택된 게시판 없음')
    return false
  }

  const access = selectedBoard.value.accessCode
  console.log('게시판 접근 권한:', access, '| 사용자 유형:', userType.value)

  if (access === 'ALL') return true
  if (access === 'TRAINER_ONLY') return userType.value === 0 || userType.value === 1
  if (access === 'ADMIN_ONLY') return userType.value === 0

  return false
})

// 작성 버튼 클릭 시 라우팅
const goToWrite = () => {
  router.push(`/articles/write?boardId=${selectedBoardId.value}`)
}

const fetchBoards = async () => {
  const res = await axios.get('/api/boards')
  boards.value = res.data.data

  if (boards.value.length > 0) {
    selectedBoardId.value = boards.value[0].boardId
    await fetchArticles(selectedBoardId.value) // 바로 데이터 요청!
  }
}

const fetchArticles = async (boardId) => {
  const res = await axios.get('/api/articles', { params: { boardId } })
  articles.value = res.data.data
}

const selectBoard = (id) => {
  selectedBoardId.value = id
  selectedArticleId.value = null
}

watch(selectedBoardId, (newVal) => {
  if (newVal !== null) fetchArticles(newVal)
})

const logout = () => {
  auth.logout()
  router.push('/')
}

onMounted(fetchBoards)
</script>
