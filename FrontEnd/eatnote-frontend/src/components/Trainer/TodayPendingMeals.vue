<template>
  <div class="pending-meals-container">
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner">⏳</div>
      <p class="loading-text">피드백 대기 식단을 불러오는 중...</p>
    </div>
    
    <div v-else-if="meals.length === 0" class="empty-state">
      <div class="empty-icon">🍽️</div>
      <p class="empty-text">피드백 미완료 식단이 없습니다</p>
      <p class="empty-subtext">모든 식단에 피드백을 완료하셨네요! 👏</p>
    </div>

    <div v-else class="meals-swiper-container">
      <Swiper 
        :modules="[Navigation]" 
        :slides-per-view="1"
        :space-between="16" 
        :breakpoints="{
          640: { slidesPerView: 2, spaceBetween: 12 },
          768: { slidesPerView: 3, spaceBetween: 16 },
          1024: { slidesPerView: 5, spaceBetween: 16 },
          1280: { slidesPerView: 5, spaceBetween: 20 }
        }"
        navigation
        class="pending-meals-swiper"
      >
        <SwiperSlide v-for="meal in meals" :key="meal.mealId">
          <div class="meal-card-wrapper">
            <div class="meal-card" @click="goToMeal(meal.mealId)">
              <div class="meal-image-container">
                <img :src="getImageUrl(meal.imageUrl)" alt="식단 이미지" class="meal-image" />
                <div class="urgent-badge">
                  <span class="urgent-text">피드백 대기</span>
                </div>
              </div>
              
              <div class="meal-info">
                <div class="user-date-row">
                  <p class="user-name">{{ meal.nickname }}</p>
                  <p class="meal-date">{{ formatDate(meal.mealTime) }}</p>
                </div>
                
                <div class="meal-details">
                  <p class="meal-foods">🍽️ {{ meal.detectedFoods }}</p>
                </div>
                
                <button class="feedback-button" @click.stop="goToMeal(meal.mealId)">
                  <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                  </svg>
                  피드백 작성
                </button>
              </div>
            </div>
          </div>
        </SwiperSlide>
      </Swiper>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const meals = ref([])
const loading = ref(true)

// emit 정의
const emit = defineEmits(['update-count'])

// 아바타 이모지 생성 함수
const getAvatarEmoji = (nickname) => {
  const emojis = ['😊', '😎', '🤗', '😄', '🙂', '😋', '🤔', '😌', '🥰', '😇']
  const index = nickname ? nickname.charCodeAt(0) % emojis.length : 0
  return emojis[index]
}

// 이미지 URL 생성
const getImageUrl = (path) => `http://localhost:8080${path}`

// 날짜 포맷팅
const formatDate = (datetime) => {
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}.${month}.${day}`
}

// 피드백 페이지로 이동
const goToMeal = (mealId) => {
  router.push(`/trainer/feedback/${mealId}`)
}

// 대기 중인 식단 목록 조회
const fetchPendingMeals = async () => {
  try {
    const token = auth.accessToken
    const res = await axios.get('/api/trainer/pending', {
      headers: { Authorization: `Bearer ${token}` }
    })
    meals.value = res.data.data || []
  } catch (e) {
    console.error('피드백 대기 식단 불러오기 실패:', e)
  } finally {
    loading.value = false
  }
}

// meals 배열 변화 감지하여 부모에 개수 전달
watch(meals, (newMeals) => {
  emit('update-count', newMeals.length)
}, { immediate: true })

onMounted(fetchPendingMeals)
</script>

<style scoped>
.pending-meals-container {
  width: 100%;
  min-height: 200px;
}

/* 로딩 상태 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
  text-align: center;
}

.loading-spinner {
  font-size: 48px;
  margin-bottom: 16px;
  animation: pulse 2s infinite;
}

.loading-text {
  font-size: 16px;
  color: #6b7280;
  font-weight: 500;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
  text-align: center;
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

/* 스위퍼 컨테이너 */
.meals-swiper-container {
  position: relative;
  width: 100%;
}

.pending-meals-swiper {
  padding: 0;
  margin: 0;
}

.pending-meals-swiper :deep(.swiper-button-prev),
.pending-meals-swiper :deep(.swiper-button-next) {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border-radius: 50%;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  color: #f59e0b;
  transition: all 0.3s ease;
  border: 1px solid rgba(245, 158, 11, 0.3);
  z-index: 10;
}

.pending-meals-swiper :deep(.swiper-button-prev:hover),
.pending-meals-swiper :deep(.swiper-button-next:hover) {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  transform: scale(1.15);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.5);
}

.pending-meals-swiper :deep(.swiper-button-prev) {
  left: 10px;
}

.pending-meals-swiper :deep(.swiper-button-next) {
  right: 10px;
}

.pending-meals-swiper :deep(.swiper-button-prev::after),
.pending-meals-swiper :deep(.swiper-button-next::after) {
  font-size: 14px;
  font-weight: bold;
}

/* 식단 카드 래퍼 */
.meal-card-wrapper {
  padding: 0;
  width: 100%;
}

/* 식단 카드 */
.meal-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(245, 158, 11, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-top: 5px;
  margin-left: 5px;
}

.meal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.2);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 식단 이미지 컨테이너 */
.meal-image-container {
  position: relative;
  width: 100%;
  height: 160px;
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

.urgent-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #eb5c5c, #eb5c5c);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
  animation: pulse 2s infinite;
}

.urgent-text {
  white-space: nowrap;
}

/* 식단 정보 */
.meal-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 사용자명과 날짜 행 - 한 줄에 좌우 배치 */
.user-date-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-name {
  font-weight: 700;
  color: #374151;
  font-size: 14px;
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meal-date {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
  margin: 0;
  flex-shrink: 0;
  margin-left: 8px;
}

.meal-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meal-foods {
  font-size: 13px;
  color: #374151;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 피드백 버튼 */
.feedback-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  padding: 10px 16px;
  background: linear-gradient(135deg, #f1976a, #f1976a);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.feedback-button:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
}

.button-icon {
  width: 14px;
  height: 14px;
}

/* 애니메이션 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .loading-state,
  .empty-state {
    padding: 32px 20px;
  }
  
  .empty-icon {
    font-size: 40px;
  }
  
  .empty-text {
    font-size: 16px;
  }
  
  .empty-subtext {
    font-size: 13px;
  }
  
  .meal-image-container {
    height: 140px;
  }
  
  .meal-info {
    padding: 12px;
    gap: 10px;
  }
  
  .user-name {
    font-size: 13px;
  }
  
  .meal-date {
    font-size: 11px;
  }
  
  .meal-foods {
    font-size: 12px;
  }
  
  .feedback-button {
    padding: 8px 14px;
    font-size: 12px;
  }
  
  .button-icon {
    width: 12px;
    height: 12px;
  }
}

@media (max-width: 640px) {
  .meal-image-container {
    height: 120px;
  }
  
  .urgent-badge {
    font-size: 10px;
    padding: 3px 6px;
  }
}
</style>