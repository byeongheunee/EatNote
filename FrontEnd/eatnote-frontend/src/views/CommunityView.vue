<template>
  <div class="community-page">
    <Header @go-feature="scrollToFeature" @go-usage="scrollToUsage" />

    <!-- 메인 컨테이너 -->
    <div class="main-container">

      <!-- 페이지 헤더 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="main-title">커뮤니티</h1>
          <p class="main-subtitle">다양한 주제로 소통하고 정보를 공유해보세요</p>
        </div>
      </div>


      <!-- 게시판 슬라이더 -->
      <div class="slider-wrapper">
        <BoardSlider :boards="filteredBoards" :selectedBoardId="selectedBoardId" @selectBoard="selectBoard" />
      </div>

      <!-- 게시글 목록 -->
      <div class="content-card">
        <!-- 게시글 헤더 + 검색 영역 통합 -->
        <div class="content-header">
          <!-- 상단: 게시판 제목과 게시글 수 -->
          <div class="header-top">
            <h2 class="board-title">
              {{ selectedBoard?.name || '게시판' }}
            </h2>
            <p v-if="selectedBoard?.description" class="board-description">
              {{ selectedBoard.description }}
            </p>
            <div class="article-count">
              총 {{ articles.length }}개의 게시글
            </div>
          </div>

          <!-- 하단: 검색 및 필터 영역 -->
          <div class="search-controls">
            <!-- 왼쪽: 검색 컨트롤들 -->
            <div class="search-left">
              <!-- 검색 기준 선택 -->
              <div class="select-wrapper">
                <select v-model="searchField" class="search-select">
                  <option value="ALL">🔍 전체</option>
                  <option value="TITLE">📝 제목</option>
                  <option value="CONTENT">📄 내용</option>
                  <option value="NICKNAME">👤 작성자</option>
                </select>
                <div class="select-arrow">
                  <svg class="arrow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>

              <!-- 검색어 입력 -->
              <div class="input-wrapper">
                <input 
                  v-model="keyword" 
                  type="text" 
                  placeholder="검색어를 입력하세요"
                  class="search-input"
                  @keyup.enter="handleSearch" 
                />
                <div class="input-icon">
                  <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                  </svg>
                </div>
              </div>

              <!-- 정렬 기준 선택 -->
              <div class="select-wrapper">
                <select v-model="sort" class="search-select">
                  <option value="createdAt">🕒 최신순</option>
                  <option value="viewCnt">👁️ 조회순</option>
                </select>
                <div class="select-arrow">
                  <svg class="arrow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </div>
              </div>
            </div>

            <!-- 오른쪽: 버튼들 -->
            <div class="search-right">
              <!-- 검색 버튼 -->
              <button class="search-button" @click="handleSearch">
                <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                검색
              </button>

              <!-- 글쓰기 버튼 -->
              <button v-if="canWriteArticle" class="write-button" @click="goToWrite">
                <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                글쓰기
              </button>
            </div>
          </div>
        </div>

        <!-- 게시글 리스트 -->
        <div class="article-list-wrapper">
          <ArticleList :articles="pagedArticles" @open-detail="openDetail" @refresh="handleRefresh" />
        </div>

        <!-- 빈 상태 -->
        <div v-if="articles.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
              </path>
            </svg>
          </div>
          <h3 class="empty-title">게시글이 없습니다</h3>
          <p class="empty-description">첫 번째 게시글을 작성해보세요!</p>
          <button v-if="canWriteArticle" @click="goToWrite" class="empty-write-button">
            <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            글쓰기
          </button>
        </div>
      </div>

      <!-- 더보기 버튼 -->
      <div v-if="pagedArticles.length < articles.length" class="load-more-wrapper">
        <button @click="loadMore" class="load-more-button">
          <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
/* 페이지 전체 배경 */
.community-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 설정 */
.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f59e0b;
}

.header-content {
  text-align: center;
}

.main-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
  margin-bottom: 12px;
}

.main-subtitle {
  font-size: 18px;
  color: #5D4037;
  line-height: 1.6;
}

