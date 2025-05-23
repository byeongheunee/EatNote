<template>
  <div class="fixed top-10 right-10 z-50">
    <div class="relative" ref="dropdownRef">
      <!-- 종 아이콘 -->
      <img
        src="@/assets/icons/Notification2.png"
        alt="알림"
        class="w-10 h-10 cursor-pointer"
        @click="toggleDropdown"
      />

      <!-- 알림 배지 -->
      <span
        v-if="unreadCount > 0"
        class="absolute -top-1 -right-1 bg-red-500 text-white text-xs w-5 h-5 rounded-full flex justify-center items-center animate-pulse"
      >
        {{ unreadCount }}
      </span>

      <!-- 알림 목록 드롭다운 -->
      <div
        v-if="showDropdown"
        class="absolute right-0 mt-2 w-100 max-h-96 overflow-y-auto bg-white border shadow-lg rounded-lg"
      >
        <div class="p-2 border-b bg-gray-50 flex justify-between items-center">
          <span class="text-sm font-semibold">알림</span>
          <button 
            v-if="unreadCount > 0"
            @click="markAllAsRead"
            class="text-xs text-blue-500 hover:text-blue-700"
          >
            모두 읽음
          </button>
        </div>
        <ul>
          <li
            v-for="notification in notifications"
            :key="notification.notificationId"
            class="p-4 border-b hover:bg-gray-50 cursor-pointer transition-colors duration-300"
            :class="{
              'bg-blue-50 text-black border-l-4 border-l-blue-500': !notification.isRead,
              'bg-white text-gray-400': notification.isRead
            }"
            @click="markAsRead(notification)"
          >
            <p class="text-sm flex justify-between items-center">
              {{ notification.content }}
              <span v-if="!notification.isRead" class="ml-2 text-blue-500">●</span>
            </p>
            <p class="text-xs mt-1">
              {{ formatDate(notification.createdAt) }}
            </p>
          </li>
          <li v-if="notifications.length === 0" class="p-4 text-gray-500 text-center">
            알림이 없습니다.
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import { format } from 'date-fns'

// 🔥 기존 auth.js 그대로 사용!
const authStore = useAuthStore()

const showDropdown = ref(false)
const dropdownRef = ref(null)
const notifications = ref([])
const pollingInterval = ref(null)
const lastNotificationCount = ref(0)

// Pinia 스토어에서 사용자 정보 가져오기
const userId = computed(() => authStore.user?.userId)
const isLoggedIn = computed(() => authStore.isLoggedIn)

const toggleDropdown = async () => {
  showDropdown.value = !showDropdown.value
  
  // 드롭다운을 열 때마다 최신 알림 가져오기
  if (showDropdown.value) {
    await fetchNotifications()
  }
}

// 외부 클릭 감지하여 드롭다운 닫기
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    showDropdown.value = false
  }
}

const fetchNotifications = async () => {
  if (!isLoggedIn.value) {
    return
  }

  try {
    const res = await axios.get('/api/notifications', {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`
      }
    })
    
    const newNotifications = res.data.data
    const currentCount = newNotifications.filter(n => !n.isRead).length
    
    // 🔔 새 알림이 있으면 브라우저 알림 표시
    if (currentCount > lastNotificationCount.value && lastNotificationCount.value > 0) {
      const newUnreadNotifications = newNotifications
        .filter(n => !n.isRead)
        .slice(0, currentCount - lastNotificationCount.value)
      
      newUnreadNotifications.forEach(notification => {
        if (Notification.permission === 'granted') {
          new Notification('새 알림', {
            body: notification.content,
            icon: '/favicon.ico'
          })
        }
      })
    }
    
    notifications.value = newNotifications
    lastNotificationCount.value = currentCount
    
    console.log('🔄 알림 목록 새로고침:', notifications.value.length, '개')
  } catch (e) {
    console.error('❌ 알림 목록 불러오기 실패', e)
    
    // 토큰이 만료된 경우 로그아웃 처리
    if (e.response?.status === 401) {
      authStore.logout()
    }
  }
}

// 🔄 주기적으로 알림 확인 (WebSocket 백업용)
const startPolling = () => {
  // 이미 폴링이 실행 중이면 중단
  if (pollingInterval.value) return
  
  pollingInterval.value = setInterval(async () => {
    // 드롭다운이 열려있지 않을 때만 백그라운드에서 새로고침
    if (!showDropdown.value && isLoggedIn.value) {
      await fetchNotifications()
    }
  }, 10000) // 10초마다 체크
  
  console.log('🔄 알림 폴링 시작 (10초 간격)')
}

const stopPolling = () => {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value)
    pollingInterval.value = null
    console.log('⏹️ 알림 폴링 중지')
  }
}

// 읽지 않은 알림 수 계산
const unreadCount = computed(() =>
  notifications.value.filter(n => !n.isRead).length
)

const formatDate = (datetimeStr) => {
  return format(new Date(datetimeStr), 'yyyy-MM-dd HH:mm')
}

const markAsRead = async (notification) => {
  if (notification.isRead) return
  
  try {
    await axios.put(`/api/notifications/${notification.notificationId}/read`, {}, {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`
      }
    })
    notification.isRead = true
    console.log('✅ 알림 읽음 처리:', notification.notificationId)
  } catch (e) {
    console.error('❌ 읽음 처리 실패', e)
  }
}

const markAllAsRead = async () => {
  try {
    const unreadNotifications = notifications.value.filter(n => !n.isRead)
    
    await Promise.all(
      unreadNotifications.map(notification =>
        axios.put(`/api/notifications/${notification.notificationId}/read`, {}, {
          headers: {
            Authorization: `Bearer ${authStore.accessToken}`
          }
        })
      )
    )
    
    unreadNotifications.forEach(notification => {
      notification.isRead = true
    })
    
    console.log('✅ 모든 알림 읽음 처리 완료')
  } catch (e) {
    console.error('❌ 모든 알림 읽음 처리 실패', e)
  }
}

// 브라우저 알림 권한 요청
const requestNotificationPermission = () => {
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission().then(permission => {
      console.log('🔔 브라우저 알림 권한:', permission)
    })
  }
}

// 로그인 상태 변화 감지
watch(isLoggedIn, async (newValue) => {
  if (newValue) {
    console.log('✅ 로그인 감지 - 알림 시스템 시작')
    await fetchNotifications()
    startPolling()
  } else {
    console.log('👋 로그아웃 감지 - 알림 시스템 중지')
    notifications.value = []
    lastNotificationCount.value = 0
    stopPolling()
  }
})

// 컴포넌트 마운트 시
onMounted(async () => {
  document.addEventListener('click', handleClickOutside)
  console.log('🚀 알림 컴포넌트 초기화')
  
  // 브라우저 알림 권한 요청
  requestNotificationPermission()

  // 로그인된 상태면 알림 시스템 시작
  if (isLoggedIn.value) {
    await fetchNotifications()
    startPolling()
  }
})

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  console.log('🔄 알림 컴포넌트 정리')
  stopPolling()
})
</script>