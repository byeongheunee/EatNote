<template>
  <div class="follow-request-card group">
    <!-- 프로필 이미지 컨테이너 -->
    <div class="profile-container">
      <img 
        :src="getProfileImage(user.profileImage)" 
        :alt="user.nickname"
        class="profile-image" 
        @error="handleImageError"
      />
      
      <!-- 요청 날짜 뱃지 -->
      <div class="date-badge">
        <span class="date-text">{{ formatDate(user.requestedAt) }}</span>
      </div>
    </div>

    <!-- 카드 내용 -->
    <div class="card-content">
      <!-- 사용자 정보 -->
      <div class="user-info">
        <h3 class="username">{{ user.nickname }}</h3>
        <p class="request-label">팔로우 요청</p>
      </div>
      
      <!-- 액션 버튼들 -->
      <div class="action-buttons">
        <button 
          class="accept-button"
          @click="$emit('accept', user.followId)"
        >
          <span class="button-icon">✓</span>
          <span class="button-text">수락</span>
        </button>
        
        <button 
          class="reject-button"
          @click="$emit('reject', user.followId)"
        >
          <span class="button-icon">✕</span>
          <span class="button-text">거절</span>
        </button>
      </div>
    </div>

    <!-- 카드 테두리 효과 -->
    <div class="card-border"></div>
  </div>
</template>

<script setup>
const props = defineProps(['user'])

// 이벤트 정의
defineEmits(['accept', 'reject'])

// 날짜 포맷
const formatDate = (datetime) => {
  const date = new Date(datetime)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}.${day}`
}

// 프로필 이미지 URL
const getProfileImage = (path) => {
  return path ? `http://localhost:8080${path}` : '/images/default-profile.png'
}

// 이미지 로드 에러 처리
const handleImageError = (event) => {
  event.target.src = '/images/default-profile.png'
}
</script>

<style scoped>
.follow-request-card {
  position: relative;
  width: 100%;
  max-width: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.06);
  margin-top: 10px;
  margin-left: 8px;
}

.follow-request-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.15);
  border-color: rgba(16, 185, 129, 0.3);
}

/* 프로필 이미지 컨테이너 */
.profile-container {
  position: relative;
  padding: 20px 20px 10px 20px;
  display: flex;
  justify-content: center;
}

.profile-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.follow-request-card:hover .profile-image {
  transform: scale(1.05);
}

/* 날짜 뱃지 */
.date-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: 12px;
  padding: 4px 8px;
}

.date-text {
  font-size: 0.75rem;
  font-weight: 600;
  color: #10b981;
}

/* 카드 내용 */
.card-content {
  padding: 0 16px 16px 16px;
  text-align: center;
}

/* 사용자 정보 */
.user-info {
  margin-bottom: 16px;
}

.username {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 4px;
  line-height: 1.3;
}

.request-label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

/* 액션 버튼들 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.accept-button,
.reject-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px 12px;
  border: none;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 36px;
}

.accept-button {
  background: linear-gradient(135deg, #93d48f, #93d48f);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.accept-button:hover {
  background: linear-gradient(135deg, #7bbb77, #7bbb77);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.reject-button {
  background: linear-gradient(135deg, #f07c7c, #f07c7c);
  color: white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

.reject-button:hover {
  background: linear-gradient(135deg, #ec5656, #ec5656);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}

.button-icon {
  font-size: 0.9rem;
  font-weight: bold;
}

.button-text {
  font-size: 0.8rem;
}

/* 카드 테두리 효과 */
.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, transparent 0%, rgba(16, 185, 129, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.follow-request-card:hover .card-border {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .follow-request-card {
    max-width: 100%;
  }
  
  .profile-container {
    padding: 16px 16px 8px 16px;
  }
  
  .profile-image {
    width: 70px;
    height: 70px;
  }
  
  .card-content {
    padding: 0 12px 12px 12px;
  }
  
  .username {
    font-size: 1rem;
  }
  
  .action-buttons {
    gap: 6px;
  }
  
  .accept-button,
  .reject-button {
    padding: 6px 10px;
    min-height: 32px;
  }
  
  .button-text {
    font-size: 0.75rem;
  }
}

/* 포커스 상태 (접근성) */
.accept-button:focus,
.reject-button:focus {
  outline: 2px solid;
  outline-offset: 2px;
}

.accept-button:focus {
  outline-color: #10b981;
}

.reject-button:focus {
  outline-color: #ef4444;
}

/* 애니메이션 */
@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.follow-request-card {
  animation: cardEnter 0.5s ease-out;
}
</style>