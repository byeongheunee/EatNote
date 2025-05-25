<template>
  <div class="member-dashboard-page">
    <Header />

    <div class="dashboard-container">
      <!-- 환영 섹션 -->
      <section class="welcome-section">
        <h1 class="welcome-title">
          안녕하세요, 
          <span class="username-highlight">{{ userNickname }}</span>님! 👋
        </h1>
      </section>

      <div class="dashboard-grid">
        <!-- 왼쪽 열 -->
        <div class="left-column">
          <!-- 나의 식단 현황 -->
          <section class="stats-section" v-if="mealStats">
            <div class="section-header">
              <h2 class="section-title">나의 식단 현황</h2>
              <RouterLink 
                to="/meal/upload" 
                class="new-meal-button"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                새 식단 등록
              </RouterLink>
            </div>
            <div class="stats-card">
              <MealStatsCard :stats="mealStats" />
            </div>
          </section>

          <!-- 최근 등록 식단 -->
          <section class="content-section">
            <div class="section-header">
              <h2 class="section-title">최근 등록 식단</h2>
              <div class="section-badge">
                <div class="w-2 h-2 bg-orange-400 rounded-full animate-pulse"></div>
                <span class="badge-text">{{ recentMeals.length }}개</span>
              </div>
            </div>
            <div class="card-container">
              <!-- 빈 상태 -->
              <div v-if="recentMeals.length === 0" class="empty-state">
                <div class="empty-icon">🍽️</div>
                <p class="empty-text">최근 등록 식단이 없습니다</p>
                <p class="empty-subtext">새로운 식단을 등록해 보세요!</p>
              </div>
              
              <!-- Swiper로 변경 -->
              <div v-else class="swiper-section">
                <Swiper 
                  :modules="[Navigation]" 
                  :slides-per-view="2"
                  :space-between="8" 
                  :breakpoints="{
                    640: { slidesPerView: 3, spaceBetween: 10 },
                    768: { slidesPerView: 3, spaceBetween: 12 },
                    1024: { slidesPerView: 3, spaceBetween: 14 }
                  }"
                  navigation
                  class="recent-meal-swiper"
                >
                  <SwiperSlide v-for="meal in recentMeals" :key="meal.mealId">
                    <div class="meal-card-wrapper">
                      <RecentMealsCard :meal="meal" />
                    </div>
                  </SwiperSlide>
                </Swiper>
              </div>
            </div>
          </section>

          <!-- 팔로우 요청 -->
          <section class="content-section">
            <div class="section-header">
              <h2 class="section-title">팔로우 요청</h2>
              <div class="section-badge">
                <div class="w-2 h-2 bg-blue-400 rounded-full animate-pulse"></div>
                <span class="badge-text">{{ followRequestCount }}개</span>
              </div>
            </div>
            <div class="card-container">
              <PendingFollowRequests @update-count="updateFollowRequestCount" />
            </div>
          </section>
        </div>

        <!-- 오른쪽 열 -->
        <div class="right-column">
          <!-- 최근 받은 피드백 -->
          <section class="feedback-section">
            <div class="section-header">
              <h2 class="section-title">최근 받은 피드백</h2>
              <div class="section-badge">
                <div class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                <span class="badge-text">{{ recentFeedbacks.length }}개</span>
              </div>
            </div>
            <div class="feedback-card-container">
              <RecentFeedbackCard :feedbacks="recentFeedbacks" />
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'

import Header from '@/components/common/Header.vue'
import MealStatsCard from '@/components/member/MealStatsCard.vue'
import RecentMealsCard from '@/components/member/RecentMealsCard.vue'
import RecentFeedbackCard from '@/components/member/RecentFeedbackCard.vue'
import PendingFollowRequests from '@/components/PendingFollowRequests.vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const token = computed(() => auth.accessToken)
const userNickname = computed(() => auth.user?.nickname || '회원')

const mealStats = ref(null)
const recentMeals = ref([])
const recentFeedbacks = ref([])
const followRequestCount = ref(0) // 팔로우 요청 개수 추가

