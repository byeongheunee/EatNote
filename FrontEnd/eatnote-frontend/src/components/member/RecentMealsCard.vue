<template>
  <div class="recent-meals-container">
    <!-- 헤더 -->
    <div class="meals-header">
      <h2 class="meals-title">최근 식단</h2>
      <RouterLink to="/meals" class="view-all-link">
        <span>전체보기</span>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 18l6-6-6-6"/>
        </svg>
      </RouterLink>
    </div>

    <!-- 식단 슬라이더 -->
    <div class="meals-content">
      <div v-if="meals.length === 0" class="empty-state">
        <div class="empty-icon">🍽️</div>
        <p class="empty-text">아직 등록된 식단이 없어요</p>
        <RouterLink to="/meal/upload" class="empty-action">
          첫 식단 등록하기
        </RouterLink>
      </div>

      <div v-else class="slider-wrapper">
        <!-- 왼쪽 화살표 -->
        <button
          v-if="meals.length > visibleCount && canScrollLeft"
          class="nav-button nav-left"
          @click="scrollLeft"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </button>

        <!-- 가로 슬라이드 카드 리스트 -->
        <div 
          ref="scrollContainer" 
          class="meals-slider" 
          @scroll="checkScroll"
        >
          <div class="meals-track">
            <div
              v-for="meal in meals"
              :key="meal.mealId"
              class="meal-card"
              @click="goToMealDetail(meal.mealId)"
            >
              <!-- 이미지 컨테이너 -->
              <div class="meal-image-container">
                <img
                  :src="getImageUrl(meal.imageUrl)"
                  :alt="meal.detectedFoods"
                  class="meal-image"
                  @error="handleImageError"
                />
                
                <!-- 호버 오버레이 -->
                <div class="meal-overlay">
                  <div class="overlay-icon">👀</div>
                </div>
                
                <!-- 식사 시간 뱃지 -->
                <div class="meal-badge">
                  <span class="badge-emoji">{{ getMealTypeEmoji(meal.mealType) }}</span>
                </div>
              </div>

              <!-- 카드 내용 -->
              <div class="meal-content">
                <h4 class="meal-title">{{ meal.detectedFoods }}</h4>
                
                <div class="meal-meta">
                  <div class="meta-item">
                    <span class="meta-icon">📅</span>
                    <span class="meta-text">{{ formatDate(meal.mealTime) }}</span>
                  </div>
                  
                  <div v-if="meal.totalCalories" class="meta-item calories">
                    <span class="meta-icon">🔥</span>
                    <span class="meta-text">{{ Math.round(meal.totalCalories) }}kcal</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽 화살표 -->
        <button
          v-if="meals.length > visibleCount && canScrollRight"
          class="nav-button nav-right"
          @click="scrollRight"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6 6-6"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRouter, RouterLink } from 'vue-router'

const props = defineProps({
  meals: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()
const scrollContainer = ref(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)

// 화면 크기에 따른 보이는 카드 수 계산
const visibleCount = computed(() => {
  if (typeof window === 'undefined') return 3
  const width = window.innerWidth
  if (width >= 1200) return 3  // 대형 화면
  if (width >= 768) return 2   // 중간 화면
  return 1                     // 작은 화면
})

const scrollLeft = () => {
  const cardWidth = 220 // 카드 너비 + 간격
  scrollContainer.value?.scrollBy({ left: -cardWidth, behavior: 'smooth' })
}

const scrollRight = () => {
  const cardWidth = 220 // 카드 너비 + 간격
  scrollContainer.value?.scrollBy({ left: cardWidth, behavior: 'smooth' })
}

const checkScroll = () => {
  const el = scrollContainer.value
  if (!el) return
  canScrollLeft.value = el.scrollLeft > 5 // 5px 여유
  canScrollRight.value = el.scrollLeft + el.clientWidth < el.scrollWidth - 5
}

onMounted(() => {
  nextTick(() => {
    if (props.meals.length > visibleCount.value) {
      checkScroll()
    }
    
    // 윈도우 리사이즈 이벤트 리스너
    window.addEventListener('resize', checkScroll)
  })
})

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}`)
}

const getImageUrl = (url) => {
  return url ? `http://localhost:8080${url}` : '/images/default-meal.png'
}

