<template>
  <div class="follow-requests-container">
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner">⏳</div>
      <p class="loading-text">팔로우 요청을 불러오는 중...</p>
    </div>
    
    <div v-else-if="users.length === 0" class="empty-state">
      <div class="empty-icon">👥</div>
      <p class="empty-text">현재 팔로우 요청이 없습니다</p>
      <p class="empty-subtext">새로운 요청이 오면 여기에 표시됩니다</p>
    </div>

    <div v-else class="requests-swiper-container">
      <Swiper 
        :modules="[Navigation]" 
        :slides-per-view="3"
        :space-between="15" 
        :breakpoints="{
          640: { slidesPerView: 3, spaceBetween: 10 },
          768: { slidesPerView: 3, spaceBetween: 12 },
          1024: { slidesPerView: 3, spaceBetween: 14 }
        }"
        navigation
        class="follow-requests-swiper"
      >
        <SwiperSlide v-for="user in users" :key="user.followId">
          <div class="request-card-wrapper">
            <FollowRequestCard 
              :user="user" 
              @accept="acceptRequest"
              @reject="rejectRequest"
            />
          </div>
        </SwiperSlide>
      </Swiper>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import axios from 'axios'
import FollowRequestCard from './FollowRequestCard.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const users = ref([])
const loading = ref(true)

// emit 정의
const emit = defineEmits(['update-count'])

const fetchRequests = async () => {
  try {
    const token = auth.accessToken
    const res = await axios.get('/api/trainer/follow/requests', {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = res.data.data || []
  } catch (e) {
    console.error('팔로우 요청 조회 실패', e)
  } finally {
    loading.value = false
  }
}

const acceptRequest = async (followId) => {
  try {
    const token = auth.accessToken
    await axios.post('/api/follow/respond', {
      followId,
      action: 'ACCEPT'
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = users.value.filter(u => u.followId !== followId)
    console.log('팔로우 요청 수락 완료')
  } catch (e) {
    console.error('팔로우 수락 실패', e)
    alert('팔로우 수락에 실패했습니다.')
  }
}

const rejectRequest = async (followId) => {
  try {
    const token = auth.accessToken
    await axios.post('/api/follow/respond', {
      followId,
      action: 'REJECT'
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = users.value.filter(u => u.followId !== followId)
    console.log('팔로우 요청 거절 완료')
  } catch (e) {
    console.error('팔로우 거절 실패', e)
    alert('팔로우 거절에 실패했습니다.')
  }
}

// users 배열 변화 감지하여 부모에 개수 전달
watch(users, (newUsers) => {
  emit('update-count', newUsers.length)
}, { immediate: true })

onMounted(fetchRequests)
</script>

<style scoped>
.follow-requests-container {
  width: 100%;
  min-height: 200px;
}

/* 로딩 상태 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.loading-spinner {
  font-size: 2rem;
  margin-bottom: 16px;
  animation: pulse 2s infinite;
}

.loading-text {
  font-size: 1rem;
  color: #6b7280;
  font-weight: 500;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.empty-subtext {
  font-size: 0.9rem;
  color: #6b7280;
}

/* 스위퍼 컨테이너 */
.requests-swiper-container {
  position: relative;
  width: 100%;
}

.follow-requests-swiper {
  padding: 0;
  margin: 0;
}

.follow-requests-swiper :deep(.swiper-button-prev),
.follow-requests-swiper :deep(.swiper-button-next) {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border-radius: 50%;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.15);
  color: #10b981;
  transition: all 0.3s ease;
  border: 1px solid rgba(16, 185, 129, 0.2);
  z-index: 10;
}

.follow-requests-swiper :deep(.swiper-button-prev:hover),
.follow-requests-swiper :deep(.swiper-button-next:hover) {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  transform: scale(1.1);
  box-shadow: 0 5px 18px rgba(16, 185, 129, 0.3);
}

.follow-requests-swiper :deep(.swiper-button-prev) {
  left: 10px;
}

.follow-requests-swiper :deep(.swiper-button-next) {
  right: 10px;
}

.follow-requests-swiper :deep(.swiper-button-prev::after),
.follow-requests-swiper :deep(.swiper-button-next::after) {
  font-size: 12px;
  font-weight: bold;
}

.request-card-wrapper {
  padding: 0;
  width: 100%;
}

/* 애니메이션 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .loading-state,
  .empty-state {
    padding: 30px 15px;
  }
  
  .empty-icon {
    font-size: 2.5rem;
  }
  
  .empty-text {
    font-size: 1rem;
  }
  
  .empty-subtext {
    font-size: 0.85rem;
  }
}
</style>