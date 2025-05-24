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

      <!-- 메인 콘텐츠: 좌우 2열 레이아웃 -->
      <div class="main-content">
        <!-- 왼쪽 컬럼: 나의 식단 현황 + 최근 등록 식단 + 팔로우 요청 -->
        <div class="left-column">
          <!-- 나의 식단 현황 -->
          <section class="content-section stats-section" v-if="mealStats">
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
              <RecentMealsCard :meals="recentMeals" />
            </div>
          </section>

          <!-- 팔로우 요청 -->
          <section class="content-section">
            <div class="section-header">
              <h2 class="section-title">팔로우 요청</h2>
              <div class="section-icon">👥</div>
            </div>
            
            <div class="card-container">
              <PendingFollowRequests />
            </div>
          </section>
        </div>

        <!-- 오른쪽 컬럼: 빠른 메뉴 + 최근 받은 피드백 -->
        <div class="right-column">
          <!-- 빠른 메뉴 -->
          <section class="content-section quick-menu-section">
            <div class="section-header">
              <h2 class="section-title">빠른 메뉴</h2>
              <div class="section-icon">⚡</div>
            </div>
            
            <div class="card-container">
              <QuickMenuCard />
            </div>
          </section>

          <!-- 최근 받은 피드백 -->
          <section class="content-section">
            <div class="section-header">
              <h2 class="section-title">최근 받은 피드백</h2>
              <div class="section-badge">
                <div class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                <span class="badge-text">{{ recentFeedbacks.length }}개</span>
              </div>
            </div>
            
            <div class="card-container feedback-card">
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
import Header from '@/components/common/Header.vue'
import MealStatsCard from '@/components/member/MealStatsCard.vue'
import RecentMealsCard from '@/components/member/RecentMealsCard.vue'
import RecentFeedbackCard from '@/components/member/RecentFeedbackCard.vue'
import QuickMenuCard from '@/components/member/QuickMenuCard.vue'
import PendingFollowRequests from '@/components/Trainer/PendingFollowRequests.vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const token = computed(() => auth.accessToken)
const userNickname = computed(() => auth.user?.nickname || '회원')

const mealStats = ref(null)
const recentMeals = ref([])
const recentFeedbacks = ref([])

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

onMounted(fetchDashboardData)
</script>

<style scoped>
/* 페이지 전체 배경 */
.member-dashboard-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

.dashboard-container {
  max-width: 1200px;
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

/* 메인 콘텐츠 레이아웃 */
.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr; /* 왼쪽 2, 오른쪽 1 비율 */
  gap: 2rem;
  align-items: start;
}

/* 컬럼 스타일 */
.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 콘텐츠 섹션 */
.content-section {
  animation: fadeInUp 0.6s ease-out;
}

.content-section:nth-child(1) { animation-delay: 0.1s; }
.content-section:nth-child(2) { animation-delay: 0.2s; }
.content-section:nth-child(3) { animation-delay: 0.3s; }
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

/* 섹션별 스타일 */
.stats-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
}

/* 피드백 카드 높이 조정 */
.feedback-card {
  height: 100%; /* 부모 컨테이너 높이에 맞춤 */
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
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr; /* 모바일에서 1열로 변경 */
    gap: 1.5rem;
  }
  
  .welcome-title {
    font-size: 1.75rem;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .welcome-title {
    font-size: 1.5rem;
  }
  
  .card-container,
  .stats-card {
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
.left-column .content-section:nth-child(1) { animation-delay: 0.1s; }
.left-column .content-section:nth-child(2) { animation-delay: 0.2s; }
.left-column .content-section:nth-child(3) { animation-delay: 0.3s; }
.right-column .content-section:nth-child(1) { animation-delay: 0.4s; }
.right-column .content-section:nth-child(2) { animation-delay: 0.5s; }
</style>