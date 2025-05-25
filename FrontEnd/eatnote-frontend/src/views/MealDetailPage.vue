<template>
  <div class="meal-detail-page">
    <Header />

    <div class="detail-container">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <div class="header-left">
          <button @click="goBackToMealList" class="back-button">
            <span class="back-icon">←</span>
            <span>목록으로 돌아가기</span>
          </button>
        </div>
        <h1 class="page-title">🍽️ 식단 상세보기</h1>
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
                <div class="meal-type-badge" :class="getMealTypeBadgeClass(meal.mealType)">
                  <span class="meal-emoji">{{ getMealTypeEmoji(meal.mealType) }}</span>
                  <span class="meal-type-text">{{ getMealTypeText(meal.mealType) }}</span>
                </div>
              </div>
              
              <div class="card-container">
                <div class="meal-image-section">
                  <img :src="getImageUrl(meal.imageUrl)" alt="식단 이미지" class="meal-image" />
                  
                  <div class="meal-basic-info">
                    <div class="author-info">
                      <span class="author-label">작성자</span>
                      <router-link :to="`/profile/${meal.userId}`" class="author-link">
                        <span class="author-icon">👤</span>
                        <span>{{ meal.userNickname }}</span>
                      </router-link>
                    </div>
                    
                    <div class="ai-score-display">
                      <span class="score-label">AI 점수</span>
                      <span class="score-value">{{ meal.autoScore }}/10</span>
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
                  <div class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                  <span class="badge-text">{{ trainerFeedbacks.length }}개</span>
                </div>
              </div>
              
              <div class="card-container feedback-container">
                <div v-if="trainerFeedbacks.length > 0" class="feedbacks-list">
                  <div 
                    v-for="feedback in trainerFeedbacks" 
                    :key="feedback.feedbackId" 
                    class="feedback-item"
                    :class="{ 'my-feedback': feedback.isWrittenByMe }"
                  >
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

        <!-- 2행: 반응 + 댓글 -->
        <div class="bottom-row">
          <section class="interaction-comments-section">
            <!-- 반응 부분 -->
            <div class="interaction-part">
              <!-- <div class="section-header">
                <h2 class="section-title">👍 반응</h2>
              </div> -->
              
              <LikeDislikeButtons 
                contentType="MEAL" 
                :contentId="meal.mealId" 
                :likeCount="meal.likeCount"
                :dislikeCount="meal.dislikeCount" 
                :myReaction="meal.myReaction" 
                :onUpdated="loadMeal" 
              />


              <!-- <div class="interaction-content">
                <LikeDislikeButtons 
                  contentType="MEAL" 
                  :contentId="meal.mealId" 
                  :likeCount="meal.likeCount"
                  :dislikeCount="meal.dislikeCount" 
                  :myReaction="meal.myReaction" 
                  :onUpdated="loadMeal" 
                />
              </div> -->
            </div>

            <!-- 댓글 부분 -->
            <div class="comments-part">
              <div class="comments-header">
                <h3 class="comments-title">
                  <svg class="title-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-3.582 8-8 8a9.863 9.863 0 01-4.906-1.294l-3.181.795.795-3.181A9.863 9.863 0 013 12c0-4.418 3.582-8 8-8s8 3.582 8 8z"></path>
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
const goBackToMealList = () => router.push('/meals')

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
    const token = localStorage.getItem('accessToken')
    const response = await axios.get(`/api/meal/${mealId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    meal.value = response.data.data
  } catch (err) {
    console.error('식단 불러오기 실패:', err)
  }
}

const loadTrainerFeedbacks = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      console.warn('토큰이 없습니다. 피드백 요청에 실패할 수 있습니다.')
    }

    const res = await axios.get(`/api/meal/${mealId}/feedback`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    console.log('🥩 받은 피드백:', res.data.data)
    trainerFeedbacks.value = res.data.data || []

  } catch (e) {
    console.error('트레이너 피드백 불러오기 실패:', e)
  }
}

const loadComments = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/comments', {
      params: { targetType: 'MEAL', targetId: mealId },
      headers: { Authorization: `Bearer ${token}` }
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
    const token = localStorage.getItem('accessToken')
    console.log('🔐 token:', token)
    await axios.delete(`/api/trainer/feedback/${feedbackId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    toast.success('피드백이 삭제되었습니다!')

    await loadTrainerFeedbacks()
  } catch (e) {
    toast.error('피드백 삭제에 실패했습니다.')
    console.error('삭제 오류:', e)
  }
}

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    myUserId.value = extractUserIdFromToken(token)

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
/* 페이지 전체 배경 */
.meal-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 페이지 헤더 */
.page-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  align-items: center;
  margin-bottom: 2rem;
}

.header-left, .header-right {
  display: flex;
}

.header-right {
  justify-content: flex-end;
}

.page-title {
  font-size: 2.25rem;
  font-weight: 700;
  color: #374151;
  margin: 0;
  text-align: center;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 뒤로가기 버튼 */
.back-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.3);
}

.back-icon {
  font-size: 1.1rem;
  font-weight: 700;
}

/* 로딩 및 에러 상태 */
.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

.loading-spinner, .error-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.loading-spinner {
  animation: pulse 2s infinite;
}

.loading-text, .error-text {
  font-size: 1.1rem;
  color: #6b7280;
  font-weight: 500;
}

/* 콘텐츠 레이아웃 - 2행 구성 */
.content-layout {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 1행: 식단 정보 + 트레이너 피드백 */
.top-row {
  display: grid;
  grid-template-columns: 7fr 5fr;
  gap: 2rem;
  align-items: start;
}

/* 2행: 반응 + 댓글 */
.bottom-row {
  width: 100%;
}

.interaction-comments-section {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.4s;
}

.interaction-comments-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.12);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 반응 부분 */
.interaction-part {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid rgba(229, 231, 235, 0.3);
}

