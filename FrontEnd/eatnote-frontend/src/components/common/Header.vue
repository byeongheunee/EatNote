<template>
  <header class="flex justify-between items-center px-6 py-3 bg-gray-100 shadow">
    <!-- 1. 왼쪽: 로고/이름 -->
    <div class="flex items-center space-x-8">
      <div class="flex items-center cursor-pointer" @click="$router.push('/')">
        <!-- <img src="/logo.png" alt="logo" class="w-8 h-8 mr-2" /> -->
        <span class="text-green-600 text-xl font-bold">EatNote</span>
      </div>

      <!-- 2. 가운데 메뉴: 로그인 여부에 따라 다르게 표시 -->
      <nav class="flex space-x-6 text-lg">
        <!-- ✨ 로그인 안한 경우 -->
        <template v-if="!isLogin">
          <RouterLink to="/community" class="cursor-pointer hover:font-bold" >커뮤니티</RouterLink>
          <span class="cursor-pointer hover:font-bold" @click="$emit('go-feature')">주요기능</span>
          <span class="cursor-pointer hover:font-bold" @click="$emit('go-usage')">이용방법</span>
        </template>

        <!-- ✨ 로그인 한 경우 -->
        <template v-else>
          <RouterLink to="/community" class="cursor-pointer hover:font-bold" >커뮤니티</RouterLink>
          <RouterLink to="/meals" class="cursor-pointer hover:font-bold" >식단</RouterLink>
          <RouterLink to="/videos" class="cursor-pointer hover:font-bold" >추천 영상</RouterLink>
        </template>
      </nav>
    </div>

    <!-- 3. 오른쪽: 로그인 여부에 따라 분기 -->
    <div class="flex items-center space-x-4">
      <!-- ✨ 로그인 한 경우 -->
      <template v-if="isLogin">
        <span class="text-lg font-semibold text-purple-900">{{ user.nickname }}</span>
        <img :src="getProfileImage(user.profileImage)" alt="프로필" class="w-9 h-9 rounded-full object-cover border" />
        <button @click="$emit('go-profile')" class="px-4 py-1 bg-purple-600 rounded-full hover:bg-purple-700">
          회원정보
        </button>
        <button @click="$emit('logout')" class="px-4 py-1 bg-white border rounded-full hover:bg-gray-200">
          로그아웃
        </button>
      </template>

      <!-- ✨ 로그인 안한 경우 -->
      <template v-else>
        <button @click="$emit('go-login')" class="px-4 py-1 border rounded hover:bg-gray-200">로그인</button>
        <button @click="$emit('go-register')" class="px-4 py-1 bg-green-600 rounded hover:bg-green-700">
          회원가입
        </button>
      </template>
    </div>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'

defineProps({
isLogin: Boolean,
user: Object
})

defineEmits([
'go-community',
'go-feature',     // 비회원 전용
'go-usage',       // 비회원 전용
'go-meals',       // 회원 전용
'go-videos',      // 회원 전용
'go-profile',
'logout',
'go-login',
'go-register'
])

// 기본 이미지
const defaultProfile = '/images/default-profile.png'

// 8080 서버 기반 절대 경로 반환 함수
const getProfileImage = (path) => {
  if (!path) return defaultProfile
  return `http://localhost:8080${path}`
}

</script>