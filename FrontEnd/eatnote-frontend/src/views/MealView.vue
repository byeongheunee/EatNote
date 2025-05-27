<template>
  <div class="meal-view-page">
    <!-- 상단 헤더 -->
    <Header />

    <!-- 메인 컨테이너 -->
    <div class="main-container">
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="main-title">식단 관리</h1>
          <p class="main-subtitle">나의 식단을 기록하고 분석해보세요</p>
        </div>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tab-wrapper">
        <div class="tab-container">
          <button 
            :class="{ active: tab === 'my' }" 
            @click="tab = 'my'"
            class="tab-button"
          >
            <span class="tab-icon">📝</span>
            <span class="tab-text">나의 식단</span>
          </button>
          
          <button 
            :class="{ active: tab === 'followers' }" 
            @click="tab = 'followers'"
            class="tab-button"
          >
            <span class="tab-icon">👥</span>
            <span class="tab-text">팔로워 식단</span>
          </button>
          
          <button 
            :class="{ active: tab === 'stats' }" 
            @click="tab = 'stats'"
            class="tab-button"
          >
            <span class="tab-icon">📊</span>
            <span class="tab-text">나의 식단 통계</span>
          </button>
        </div>
      </div>

      <!-- 콘텐츠 영역 -->
      <div class="content-card">
        <Transition name="tab-fade" mode="out-in">
          <div :key="tab" class="tab-content">
            <MyMealList v-if="tab === 'my'" />
            <FollowerMealList v-if="tab === 'followers'" />
            <MyMealStats v-if="tab === 'stats'" />
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/common/Header.vue'
import MyMealList from '@/components/meal/MyMealList.vue'
import FollowerMealList from '@/components/meal/FollowerMealList.vue'
import MyMealStats from '@/components/meal/MyMealStats.vue'

const router = useRouter()
const tab = ref('my')

// 로그인 상태 (예시: localStorage 기반)
const auth = {
  isLoggedIn: !!localStorage.getItem('accessToken'),
  user: JSON.parse(localStorage.getItem('user') || '{}')
}

const scrollToFeature = () => document.getElementById('feature')?.scrollIntoView({ behavior: 'smooth' })
const scrollToUsage = () => document.getElementById('usage')?.scrollIntoView({ behavior: 'smooth' })

const handleLogout = () => {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
/* 페이지 전체 배경 */
.meal-view-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 설정 */
.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid #0c0c0c;
}

.header-content {
  text-align: center;
}

.main-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
  margin-bottom: 12px;
}

.main-subtitle {
  font-size: 18px;
  color: #5D4037;
  line-height: 1.6;
}

/* 탭 래퍼 */
.tab-wrapper {
  margin-bottom: 32px;
}

.tab-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  padding: 8px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
}

.tab-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 24px;
  border: none;
  background: transparent;
  color: #6b7280;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  white-space: nowrap;
  position: relative;
}

.tab-button:hover {
  color: #374151;
  background: rgba(249, 250, 251, 0.8);
  transform: translateY(-1px);
}

.tab-button.active {
  background: linear-gradient(135deg, #ebb861, #e99a40);
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  transform: translateY(-2px);
}

.tab-button.active:hover {
  background: linear-gradient(135deg, #d8a753, #ce8634);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

.tab-icon {
  font-size: 20px;
}

.tab-text {
  font-weight: inherit;
}

/* 콘텐츠 카드 */
.content-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
  min-height: 600px;
}

.tab-content {
  padding: 32px;
  width: 100%;
}

/* 탭 전환 애니메이션 */
.tab-fade-enter-active,
.tab-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-fade-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.98);
}

.tab-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.98);
}

.tab-fade-enter-to,
.tab-fade-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* 애니메이션 - 헤더 제외 */
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

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .main-container {
    padding: 24px 16px;
  }

  .tab-container {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .tab-button {
    padding: 14px 20px;
    font-size: 15px;
  }
}

@media (max-width: 768px) {
  .main-container {
    padding: 16px 12px;
  }
  
  .page-header {
    margin-bottom: 32px;
  }
  
  .main-title {
    font-size: 32px;
  }
  
  .main-subtitle {
    font-size: 16px;
  }

  .tab-container {
    padding: 6px;
    gap: 6px;
  }
  
  .tab-button {
    padding: 12px 16px;
    font-size: 14px;
    gap: 6px;
  }
  
  .tab-icon {
    font-size: 18px;
  }

  .tab-content {
    padding: 24px;
  }

  .content-card {
    min-height: 500px;
  }
}

@media (max-width: 640px) {
  .main-title {
    font-size: 28px;
  }
  
  .main-subtitle {
    font-size: 16px;
  }

  .tab-button {
    padding: 10px 14px;
    font-size: 13px;
  }

  .tab-content {
    padding: 20px;
  }

  .content-card {
    min-height: 400px;
  }
}

/* 탭 버튼 애니메이션 지연 */
.tab-button:nth-child(1) {
  animation-delay: 0.1s;
}

.tab-button:nth-child(2) {
  animation-delay: 0.2s;
}

.tab-button:nth-child(3) {
  animation-delay: 0.3s;
}

/* 호버 효과 */
.tab-container:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 20px -3px rgba(100, 116, 139, 0.12), 0 6px 8px -2px rgba(100, 116, 139, 0.07);
}

.content-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px -3px rgba(100, 116, 139, 0.12), 0 6px 8px -2px rgba(100, 116, 139, 0.07);
}

/* 접근성 */
.tab-button:focus {
  outline: 2px solid #f59e0b;
  outline-offset: 2px;
}

.tab-button:focus:not(:focus-visible) {
  outline: none;
}
</style>