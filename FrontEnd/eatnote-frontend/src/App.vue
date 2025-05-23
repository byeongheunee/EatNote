<script setup>
// 예: App.vue <script setup> 최상단에 추가
import { onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth' // Pinia 사용하는 경우
import NotificationBell from '@/components/common/NotificationBell.vue'

const authStore = useAuthStore() 

const isLoggedIn = computed(() => authStore.isLoggedIn)

onMounted(() => {
  const token = localStorage.getItem('accessToken')
  const user = localStorage.getItem('user')

  if (user && token) {
    const parsedUser = JSON.parse(user)

    authStore.setToken(token)
    authStore.setUser(parsedUser)

    console.log('✅ 앱 시작 시 로그인 상태 복원 완료')
  }
})
</script>

<template>
  <router-view />
  <NotificationBell v-if="isLoggedIn" />
</template>

<style scoped>
/* 필요하면 공통 스타일 여기에 작성 */
</style>
