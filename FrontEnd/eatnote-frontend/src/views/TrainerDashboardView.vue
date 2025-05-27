<template>
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
            <div class="section-title-with-tooltip">
              <h2 class="section-title">피드백을 기다리고 있는 식단</h2>
              <div class="tooltip-container">
                <div class="tooltip-trigger">❓</div>
                <div class="tooltip-content">
                  피드백이 필요한 오늘의 식단 목록을 확인할 수 있습니다.
                </div>
              </div>
            </div>
            <div class="section-badge">
              <div class="pulse-dot red"></div>
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
            <div class="section-title-with-tooltip">
              <h2 class="section-title">팔로우 요청</h2>
              <div class="tooltip-container">
                <div class="tooltip-trigger">❓</div>
                <div class="tooltip-content">
                  새로운 팔로우 요청을 확인하고 승인/거부할 수 있습니다.
                </div>
              </div>
            </div>
            <div class="section-badge">
              <div class="pulse-dot blue"></div>
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
            <div class="section-title-with-tooltip">
              <h2 class="section-title">피드백 캘린더</h2>
              <div class="tooltip-container">
                <div class="tooltip-trigger">❓</div>
                <div class="tooltip-content">
                  월별 피드백 활동 현황을 캘린더 형태로 확인할 수 있습니다.
                </div>
              </div>
            </div>
            <div class="section-badge">
              <div class="pulse-dot green"></div>
              <span class="badge-text">활동 기록</span>
            </div>
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
/* 페이지 전체 배경 - 멤버와 동일한 베이지 톤 */
.trainer-dashboard-page {
  padding-top: 80px; /* 헤더 높이만큼 여백 */
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 통일 */
.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 환영 섹션 */
.welcome-section {
  margin-bottom: 20px;
  padding-bottom: 24px;
  text-align: center;
}

.welcome-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
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
  gap: 32px;
  width: 100%;
}

/* 일반 콘텐츠 섹션 */
.content-section {
  animation: fadeInUp 0.6s ease-out;
}

.content-section:nth-child(1) {
  animation-delay: 0.1s;
}

.content-section:nth-child(2) {
  animation-delay: 0.2s;
}

.content-section:nth-child(3) {
  animation-delay: 0.3s;
}

/* 섹션 헤더 */
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

/* 섹션 타이틀과 툴팁 컨테이너 */
.section-title-with-tooltip {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2D1810;
  margin: 0;
}

/* 툴팁 스타일 - 멤버와 동일 */
.tooltip-container {
  position: relative;
  display: inline-block;
}

.tooltip-trigger {
  width: 18px;
  height: 18px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  cursor: help;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.tooltip-trigger:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
}

.tooltip-content {
  visibility: hidden;
  opacity: 0;
  position: absolute;
  top: -45px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.9);
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  z-index: 1000;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.tooltip-content::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: rgba(0, 0, 0, 0.9) transparent transparent transparent;
}

.tooltip-container:hover .tooltip-content {
  visibility: visible;
  opacity: 1;
  transform: translateX(-50%) translateY(-2px);
}

/* 섹션 뱃지 */
.section-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 20px;
  padding: 8px 16px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.pulse-dot.red {
  background: #ef4444;
}

.pulse-dot.blue {
  background: #3b82f6;
}

.pulse-dot.green {
  background: #10b981;
}

.badge-text {
  font-size: 14px;
  font-weight: 600;
  color: #f59e0b;
}

/* 카드 컨테이너 - 글래스모피즘 통일 */
.card-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  padding: 10px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(100, 116, 139, 0.1), 0 10px 10px -5px rgba(100, 116, 139, 0.04);
}

/* 대기 중인 식단 카드 - 높이 제한 */
.pending-meals-card {
  max-height: 600px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 캘린더 카드 */
.calendar-card {
  min-height: 500px;
  max-height: 900px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .dashboard-container {
    padding: 24px 16px;
  }
  
  .welcome-title {
    font-size: 32px;
  }
  
  .section-title {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px 12px;
  }

  .welcome-section {
    margin-bottom: 32px;
  }

  .welcome-title {
    font-size: 28px;
  }

  .dashboard-content {
    gap: 24px;
  }

  .card-container {
    padding: 20px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  /* 모바일에서 툴팁 위치 조정 */
  .tooltip-content {
    top: -50px;
    font-size: 11px;
    padding: 6px 10px;
  }
}

@media (max-width: 640px) {
  .welcome-title {
    font-size: 24px;
  }
  
  .section-title {
    font-size: 18px;
  }

  /* 모바일에서 툴팁 크기 조정 */
  .tooltip-trigger {
    width: 16px;
    height: 16px;
    font-size: 9px;
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

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #f59e0b;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #d97706;
}
</style>