const fetchDashboardData = async () => {
  try {
    const headers = {
      Authorization: `Bearer ${token.value}`
    }

    const [statsRes, mealsRes, feedbacksRes] = await Promise.all([
      axios.get('/api/meal/stats', { headers }),
      axios.get('/api/meal/my/recent', { headers }),
      axios.get('/api/meal/my/recent-feedbacks', { headers }),
    ])

    mealStats.value = statsRes.data.data
    recentMeals.value = mealsRes.data.data
    recentFeedbacks.value = feedbacksRes.data.data

    console.log('mealStats:', mealStats.value)
  } catch (e) {
    console.error('대시보드 데이터 로딩 실패:', e.response?.data || e)
  }
}

// 팔로우 요청 개수 업데이트 함수
const updateFollowRequestCount = (count) => {
  followRequestCount.value = count
}

onMounted(fetchDashboardData)
</script>

<style scoped>
/* 페이지 전체 배경 */
.member-dashboard-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 환영 섹션 */
.welcome-section {
  margin-bottom: 1.5rem;
  text-align: center;
}

.welcome-title {
  font-size: 2rem;
  font-weight: 700;
  color: #374151;
  margin: 0;
}

.username-highlight {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 통계 섹션 */
.stats-section {
  width: 100%;
}

.stats-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
}

/* 대시보드 그리드 */
.dashboard-grid {
  display: grid;
  grid-template-columns: 6.5fr 3.5fr;
  gap: 2rem;
  margin-bottom: 2rem;
  width: 100%;
  align-items: start;
  grid-template-rows: min-content;
}

/* 반응형 대응 */
@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
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

.section-icon {
  font-size: 1.5rem;
  opacity: 0.8;
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

/* 왼쪽 열 */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  min-width: 0;
}

/* 오른쪽 열 - 피드백 전용 */
.right-column {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* 피드백 섹션 - 고정 높이로 제한 */
.feedback-section {
  display: flex;
  flex-direction: column;
  animation: fadeInUp 0.6s ease-out;
  max-height: 1400px; /* 최대 높이 제한 */
}

/* 피드백 카드 컨테이너 - 스크롤 가능 */
.feedback-card-container {
  flex: 1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 400px; /* 최소 높이 설정 */
  max-height: 993px; /* 최대 높이 설정 */
}

.feedback-card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.12);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 일반 콘텐츠 섹션 */
.content-section {
  animation: fadeInUp 0.6s ease-out;
}

.content-section:nth-child(even) {
  animation-delay: 0.1s;
}

/* 빈 상태 */
.empty-state {
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

/* 카드 컨테이너 */
.card-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
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

/* 스위퍼 섹션 */
.swiper-section {
  position: relative;
  background: transparent;
  border-radius: 0;
  padding: 0 0px;
  box-shadow: none;
  border: none;
  overflow: hidden;
}

/* 최근 식단 스위퍼 스타일 */
.recent-meal-swiper {
  padding: 0;
  margin: 0;
}

.recent-meal-swiper :deep(.swiper-button-prev),
.recent-meal-swiper :deep(.swiper-button-next) {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border-radius: 50%;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.15);
  color: #f59e0b;
  transition: all 0.3s ease;
  border: 1px solid rgba(251, 191, 36, 0.3);
  z-index: 10;
}

.recent-meal-swiper :deep(.swiper-button-prev:hover),
.recent-meal-swiper :deep(.swiper-button-next:hover) {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  transform: scale(1.1);
  box-shadow: 0 5px 18px rgba(245, 158, 11, 0.4);
}

.recent-meal-swiper :deep(.swiper-button-prev) {
  left: 10px;
}

.recent-meal-swiper :deep(.swiper-button-next) {
  right: 10px;
}

.recent-meal-swiper :deep(.swiper-button-prev::after),
.recent-meal-swiper :deep(.swiper-button-next::after) {
  font-size: 12px;
  font-weight: bold;
}

.meal-card-wrapper {
  padding: 0;
  width: 100%;
}

/* 새 식단 등록 버튼 */
.new-meal-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  text-decoration: none;
}

.new-meal-button:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
  color: white;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .welcome-title {
    font-size: 1.5rem;
  }
  
  .card-container,
  .stats-card,
  .feedback-card-container {
    padding: 1.25rem;
  }
}

@media (max-width: 640px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .section-title {
    font-size: 1.25rem;
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

.welcome-title {
  animation: fadeInUp 0.6s ease-out;
}

.stats-card {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.1s;
}

/* 스크롤 애니메이션 */
.content-section:nth-child(1) { animation-delay: 0.2s; }
.content-section:nth-child(2) { animation-delay: 0.3s; }
.content-section:nth-child(3) { animation-delay: 0.4s; }
</style>