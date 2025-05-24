<template>
  <header class="bg-gradient-to-r from-yellow-50 via-orange-50 to-amber-50 backdrop-blur-md border-b border-yellow-200/50 shadow-lg sticky top-0 z-50">
    <div class="container mx-auto px-4 py-3">
      <div class="flex justify-between items-center max-w-6xl mx-auto">
        
        <!-- 왼쪽: 로고 -->
        <div class="flex items-center cursor-pointer group" @click="goHome">
          <span class="text-2xl font-bold bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent group-hover:from-orange-700 group-hover:to-amber-700 transition-all duration-300">
            EatNote
          </span>
        </div>

        <!-- 가운데: 네비게이션 메뉴 -->
        <nav class="hidden md:flex items-center justify-center flex-1 mx-8">
          <div class="flex items-center space-x-1 bg-white/60 backdrop-blur-sm rounded-full px-6 py-2 border border-yellow-200/50">
            <!-- 로그인 안한 경우 -->
            <template v-if="!isLogin">
              <RouterLink 
                :to="`/community/${defaultBoardId}`" 
                class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
              >
                <span>💬</span> 커뮤니티
              </RouterLink>
              <button 
                @click="$emit('go-feature')" 
                class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
              >
                <span>✨</span> 주요기능
              </button>
              <button 
                @click="$emit('go-usage')" 
                class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
              >
                <span>📖</span> 이용방법
              </button>
            </template>

            <!-- 로그인 한 경우 -->
            <template v-else>
              <RouterLink 
                :to="`/community/${defaultBoardId}`" 
                class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
              >
                <span>💬</span> 커뮤니티
              </RouterLink>
              
              <!-- 일반회원 메뉴 -->
              <template v-if="user.userType === 2">
                <RouterLink 
                  to="/meals" 
                  class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
                >
                  <span>🍽️</span> 식단
                </RouterLink>
                <RouterLink 
                  to="/videos" 
                  class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
                >
                  <span>🎥</span> 추천 영상
                </RouterLink>
              </template>

              <!-- 트레이너 메뉴 -->
              <template v-else-if="user.userType === 1">
                <RouterLink 
                  to="/trainer/feedback" 
                  class="flex items-center gap-2 px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-white/80 rounded-lg transition-all duration-300 font-medium"
                >
                  <span>💪</span> 나의 피드백
                </RouterLink>
              </template>
            </template>
          </div>
        </nav>

        <!-- 오른쪽: 사용자 정보 & 버튼 -->
        <div class="flex items-center space-x-3">
          <!-- 로그인 한 경우 -->
          <template v-if="isLogin">
            <div class="hidden sm:flex items-center space-x-2 bg-white/60 backdrop-blur-sm rounded-full px-3 py-1.5 border border-yellow-200/50">
              <img 
                :src="getProfileImage(user.profileImage)" 
                alt="프로필" 
                class="w-7 h-7 rounded-full object-cover border-2 border-white shadow-sm"
              />
              <span class="font-semibold text-gray-800 text-sm">{{ user.nickname }}</span>
            </div>
            
            <button 
              @click="$router.push('/profile')" 
              class="hidden sm:flex items-center gap-1 px-3 py-1.5 bg-gradient-to-r from-blue-400 to-indigo-400 hover:from-blue-500 hover:to-indigo-500 text-white font-semibold rounded-full transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl text-sm"
            >
              <span>⚙️</span> 회원정보
            </button>
            
            <button 
              @click="handleLogout" 
              class="flex items-center gap-1 px-3 py-1.5 bg-white/70 hover:bg-white/90 backdrop-blur-sm text-gray-700 font-semibold rounded-full border border-gray-200 hover:border-gray-300 transition-all duration-300 transform hover:scale-105 shadow-md hover:shadow-lg text-sm"
            >
              <span>👋</span> 로그아웃
            </button>
          </template>

          <!-- 로그인 안한 경우 -->
          <template v-else>
            <button 
              @click="$router.push('/login')" 
              class="px-4 py-1.5 bg-white/70 hover:bg-white/90 backdrop-blur-sm text-gray-700 font-semibold rounded-full border border-gray-200 hover:border-gray-300 transition-all duration-300 transform hover:scale-105 shadow-md hover:shadow-lg text-sm"
            >
              로그인
            </button>
            <button 
              @click="$router.push('/register')" 
              class="px-4 py-1.5 bg-gradient-to-r from-green-400 to-emerald-400 hover:from-green-500 hover:to-emerald-500 text-white font-semibold rounded-full transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl text-sm"
            >
              회원가입
            </button>
          </template>

          <!-- 모바일 메뉴 버튼 -->
          <button 
            @click="toggleMobileMenu"
            class="md:hidden p-2 bg-white/70 hover:bg-white/90 backdrop-blur-sm rounded-lg border border-gray-200 transition-all duration-300"
          >
            <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- 모바일 메뉴 -->
      <div v-if="showMobileMenu" class="md:hidden mt-4 p-4 bg-white/80 backdrop-blur-sm rounded-2xl border border-yellow-200/50 shadow-lg">
        <div class="space-y-3">
          <!-- 로그인 안한 경우 -->
          <template v-if="!isLogin">
            <RouterLink 
              :to="`/community/${defaultBoardId}`" 
              @click="showMobileMenu = false"
              class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium"
            >
              <span class="text-lg">💬</span> 커뮤니티
            </RouterLink>
            <button 
              @click="$emit('go-feature'); showMobileMenu = false" 
              class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium w-full text-left"
            >
              <span class="text-lg">✨</span> 주요기능
            </button>
            <button 
              @click="$emit('go-usage'); showMobileMenu = false" 
              class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium w-full text-left"
            >
              <span class="text-lg">📖</span> 이용방법
            </button>
          </template>

          <!-- 로그인 한 경우 -->
          <template v-else>
            <!-- 사용자 정보 -->
            <div class="flex items-center space-x-3 p-3 bg-gradient-to-r from-yellow-50 to-orange-50 rounded-xl border border-yellow-200/50">
              <img 
                :src="getProfileImage(user.profileImage)" 
                alt="프로필" 
                class="w-10 h-10 rounded-full object-cover border-2 border-white shadow-sm"
              />
              <span class="font-semibold text-gray-800">{{ user.nickname }}</span>
            </div>

            <RouterLink 
              :to="`/community/${defaultBoardId}`" 
              @click="showMobileMenu = false"
              class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium"
            >
              <span class="text-lg">💬</span> 커뮤니티
            </RouterLink>
            
            <!-- 일반회원 메뉴 -->
            <template v-if="user.userType === 2">
              <RouterLink 
                to="/meals" 
                @click="showMobileMenu = false"
                class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium"
              >
                <span class="text-lg">🍽️</span> 식단
              </RouterLink>
              <RouterLink 
                to="/videos" 
                @click="showMobileMenu = false"
                class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium"
              >
                <span class="text-lg">🎥</span> 추천 영상
              </RouterLink>
            </template>

            <!-- 트레이너 메뉴 -->
            <template v-else-if="user.userType === 1">
              <RouterLink 
                to="/trainer/feedback" 
                @click="showMobileMenu = false"
                class="flex items-center gap-3 p-3 text-gray-700 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all duration-300 font-medium"
              >
                <span class="text-lg">💪</span> 나의 피드백
              </RouterLink>
            </template>

            <div class="border-t border-gray-200 pt-3 space-y-2">
              <button 
                @click="$router.push('/profile'); showMobileMenu = false" 
                class="flex items-center gap-3 p-3 text-gray-700 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all duration-300 font-medium w-full text-left"
              >
                <span class="text-lg">⚙️</span> 회원정보
              </button>
              <button 
                @click="handleLogout" 
                class="flex items-center gap-3 p-3 text-gray-700 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all duration-300 font-medium w-full text-left"
              >
                <span class="text-lg">👋</span> 로그아웃
              </button>
            </div>
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
/* 활성 링크 스타일 (@apply 대신 일반 CSS 사용) */
.router-link-active {
  color: #ea580c; /* text-orange-600 */
  background-color: rgba(255, 255, 255, 0.7); /* bg-white/70 */
  font-weight: 600; /* font-semibold */
}

/* 그라데이션 텍스트 */
.bg-clip-text {
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 백드롭 블러 */
.backdrop-blur-md {
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.backdrop-blur-sm {
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

/* 반응형 조정 */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

/* 호버 효과 개선 */
nav a:hover,
nav button:hover {
  transform: translateY(-1px);
}

/* 그림자 효과 */
.shadow-lg {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.shadow-md {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.shadow-xl {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* 모바일 메뉴 슬라이드 애니메이션 */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.3s ease;
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>