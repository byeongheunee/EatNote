<template>
  <div>
    <!-- 상단 헤더 -->
    <Header
      :isLogin="auth.isLoggedIn"
      :user="auth.user"
      @go-feature="scrollToFeature"
      @go-usage="scrollToUsage"
      @go-meals="router.push('/meals')"
      @go-videos="router.push('/videos')"
      @go-profile="router.push('/profile')"
      @logout="handleLogout"
      @go-login="router.push('/login')"
      @go-register="router.push('/register')"
    />

    <!-- 탭 메뉴 + 업로드 버튼 묶음 -->
    <div class="tab-menu-wrapper">
      <div class="tab-menu">
        <button :class="{ active: tab === 'my' }" @click="tab = 'my'">나의 식단</button>
        <button :class="{ active: tab === 'followers' }" @click="tab = 'followers'">팔로워 식단</button>
        <button :class="{ active: tab === 'stats' }" @click="tab = 'stats'">나의 식단 통계</button>

        <!-- 업로드 버튼: 나의 식단 탭일 때만 표시 -->
        <button
          v-if="tab === 'my'"
          @click="router.push('/meal/upload')"
          class="ml-4 px-4 py-2 bg-blue-600 text-white font-semibold rounded hover:bg-blue-700 shadow"
        >
          + 식단 업로드
        </button>
      </div>
    </div>

    <!-- 탭별 내용 -->
    <div class="tab-content">
      <MyMealList v-if="tab === 'my'" />
      <FollowerMealList v-if="tab === 'followers'" />
      <MyMealStats v-if="tab === 'stats'" />
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
.tab-menu-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.tab-menu {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.tab-menu button {
  padding: 10px 20px;
  border: none;
  background-color: #ddd;
  cursor: pointer;
  font-weight: bold;
}

.tab-menu .active {
  background-color: #f58c42;
  color: white;
  border-radius: 5px;
}
</style>
