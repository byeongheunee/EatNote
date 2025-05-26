<template>
  <div class="article-detail-page">
    <Header />

    <div class="container mx-auto px-6 py-12 max-w-7xl">
      
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="main-title">게시글 상세</h1>
          <p class="main-subtitle">게시글의 자세한 내용을 확인하고 소통해보세요</p>
        </div>
      </div>

      <!-- 게시판 슬라이더 -->
      <div class="board-slider-wrapper">
        <BoardSlider
          :boards="filteredBoards"
          :selectedBoardId="selectedBoardId"
          @selectBoard="selectBoard"
        />
      </div>

      <!-- 게시글 상세 컨테이너 -->
      <div class="article-detail-container">
        <div class="article-wrapper">
          <!-- 상단 네비게이션 -->
          <div class="article-navigation">
            <div class="breadcrumb">
              <span class="breadcrumb-item">커뮤니티</span>
              <span class="breadcrumb-separator">›</span>
              <span class="breadcrumb-item current">게시글 상세</span>
            </div>
            <div class="article-actions">
              <button 
                @click="goBack"
                class="action-button list"
              >
                <span class="action-icon">📋</span>
                <span>목록으로</span>
              </button>
            </div>
          </div>

          <!-- 게시글 내용 -->
          <div class="article-content-wrapper">
            <ArticleDetail :articleId="articleId" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'
import Header from '@/components/common/Header.vue'
import BoardSlider from '@/components/BoardSlider.vue'
import ArticleDetail from '@/components/ArticleDetail.vue'
import axios from 'axios'

const auth = useAuthStore()
const userType = computed(() => auth.user?.userType)

const route = useRoute()
const router = useRouter()

const articleId = Number(route.params.articleId)
const selectedBoardId = ref(Number(route.params.boardId))

const boards = ref([])

const fetchBoards = async () => {
  try {
    const res = await axios.get('/api/boards')
    boards.value = res.data.data
  } catch (error) {
    console.error('게시판 목록 조회 실패:', error)
  }
}

const selectBoard = (id) => {
  router.push(`/community/${id}`)
}

const goBack = () => {
  router.push(`/community/${selectedBoardId.value}`)
}

const goToWrite = () => {
  router.push(`/community/${selectedBoardId.value}/write`)
}

const filteredBoards = computed(() => {
  const type = userType.value

  return boards.value.filter(b => {
    if (b.accessCode === 'TRAINER_ONLY') {
      return type === 0 || type === 1
    }
    return true
  })
})

onMounted(() => {
  fetchBoards()
})
</script>

<style scoped>
/* 페이지 전체 배경 */
.article-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
  position: relative;
}

.article-detail-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(255, 107, 71, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 215, 0, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.container {
  max-width: 1400px;
  position: relative;
  z-index: 1;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid rgba(245, 158, 11, 0.2);
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

/* 네비게이션 버튼 */
.nav-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
  min-width: 140px;
  justify-content: center;
}

.nav-button.secondary {
  background: rgba(255, 255, 255, 0.9);
  color: #5D4037;
  border: 2px solid rgba(255, 140, 105, 0.3);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.nav-button.secondary:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #FF8C69;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 140, 105, 0.3);
}

.button-icon {
  font-size: 18px;
}

/* 게시판 슬라이더 래퍼 */
.board-slider-wrapper {
  margin-bottom: 40px;
}

/* 게시글 상세 컨테이너 */
.article-detail-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 
    0 12px 40px rgba(255, 107, 71, 0.15),
    0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 140, 105, 0.2);
  overflow: hidden;
}

.article-wrapper {
  position: relative;
}

/* 상단 네비게이션 */
.article-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background: linear-gradient(135deg, #FFF8E7 0%, #FFFBF0 100%);
  border-bottom: 1px solid rgba(255, 140, 105, 0.2);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-item {
  color: #8D6E63;
  font-weight: 500;
}

.breadcrumb-item.current {
  color: #FF6B47;
  font-weight: 600;
}

.breadcrumb-separator {
  color: #8D6E63;
  font-size: 16px;
}

.article-actions {
  display: flex;
  gap: 12px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 140, 105, 0.3);
  border-radius: 8px;
  color: #5D4037;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-button:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #FF8C69;
  transform: translateY(-1px);
}

.action-icon {
  font-size: 16px;
}

/* 게시글 내용 래퍼 */
.article-content-wrapper {
  padding: 0;
}

/* 애니메이션 */
.container {
  animation: fadeInUp 0.6s ease-out;
}

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
@media (max-width: 768px) {
  .container {
    padding: 24px 16px;
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
  
  .board-slider-wrapper {
    margin-bottom: 32px;
  }
  
  .article-navigation {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
    padding: 20px;
  }
  
  .article-actions {
    width: 100%;
    justify-content: flex-end;
  }
}

@media (max-width: 480px) {
  .article-navigation {
    padding: 16px;
  }
  
  .action-button {
    padding: 6px 12px;
    font-size: 13px;
  }
}
</style>