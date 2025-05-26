<template>
  <header class="header-container">
    <div class="header-content">
      <div class="header-layout">
        
        <!-- 왼쪽: 로고 -->
        <div class="logo-section" @click="goHome">
          <span class="logo-text">EatNote</span>
        </div>

        <!-- 가운데: 네비게이션 메뉴 -->
        <nav class="nav-section">
          <div class="nav-container">
            <!-- 로그인 안한 경우 -->
            <template v-if="!isLogin">
              <RouterLink 
                :to="`/community/${defaultBoardId}`" 
                class="nav-link"
              >
                <span class="nav-icon">💬</span> 
                <span class="nav-text">커뮤니티</span>
              </RouterLink>
              <button 
                @click="$emit('go-feature')" 
                class="nav-button"
              >
                <span class="nav-icon">✨</span> 
                <span class="nav-text">주요기능</span>
              </button>
              <button 
                @click="$emit('go-usage')" 
                class="nav-button"
              >
                <span class="nav-icon">📖</span> 
                <span class="nav-text">이용방법</span>
              </button>
            </template>

            <!-- 로그인 한 경우 -->
            <template v-else>
              <RouterLink 
                :to="`/community/${defaultBoardId}`" 
                class="nav-link"
              >
                <span class="nav-icon">💬</span> 
                <span class="nav-text">커뮤니티</span>
              </RouterLink>
              
              <!-- 일반회원 메뉴 -->
              <template v-if="user.userType === 2">
                <RouterLink 
                  to="/meals" 
                  class="nav-link"
                >
                  <span class="nav-icon">🍽️</span> 
                  <span class="nav-text">식단</span>
                </RouterLink>
                <RouterLink 
                  to="/videos" 
                  class="nav-link"
                >
                  <span class="nav-icon">🎥</span> 
                  <span class="nav-text">추천 영상</span>
                </RouterLink>
              </template>

              <!-- 트레이너 메뉴 -->
              <template v-else-if="user.userType === 1">
                <RouterLink 
                  to="/trainer/feedback" 
                  class="nav-link"
                >
                  <span class="nav-icon">💪</span> 
                  <span class="nav-text">나의 피드백</span>
                </RouterLink>
              </template>
            </template>
          </div>
        </nav>

        <!-- 오른쪽: 사용자 정보 & 버튼 -->
        <div class="right-section">
          <!-- 로그인 한 경우 -->
          <template v-if="isLogin">
            <div class="user-info">
              <img 
                :src="getProfileImage(user.profileImage)" 
                alt="프로필" 
                class="user-avatar"
              />
              <div class="user-details">
                <span class="user-name">{{ user.nickname }}</span>
                <span class="user-type">{{ user.userType === 1 ? '트레이너' : '회원' }}</span>
              </div>
            </div>
            
            <button 
              @click="$router.push('/profile')" 
              class="profile-button"
            >
              <span class="button-icon">⚙️</span> 
              <span class="button-text">회원정보</span>
            </button>
            
            <button 
              @click="handleLogout" 
              class="logout-button"
            >
              <span class="button-icon">👋</span> 
              <span class="button-text">로그아웃</span>
            </button>
          </template>

          <!-- 로그인 안한 경우 -->
          <template v-else>
            <button 
              @click="$router.push('/login')" 
              class="login-button"
            >
              <span class="button-text">로그인</span>
            </button>
            <button 
              @click="$router.push('/register')" 
              class="register-button"
            >
              <span class="button-text">회원가입</span>
            </button>
          </template>

          <!-- 모바일 메뉴 버튼 -->
          <button 
            @click="toggleMobileMenu"
            class="mobile-menu-button"
          >
            <svg class="menu-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- 모바일 메뉴 -->
      <transition name="mobile-menu">
        <div v-if="showMobileMenu" class="mobile-menu">
          <div class="mobile-menu-content">
            <!-- 로그인 안한 경우 -->
            <template v-if="!isLogin">
              <RouterLink 
                :to="`/community/${defaultBoardId}`" 
                @click="showMobileMenu = false"
                class="mobile-nav-link"
              >
                <span class="mobile-nav-icon">💬</span> 커뮤니티
              </RouterLink>
              <button 
                @click="$emit('go-feature'); showMobileMenu = false" 
                class="mobile-nav-button"
              >
                <span class="mobile-nav-icon">✨</span> 주요기능
              </button>
              <button 
                @click="$emit('go-usage'); showMobileMenu = false" 
                class="mobile-nav-button"
              >
                <span class="mobile-nav-icon">📖</span> 이용방법
              </button>
            </template>

            <!-- 로그인 한 경우 -->
            <template v-else>
              <!-- 사용자 정보 -->
              <div class="mobile-user-info">
                <img 
                  :src="getProfileImage(user.profileImage)" 
                  alt="프로필" 
                  class="mobile-user-avatar"
                />
                <div class="mobile-user-details">
                  <span class="mobile-user-name">{{ user.nickname }}</span>
                  <span class="mobile-user-type">{{ user.userType === 1 ? '트레이너' : '회원' }}</span>
                </div>
              </div>

              <RouterLink 
                :to="`/community/${defaultBoardId}`" 
                @click="showMobileMenu = false"
                class="mobile-nav-link"
              >
                <span class="mobile-nav-icon">💬</span> 커뮤니티
              </RouterLink>
              
              <!-- 일반회원 메뉴 -->
              <template v-if="user.userType === 2">
                <RouterLink 
                  to="/meals" 
                  @click="showMobileMenu = false"
                  class="mobile-nav-link"
                >
                  <span class="mobile-nav-icon">🍽️</span> 식단
                </RouterLink>
                <RouterLink 
                  to="/videos" 
                  @click="showMobileMenu = false"
                  class="mobile-nav-link"
                >
                  <span class="mobile-nav-icon">🎥</span> 추천 영상
                </RouterLink>
              </template>

              <!-- 트레이너 메뉴 -->
              <template v-else-if="user.userType === 1">
                <RouterLink 
                  to="/trainer/feedback" 
                  @click="showMobileMenu = false"
                  class="mobile-nav-link"
                >
                  <span class="mobile-nav-icon">💪</span> 나의 피드백
                </RouterLink>
              </template>

              <div class="mobile-menu-divider"></div>
              
              <button 
                @click="$router.push('/profile'); showMobileMenu = false" 
                class="mobile-action-button profile"
              >
                <span class="mobile-nav-icon">⚙️</span> 회원정보
              </button>
              <button 
                @click="handleLogout" 
                class="mobile-action-button logout"
              >
                <span class="mobile-nav-icon">👋</span> 로그아웃
              </button>
            </template>
          </div>
        </div>
      </transition>
    </div>
  </header>
