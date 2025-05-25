<template>
  <div class="meal-view-page">
    <!-- 상단 헤더 -->
    <Header />

    <div class="meal-container">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <h1 class="page-title">🍽️ 식단 관리</h1>
        <p class="page-subtitle">나의 식단을 기록하고 분석해보세요</p>
      </section>

      <!-- 탭 메뉴 -->
      <section class="tab-section">
        <div class="tab-menu-container">
          <div class="tab-menu">
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
      </section>

      <!-- 탭별 내용 -->
      <section class="content-section">
        <div class="tab-content-wrapper">
          <Transition name="tab-fade" mode="out-in">
            <div :key="tab" class="tab-content">
              <MyMealList v-if="tab === 'my'" />
              <FollowerMealList v-if="tab === 'followers'" />
              <MyMealStats v-if="tab === 'stats'" />
            </div>
          </Transition>
        </div>
      </section>
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
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

.meal-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 페이지 헤더 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
  animation: fadeInUp 0.6s ease-out;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #6b7280;
  font-weight: 500;
  margin: 0;
}

/* 탭 섹션 */
.tab-section {
  margin-bottom: 2rem;
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.2s;
}

.tab-menu-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.tab-menu {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 0;
  background: rgba(249, 250, 251, 0.6);
  border-radius: 12px;
  padding: 0.25rem;
  border: 1px solid rgba(229, 231, 235, 0.3);
  position: relative;
}

.tab-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  border: none;
  background: transparent;
  color: #6b7280;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  border-radius: 8px;
  white-space: nowrap;
  min-height: 60px;
}

.tab-button:hover {
  color: #374151;
  background: rgba(255, 255, 255, 0.8);
}

.tab-button.active {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.tab-button.active:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
}

.tab-icon {
  font-size: 1.1rem;
}

.tab-text {
  font-weight: inherit;
}

/* 콘텐츠 섹션 */
.content-section {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.4s;
}

.tab-content-wrapper {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
  min-height: 500px;
}

.tab-content-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.2);
}

.tab-content {
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

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .tab-button {
    padding: 0.875rem 1.25rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 768px) {
  .meal-container {
    padding: 1rem;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .tab-menu {
    grid-template-columns: 1fr;
    gap: 0.25rem;
  }
  
  .tab-button {
    justify-content: center;
    padding: 1rem 1.25rem;
    min-height: auto;
  }
  
  .tab-content-wrapper {
    padding: 1.5rem;
  }
}

@media (max-width: 640px) {
  .page-header {
    margin-bottom: 2rem;
  }
  
  .tab-menu-container {
    padding: 0.75rem;
  }
  
  .tab-button {
    font-size: 0.85rem;
    padding: 0.875rem 1rem;
  }
  
  .tab-content-wrapper {
    padding: 1.25rem;
    min-height: 400px;
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

/* 호버 효과 강화 */
.tab-menu-container:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 25px rgba(245, 158, 11, 0.08);
  border-color: rgba(245, 158, 11, 0.2);
}

/* 로딩 애니메이션 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.loading {
  animation: pulse 2s infinite;
}
</style>