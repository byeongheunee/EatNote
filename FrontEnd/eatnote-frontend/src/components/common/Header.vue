<template>
  <header class="header-container">
    <div class="header-content">
      <div class="header-layout">
        
        <!-- 왼쪽: 로고 -->
        <div class="logo-section" @click="goHome">
          <img
            @click="goHome"
            src="@/assets/icons/EatNoteLogo.png"
            alt="EatNote 로고"
            class="logo-image"
          />
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
/* 헤더 컨테이너 - 완전 고정 디자인 */
.header-container {
  background: #fafbfc;
  border-bottom: 1px solid #e1e5e9;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
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
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.logo-section:hover {
  background: #f8fafc;
}

.logo-image {
  height: 40px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.08));
}

/* 네비게이션 섹션 */
.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 60px;
}

.nav-container {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link,
.nav-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-weight: 600;
  font-size: 15px;
  color: #475569;
  text-decoration: none;
  background: transparent;
  border: none;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-link:hover,
.nav-button:hover {
  color: #1e293b;
  background: #f1f5f9;
}

.nav-icon {
  font-size: 16px;
}

/* 활성 링크 스타일 */
.nav-link.router-link-active {
  color: #0f172a;
  background: #e2e8f0;
  font-weight: 700;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2);
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
  gap: 12px;
  padding: 10px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #cbd5e1;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.2;
}

/* 버튼들 - 깔끔한 솔리드 디자인 */
.profile-button,
.logout-button,
.login-button,
.register-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-weight: 600;
  font-size: 14px;
  border: 2px solid;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  border-radius: 8px;
  white-space: nowrap;
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
  background: #f8fafc;
  color: #64748b;
  border-color: #cbd5e1;
}

.logout-button:hover {
  background: #f1f5f9;
  color: #475569;
  border-color: #94a3b8;
}

.login-button {
  background: white;
  color: #64748b;
  border-color: #cbd5e1;
}

.login-button:hover {
  background: #f8fafc;
  color: #475569;
  border-color: #94a3b8;
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
  padding: 12px;
  background: white;
  border: 2px solid #cbd5e1;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.mobile-menu-button:hover {
  background: #f8fafc;
  border-color: #94a3b8;
}

.menu-icon {
  width: 20px;
  height: 20px;
  color: #64748b;
}

/* 모바일 메뉴 */
.mobile-menu {
  margin-top: 12px;
  background: #fafbfc;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.mobile-menu-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  margin-bottom: 12px;
}

.mobile-user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #cbd5e1;
}

.mobile-user-details {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.mobile-user-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 16px;
}

.mobile-nav-link,
.mobile-nav-button,
.mobile-action-button {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  font-weight: 600;
  font-size: 15px;
  color: #64748b;
  text-decoration: none;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  width: 100%;
  text-align: left;
  border-radius: 8px;
}

.mobile-nav-link:hover,
.mobile-nav-button:hover {
  color: #1e293b;
  background: #f1f5f9;
}

.mobile-action-button.profile:hover {
  color: #3b82f6;
  background: #eff6ff;
}

.mobile-action-button.logout:hover {
  color: #ef4444;
  background: #fef2f2;
}

.mobile-nav-icon {
  font-size: 18px;
}

.mobile-menu-divider {
  height: 2px;
  background: #e2e8f0;
  margin: 12px 0;
  border-radius: 1px;
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
    padding: 0 20px;
  }
  
  .right-section {
    gap: 12px;
  }

  .header-layout {
    height: 70px;
  }
}

@media (max-width: 480px) {
  .logo-image {
    height: 36px;
  }
  
  .login-button,
  .register-button,
  .logout-button {
    padding: 10px 16px;
    font-size: 13px;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .right-section {
    gap: 8px;
  }

  .header-layout {
    height: 65px;
  }

  .nav-container {
    gap: 16px;
  }
}
</style>