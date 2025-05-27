<template>
  <div class="fixed top-3 right-6 z-50">
    <div class="relative" ref="dropdownRef">
      <!-- 알림 아이콘 버튼 -->
      <button
        @click="toggleDropdown"
        class="notification-button"
        :class="{ 'active': showDropdown }"
      >

        <img 
          src="@/assets/icons/letter.png" 
          alt="알림 아이콘"
          class="w-7 h-7"
        />

        <!-- 종 아이콘 -->
        <!-- <svg 
          class="notification-icon" 
          fill="none" 
          stroke="currentColor" 
          viewBox="0 0 24 24"
        >
          <path 
            stroke-linecap="round" 
            stroke-linejoin="round" 
            stroke-width="2" 
            d="M15 17h5l-5 5-5-5h5v-5a6 6 0 1 0-12 0v5H3l5 5 5-5H8v-5a3 3 0 1 1 6 0v5z"
          />
        </svg> -->
        
        <!-- 알림 배지 -->
        <transition name="badge-bounce">
          <span
            v-if="unreadCount > 0"
            class="notification-badge"
          >
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </transition>
      </button>

      <!-- 알림 드롭다운 -->
      <transition name="dropdown-slide">
        <div
          v-if="showDropdown"
          class="notification-dropdown"
        >
          <!-- 헤더 -->
          <div class="dropdown-header">
            <div class="header-content">
              <h3 class="header-title">
                <img 
                  src="@/assets/icons/letter.png" 
                  alt="알림 아이콘"
                  class="w-5 h-5"
                />
                알림
              </h3>
              <span class="notification-count">{{ notifications.length }}개</span>
            </div>
            <button 
              v-if="unreadCount > 0"
              @click="markAllAsRead"
              class="mark-all-button"
            >
              <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
              </svg>
              모두 읽음
            </button>
          </div>

          <!-- 알림 목록 -->
          <div class="notification-list">
            <transition-group name="notification-item" tag="div">
              <div
                v-for="notification in notifications"
                :key="notification.notificationId"
                class="notification-item"
                :class="{
                  'unread': !notification.isRead,
                  'read': notification.isRead
                }"
                @click="markAsRead(notification)"
              >
                <!-- 알림 아이콘 -->
                <div class="item-icon">
                  <svg v-if="!notification.isRead" class="unread-icon" fill="currentColor" viewBox="0 0 24 24">
                    <circle cx="12" cy="12" r="8"/>
                  </svg>
                  <svg v-else class="read-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                  </svg>
                </div>

                <!-- 알림 내용 -->
                <div class="item-content">
                  <p class="item-message">{{ notification.content }}</p>
                  <div class="item-meta">
                    <span class="item-time">{{ formatDate(notification.createdAt) }}</span>
                    <span v-if="!notification.isRead" class="new-badge">NEW</span>
                  </div>
                </div>

                <!-- 읽지 않음 표시 -->
                <div v-if="!notification.isRead" class="unread-indicator"></div>
              </div>
            </transition-group>

            <!-- 알림 없음 상태 -->
            <div v-if="notifications.length === 0" class="empty-state">
              <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
              </svg>
              <p class="empty-text">새로운 알림이 없습니다</p>
              <p class="empty-subtext">알림이 도착하면 여기에 표시됩니다</p>
            </div>
          </div>
        </div>
      </transition>
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
  const date = new Date(datetimeStr)
  const now = new Date()
  const diffInHours = Math.floor((now - date) / (1000 * 60 * 60))
  
  if (diffInHours < 1) {
    const diffInMinutes = Math.floor((now - date) / (1000 * 60))
    return diffInMinutes < 1 ? '방금 전' : `${diffInMinutes}분 전`
  } else if (diffInHours < 24) {
    return `${diffInHours}시간 전`
  } else {
    return format(date, 'MM월 dd일 HH:mm')
  }
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

<style scoped>
/* 알림 버튼 */
.notification-button {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: white;
  border: 2px solid #f3f4f6;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.notification-button:hover {
  background: #f8fafc;
  border-color: #f59e0b;
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.2);
  transform: translateY(-2px);
}

.notification-button.active {
  background: #f59e0b;
  border-color: #f59e0b;
  color: white;
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

.notification-icon {
  width: 24px;
  height: 24px;
  color: inherit;
}

/* 알림 배지 */
.notification-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  font-size: 10px;
  font-weight: 700;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 드롭다운 */
.notification-dropdown {
  position: absolute;
  right: 0;
  top: 56px;
  width: 400px;
  max-height: 500px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(0, 0, 0, 0.05);
  overflow: hidden;
  backdrop-filter: blur(10px);
}

/* 헤더 */
.dropdown-header {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.header-icon {
  width: 20px;
  height: 20px;
  color: #f59e0b;
}

.notification-count {
  background: #f59e0b;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 8px;
}

.mark-all-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #3b82f6;
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.mark-all-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.button-icon {
  width: 14px;
  height: 14px;
}

/* 알림 목록 */
.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.notification-item:hover {
  background: #f8fafc;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item.unread {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-left: 4px solid #3b82f6;
}

.notification-item.read {
  opacity: 0.7;
}

/* 아이콘 */
.item-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 4px;
}

.unread-icon {
  width: 12px;
  height: 12px;
  color: #3b82f6;
}

.read-icon {
  width: 16px;
  height: 16px;
  color: #10b981;
}

/* 컨텐츠 */
.item-content {
  flex: 1;
  min-width: 0;
}

.item-message {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  line-height: 1.5;
  margin: 0 0 8px 0;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-time {
  font-size: 12px;
  color: #6b7280;
}

.new-badge {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  text-transform: uppercase;
}

/* 읽지 않음 표시 */
.unread-indicator {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 8px;
  height: 8px;
  background: #3b82f6;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  text-align: center;
}

.empty-icon {
  width: 48px;
  height: 48px;
  color: #9ca3af;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  font-weight: 600;
  color: #6b7280;
  margin: 0 0 8px 0;
}

.empty-subtext {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

/* 애니메이션 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.1);
  }
}

/* 트랜지션 */
.dropdown-slide-enter-active,
.dropdown-slide-leave-active {
  transition: all 0.3s ease;
}

.dropdown-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.dropdown-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.badge-bounce-enter-active {
  animation: bounceIn 0.5s ease;
}

.badge-bounce-leave-active {
  animation: bounceOut 0.3s ease;
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes bounceOut {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(0);
  }
}

.notification-item-enter-active,
.notification-item-leave-active {
  transition: all 0.3s ease;
}

.notification-item-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.notification-item-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 스크롤바 스타일링 */
.notification-list::-webkit-scrollbar {
  width: 6px;
}

.notification-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 반응형 */
@media (max-width: 768px) {
  .notification-dropdown {
    width: 350px;
    right: -16px;
  }
  
  .dropdown-header {
    padding: 16px 20px;
  }
  
  .notification-item {
    padding: 16px 20px;
  }
  
  .empty-state {
    padding: 40px 20px;
  }
}

@media (max-width: 480px) {
  .notification-dropdown {
    width: 320px;
    right: -24px;
  }
}
</style>