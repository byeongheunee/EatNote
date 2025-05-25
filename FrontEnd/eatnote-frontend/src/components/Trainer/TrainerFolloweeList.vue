<template>
  <div class="followee-list-container">
    <div class="followee-scroll-area">
      <!-- 전체 보기 카드 -->
      <div 
        class="followee-card all-card"
        :class="{ 'selected': !selectedUserId }"
        @click="$emit('select', null)"
      >
        <div class="profile-section">
          <div class="profile-image-wrapper all-profile">
            <img :src="defaultProfile" class="profile-image" alt="전체" />
            <!-- <div class="status-indicator all-indicator">📊</div> -->
          </div>
        </div>
        
        <div class="user-info">
          <h3 class="user-name">미완료 전체 보기</h3>
          <div class="stats-container">
            <div class="stat-item pending">
              <span class="stat-icon">🟡</span>
              <span class="stat-text">{{ totalPending || 0 }}개 대기</span>
            </div>
            <!-- <div class="stat-item completed">
              <span class="stat-icon">🟢</span>
              <span class="stat-text">{{ totalDone || 0 }}개 완료</span>
            </div> -->
          </div>
        </div>
      </div>

      <!-- 유저 카드들 -->
      <div 
        v-for="user in users" 
        :key="user.userId" 
        class="followee-card user-card"
        :class="{ 'selected': selectedUserId === user.userId }"
        @click="$emit('select', user.userId)"
      >
        <div class="profile-section">
          <div class="profile-image-wrapper">
            <img :src="getImageUrl(user.profileImage)" class="profile-image" :alt="user.nickname" />
            <div class="status-indicator" :class="getStatusClass(user)">
              {{ getStatusEmoji(user) }}
            </div>
          </div>
        </div>
        
        <div class="user-info">
          <h3 class="user-name">{{ user.nickname }}</h3>
          <div class="stats-container">
            <div class="stat-item pending">
              <span class="stat-icon">🟡</span>
              <span class="stat-text">{{ user.pendingCount || 0 }}개</span>
            </div>
            <div class="stat-item completed">
              <span class="stat-icon">🟢</span>
              <span class="stat-text">{{ user.writtenCount || 0 }}개</span>
            </div>
          </div>
        </div>

        <!-- 우선순위 뱃지 -->
        <div v-if="user.pendingCount > 0" class="priority-badge">
          <span class="priority-text">{{ user.pendingCount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import defaultProfile from '@/default-profile.png'

defineProps({
  users: Array,
  selectedUserId: [Number, null],
  totalPending: Number,
  totalDone: Number
})

defineEmits(['select'])

// 이미지 경로 조립
const getImageUrl = (path) => {
  if (!path) return defaultProfile
  return `http://localhost:8080/${path.replace(/^\/+/, '')}`
}

// 상태 클래스 결정
const getStatusClass = (user) => {
  const pending = user.pendingCount || 0
  const completed = user.writtenCount || 0
  
  if (pending > 0) return 'status-pending'
  if (completed > 0) return 'status-completed'
  return 'status-inactive'
}

// 상태 이모지 결정
const getStatusEmoji = (user) => {
  const pending = user.pendingCount || 0
  const completed = user.writtenCount || 0
  
  if (pending > 0) return '⚡'
  if (completed > 0) return '✅'
  return '💤'
}
</script>

<style scoped>
.followee-list-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.followee-scroll-area {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  padding: 0.5rem 0 1rem 0;
  scrollbar-width: thin;
  scrollbar-color: rgba(245, 158, 11, 0.3) transparent;
}

.followee-scroll-area::-webkit-scrollbar {
  height: 6px;
}

.followee-scroll-area::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.followee-scroll-area::-webkit-scrollbar-thumb {
  background: rgba(245, 158, 11, 0.3);
  border-radius: 3px;
}

.followee-scroll-area::-webkit-scrollbar-thumb:hover {
  background: rgba(245, 158, 11, 0.5);
}

/* 카드 기본 스타일 */
.followee-card {
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
}

.followee-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.4);
}

.followee-card.selected {
  border-color: #f59e0b;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15) 0%, rgba(217, 119, 6, 0.1) 100%);
  transform: translateY(-2px);
}

/* 전체 보기 카드 특별 스타일 */
.all-card {
  border-color: rgba(16, 185, 129, 0.3);
}

.all-card:hover {
  border-color: rgba(16, 185, 129, 0.6);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.15);
}

.all-card.selected {
  border-color: #10b981;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.1) 100%);
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

.all-profile .profile-image {
  border-color: rgba(16, 185, 129, 0.5);
}

.followee-card:hover .profile-image {
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

.all-indicator {
  background: linear-gradient(135deg, #10b981, #059669);
}

.status-pending {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.status-completed {
  background: linear-gradient(135deg, #10b981, #059669);
}

.status-inactive {
  background: linear-gradient(135deg, #9ca3af, #6b7280);
}

/* 유저 정보 */
.user-info {
  width: 100%;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.75rem;
  line-height: 1.2;
}

/* 통계 컨테이너 */
.stats-container {
  display: flex;
  gap: 0.5rem; /* 세로 배치에서 가로 배치로 변경 */
}

.stat-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem; /* 간격 축소 */
  padding: 0.25rem 0.5rem; /* 패딩 축소 */
  border-radius: 8px; /* 둥근 모서리 축소 */
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  transition: all 0.2s ease;
  flex: 1; /* 동일한 너비로 분배 */
}

.stat-item.pending {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.05));
  border-color: rgba(245, 158, 11, 0.2);
}

.stat-item.completed {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(5, 150, 105, 0.05));
  border-color: rgba(16, 185, 129, 0.2);
}

.stat-icon {
  font-size: 0.8rem;
}

.stat-text {
  font-size: 0.7rem; /* 폰트 크기 축소 */
  font-weight: 600;
  color: #374151;
  white-space: nowrap; /* 텍스트 줄바꿈 방지 */
}

/* 우선순위 뱃지 */
.priority-badge {
  position: absolute;
  top: -0.5rem;
  right: -0.5rem;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  border-radius: 50%;
  width: 1.5rem;
  height: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
  border: 2px solid white;
  animation: pulse 2s infinite;
}

.priority-text {
  line-height: 1;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .followee-card {
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
  
  .stat-text {
    font-size: 0.65rem; /* 모바일에서 더 작게 */
  }
}

@media (max-width: 640px) {
  .followee-scroll-area {
    gap: 1rem;
  }
  
  .followee-card {
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

/* 애니메이션 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 카드 등장 애니메이션 */
.followee-card {
  animation: fadeInScale 0.5s ease-out;
}

.followee-card:nth-child(1) { animation-delay: 0s; }
.followee-card:nth-child(2) { animation-delay: 0.1s; }
.followee-card:nth-child(3) { animation-delay: 0.2s; }
.followee-card:nth-child(4) { animation-delay: 0.3s; }
.followee-card:nth-child(5) { animation-delay: 0.4s; }

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