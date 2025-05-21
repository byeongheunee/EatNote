<template>
  <div class="community-view px-6 pb-10">
    <Header
      @go-feature="scrollToFeature"
      @go-usage="scrollToUsage"
    />

    <h1 class="text-3xl font-bold text-center mt-10 mb-6">게시판</h1>

    <!-- 게시판 슬라이더 -->
    <BoardSlider
      :boards="boards"
      :selectedBoardId="selectedBoardId"
      @selectBoard="selectBoard"
    />

    <!-- 게시글 목록 -->
    <div>
      <div class="text-right mb-4" v-if="canWriteArticle">
        <button
          class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
          @click="goToWrite"
        >
          ✏️ 게시글 작성
        </button>
      </div>
      <ArticleList
        :articles="articles"
        @open-detail="openDetail"
        @refresh="handleRefresh"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

import axios from 'axios'
import Header from '@/components/common/Header.vue'
import BoardSlider from '@/components/BoardSlider.vue'
import ArticleList from '@/components/ArticleList.vue'



const router = useRouter()
const route = useRoute()

// 로그인 사용자 유형 가져오기
const auth = useAuthStore()
const userType = computed(() => auth.user?.userType) // 0, 1, 2

const boards = ref([])
const articles = ref([])
const selectedBoardId = ref(null)
// const selectedArticleId = ref(null)

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

  const boardId = Number(route.params.boardId)
  const validBoard = boards.value.find((b) => b.boardId === boardId)
  selectedBoardId.value = validBoard ? validBoard.boardId : boards.value[0].boardId

  await fetchArticles(selectedBoardId.value)
}

const fetchArticles = async (boardId) => {
  if (!boardId) return

  const res = await axios.get('/api/articles', {
    params: { boardId },
    headers: {
      Authorization: `Bearer ${auth.accessToken}`
    }
  })

  articles.value = res.data.data
}

const selectBoard = (id) => {
  router.push(`/community/${id}`)
}

const openDetail = (articleId) => {
  router.push(`/community/${selectedBoardId.value}/article/${articleId}`)
}

const handleRefresh = () => {
  if (selectedBoardId.value) {
    fetchArticles(selectedBoardId.value)
  }
}

// 1. selectedBoardId가 바뀌면 게시글을 다시 불러온다
watch(selectedBoardId, (newVal) => {
  if (newVal !== null) fetchArticles(newVal)
})

// 2. URL의 boardId 파라미터가 바뀌면 selectedBoardId도 바꾼다
watch(
  () => route.params.boardId,
  (newBoardId) => {
    const boardId = Number(newBoardId)
    const validBoard = boards.value.find(b => b.boardId === boardId)

    selectedBoardId.value = validBoard ? validBoard.boardId : boards.value[0]?.boardId
  }
)

onMounted(fetchBoards)
</script>


