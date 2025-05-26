<template>
  <div class="community-page">
    <Header @go-feature="scrollToFeature" @go-usage="scrollToUsage" />

    <!-- 메인 컨테이너 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 페이지 헤더 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">
          <span class="bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent">
            커뮤니티
          </span>
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          다양한 주제로 소통하고 정보를 공유해보세요
        </p>
      </div>

      <!-- 게시판 슬라이더 -->
      <div class="mb-8">
        <BoardSlider :boards="filteredBoards" :selectedBoardId="selectedBoardId" @selectBoard="selectBoard" />
      </div>

      <!-- 게시글 목록 -->
      <div class="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-orange-100 overflow-hidden">
        <!-- 게시글 헤더 + 검색 영역 통합 -->
        <div class="bg-gradient-to-r from-orange-50 to-amber-50 px-6 py-6 border-b border-orange-200">
          <!-- 상단: 게시판 제목과 게시글 수 -->
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">
              {{ selectedBoard?.name || 게시판 }}
            </h2>
            <p v-if="selectedBoard?.description" class="text-sm text-gray-500 sm:ml-4">
              {{ selectedBoard.description }}
            </p>
            <div class="text-sm text-gray-500 font-medium">
              총 {{ articles.length }}개의 게시글
            </div>
          </div>

          <!-- 하단: 검색 및 필터 영역 -->
          <div class="flex flex-col lg:flex-row gap-4 items-stretch lg:items-center">
            <!-- 왼쪽: 검색 컨트롤들 -->
            <div class="flex flex-1 gap-3 items-center">
              <!-- 검색 기준 선택 -->
              <div class="relative flex-shrink-0">
                <select v-model="searchField"
                  class="appearance-none bg-white/90 border border-orange-200 rounded-lg px-4 py-2.5 pr-8 text-sm font-medium text-gray-700 hover:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-orange-500 transition-all duration-200">
                  <option value="ALL">🔍 전체</option>
                  <option value="TITLE">📝 제목</option>
                  <option value="CONTENT">📄 내용</option>
                  <option value="NICKNAME">👤 작성자</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg class="w-4 h-4 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>

              <!-- 검색어 입력 - 더 넓게 -->
              <div class="relative flex-1">
                <input v-model="keyword" type="text" placeholder="검색어를 입력하세요"
                  class="w-full pl-10 pr-4 py-2.5 border border-orange-200 rounded-lg text-sm placeholder-gray-400 hover:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-orange-500 transition-all duration-200 bg-white/90"
                  @keyup.enter="handleSearch" />
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-4 h-4 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                  </svg>
                </div>
              </div>

              <!-- 정렬 기준 선택 -->
              <div class="relative flex-shrink-0">
                <select v-model="sort"
                  class="appearance-none bg-white/90 border border-orange-200 rounded-lg px-4 py-2.5 pr-8 text-sm font-medium text-gray-700 hover:border-orange-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-orange-500 transition-all duration-200">
                  <option value="createdAt">🕒 최신순</option>
                  <option value="viewCnt">👁️ 조회순</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg class="w-4 h-4 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <!-- 오른쪽: 버튼들 -->
            <div class="flex gap-3 flex-shrink-0">
              <!-- 검색 버튼 -->
              <button
                class="inline-flex items-center px-4 py-2.5 bg-gradient-to-r from-orange-500 to-amber-500 text-white text-sm font-medium rounded-lg hover:from-orange-600 hover:to-amber-600 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 transform hover:scale-105 transition-all duration-200 shadow-lg"
                @click="handleSearch">
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                검색
              </button>

              <!-- 글쓰기 버튼 -->
              <button v-if="canWriteArticle"
                class="inline-flex items-center px-6 py-2.5 bg-gradient-to-r from-green-500 to-emerald-500 text-white text-sm font-medium rounded-lg hover:from-green-600 hover:to-emerald-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transform hover:scale-105 transition-all duration-200 shadow-lg"
                @click="goToWrite">
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                글쓰기
              </button>
            </div>
          </div>
        </div>

        <!-- 게시글 리스트 -->
        <div class="divide-y divide-orange-100">
          <ArticleList :articles="pagedArticles" @open-detail="openDetail" @refresh="handleRefresh" />
        </div>

        <!-- 빈 상태 -->
        <div v-if="articles.length === 0" class="text-center py-16">
          <div class="w-24 h-24 mx-auto mb-4 text-orange-300">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
              </path>
            </svg>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">게시글이 없습니다</h3>
          <p class="text-gray-600 mb-6">첫 번째 게시글을 작성해보세요!</p>
          <button v-if="canWriteArticle" @click="goToWrite"
            class="inline-flex items-center px-4 py-2 bg-gradient-to-r from-orange-500 to-amber-500 text-white text-sm font-medium rounded-lg hover:from-orange-600 hover:to-amber-600 transition-all duration-200 shadow-md">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            글쓰기
          </button>
        </div>
      </div>

      <!-- 더보기 버튼 -->
      <div v-if="pagedArticles.length < articles.length" class="text-center mt-8">
        <button @click="loadMore"
          class="inline-flex items-center px-8 py-3 bg-white/80 backdrop-blur-sm text-gray-700 text-sm font-medium rounded-lg border border-orange-200 hover:bg-white/90 hover:border-orange-300 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 transition-all duration-200 shadow-md hover:shadow-lg">
          <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
          </svg>
          더보기 ({{ articles.length - pagedArticles.length }}개 남음)
        </button>
      </div>
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

