<template>
  <div class="feedback-container">
    <!-- 빈 상태 -->
    <div v-if="feedbacks.length === 0" class="empty-state">
      <div class="empty-icon">💭</div>
      <p class="empty-text">아직 받은 피드백이 없습니다</p>
      <p class="empty-subtext">트레이너의 피드백을 기다려보세요!</p>
    </div>

    <!-- 피드백 목록 -->
    <div v-else class="feedback-scroll-area">
      <div class="feedback-list">
        <div
          v-for="feedback in feedbacks"
          :key="feedback.feedbackId"
          class="feedback-item group"
          @click="goToMealDetail(feedback.mealId)"
        >
          <!-- 헤더: 트레이너 정보 -->
          <div class="feedback-header">
            <div class="trainer-info">
              <img
                :src="getProfileImage(feedback.profileImage)"
                alt="프로필"
                class="trainer-avatar"
                @error="handleImageError"
              />
              <div class="trainer-details">
                <p class="trainer-name">{{ feedback.trainerNickname }}</p>
                <p class="gym-name">{{ feedback.gymName }}</p>
              </div>
            </div>
            
            <!-- 점수 뱃지 -->
            <div class="score-badge" :class="getScoreClass(feedback.trainerScore)">
              <span class="score-text">{{ feedback.trainerScore }}/10</span>
            </div>
          </div>

          <!-- 식사 정보 -->
          <div class="meal-info">
            <div class="meal-type-badge" :class="getMealTypeBadgeClass(feedback.mealType)">
              <span class="meal-emoji">{{ getMealTypeEmoji(feedback.mealType) }}</span>
              <span class="meal-text">{{ translateMealType(feedback.mealType) }}</span>
            </div>
            <p class="detected-foods">{{ feedback.detectedFoods }}</p>
          </div>

          <!-- 피드백 내용 -->
          <div class="feedback-content">
            <!-- <div class="content-label">
              <span class="label-icon">💬</span>
              <span class="label-text">피드백</span>
            </div> -->
            <p class="content-text">{{ feedback.content }}</p>
          </div>

          <!-- 클릭 힌트 -->
          <div class="click-hint">
            <span class="hint-text">자세히 보기</span>
            <span class="hint-arrow">→</span>
          </div>

          <!-- 호버 효과 -->
          <div class="hover-overlay"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  feedbacks: Array
})

const router = useRouter()

const getProfileImage = (url) => {
  return url ? `http://localhost:8080${url}` : '/images/default-profile.png'
}

const handleImageError = (event) => {
  event.target.src = '/images/default-profile.png'
}

const translateMealType = (type) => {
  switch(type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}

const getMealTypeEmoji = (type) => {
  switch(type) {
    case 'breakfast': return '☀️'
    case 'lunch': return '🍽️'
    case 'dinner': return '🌙'
    case 'extra': return '🍩'
    default: return '🍽️'
  }
}

const getMealTypeBadgeClass = (type) => {
  switch(type) {
    case 'breakfast': return 'meal-breakfast'
    case 'lunch': return 'meal-lunch'
    case 'dinner': return 'meal-dinner'
    case 'extra': return 'meal-extra'
    default: return 'meal-lunch'
  }
}

const getScoreClass = (score) => {
  if (score >= 8) return 'score-excellent'
  if (score >= 6) return 'score-good'
  return 'score-needs-improvement'
}

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}`)
}
</script>

<style scoped>
.feedback-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 빈 상태 */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 2rem;
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

/* 스크롤 영역 */
.feedback-scroll-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
  min-height: 0; /* flex item이 줄어들 수 있도록 */
}

.feedback-scroll-area::-webkit-scrollbar {
  width: 6px;
}

.feedback-scroll-area::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.feedback-scroll-area::-webkit-scrollbar-thumb {
  background: rgba(245, 158, 11, 0.3);
  border-radius: 3px;
}

.feedback-scroll-area::-webkit-scrollbar-thumb:hover {
  background: rgba(245, 158, 11, 0.5);
}

/* 피드백 목록 */
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 개별 피드백 아이템 */
.feedback-item {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 211, 211, 0.3);
  border-radius: 16px;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  margin-top: 10px;
  margin-left: 5px;
}

.feedback-item:hover {
  transform: translateY(-2px) scale(1.01);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.15);
  border-color: rgba(16, 185, 129, 0.3);
}

/* 피드백 헤더 */
.feedback-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.trainer-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.trainer-avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.trainer-details {
  flex: 1;
}

.trainer-name {
  font-size: 0.95rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.125rem;
}

.gym-name {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

/* 점수 뱃지 */
.score-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 700;
  text-align: center;
  min-width: 3rem;
}

.score-excellent {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.score-good {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.score-needs-improvement {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 식사 정보 */
.meal-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.meal-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 600;
  flex-shrink: 0;
}

.meal-breakfast {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #d97706;
  border: 1px solid rgba(251, 191, 36, 0.3);
}

.meal-lunch {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #2563eb;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.meal-dinner {
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #4338ca;
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.meal-extra {
  background: linear-gradient(135deg, #fce7f3, #fbcfe8);
  color: #be185d;
  border: 1px solid rgba(236, 72, 153, 0.3);
}

.meal-emoji {
  font-size: 0.9rem;
}

.detected-foods {
  font-size: 0.9rem;
  color: #374151;
  font-weight: 600;
  line-height: 1.4;
}

/* 피드백 내용 */
.feedback-content {
  margin-bottom: 0.75rem;
}

.content-label {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  margin-bottom: 0.5rem;
}

.label-icon {
  font-size: 0.9rem;
}

.label-text {
  font-size: 0.8rem;
  font-weight: 600;
  color: #6b7280;
}

.content-text {
  font-size: 0.9rem;
  color: #374151;
  line-height: 1.5;
  background: rgba(249, 250, 251, 0.8);
  padding: 0.75rem;
  border-radius: 10px;
  border: 1px solid rgba(229, 231, 235, 0.5);
}

/* 클릭 힌트 */
.click-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.375rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feedback-item:hover .click-hint {
  opacity: 1;
}

.hint-text {
  font-size: 0.8rem;
  color: #10b981;
  font-weight: 600;
}

.hint-arrow {
  font-size: 0.8rem;
  color: #10b981;
  font-weight: bold;
}

/* 호버 효과 */
.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(5, 150, 105, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  border-radius: 16px;
}

.feedback-item:hover .hover-overlay {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .feedback-scroll-area {
    max-height: 300px;
  }
  
  .empty-state {
    min-height: 200px;
    padding: 2rem 1rem;
  }
  
  .feedback-item {
    padding: 1rem;
  }
  
  .trainer-avatar {
    width: 2.25rem;
    height: 2.25rem;
  }
  
  .trainer-name {
    font-size: 0.9rem;
  }
  
  .gym-name {
    font-size: 0.75rem;
  }
  
  .content-text {
    font-size: 0.85rem;
    padding: 0.625rem;
  }
}

/* 애니메이션 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feedback-item {
  animation: fadeInUp 0.5s ease-out;
}

.feedback-item:nth-child(1) { animation-delay: 0s; }
.feedback-item:nth-child(2) { animation-delay: 0.1s; }
.feedback-item:nth-child(3) { animation-delay: 0.2s; }
.feedback-item:nth-child(4) { animation-delay: 0.3s; }
.feedback-item:nth-child(5) { animation-delay: 0.4s; }
</style>