</template>

<script setup>
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { computed, ref } from 'vue'
import axios from 'axios'

const auth = useAuthStore()
const router = useRouter()

const isLogin = computed(() => auth.isLoggedIn)
const user = computed(() => auth.user)
const showMobileMenu = ref(false)

const defaultBoardId = 1

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const handleLogout = async () => {
  try {
    await axios.post('/api/auth/logout', {}, {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
  } catch (e) {
    console.warn('로그아웃 요청 실패 (무시하고 진행)', e)
  } finally {
    auth.logout()
    router.replace('/')
    showMobileMenu.value = false
  }
}

const goHome = () => {
  if (auth.user?.userType === 1) {
    router.push('/trainer')
  } else if (auth.user?.userType === 2) {
    router.push('/member')
  } else {
    router.push('/')
  }
  showMobileMenu.value = false
}

defineEmits([
  'go-feature',
  'go-usage'
])

const defaultProfile = '/images/default-profile.png'
const getProfileImage = (path) => path ? `http://localhost:8080${path}` : defaultProfile
</script>

<style scoped>
/* 헤더 컨테이너 */
.header-container {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.header-layout {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
}

/* 로고 섹션 */
.logo-section {
  cursor: pointer;
  transition: color 0.2s ease;
}

.logo-section:hover .logo-text {
  color: #f59e0b;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -0.5px;
}

/* 네비게이션 섹션 */
.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 48px;
}

.nav-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link,
.nav-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-weight: 500;
  font-size: 18px;
  color: #6b7280;
  text-decoration: none;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.nav-link:hover,
.nav-button:hover {
  color: #f59e0b;
}

.nav-icon {
  font-size: 16px;
}

/* 활성 링크 스타일 */
.nav-link.router-link-active {
  color: #f59e0b;
  font-weight: 600;
}

/* 오른쪽 섹션 */
.right-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 사용자 정보 */
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.user-name {
  font-weight: 600;
  color: #111827;
  font-size: 13px;
  line-height: 1.2;
}

.user-type {
  font-size: 11px;
  color: #6b7280;
  font-weight: 400;
}

/* 버튼들 */
.profile-button,
.logout-button,
.login-button,
.register-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-weight: 500;
  font-size: 13px;
  border: 1px solid;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  border-radius: 6px;
}

.profile-button {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.profile-button:hover {
  background: #2563eb;
  border-color: #2563eb;
}

.logout-button {
  background: white;
  color: #6b7280;
  border-color: #d1d5db;
}

.logout-button:hover {
  background: #f9fafb;
  color: #374151;
  border-color: #9ca3af;
}

.login-button {
  background: white;
  color: #6b7280;
  border-color: #d1d5db;
}

.login-button:hover {
  background: #f9fafb;
  color: #374151;
  border-color: #9ca3af;
}

.register-button {
  background: #f59e0b;
  color: white;
  border-color: #f59e0b;
}

.register-button:hover {
  background: #d97706;
  border-color: #d97706;
}

/* 모바일 메뉴 버튼 */
.mobile-menu-button {
  display: none;
  padding: 8px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.mobile-menu-button:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.menu-icon {
  width: 20px;
  height: 20px;
  color: #6b7280;
}

/* 모바일 메뉴 */
.mobile-menu {
  margin-top: 8px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.mobile-menu-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 8px;
}

.mobile-user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.mobile-user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.mobile-user-name {
  font-weight: 600;
  color: #111827;
  font-size: 15px;
}

.mobile-user-type {
  font-size: 13px;
  color: #6b7280;
  font-weight: 400;
}

.mobile-nav-link,
.mobile-nav-button,
.mobile-action-button {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  font-weight: 500;
  font-size: 14px;
  color: #6b7280;
  text-decoration: none;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  width: 100%;
  text-align: left;
  border-radius: 4px;
}

.mobile-nav-link:hover,
.mobile-nav-button:hover {
  color: #f59e0b;
  background: #fef3c7;
}

.mobile-action-button.profile:hover {
  color: #3b82f6;
  background: #dbeafe;
}

.mobile-action-button.logout:hover {
  color: #ef4444;
  background: #fee2e2;
}

.mobile-nav-icon {
  font-size: 16px;
}

.mobile-menu-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 8px 0;
}

/* 트랜지션 */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.2s ease;
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .nav-section {
    display: none;
  }
  
  .mobile-menu-button {
    display: block;
  }
  
  .user-info {
    display: none;
  }
  
  .profile-button {
    display: none;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .right-section {
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .logo-text {
    font-size: 20px;
  }
  
  .login-button,
  .register-button,
  .logout-button {
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .header-content {
    padding: 0 12px;
  }
  
  .right-section {
    gap: 8px;
  }
}
</style>