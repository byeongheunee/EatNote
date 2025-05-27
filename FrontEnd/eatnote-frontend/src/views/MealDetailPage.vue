<template>
  <div class="meal-detail-page">
    <Header />

    <div class="detail-container">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <div class="header-left">
          <button @click="goBack" class="back-button">
            <span class="back-icon">←</span>
            <span>뒤로가기</span>
          </button>
        </div>

        <h1 class="page-title">식단 상세보기</h1>
        
        <!-- 빈 공간 -->
        <div class="header-right"></div>
      </section>

      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p class="loading-text">불러오는 중입니다...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <div class="error-icon">❌</div>
        <p class="error-text">에러가 발생했습니다: {{ error }}</p>
      </div>

      <div v-else-if="meal" class="content-layout">
        <!-- 1행: 식단 정보 + 트레이너 피드백 -->
        <div class="top-row">
          <!-- 왼쪽: 식단 정보 -->
          <div class="meal-column">
            <section class="meal-section">
              <div class="section-header">
                <h2 class="section-title">🥗 식단 정보</h2>
                <div class="header-actions">
                  <!-- 삭제 버튼을 여기로 이동 -->
                  <button 
                    v-if="meal && myUserId === meal.userId" 
                    @click="deleteMeal" 
                    class="delete-button-small"
                  >
                    <span class="delete-icon">🗑️</span>
                    <span>삭제</span>
                  </button>
                  <div class="meal-type-badge" :class="getMealTypeBadgeClass(meal.mealType)">
                    <span class="meal-emoji">{{ getMealTypeEmoji(meal.mealType) }}</span>
                    <span class="meal-type-text">{{ getMealTypeText(meal.mealType) }}</span>
                  </div>
                </div>
              </div>

              <div class="card-container">
                <div class="meal-image-section">
                  <img :src="getImageUrl(meal.imageUrl)" alt="식단 이미지" class="meal-image" />
                  <div class="meal-basic-info">
                    <!-- 첫 번째 줄: 식사 날짜 + 식사 종류 -->
                    <div class="info-row">
                      <div class="info-item">
                        <span class="info-label">식사 날짜</span>
                        <span class="info-value">{{ formatDate(meal.mealTime) }}</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">식사 종류</span>
                        <span class="info-value">{{ getMealTypeText(meal.mealType) }}</span>
                      </div>
                    </div>

                    <!-- 두 번째 줄: 작성자 + AI 점수 -->
                    <div class="info-row">
                      <div class="info-item">
                        <span class="info-label">작성자</span>
                        <router-link :to="`/profile/${meal.userId}`" class="author-link">
                          <span class="author-icon">👤</span>
                          <span>{{ meal.userNickname }}</span>
                        </router-link>
                      </div>
                      <div class="info-item">
                        <span class="info-label">AI 점수</span>
                        <span class="info-value score-highlight">{{ meal.autoScore }}/10</span>
                      </div>
                    </div>

                    <!-- 세 번째 줄: 좋아요 버튼 (오른쪽 정렬) -->
                    <div class="like-buttons-row">
                      <LikeDislikeButtons 
                        contentType="MEAL" 
                        :contentId="meal.mealId" 
                        :likeCount="meal.likeCount"
                        :dislikeCount="meal.dislikeCount" 
                        :myReaction="meal.myReaction" 
                        :onUpdated="loadMeal" 
                      />
                    </div>
                  </div>
                </div>

                <div class="food-info">
                  <div class="info-label-header">
                    <span class="label-icon">🍽️</span>
                    <span class="label-text">감지된 음식</span>
                  </div>
                  <p class="food-text">{{ meal.detectedFoods }}</p>
                </div>

                <div class="ai-feedback-section">
                  <div class="info-label-header">
                    <span class="label-icon">🤖</span>
                    <span class="label-text">AI 피드백</span>
                  </div>
                  <p class="ai-feedback-text">{{ meal.aiFeedback }}</p>
                </div>

                <!-- 영양 정보 -->
                <div class="nutrition-section">
                  <h3 class="nutrition-title">🧪 영양 성분</h3>
                  <div class="nutrition-grid">
                    <div class="nutrition-item calories">
                      <span class="nutrition-label">총 열량</span>
                      <span class="nutrition-value">{{ meal.totalCalories }} kcal</span>
                    </div>
                    <div class="nutrition-item">
                      <span class="nutrition-label">탄수화물</span>
                      <span class="nutrition-value">{{ meal.carbohydrates }} g</span>
                    </div>
                    <div class="nutrition-item">
                      <span class="nutrition-label">단백질</span>
                      <span class="nutrition-value">{{ meal.protein }} g</span>
                    </div>
                    <div class="nutrition-item">
                      <span class="nutrition-label">지방</span>
                      <span class="nutrition-value">{{ meal.fat }} g</span>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          </div>

          <!-- 오른쪽: 트레이너 피드백 -->
          <div class="feedback-column">
            <section class="feedback-section">
              <div class="section-header">
                <h2 class="section-title">💬 트레이너 피드백</h2>
                <div v-if="trainerFeedbacks.length > 0" class="section-badge">
                  <div class="pulse-dot"></div>
                  <span class="badge-text">{{ trainerFeedbacks.length }}개</span>
                </div>
              </div>

              <div class="card-container feedback-container">
                <div v-if="trainerFeedbacks.length > 0" class="feedbacks-list">
                  <div v-for="feedback in trainerFeedbacks" :key="feedback.feedbackId" class="feedback-item"
                    :class="{ 'my-feedback': feedback.isWrittenByMe }">
                    <div class="feedback-header">
                      <div class="trainer-info">
                        <span class="trainer-name">👤 {{ feedback.trainerNickname }}</span>
                        <span v-if="feedback.isWrittenByMe" class="my-badge">내 피드백</span>
                      </div>

                      <div v-if="feedback.isWrittenByMe" class="feedback-actions">
                        <button @click="goToEditFeedback(feedback.feedbackId)" class="action-button edit">
                          <span class="action-icon">✏️</span>
                          <span>수정</span>
                        </button>
                        <button @click="deleteFeedback(feedback.feedbackId)" class="action-button delete">
                          <span class="action-icon">🗑</span>
                          <span>삭제</span>
                        </button>
                      </div>
                    </div>

                    <div class="feedback-content">
                      <p class="feedback-text">{{ feedback.comment }}</p>
                      <div class="feedback-meta">
                        <div class="score-display">
                          <span class="score-icon">⭐</span>
                          <span class="score-text">{{ feedback.trainerScore }}/10</span>
                        </div>
                        <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-else class="empty-feedback">
                  <div class="empty-icon">💬</div>
                  <p class="empty-text">작성된 트레이너 피드백이 없습니다</p>
                  <p class="empty-subtext">트레이너의 피드백을 기다려보세요!</p>
                </div>
              </div>
            </section>
          </div>
        </div>

        <!-- 2행: 댓글 -->
        <div class="bottom-row">
          <section class="comments-section-only">
            <!-- 댓글 부분 -->
            <div class="comments-part">
              <div class="comments-header">
                <h3 class="comments-title">
                  <svg class="title-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-3.582 8-8 8a9.863 9.863 0 01-4.906-1.294l-3.181.795.795-3.181A9.863 9.863 0 013 12c0-4.418 3.582-8 8-8s8 3.582 8 8z">
                    </path>
                  </svg>
                  댓글 {{ comments.length }}개
                </h3>
              </div>

              <div class="comments-content">
                <div class="comment-input-section">
                  <CommentInput 
                    :parentCommentId="null" 
                    :onSubmit="loadComments" 
                    :targetType="targetType"
                    :targetId="mealId" 
                  />
                </div>

                <div v-if="comments.length > 0" class="comments-list">
                  <CommentItem 
                    v-for="comment in comments" 
                    :key="comment.commentId" 
                    :comment="comment"
                    :onReload="loadComments" 
                    :targetType="targetType" 
                    :targetId="mealId" 
                  />
                </div>

                <div v-else class="empty-comments">
                  <div class="empty-icon">💭</div>
                  <p class="empty-text">아직 댓글이 없습니다</p>
                  <p class="empty-subtext">첫 댓글을 작성해보세요!</p>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import CommentItem from '@/components/CommentItem.vue'
