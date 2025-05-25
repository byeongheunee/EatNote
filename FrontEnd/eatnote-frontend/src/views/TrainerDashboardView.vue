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
}<template>
  <div class="trainer-dashboard-page">
    <Header />

    <div class="dashboard-container">
      <!-- 환영 섹션 -->
      <section class="welcome-section">
        <h1 class="welcome-title">
          안녕하세요, 
          <span class="username-highlight">{{ userNickname }}</span>님! 💪
        </h1>
      </section>

      <div class="dashboard-content">
        <!-- 오늘의 대기 중인 식단 -->
        <section class="content-section">
          <div class="section-header">
            <h2 class="section-title">오늘의 대기 중인 식단</h2>
            <div class="section-badge">
              <div class="w-2 h-2 bg-red-400 rounded-full animate-pulse"></div>
              <span class="badge-text">{{ pendingMealsCount }}개</span>
            </div>
          </div>
          <div class="card-container pending-meals-card">
            <TodayPendingMeals @update-count="updatePendingMealsCount" />
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
            <PendingFollowRequests2 @update-count="updateFollowRequestCount" />
          </div>
        </section>

        <!-- 피드백 캘린더 -->
        <section class="content-section">
          <div class="section-header">
            <h2 class="section-title">피드백 캘린더</h2>
            <div class="section-icon">📅</div>
          </div>
          <div class="card-container calendar-card">
            <FeedbackCalendar />
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import TodayPendingMeals from '@/components/Trainer/TodayPendingMeals.vue'
import PendingFollowRequests2 from '@/components/PendingFollowRequests2.vue'
import FeedbackCalendar from '@/components/Trainer/FeedbackCalendar.vue'
import Header from '@/components/common/Header.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userNickname = computed(() => auth.user?.nickname || '트레이너')

const pendingMealsCount = ref(0)
const followRequestCount = ref(0)

// 대기 중인 식단 개수 업데이트 함수
const updatePendingMealsCount = (count) => {
  pendingMealsCount.value = count
}

// 팔로우 요청 개수 업데이트 함수
const updateFollowRequestCount = (count) => {
  followRequestCount.value = count
}
</script>

<style scoped>
/* 페이지 전체 배경 */
.trainer-dashboard-page {
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

/* 대시보드 콘텐츠 - 1열 세로 배치 */
.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  width: 100%;
}

/* 일반 콘텐츠 섹션 */
.content-section {
  animation: fadeInUp 0.6s ease-out;
}

.content-section:nth-child(1) { animation-delay: 0.2s; }
.content-section:nth-child(2) { animation-delay: 0.3s; }
.content-section:nth-child(3) { animation-delay: 0.4s; }

/* 대기 중인 식단 카드 - 높이 제한 */
.pending-meals-card {
  max-height: 600px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 캘린더 카드 */
.calendar-card {
  min-height: 500px; /* 최소 높이 증가 */
  max-height: 900px; /* 최대 높이 증가 */
  overflow: hidden;
  display: flex;
  flex-direction: column;
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

/* 반응형 디자인 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .welcome-title {
    font-size: 1.5rem;
  }
  
  .card-container {
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
</style>