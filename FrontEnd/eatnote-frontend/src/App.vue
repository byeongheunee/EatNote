<script setup>
import { onMounted, onUnmounted, ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
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
const showScrollTop = ref(false)

const handleScroll = () => {
  const scrollY = window.scrollY
  const pageHeight = document.documentElement.scrollHeight
  showScrollTop.value = pageHeight > 1200 && scrollY > 300
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <Suspense>
    <keep-alive include="FollowerMealList">
      <router-view />
    </keep-alive>
  </Suspense>

  <NotificationBell v-if="isLoggedIn" />
  <button v-show="showScrollTop" @click="scrollToTop" class="scroll-top-btn" aria-label="맨 위로 이동">⬆</button>
</template>

<style scoped>
.scroll-top-btn {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  padding: 0.75rem 1rem;
  border-radius: 9999px;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  transition: opacity 0.3s ease, transform 0.2s ease;
}

.scroll-top-btn:hover {
  transform: scale(1.05);
  background: linear-gradient(135deg, #d97706, #b45309);
}
</style>
