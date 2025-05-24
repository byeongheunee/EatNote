<template>
  <div class="article-detail-page">
    <Header />

    <!-- 메인 컨테이너 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 페이지 헤더 -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">
          <span class="bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent">
            게시글 상세
          </span>
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          게시글의 자세한 내용을 확인하세요
        </p>
      </div>

      <!-- 게시판 슬라이더 -->
      <div class="mb-8">
        <BoardSlider
          :boards="filteredBoards"
          :selectedBoardId="selectedBoardId"
          @selectBoard="selectBoard"
        />
      </div>

      <!-- 뒤로가기 버튼 -->
      <div class="mb-6">
        <button 
          @click="goBack"
          class="inline-flex items-center gap-2 px-4 py-2.5 bg-white/80 backdrop-blur-sm text-gray-700 text-sm font-medium rounded-lg border border-orange-200 hover:bg-white/90 hover:border-orange-300 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 transition-all duration-200 shadow-md hover:shadow-lg"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          목록으로 돌아가기
        </button>
      </div>

      <!-- 게시글 상세 컨테이너 -->
      <div class="article-detail-container">
        <ArticleDetail :articleId="articleId" />
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
  const res = await axios.get('/api/boards')
  boards.value = res.data.data
}

const selectBoard = (id) => {
  router.push(`/community/${id}`) // 게시판 선택 시 목록으로 이동
}

const goBack = () => {
  router.push(`/community/${selectedBoardId.value}`)
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

onMounted(fetchBoards)
</script>

<style scoped>
/* 페이지 전체 배경 - 홈뷰와 동일한 색상 */
.article-detail-page {
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

/* 게시글 상세 컨테이너 */
.article-detail-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.1);
  overflow: hidden;
  padding: 0;
}

/* 뒤로가기 버튼 스타일 개선 */
button:hover {
  transform: translateY(-1px);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .article-detail-page {
    padding: 1rem;
  }

  .max-w-7xl {
    padding: 0 1rem;
  }

  .text-4xl {
    font-size: 2rem;
  }

  .py-8 {
    padding-top: 1.5rem;
    padding-bottom: 1.5rem;
  }
}

@media (max-width: 640px) {
  .px-4 {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .py-8 {
    padding-top: 1rem;
    padding-bottom: 1rem;
  }

  .mb-8 {
    margin-bottom: 1.5rem;
  }

  .mb-6 {
    margin-bottom: 1rem;
  }
}

/* 애니메이션 */
.article-detail-page {
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

/* 그림자 효과 개선 */
.shadow-lg {
  box-shadow: 0 10px 15px -3px rgba(245, 158, 11, 0.1), 0 4px 6px -2px rgba(245, 158, 11, 0.05);
}

.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(245, 158, 11, 0.1), 0 2px 4px -1px rgba(245, 158, 11, 0.06);
}

/* 포커스 효과 - 오렌지 색상으로 변경 */
button:focus {
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

/* 테두리 및 배경 투명도 조정 */
.bg-white\/80 {
  background-color: rgba(255, 255, 255, 0.8);
}

.bg-white\/90 {
  background-color: rgba(255, 255, 255, 0.9);
}

/* 오렌지 테마 색상 변수들 */
.border-orange-200 {
  border-color: #fec89f;
}

.border-orange-300 {
  border-color: #fdba74;
}

.hover\:border-orange-300:hover {
  border-color: #fdba74;
}

/* 부드러운 전환 효과 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

.duration-200 {
  transition-duration: 200ms;
}

/* 호버 시 상승 효과 */
.hover\:shadow-lg:hover {
  box-shadow: 0 10px 15px -3px rgba(245, 158, 11, 0.1), 0 4px 6px -2px rgba(245, 158, 11, 0.05);
}
</style>