<template>
  <div class="follow-list-container">
    <!-- 섹션 제목 (MyPageView와 동일한 스타일) -->
    <h3 class="section-title">팔로우</h3>
    
    <div class="follow-scroll-area">
      <!-- 빈 상태 -->
      <div v-if="users.length === 0" class="empty-state">
        <div class="empty-icon">{{ isTrainer ? '👥' : '🔍' }}</div>
        <p class="empty-text">
          {{ isTrainer ? '관리중인 회원이 없습니다' : '팔로우한 사용자가 없습니다' }}
        </p>
      </div>

      <!-- 유저 카드들 -->
      <div 
        v-for="otherUser in users" 
        :key="otherUser.nickname" 
        class="follow-card"
        @click="$emit('open-profile', otherUser)"
      >
        <div class="profile-section">
          <div class="profile-image-wrapper">
            <img 
              :src="getProfileImage(otherUser.profileImage)" 
              class="profile-image" 
              :alt="otherUser.nickname" 
            />
            <div class="status-indicator" :class="getStatusClass(otherUser)">
              {{ getStatusEmoji(otherUser) }}
            </div>
          </div>
        </div>
        
        <div class="user-info">
          <h3 class="user-name">{{ otherUser.nickname }}</h3>
          <div class="user-type">
            <span class="type-badge" :class="otherUser.userType === 1 ? 'trainer-badge' : 'member-badge'">
              {{ otherUser.userType === 1 ? '🏋️ 트레이너' : '👤 회원' }}
            </span>
          </div>
        </div>

        <!-- 활동 상태 표시 -->
        <div v-if="otherUser.lastActivity" class="activity-info">
          <span class="activity-text">{{ getActivityText(otherUser) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userType = computed(() => auth.user.userType)
const isTrainer = computed(() => userType.value === 1)

const users = ref([])

defineEmits(['open-profile'])

const getProfileImage = (path) =>
  path ? `http://localhost:8080${path}` : '/images/default-profile.png'

// 상태 클래스 결정 (활동 여부에 따라)
const getStatusClass = (user) => {
  // 실제 활동 데이터가 있다면 그에 따라 분기
  if (user.isActive) return 'status-active'
  if (user.lastActivity) return 'status-recent'
  return 'status-inactive'
}

// 상태 이모지 결정
const getStatusEmoji = (user) => {
  if (user.isActive) return '🟢'
  if (user.lastActivity) return '🟡'
  return '⚪'
}

// 활동 텍스트 생성
const getActivityText = (user) => {
  if (user.isActive) return '온라인'
  if (user.lastActivity) return '최근 활동'
  return '오프라인'
}

const fetchUsers = async () => {
  try {
    const url = isTrainer.value ? '/api/follow/followers' : '/api/follow/following'
    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
    users.value = res.data.data
  } catch (err) {
    console.error('팔로우 목록 조회 실패', err)
  }
}

const refresh = async () => {
  await fetchUsers()
}
defineExpose({ refresh })

onMounted(fetchUsers)
</script>

<style scoped>
/* 섹션 제목 (MyPageView와 동일) */
.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f59e0b;
}

.follow-list-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.follow-scroll-area {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  padding: 0.5rem 0 1rem 0;
  scrollbar-width: thin;
  scrollbar-color: rgba(245, 158, 11, 0.3) transparent;
}

.follow-scroll-area::-webkit-scrollbar {
  height: 6px;
}

.follow-scroll-area::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.follow-scroll-area::-webkit-scrollbar-thumb {
  background: rgba(245, 158, 11, 0.3); /* 스크롤바 색상 */
  border-radius: 3px;
}

.follow-scroll-area::-webkit-scrollbar-thumb:hover {
  background: rgba(245, 158, 11, 0.5); /* 호버 시 색상 */
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  text-align: center;
  width: 100%;
  background: linear-gradient(135deg, rgba(249, 250, 251, 0.8) 0%, rgba(243, 244, 246, 0.6) 100%);
  border: 2px dashed #d1d5db;
  border-radius: 20px;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-text {
  color: #6b7280;
  font-size: 1rem;
  font-weight: 500;
}

/* 카드 기본 스타일 */
.follow-card {
  position: relative;
  min-width: 180px;
  max-width: 200px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  padding: 1.25rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-left: 5px;
}

.follow-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.4);
}

/* 프로필 섹션 */
.profile-section {
  position: relative;
  margin-bottom: 1rem;
}

.profile-image-wrapper {
  position: relative;
  display: inline-block;
}

.profile-image {
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.follow-card:hover .profile-image {
  transform: scale(1.05);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
}

/* 상태 인디케이터 */
.status-indicator {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 1.5rem;
  height: 1.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  border: 2px solid white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.status-active {
  background: linear-gradient(135deg, #10b981, #059669);
}

.status-recent {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.status-inactive {
  background: linear-gradient(135deg, #9ca3af, #6b7280);
}

/* 유저 정보 */
.user-info {
  width: 100%;
  margin-bottom: 0.75rem;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.5rem;
  line-height: 1.2;
}

.user-type {
  display: flex;
  justify-content: center;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  border: 1px solid;
}

.trainer-badge {
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.1), rgba(139, 92, 246, 0.05));
  border-color: rgba(168, 85, 247, 0.2);
  color: #7c3aed;
}

.member-badge {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(37, 99, 235, 0.05));
  border-color: rgba(59, 130, 246, 0.2);
  color: #2563eb;
}

/* 활동 정보 */
.activity-info {
  width: 100%;
  text-align: center;
}

.activity-text {
  font-size: 0.7rem;
  color: #6b7280;
  font-weight: 500;
  background: rgba(249, 250, 251, 0.8);
  padding: 0.25rem 0.5rem;
  border-radius: 8px;
  border: 1px solid rgba(229, 231, 235, 0.5);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .follow-card {
    min-width: 150px;
    max-width: 160px;
    padding: 1rem;
  }
  
  .profile-image {
    width: 3.5rem;
    height: 3.5rem;
  }
  
  .user-name {
    font-size: 0.85rem;
  }
  
  .type-badge {
    font-size: 0.7rem;
    padding: 0.2rem 0.6rem;
  }
}

@media (max-width: 640px) {
  .follow-scroll-area {
    gap: 1rem;
  }
  
  .follow-card {
    min-width: 140px;
    max-width: 150px;
    padding: 0.875rem;
  }
  
  .profile-image {
    width: 3rem;
    height: 3rem;
  }
  
  .status-indicator {
    width: 1.25rem;
    height: 1.25rem;
    font-size: 0.6rem;
  }
}

/* 카드 등장 애니메이션 */
.follow-card {
  animation: fadeInScale 0.5s ease-out;
}

.follow-card:nth-child(1) { animation-delay: 0s; }
.follow-card:nth-child(2) { animation-delay: 0.1s; }
.follow-card:nth-child(3) { animation-delay: 0.2s; }
.follow-card:nth-child(4) { animation-delay: 0.3s; }
.follow-card:nth-child(5) { animation-delay: 0.4s; }

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>