import CommentInput from '@/components/CommentInput.vue'
import LikeDislikeButtons from '@/components/LikeDislikeButtons.vue'
import Header from '@/components/common/Header.vue'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const toast = useToast()
const router = useRouter()
const route = useRoute()
const mealId = route.params.id

const meal = ref(null)
const comments = ref([])
const trainerFeedbacks = ref([])
const myUserId = ref(null)
const loading = ref(true)
const error = ref(null)
const targetType = "MEAL"

const getImageUrl = (path) => `http://localhost:8080${path}`
const goBack = () => { router.back() }

const formatDate = (datetime) => {
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}.${month}.${day} ${hours}:${minutes}`
}

const getMealTypeText = (type) => {
  const typeMap = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
    extra: '간식'
  }
  return typeMap[type] || type
}

const getMealTypeEmoji = (type) => {
  const emojiMap = {
    breakfast: '☀️',
    lunch: '🍽️',
    dinner: '🌙',
    extra: '🍩'
  }
  return emojiMap[type] || '🍽️'
}

const getMealTypeBadgeClass = (type) => {
  const classMap = {
    breakfast: 'breakfast',
    lunch: 'lunch',
    dinner: 'dinner',
    extra: 'extra'
  }
  return classMap[type] || 'lunch'
}

const extractUserIdFromToken = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1])).userId
  } catch {
    return null
  }
}

const loadMeal = async () => {
  try {
    const response = await axios.get(`/api/meal/${mealId}`)
    meal.value = response.data.data
  } catch (err) {
    console.error('식단 불러오기 실패:', err)
  }
}

const loadTrainerFeedbacks = async () => {
  try {
    if (!auth.accessToken) {
      console.warn('토큰이 없습니다. 피드백 요청에 실패할 수 있습니다.')
    }

    const res = await axios.get(`/api/meal/${mealId}/feedback`)

    console.log('🥩 받은 피드백:', res.data.data)
    trainerFeedbacks.value = res.data.data || []

  } catch (e) {
    console.error('트레이너 피드백 불러오기 실패:', e)
  }
}

const loadComments = async () => {
  try {
    const res = await axios.get('/api/comments', {
      params: { targetType: 'MEAL', targetId: mealId }
    })
    comments.value = res.data.data
  } catch (e) {
    console.error('댓글 불러오기 실패:', e)
  }
}

const goToEditFeedback = (feedbackId) => {
  router.push(`/trainer/feedback/edit/${feedbackId}`)
}

const deleteFeedback = async (feedbackId) => {
  if (!confirm('정말로 이 피드백을 삭제하시겠습니까?')) return
  try {
    console.log('🔐 token:', auth.accessToken)
    await axios.delete(`/api/trainer/feedback/${feedbackId}`)
    toast.success('피드백이 삭제되었습니다!')

    await loadTrainerFeedbacks()
  } catch (e) {
    toast.error('피드백 삭제에 실패했습니다.')
    console.error('삭제 오류:', e)
  }
}

// 식단 삭제 함수 추가
const deleteMeal = async () => {
  if (!confirm('정말로 이 식단을 삭제하시겠습니까?')) return
  
  try {
    await axios.delete(`/api/meal/${mealId}`)
    
    toast.success('식단이 삭제되었습니다!')
    
    // 삭제 후 이전 페이지로 이동
    router.back()
  } catch (e) {
    console.error('식단 삭제 실패:', e)
    
    // 에러 메시지 처리
    if (e.response && e.response.data && e.response.data.message) {
      toast.error(e.response.data.message)
    } else {
      toast.error('식단 삭제에 실패했습니다.')
    }
  }
}

onMounted(async () => {
  try {
    // 로그인한 사용자의 경우에만 userId 설정
    if (auth.user && auth.user.userId) {
      myUserId.value = auth.user.userId
    }

    await loadMeal()
    await loadComments()
    await loadTrainerFeedbacks()
  } catch (err) {
    error.value = err.message || '알 수 없는 오류'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 페이지 전체 배경 - 통일된 베이지 톤 */
.meal-detail-page {
  padding-top: 80px; /* 헤더 높이만큼 여백 */
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 통일 */
.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 페이지 헤더 */
.page-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  align-items: center;
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f59e0b;
}

.header-left,
.header-right {
  display: flex;
}

.header-right {
  justify-content: flex-end;
}

.page-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
  margin: 0;
  text-align: center;
}

/* 뒤로가기 버튼 - 통일된 버튼 스타일 */
.back-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: #d1d5db;
  transform: translateY(-1px);
}

.back-icon {
  font-size: 18px;
  font-weight: 700;
}

/* 섹션 헤더 액션 영역 - 식단 타입 뱃지와 삭제 버튼을 나란히 배치 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 작은 삭제 버튼 스타일 */
.delete-button-small {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #e96969, #dd5a5a);
  color: white;
  border: none;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 3px 5px -1px rgba(239, 68, 68, 0.3);
}

.delete-button-small:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  transform: translateY(-1px);
  box-shadow: 0 5px 7px -1px rgba(239, 68, 68, 0.4);
}

.delete-button-small .delete-icon {
  font-size: 14px;
}

/* 로딩 및 에러 상태 */
.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  text-align: center;
}

.loading-spinner,
.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.loading-spinner {
  animation: pulse 2s infinite;
}

.loading-text,
.error-text {
  font-size: 18px;
  color: #6b7280;
  font-weight: 500;
}

/* 콘텐츠 레이아웃 */
.content-layout {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 1행: 식단 정보 + 트레이너 피드백 */
.top-row {
  display: grid;
  grid-template-columns: 7fr 5fr;
  gap: 32px;
  align-items: start;
}

/* 2행: 반응 + 댓글 */
.bottom-row {
  width: 100%;
}

/* 2행: 댓글만 */
.comments-section-only {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.comments-section-only:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(100, 116, 139, 0.1), 0 10px 10px -5px rgba(100, 116, 139, 0.04);
}

/* 식단 열 */
.meal-column {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 피드백 열 */
.feedback-column {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 섹션 공통 */
.meal-section,
.feedback-section {
  animation: fadeInUp 0.6s ease-out;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2D1810;
}

.section-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 20px;
  padding: 8px 16px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.badge-text {
  font-size: 14px;
  font-weight: 600;
  color: #f59e0b;
}

/* 카드 컨테이너 - 통일된 글래스모피즘 스타일 */
.card-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(100, 116, 139, 0.1), 0 10px 10px -5px rgba(100, 116, 139, 0.04);
}

/* 식사 타입 뱃지 - 통일된 스타일 */
.meal-type-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.meal-type-badge.breakfast {
  background: rgba(255, 255, 255, 0.8);
  color: #f59e0b;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.meal-type-badge.lunch {
  background: rgba(255, 255, 255, 0.8);
  color: #3b82f6;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.meal-type-badge.dinner {
  background: rgba(255, 255, 255, 0.8);
  color: #8b5cf6;
  border: 1px solid rgba(139, 92, 246, 0.2);
}

.meal-type-badge.extra {
  background: rgba(255, 255, 255, 0.8);
  color: #ec4899;
  border: 1px solid rgba(236, 72, 153, 0.2);
}

/* 식단 이미지 섹션 */
.meal-image-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.meal-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

.meal-basic-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 정보 행 스타일 */
.info-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
}

.info-value {
  font-size: 16px;
  font-weight: 700;
  color: #374151;
}

.info-value.score-highlight {
  color: #f59e0b;
}

/* 좋아요 버튼 행 - 오른쪽 정렬 */
.like-buttons-row {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* LikeDislikeButtons 컴포넌트 크기 조정 */
.like-buttons-row :deep(.reaction-buttons) {
  transform: scale(0.7); /* 80% 크기로 축소 */
}

/* 정보 라벨 헤더 */
.info-label-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.label-icon {
  font-size: 16px;
}

.label-text {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
}

/* 음식 정보 */
.food-info {
  margin-bottom: 24px;
}

.food-text {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 16px;
  font-size: 15px;
  color: #374151;
  line-height: 1.6;
  margin: 0;
}

/* AI 피드백 섹션 */
.ai-feedback-section {
  margin-bottom: 24px;
}

.ai-feedback-text {
  background: rgba(59, 130, 246, 0.05);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  padding: 16px;
  font-size: 15px;
  color: #374151;
  line-height: 1.6;
  margin: 0;
}

/* 영양 정보 */
.nutrition-section {
  margin-top: 24px;
}

.nutrition-title {
  font-size: 16px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nutrition-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.nutrition-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 12px;
  text-align: center;
}

.nutrition-item.calories {
  background: rgba(245, 158, 11, 0.05);
  border-color: rgba(245, 158, 11, 0.2);
}

.nutrition-label {
  display: block;
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
  margin-bottom: 4px;
}

.nutrition-value {
  display: block;
  font-size: 14px;
  color: #374151;
  font-weight: 700;
}

/* 피드백 컨테이너 */
.feedback-container {
  height: 748px;
  overflow-y: auto;
}

.feedbacks-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
}

.feedback-item.my-feedback {
  background: rgba(59, 130, 246, 0.05);
  border-color: rgba(59, 130, 246, 0.2);
}

.feedback-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.trainer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.trainer-name {
  font-weight: 700;
  color: #374151;
}

.my-badge {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

.feedback-actions {
  display: flex;
  gap: 8px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-button.edit {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.action-button.delete {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px -1px rgba(100, 116, 139, 0.1);
}

.feedback-content {
  margin-top: 16px;
}

.feedback-text {
  font-size: 15px;
  color: #374151;
  line-height: 1.6;
  margin-bottom: 16px;
}

.feedback-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #6b7280;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 6px;
}

.score-icon {
  font-size: 14px;
}

.score-text {
  font-weight: 600;
  color: #f59e0b;
}

.feedback-date {
  font-weight: 500;
}

/* 빈 상태 */
.empty-feedback,
.empty-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 48px 32px;
  min-height: 200px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-text {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.empty-subtext {
  font-size: 14px;
  color: #6b7280;
}

/* 댓글 부분 */
.comments-part {
  flex: 1;
}

.comments-header {
  margin-bottom: 24px;
}

.comments-title {
  font-size: 22px;
  font-weight: 700;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  width: 20px;
  height: 20px;
  color: #f59e0b;
}

.comments-content {
  max-height: 600px;
  overflow-y: auto;
}

.comment-input-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(229, 231, 235, 0.5);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 작성자 링크 스타일 */
.author-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #f59e0b;
  text-decoration: none;
  font-weight: 700;
  transition: color 0.3s ease;
}

.author-link:hover {
  color: #d97706;
}

.author-icon {
  font-size: 16px;
}

/* 반응형 디자인 - 모바일에서 1열로 변경 */
@media (max-width: 640px) {
  .info-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

@media (max-width: 1024px) {
  .top-row {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .page-header {
    grid-template-columns: auto 1fr auto;
    gap: 16px;
  }

  .page-title {
    font-size: 28px;
  }

  .header-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .detail-container {
    padding: 16px 12px;
  }

  .page-header {
    margin-bottom: 32px;
  }

  .main-title {
    font-size: 32px;
  }

  .meal-image-section {
    grid-template-columns: 1fr;
  }

  .nutrition-grid {
    grid-template-columns: 1fr;
  }

  .feedback-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .feedback-actions {
    align-self: stretch;
    justify-content: flex-end;
  }

  .feedback-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 640px) {
  .page-header {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 16px;
  }

  .header-left {
    justify-content: center;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .back-button {
    font-size: 14px;
    padding: 10px 16px;
  }

  .delete-button-small {
    font-size: 12px;
    padding: 6px 12px;
  }
}

/* 애니메이션 */
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

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 애니메이션 업데이트 */
.interaction-comments-section {
  animation-delay: 0.3s;
}

.comments-section-only {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.3s;
}

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #f59e0b;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #d97706;
}
</style>