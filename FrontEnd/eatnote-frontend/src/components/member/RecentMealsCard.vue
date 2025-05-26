<template>
  <div class="recent-meal-card group" @click="goToDetail">
    <!-- 이미지 컨테이너 -->
    <div class="image-container">
      <img :src="getImageUrl(meal.imageUrl)" :alt="meal.detectedFoods" class="meal-image" @error="handleImageError" />

      <!-- 호버 오버레이 -->
      <div class="image-overlay">
        <div class="overlay-content">
          <div class="view-icon">👀</div>
          <span class="view-text">자세히 보기</span>
        </div>
      </div>

      <!-- 식사 시간 뱃지 -->
      <div class="meal-time-badge">
        <span class="meal-time-emoji">{{ getMealTypeEmoji(meal.mealType) }}</span>
      </div>
    </div>

    <!-- 카드 내용 -->
    <div class="card-content">
      <!-- 감지된 음식 -->
      <h3 class="food-title">{{ meal.detectedFoods }}</h3>

      <!-- 정보 섹션 -->
      <div class="info-section">
        <div class="date-calories-row">
          <div class="date-info">
            <span class="date-icon">📅</span>
            <span class="date-text">{{ formatDate(meal.mealTime) }}</span>
          </div>

          <div v-if="meal.totalCalories" class="calories-info">
            <span class="calories-icon">🔥</span>
            <span class="calories-text">{{ Math.round(meal.totalCalories) }}kcal</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 카드 테두리 효과 -->
    <div class="card-border"></div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps(['meal'])
const router = useRouter()

// 날짜 포맷
const formatDate = (datetime) => {
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}.${month}.${day}`
}

// 식사 타입에 따른 이모티콘
const getMealTypeEmoji = (mealType) => {
  switch (mealType) {
    case 'breakfast': return '☀️' // 아침
    case 'lunch': return '🍽️' // 점심  
    case 'dinner': return '🌙' // 저녁
    case 'extra': return '🍩' // 간식
    default: return '🍽️'
  }
}

/* 식사 타입 번역 - 제거됨 */

// 이미지 URL
const getImageUrl = (path) => {
  return path ? `http://localhost:8080${path}` : '/images/default-meal.png'
}

// 이미지 로드 에러 처리
const handleImageError = (event) => {
  event.target.src = '/images/default-meal.png'
}

// 상세 페이지 이동
const goToDetail = () => {
  router.push(`/meal/${props.meal.mealId}`)
}
</script>

<style scoped>
.recent-meal-card {
  position: relative;
  width: 100%;
  max-width: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.06);
}

.recent-meal-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 8px 25px rgba(251, 191, 36, 0.2);
  border-color: rgba(251, 191, 36, 0.4);
}

/* 이미지 컨테이너 */
.image-container {
  position: relative;
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 12px 12px 0 0;
}

.meal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.recent-meal-card:hover .meal-image {
  transform: scale(1.08);
}

/* 호버 오버레이 */
.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.2) 0%, rgba(245, 158, 11, 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.recent-meal-card:hover .image-overlay {
  opacity: 1;
}

.overlay-content {
  text-align: center;
  color: white;
  transform: translateY(8px);
  transition: transform 0.3s ease;
}

.recent-meal-card:hover .overlay-content {
  transform: translateY(0);
}

.view-icon {
  font-size: 1.8rem;
  margin-bottom: 0.3rem;
}

.view-text {
  font-size: 0.8rem;
  font-weight: 600;
}

/* 식사 시간 뱃지 */
.meal-time-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.recent-meal-card:hover .meal-time-badge {
  transform: scale(1.1);
}

.meal-time-emoji {
  font-size: 1rem;
}

/* 카드 내용 */
.card-content {
  padding: 14px;
  position: relative;
  z-index: 2;
}

.food-title {
  font-size: 0.95rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 10px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.6rem;
}

/* 정보 섹션 */
.info-section {
  margin-bottom: 6px;
}

.date-calories-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.date-info,
.calories-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
}

.date-icon,
.calories-icon {
  font-size: 1rem;
  opacity: 0.8;
}

.date-text {
  color: #6b7280;
  font-weight: 600;
}

.calories-text {
  color: #d97706;
  font-weight: 700;
}

/* 카드 테두리 효과 */
.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, transparent 0%, rgba(251, 191, 36, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.recent-meal-card:hover .card-border {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .recent-meal-card {
    max-width: 100%;
  }

  .image-container {
    height: 120px;
  }

  .card-content {
    padding: 12px;
  }

  .food-title {
    font-size: 0.9rem;
  }
}

/* 포커스 상태 (접근성) */
.recent-meal-card:focus {
  outline: 2px solid #fbbf24;
  outline-offset: 2px;
}

/* 애니메이션 */
@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(15px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.recent-meal-card {
  animation: cardEnter 0.5s ease-out;
}
</style>