const handleImageError = (event) => {
  event.target.src = '/images/default-meal.png'
}

const formatDate = (datetime) => {
  const date = new Date(datetime)
  return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`
}

const getMealTypeEmoji = (type) => {
  switch (type) {
    case 'breakfast': return '☀️'
    case 'lunch': return '🍽️'
    case 'dinner': return '🌙'
    case 'extra': return '🍩'
    default: return '🍽️'
  }
}
</script>

<style scoped>
.recent-meals-container {
  width: 100%;
  overflow: hidden; /* 컨테이너 범위 제한 */
}

/* 헤더 */
.meals-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
}

.meals-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: #6b7280;
  text-decoration: none;
  transition: all 0.3s ease;
  padding: 0.25rem 0.5rem;
  border-radius: 8px;
}

.view-all-link:hover {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
}

/* 컨텐츠 */
.meals-content {
  width: 100%;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 2rem 1rem;
  color: #6b7280;
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-text {
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.empty-action {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  text-decoration: none;
  padding: 0.6rem 1.2rem;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(245, 158, 11, 0.3);
}

.empty-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(245, 158, 11, 0.4);
}

/* 슬라이더 래퍼 */
.slider-wrapper {
  position: relative;
  width: 100%;
  overflow: hidden;
}

/* 네비게이션 버튼 */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  color: #f59e0b;
}

.nav-button:hover {
  background: #f59e0b;
  color: white;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 4px 15px rgba(245, 158, 11, 0.4);
}

.nav-left {
  left: -5px;
}

.nav-right {
  right: -5px;
}

/* 슬라이더 */
.meals-slider {
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  width: 100%;
}

.meals-slider::-webkit-scrollbar {
  display: none;
}

.meals-track {
  display: flex;
  gap: 1rem;
  padding: 0.5rem 0;
  width: max-content; /* 콘텐츠 크기에 맞춤 */
}

/* 식단 카드 */
.meal-card {
  flex: 0 0 200px; /* 고정 너비 */
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  height: 240px; /* 고정 높이 */
}

.meal-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 이미지 컨테이너 */
.meal-image-container {
  position: relative;
  width: 100%;
  height: 130px;
  overflow: hidden;
}

.meal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.meal-card:hover .meal-image {
  transform: scale(1.05);
}

/* 호버 오버레이 */
.meal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.meal-card:hover .meal-overlay {
  opacity: 1;
}

.overlay-icon {
  font-size: 1.2rem;
  color: white;
}

/* 식사 시간 뱃지 */
.meal-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(6px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.1);
}

.badge-emoji {
  font-size: 0.8rem;
}

/* 카드 내용 */
.meal-content {
  padding: 1rem;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.meal-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #374151;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

/* 메타 정보 */
.meal-meta {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.75rem;
}

.meta-icon {
  font-size: 0.8rem;
  opacity: 0.7;
}

.meta-text {
  color: #6b7280;
  font-weight: 500;
}

.meta-item.calories .meta-text {
  color: #f59e0b;
  font-weight: 600;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .meals-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
    margin-bottom: 1rem;
  }
  
  .meal-card {
    flex: 0 0 180px;
    height: 220px;
  }
  
  .meal-image-container {
    height: 120px;
  }
  
  .meal-content {
    padding: 0.8rem;
  }
  
  .nav-button {
    width: 28px;
    height: 28px;
  }
}

@media (max-width: 640px) {
  .meal-card {
    flex: 0 0 160px;
    height: 200px;
  }
  
  .meal-image-container {
    height: 110px;
  }
  
  .meal-title {
    font-size: 0.8rem;
  }
  
  .meta-item {
    font-size: 0.7rem;
  }
}

/* 애니메이션 */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.slider-wrapper {
  animation: slideInLeft 0.5s ease-out;
}
</style>