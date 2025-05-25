<template>
  <div 
    class="meal-card group"
    @click="goToDetail"
  >
    <!-- 이미지 컨테이너 -->
    <div class="image-container">
      <img 
        :src="getImageUrl(meal.imageUrl)" 
        :alt="meal.detectedFoods"
        class="meal-image" 
        @error="handleImageError"
      />
      
      <!-- 호버 오버레이 -->
      <div class="image-overlay">
        <div class="overlay-content">
          <div class="view-icon">👀</div>
          <span class="view-text">자세히 보기</span>
        </div>
      </div>
      
      <!-- 식사 시간 뱃지 -->
      <div class="meal-time-badge">
        <span class="meal-time-emoji">{{ getMealTimeEmoji(meal.mealTime) }}</span>
      </div>
    </div>

    <!-- 카드 내용 -->
    <div class="card-content">
      <!-- 감지된 음식 -->
      <h3 class="food-title">{{ meal.detectedFoods }}</h3>
      
      <!-- 정보 섹션 -->
      <div class="info-section">
        <div class="name-date-row">
          <div class="author-info">
            <span class="author-icon">👤</span>
            <span class="author-text">{{ meal.nickname }}</span>
          </div>
          
          <div class="date-info">
            <span class="date-icon">📅</span>
            <span class="date-text">{{ formatDate(meal.mealTime) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 추가 정보 (있는 경우) -->
      <div v-if="meal.totalCalories" class="calories-info">
        <span class="calories-icon">🔥</span>
        <span class="calories-text">{{ meal.totalCalories }}kcal</span>
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

// 날짜 포맷 (연도 포함)
const formatDate = (datetime) => {
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}.${month}.${day}`
}

// 식사 시간에 따른 이모티콘
const getMealTimeEmoji = (datetime) => {
  const hour = new Date(datetime).getHours()
  if (hour >= 5 && hour < 11) return '☀️' // 아침
  if (hour >= 11 && hour < 16) return '🍽️' // 점심  
  if (hour >= 16 && hour < 21) return '🌙' // 저녁
  return '🍩' // 간식
}

// 이미지 URL
const getImageUrl = (path) => {
  return `http://localhost:8080${path}`
}

// 이미지 로드 에러 처리
const handleImageError = (event) => {
  event.target.src = '/images/default-meal.png' // 기본 이미지
}

// 상세 페이지 이동
const goToDetail = () => {
  router.push(`/meal/${props.meal.mealId}`)
}
</script>

<style scoped>
.meal-card {
  position: relative;
  width: 100%;
  max-width: 280px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.meal-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 35px rgba(251, 191, 36, 0.25);
  border-color: rgba(251, 191, 36, 0.4);
}

/* 이미지 컨테이너 */
.image-container {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  border-radius: 16px 16px 0 0;
}

.meal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.meal-card:hover .meal-image {
  transform: scale(1.1);
}

/* 호버 오버레이 */
.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.8) 0%, rgba(245, 158, 11, 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.meal-card:hover .image-overlay {
  opacity: 1;
}

.overlay-content {
  text-align: center;
  color: white;
  transform: translateY(10px);
  transition: transform 0.3s ease;
}

.meal-card:hover .overlay-content {
  transform: translateY(0);
}

.view-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.view-text {
  font-size: 0.9rem;
  font-weight: 600;
}

/* 식사 시간 뱃지 */
.meal-time-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.meal-card:hover .meal-time-badge {
  transform: scale(1.1);
}

.meal-time-emoji {
  font-size: 1.2rem;
}

/* 카드 내용 */
.card-content {
  padding: 16px;
  position: relative;
  z-index: 2;
}

.food-title {
  font-size: 1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8rem;
}

/* 정보 섹션 */
.info-section {
  margin-bottom: 8px;
}

.name-date-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.date-info,
.author-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
}

.date-icon,
.author-icon {
  font-size: 0.9rem;
  opacity: 0.8;
}

.date-text,
.author-text {
  color: #6b7280;
  font-weight: 500;
}

/* 칼로리 정보 */
.calories-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 6px 10px;
  background: linear-gradient(135deg, #f5f4f0, #f8f4e3);
  border-radius: 12px;
  border: 1.5px solid rgba(231, 126, 28, 0.3);
}

.calories-icon {
  font-size: 0.9rem;
}

.calories-text {
  font-size: 0.8rem;
  font-weight: 600;
  color: #d97706;
}

/* 카드 테두리 효과 */
.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 20px;
  background: linear-gradient(135deg, transparent 0%, rgba(251, 191, 36, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.meal-card:hover .card-border {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .meal-card {
    max-width: 100%;
  }
  
  .image-container {
    height: 140px;
  }
  
  .card-content {
    padding: 12px;
  }
  
  .food-title {
    font-size: 0.9rem;
  }
}

/* 로딩 상태 */
.meal-image[src=""] {
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
  position: relative;
}

.meal-image[src=""]:before {
  content: "🍽️";
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 2rem;
  opacity: 0.5;
}

/* 애니메이션 */
@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.meal-card {
  animation: cardEnter 0.6s ease-out;
}

/* 포커스 상태 (접근성) */
.meal-card:focus {
  outline: 2px solid #fbbf24;
  outline-offset: 2px;
}
</style>