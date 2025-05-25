<template>
  <div 
    class="meal-card" 
    :class="{ 
      'pending': highlightPending && !meal.isFeedbackedByMe,
      'completed': meal.isFeedbackedByMe
    }"
    @click="handleCardClick"
  >
    <!-- 상태 뱃지 -->
    <div class="status-badge" :class="getStatusBadgeClass()">
      <span class="status-icon">{{ getStatusIcon() }}</span>
      <span class="status-text">{{ getStatusText() }}</span>
    </div>

    <!-- 헤더 -->
    <div class="meal-header">
      <div class="user-info">
        <h3 class="user-name">{{ meal.nickname }}님</h3>
        <span class="meal-date">{{ formatDate(meal.mealTime) }}</span>
      </div>
      <div class="meal-type-badge" :class="getMealTypeBadgeClass(meal.mealType)">
        <span class="meal-emoji">{{ getMealTypeEmoji(meal.mealType) }}</span>
        <span class="meal-type-text">{{ mealTypeMap[meal.mealType] || meal.mealType }}</span>
      </div>
    </div>

    <!-- 이미지 -->
    <div class="image-container">
      <img :src="getMealImage(meal.imageUrl)" class="meal-image" alt="식사 이미지" />
      <div class="image-overlay"></div>
    </div>

    <!-- 정보 섹션 -->
    <div class="meal-info">
      <div class="food-info">
        <div class="info-label">
          <span class="label-icon">🍽️</span>
          <span class="label-text">감지된 음식</span>
        </div>
        <p class="food-text">{{ meal.detectedFoods || '정보 없음' }}</p>
      </div>

      <div class="stats-grid">
        <div class="stat-item">
          <span class="stat-label">칼로리</span>
          <span class="stat-value">{{ meal.totalCalories ?? '-' }} kcal</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">AI 점수</span>
          <span class="stat-value">{{ meal.autoScore ?? '-' }}/10</span>
        </div>
      </div>
    </div>

    <!-- 피드백 상태 및 액션 -->
    <div class="action-section">
      <!-- 미작성 시 입력 버튼 -->
      <button 
        v-if="!meal.isFeedbackedByMe && highlightPending" 
        @click.stop="$emit('feedback', meal.mealId)"
        class="feedback-button primary"
      >
        <span class="button-icon">✏️</span>
        <span>피드백 입력</span>
      </button>

      <!-- 완료 시 상태 표시 -->
      <div v-else class="feedback-completed">
        <div class="completed-info">
          <div class="score-display">
            <span class="score-label">트레이너 점수</span>
            <span class="score-value">{{ meal.trainerScore }}/10</span>
          </div>
        </div>
        <button 
          @click.stop="$emit('view', meal.mealId)"
          class="feedback-button secondary"
        >
          <span class="button-icon">👁️</span>
          <span>상세 보기</span>
        </button>
      </div>
    </div>

    <!-- 호버 효과 -->
    <div class="hover-overlay"></div>
  </div>
</template>

<script setup>
const props = defineProps(['meal', 'highlightPending'])
const emit = defineEmits(['feedback', 'view', 'delete'])

const handleCardClick = () => {
  if (props.meal.isFeedbackedByMe) return
  emit('feedback', props.meal.mealId)
}

const getMealImage = (path) =>
  path ? `http://localhost:8080${path}` : '/images/default-meal.jpg'

const formatDate = (datetime) => {
  const date = new Date(datetime)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hours = date.getHours()
  const minutes = date.getMinutes()
  return `${month}/${day} ${hours}:${minutes.toString().padStart(2, '0')}`
}

const mealTypeMap = {
  breakfast: '아침',
  lunch: '점심',
  dinner: '저녁',
  extra: '간식'
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

const getStatusBadgeClass = () => {
  if (props.meal.isFeedbackedByMe) return 'status-completed'
  if (props.highlightPending) return 'status-pending'
  return 'status-neutral'
}

const getStatusIcon = () => {
  if (props.meal.isFeedbackedByMe) return '✅'
  if (props.highlightPending) return '⏰'
  return '📝'
}

const getStatusText = () => {
  if (props.meal.isFeedbackedByMe) return '완료'
  if (props.highlightPending) return '대기'
  return '일반'
}
</script>

<style scoped>
.meal-card {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  padding: 1.25rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.meal-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.meal-card.pending {
  border-color: rgba(245, 158, 11, 0.4);
}

.meal-card.pending:hover {
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.6);
}

.meal-card.completed {
  border-color: rgba(16, 185, 129, 0.4);
}

.meal-card.completed:hover {
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.15);
  border-color: rgba(16, 185, 129, 0.6);
}

/* 상태 뱃지 */
.status-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  z-index: 10;
  backdrop-filter: blur(8px);
}

.status-badge.status-pending {
  background: rgba(245, 158, 11, 0.9);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.status-badge.status-completed {
  background: rgba(16, 185, 129, 0.9);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.status-badge.status-neutral {
  background: rgba(107, 114, 128, 0.9);
  color: white;
}

/* 헤더 */
.meal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.25rem;
}

.meal-date {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
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

/* 이미지 */
.image-container {
  position: relative;
  margin-bottom: 1rem;
  border-radius: 16px;
  overflow: hidden;
}

.meal-image {
  width: 100%;
  height: 12rem;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.meal-card:hover .meal-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0) 0%,
    rgba(0, 0, 0, 0.1) 100%
  );
  pointer-events: none;
}

/* 정보 섹션 */
.meal-info {
  margin-bottom: 1rem;
}

.food-info {
  margin-bottom: 1rem;
}

.info-label {
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

.food-text {
  font-size: 0.9rem;
  color: #374151;
  line-height: 1.4;
  background: rgba(249, 250, 251, 0.8);
  padding: 0.75rem;
  border-radius: 12px;
  border: 1px solid rgba(229, 231, 235, 0.5);
  margin: 0;
}

/* 통계 그리드 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.stat-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 0.75rem;
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.stat-value {
  display: block;
  font-size: 0.9rem;
  color: #374151;
  font-weight: 700;
}

/* 액션 섹션 */
.action-section {
  margin-top: auto;
}

.feedback-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feedback-button.primary {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.feedback-button.primary:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

.feedback-button.secondary {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.feedback-button.secondary:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

/* 완료된 피드백 */
.feedback-completed {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.completed-info {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(5, 150, 105, 0.05));
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: 12px;
  padding: 0.75rem;
}

.score-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-label {
  font-size: 0.8rem;
  color: #059669;
  font-weight: 600;
}

.score-value {
  font-size: 1rem;
  color: #059669;
  font-weight: 700;
}

/* 호버 효과 */
.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.05) 0%, rgba(217, 119, 6, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  border-radius: 20px;
}

.meal-card:hover .hover-overlay {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .meal-card {
    padding: 1rem;
  }
  
  .meal-image {
    height: 10rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
  
  .meal-header {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .status-badge {
    position: static;
    align-self: flex-end;
    margin-bottom: 0.5rem;
  }
}

@media (max-width: 640px) {
  .user-name {
    font-size: 1rem;
  }
  
  .meal-date {
    font-size: 0.75rem;
  }
  
  .feedback-button {
    padding: 0.625rem 0.875rem;
    font-size: 0.85rem;
  }
}
</style>