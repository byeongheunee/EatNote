<template>
  <div class="community-page">
    <Header />

    <!-- 메인 컨테이너 -->
    <div class="main-container">

      <!-- 페이지 헤더 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="main-title">게시글 상세</h1>
          <p class="main-subtitle">게시글의 자세한 내용을 확인하고 소통해보세요</p>
        </div>
      </div>

      <!-- 게시판 슬라이더 -->
      <div class="slider-wrapper">
        <BoardSlider
          :boards="filteredBoards"
          :selectedBoardId="selectedBoardId"
          @selectBoard="selectBoard"
        />
      </div>

      <!-- 게시글 상세 내용 -->
      <div class="content-card">
        <!-- 상단 네비게이션 -->
        <div class="content-header">
          <div class="header-top">
            <div class="breadcrumb">
              <span class="breadcrumb-item">커뮤니티</span>
              <span class="breadcrumb-separator">›</span>
              <span class="breadcrumb-item current">게시글 상세</span>
            </div>
            <div class="article-actions">
              <button @click="goBack" class="action-button">
                <span class="action-icon">📋</span>
                <span>목록으로</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 게시글 내용 -->
        <div class="article-content-wrapper">
          <ArticleDetail :articleId="articleId" />
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
.community-page {
  padding-top: 80px; /* 헤더 높이만큼 여백 */
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
}

/* 브레드크럼 */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-item {
  color: #4b5563;
  font-weight: 500;
}

.breadcrumb-item.current {
  color: #f59e0b;
  font-weight: 600;
}

.breadcrumb-separator {
  color: #4b5563;
  font-size: 16px;
}

/* 액션 버튼 */
.article-actions {
  display: flex;
  gap: 12px;
}

.action-button {
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

.action-button:hover {
  background: linear-gradient(to right, #374151, #1f2937);
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(100, 116, 139, 0.15);
}

.action-icon {
  font-size: 16px;
}

/* 게시글 내용 래퍼 */
.article-content-wrapper {
  border-top: 1px solid #e5e7eb;
  padding: 8px;
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
  
  .header-top {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .article-actions {
    width: 100%;
    justify-content: flex-end;
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
  
  .article-actions {
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