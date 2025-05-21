<script setup>
// 예: App.vue <script setup> 최상단에 추가
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth' // Pinia 사용하는 경우

onMounted(() => {
  const auth = useAuthStore()
  const token = localStorage.getItem('accessToken')
  const user = localStorage.getItem('user')

  if (user && token) {
    const parsedUser = JSON.parse(user)

    // 직접 상태 관리용 전역 ref가 있다면 거기에 할당
    // 또는 아래처럼 Pinia store 사용 시:
    auth.setToken(token)
    auth.setUser(JSON.parse(user))

    // 혹은 전역 ref가 있다면
    // authUser.value = parsedUser
    // accessToken.value = token
  }
})
</script>

<template>
  <router-view />
</template>

<style scoped>
/* 필요하면 공통 스타일 여기에 작성 */
</style>