/* 슬라이더 래퍼 */
.slider-wrapper {
  margin-bottom: 32px;
}

/* 콘텐츠 카드 */
.content-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

/* 콘텐츠 헤더 */
.content-header {
  background: linear-gradient(to right, #f9fafb, #f3f4f6);
  padding: 24px;
  border-bottom: 1px solid #d1d5db;
}

/* 헤더 상단 */
.header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.board-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.board-description {
  font-size: 14px;
  color: #4b5563;
  margin-left: 16px;
}

.article-count {
  font-size: 14px;
  color: #4b5563;
  font-weight: 500;
}

/* 검색 컨트롤 */
.search-controls {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-left {
  display: flex;
  flex: 1;
  gap: 12px;
  align-items: center;
}

.search-right {
  display: flex;
  gap: 12px;
}

/* 셀렉트 래퍼 */
.select-wrapper {
  position: relative;
  flex-shrink: 0;
}

.search-select {
  appearance: none;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 32px 10px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  transition: all 0.2s ease;
  cursor: pointer;
}

.search-select:hover {
  border-color: #9ca3af;
}

.search-select:focus {
  outline: none;
  border-color: #6b7280;
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.1);
}

.select-arrow {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  padding: 0 8px;
  pointer-events: none;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  color: #6b7280;
}

/* 인풋 래퍼 */
.input-wrapper {
  position: relative;
  flex: 1;
}

.search-input {
  width: 100%;
  padding: 10px 16px 10px 40px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.2s ease;
}

.search-input::placeholder {
  color: #9ca3af;
}

.search-input:hover {
  border-color: #9ca3af;
}

.search-input:focus {
  outline: none;
  border-color: #6b7280;
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.1);
}

.input-icon {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  padding: 0 12px;
  pointer-events: none;
}

.search-icon {
  width: 16px;
  height: 16px;
  color: #6b7280;
}

/* 버튼 스타일 */
.search-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(to right, #4b5563, #374151);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

.search-button:hover {
  background: linear-gradient(to right, #374151, #1f2937);
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(100, 116, 139, 0.15);
}

.write-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

.write-button:hover {
  background: #d97706;
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(100, 116, 139, 0.15);
}

.button-icon {
  width: 16px;
  height: 16px;
}

/* 게시글 리스트 래퍼 */
.article-list-wrapper {
  border-top: 1px solid #e5e7eb;
  padding: 8px;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 64px 24px;
}

.empty-icon {
  width: 96px;
  height: 96px;
  margin: 0 auto 16px;
  color: #9ca3af;
}

.empty-title {
  font-size: 18px;
  font-weight: 500;
  color: #111827;
  margin-bottom: 8px;
}

.empty-description {
  color: #4b5563;
  margin-bottom: 24px;
}

.empty-write-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px -1px rgba(100, 116, 139, 0.06);
}

.empty-write-button:hover {
  background: #d97706;
  transform: translateY(-1px);
}

/* 더보기 버튼 래퍼 */
.load-more-wrapper {
  text-align: center;
  margin-top: 32px;
}

.load-more-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px -1px rgba(100, 116, 139, 0.06);
}

.load-more-button:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: #9ca3af;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

/* 애니메이션 - 헤더 제외 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .main-container {
    padding: 24px 16px;
  }
  
  .search-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-left {
    flex-direction: column;
  }
  
  .search-right {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .main-container {
    padding: 16px 12px;
  }
  
  .page-header {
    margin-bottom: 32px;
  }
  
  .main-title {
    font-size: 32px;
  }
  
  .main-subtitle {
    font-size: 16px;
  }
  
  .content-header {
    padding: 16px;
  }
  
  .header-top {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .board-description {
    margin-left: 0;
  }
  
  .search-left {
    gap: 8px;
  }
  
  .search-right {
    gap: 8px;
  }
  
  .select-wrapper {
    width: 100%;
  }
  
  .search-select {
    width: 100%;
  }
}

@media (max-width: 640px) {
  .main-title {
    font-size: 28px;
  }
  
  .main-subtitle {
    font-size: 16px;
  }
  
  .content-header {
    padding: 12px;
  }
}
</style>