.interaction-content {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 16px;
  padding: 1.5rem;
}

/* 댓글 부분 */
.comments-part {
  flex: 1;
}

.comments-content {
  max-height: 600px;
  overflow-y: auto;
}

/* 식단 열 */
.meal-column {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 피드백 열 */
.feedback-column {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 섹션 공통 */
.meal-section, .interaction-section, .feedback-section, .comments-section {
  animation: fadeInUp 0.6s ease-out;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 50px;
  padding: 0.5rem 1rem;
}

.badge-text {
  font-size: 0.85rem;
  font-weight: 600;
  color: #f59e0b;
}

/* 카드 컨테이너 */
.card-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
}

.card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.12);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 식사 타입 뱃지 */
.meal-type-badge {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.meal-type-badge.breakfast {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #d97706;
  border: 1px solid rgba(251, 191, 36, 0.3);
}

.meal-type-badge.lunch {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #2563eb;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.meal-type-badge.dinner {
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #4338ca;
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.meal-type-badge.extra {
  background: linear-gradient(135deg, #fce7f3, #fbcfe8);
  color: #be185d;
  border: 1px solid rgba(236, 72, 153, 0.3);
}

/* 식단 이미지 섹션 */
.meal-image-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.meal-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.meal-basic-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.author-info, .ai-score-display {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.author-label, .score-label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 600;
}

.author-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #f59e0b;
  text-decoration: none;
  font-weight: 700;
  transition: color 0.3s ease;
}

.author-link:hover {
  color: #d97706;
}

.author-icon {
  font-size: 1rem;
}

.score-value {
  font-size: 1.1rem;
  color: #f59e0b;
  font-weight: 700;
}

/* 정보 라벨 헤더 */
.info-label-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.label-icon {
  font-size: 1rem;
}

.label-text {
  font-size: 0.9rem;
  font-weight: 600;
  color: #6b7280;
}

/* 음식 정보 */
.food-info {
  margin-bottom: 1.5rem;
}

.food-text {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 1rem;
  font-size: 0.95rem;
  color: #374151;
  line-height: 1.5;
  margin: 0;
}

/* AI 피드백 섹션 */
.ai-feedback-section {
  margin-bottom: 1.5rem;
}

.ai-feedback-text {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(37, 99, 235, 0.05));
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  padding: 1rem;
  font-size: 0.95rem;
  color: #374151;
  line-height: 1.5;
  margin: 0;
}

/* 영양 정보 */
.nutrition-section {
  margin-top: 1.5rem;
}

.nutrition-title {
  font-size: 1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nutrition-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem;
}

.nutrition-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 0.75rem;
  text-align: center;
}

.nutrition-item.calories {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.05));
  border-color: rgba(245, 158, 11, 0.2);
}

.nutrition-label {
  display: block;
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.nutrition-value {
  display: block;
  font-size: 0.9rem;
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
  gap: 1rem;
}

.feedback-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 16px;
  padding: 1.25rem;
  transition: all 0.3s ease;
}

.feedback-item.my-feedback {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(37, 99, 235, 0.05));
  border-color: rgba(59, 130, 246, 0.2);
}

.feedback-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.trainer-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.trainer-name {
  font-weight: 700;
  color: #374151;
}

.my-badge {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 600;
}

.feedback-actions {
  display: flex;
  gap: 0.5rem;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.375rem 0.75rem;
  border: none;
  border-radius: 8px;
  font-size: 0.8rem;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.feedback-content {
  margin-top: 1rem;
}

.feedback-text {
  font-size: 0.95rem;
  color: #374151;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.feedback-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
  color: #6b7280;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 0.375rem;
}

.score-icon {
  font-size: 0.9rem;
}

.score-text {
  font-weight: 600;
  color: #f59e0b;
}

.feedback-date {
  font-weight: 500;
}

/* 빈 상태 */
.empty-feedback, .empty-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 3rem 2rem;
  min-height: 200px;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.empty-subtext {
  font-size: 0.9rem;
  color: #6b7280;
}

/* 댓글 컨테이너 */
.comments-container {
  max-height: 600px;
  overflow-y: auto;
}

.comment-input-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(229, 231, 235, 0.5);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* Tailwind 클래스 대체 */
.w-2 {
  width: 0.5rem;
}

.h-2 {
  height: 0.5rem;
}

.bg-green-400 {
  background-color: #4ade80;
}

.bg-blue-400 {
  background-color: #60a5fa;
}

.rounded-full {
  border-radius: 9999px;
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .top-row {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .page-header {
    grid-template-columns: auto 1fr auto;
    gap: 1rem;
  }
  
  .page-title {
    font-size: 1.75rem;
  }
}

@media (max-width: 768px) {
  .detail-container {
    padding: 1rem;
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
    gap: 0.75rem;
  }
  
  .feedback-actions {
    align-self: stretch;
    justify-content: flex-end;
  }
  
  .feedback-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}

@media (max-width: 640px) {
  .page-header {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 1rem;
  }
  
  .header-left {
    justify-content: center;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .back-button {
    font-size: 0.8rem;
    padding: 0.625rem 0.875rem;
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

/* 섹션별 애니메이션 지연 */
.meal-section { animation-delay: 0.1s; }
.feedback-section { animation-delay: 0.2s; }
.interaction-comments-section { animation-delay: 0.4s; }











.comments-header {
  margin-bottom: 1.5rem;
}

.comments-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.title-icon {
  width: 20px;
  height: 20px;
  color: #f59e0b;
}

</style>