const keyword = ref('')
const searchField = ref('ALL')
const sort = ref('createdAt')
const visibleCount = ref(5)
const pagedArticles = computed(() => {
  return articles.value.slice(0, visibleCount.value)
})
const loadMore = () => {
  visibleCount.value += 5
}
const handleSearch = async () => {
  if (!selectedBoardId.value) return

  const res = await axios.get('/api/articles', {
    params: {
      boardId: selectedBoardId.value,
      keyword: keyword.value,
      searchField: searchField.value,
      sort: sort.value
    },
    headers: {
      Authorization: `Bearer ${auth.accessToken}`
    }
  })

  articles.value = res.data.data
}

const router = useRouter()
const route = useRoute()

// 로그인 사용자 유형 가져오기
const auth = useAuthStore()
const userType = computed(() => auth.user?.userType) // 0, 1, 2

const boards = ref([])
const articles = ref([])
const selectedBoardId = ref(null)

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

const filteredBoards = computed(() => {
  const type = userType.value

  return boards.value.filter(b => {
    if (b.accessCode === 'TRAINER_ONLY') {
      return type === 0 || type === 1 // 관리자 or 트레이너만 허용
    }
    return true // 나머지는 모두 허용
  })
})

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

// 스크롤 함수들 (Header emit용)
const scrollToFeature = () => {
  // 필요시 구현
}

const scrollToUsage = () => {
  // 필요시 구현
}

// 1. selectedBoardId가 바뀌면 게시글을 다시 불러온다
watch(selectedBoardId, (newVal) => {
  if (newVal !== null) {
    visibleCount.value = 5
    fetchArticles(newVal)
  }
})

// 2. URL의 boardId 파라미터가 바뀌면 selectedBoardId도 바꾼다
watch(
  () => route.params.boardId,
  (newBoardId) => {
    const boardId = Number(newBoardId)
    const validBoard = boards.value.find(b => b.boardId === boardId)

    selectedBoardId.value = validBoard ? validBoard.boardId : boards.value[0]?.boardId

    // 검색 조건 초기화
    keyword.value = ''
    searchField.value = 'ALL'
    sort.value = 'createdAt'
  }
)

watch(selectedBoardId, (newVal) => {
  if (newVal !== null) handleSearch()
})

onMounted(fetchBoards)
</script>

<style scoped>
/* 페이지 전체 배경 - 홈뷰와 동일한 색상 */
.community-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

/* 그라데이션 텍스트 */
.bg-clip-text {
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 백드롭 블러 효과 */
.backdrop-blur-sm {
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

/* 커스텀 스크롤바 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #fef3c7;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #f59e0b;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #d97706;
}

/* 애니메이션 */
.community-page {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 호버 효과 */
.hover\:scale-105:hover {
  transform: scale(1.05);
}

/* 포커스 효과 - 오렌지 색상으로 변경 */
input:focus,
select:focus {
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .community-page {
    padding: 1rem;
  }

  .max-w-7xl {
    padding: 0 1rem;
  }

  /* 모바일에서 검색 영역 세로 정렬 */
  .flex.flex-1.gap-3.items-center {
    flex-direction: column;
    align-items: stretch;
  }

  .flex.gap-3.flex-shrink-0 {
    flex-direction: column;
  }

  .relative.flex-shrink-0 {
    width: 100%;
  }

  .relative.flex-1 {
    width: 100%;
  }
}

@media (max-width: 640px) {
  .text-4xl {
    font-size: 2rem;
  }

  .px-6 {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .py-8 {
    padding-top: 1.5rem;
    padding-bottom: 1.5rem;
  }
}

/* 그림자 효과 개선 */
.shadow-lg {
  box-shadow: 0 10px 15px -3px rgba(245, 158, 11, 0.1), 0 4px 6px -2px rgba(245, 158, 11, 0.05);
}

.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(245, 158, 11, 0.1), 0 2px 4px -1px rgba(245, 158, 11, 0.06);
}

/* 버튼 호버 효과 개선 */
button:hover {
  transform: translateY(-1px);
}

/* 테두리 및 배경 투명도 조정 */
.bg-white\/80 {
  background-color: rgba(255, 255, 255, 0.8);
}

.bg-white\/90 {
  background-color: rgba(255, 255, 255, 0.9);
}

.bg-white\/60 {
  background-color: rgba(255, 255, 255, 0.6);
}

/* 오렌지 테마 색상 변수들 */
.border-orange-100 {
  border-color: #fed7aa;
}

.border-orange-200 {
  border-color: #fec89f;
}

.border-orange-300 {
  border-color: #fdba74;
}

.text-orange-300 {
  color: #fdba74;
}

.text-orange-400 {
  color: #fb923c;
}

.from-orange-50 {
  --tw-gradient-from: #fff7ed;
}

.to-amber-50 {
  --tw-gradient-to: #fffbeb;
}

.divide-orange-100> :not([hidden])~ :not([hidden]) {
  border-color: #fed7aa;
}
</style>