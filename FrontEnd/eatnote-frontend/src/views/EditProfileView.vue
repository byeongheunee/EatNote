<template>
  <div class="edit-profile-page">
    <!-- 헤더 추가 -->
    <Header @go-feature="scrollToFeature" @go-usage="scrollToUsage" />
    
    <div class="container mx-auto px-6 py-12 max-w-7xl">
      
      <!-- 페이지 제목과 돌아가기 버튼 -->
      <div class="mb-10 flex justify-between items-end">
        <div>
          <h1 class="text-4xl font-bold text-gray-900 mb-3">회원정보 수정</h1>
          <p class="text-lg text-gray-600">나의 정보를 최신 상태로 관리하세요</p>
        </div>
        <button
          @click="goBack"
          class="nav-button secondary"
        >
          <span class="button-icon">←</span>
          <span>돌아가기</span>
        </button>
      </div>

      <!-- 메인 레이아웃: 왼쪽 프로필, 오른쪽 수정폼 -->
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-8 mb-10">
        
        <!-- 왼쪽: 프로필 카드 (sticky 고정) -->
        <div class="xl:col-span-1">
          <div class="profile-card sticky-profile">
            <div class="profile-header">
              <img 
                :src="getProfileImage(userData.user?.profileImage)" 
                alt="프로필" 
                class="profile-image"
              />
              <div class="profile-info">
                <h2 class="profile-name">{{ userData.user?.name || '사용자' }}</h2>
                <p class="profile-nickname">@{{ userData.user?.nickname || 'nickname' }}</p>
                <div class="profile-badges">
                  <span class="badge-type">
                    {{ userType === 1 ? '트레이너' : '회원' }}
                  </span>
                  <span class="badge-join">
                    {{ userData.user?.createdAt ? new Date(userData.user.createdAt).getFullYear() + '년 가입' : '' }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="profile-actions">
              <button class="edit-button-active">
                정보 수정 중
              </button>
            </div>

            <!-- 기본 정보 -->
            <div class="profile-details">
              <div class="detail-row">
                <span class="detail-label">이메일</span>
                <span class="detail-value">{{ userData.user?.email }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">성별</span>
                <span class="detail-value">{{ userData.user?.gender === 'M' ? '남자' : userData.user?.gender === 'F' ? '여자' : userData.user?.gender }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">나이</span>
                <span class="detail-value">{{ userData.user?.age }}세</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 수정 폼 -->
        <div class="xl:col-span-2">
          <component :is="editComponent" :userData="userData" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import axios from 'axios'

import Header from '@/components/common/Header.vue'
import EditMemberForm from '@/components/profile/EditMemberForm.vue'
import EditTrainerForm from '@/components/profile/EditTrainerForm.vue'

const router = useRouter()

// 스토어에서 사용자 정보 가져오기
const auth = useAuthStore()
const userType = auth.user.userType
 
const userData = ref({})
const editComponent = ref('')

// Axios interceptor는 글로벌 설정에 적용돼 있어야 함!
const fetchMyInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userData.value = res.data.data

    // 스토어에 있는 userType으로 분기
    editComponent.value = userType === 1 ? EditTrainerForm : EditMemberForm
  } catch (err) {
    console.error('회원정보 조회 실패:', err)
  }
}

const defaultProfile = '/images/default-profile.png'
const getProfileImage = (path) => path ? `http://localhost:8080${path}` : defaultProfile

const goBack = () => {
  router.go(-1)
}

// 스크롤 함수들 (Header emit용)
const scrollToFeature = () => {}
const scrollToUsage = () => {}

onMounted(fetchMyInfo)
</script>

<style scoped>
/* 기본 레이아웃 */
.edit-profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

.container {
  max-width: 1400px;
}

/* 프로필 카드 (마이페이지와 동일 + sticky) */
.profile-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.sticky-profile {
  position: sticky;
  top: 100px; /* 헤더 높이만큼 여백 확보 */
  align-self: flex-start;
}

.profile-header {
  padding: 32px;
  text-align: center;
  border-bottom: 1px solid #f3f4f6;
}

.profile-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 auto 20px;
  border: 4px solid #f3f4f6;
}

.profile-name {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.profile-nickname {
  font-size: 16px;
  color: #6b7280;
  margin-bottom: 16px;
}

.profile-badges {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.badge-type,
.badge-join {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.profile-actions {
  padding: 24px 32px;
  border-bottom: 1px solid #f3f4f6;
}

.edit-button-active {
  width: 100%;
  background: #0aa674;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: default;
  opacity: 0.8;
}

.profile-details {
  padding: 24px 32px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f9fafb;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 500;
  color: #6b7280;
}

.detail-value {
  font-weight: 600;
  color: #111827;
}

/* 네비게이션 버튼 (상단용) */
.nav-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
  min-width: 120px;
  justify-content: center;
}

.nav-button.secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
}

.nav-button.secondary:hover {
  background: #e5e7eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.button-icon {
  font-size: 18px;
}

/* 반응형 디자인 */
@media (max-width: 1280px) {
  .sticky-profile {
    position: static; /* 모바일에서는 sticky 해제 */
  }
  
  .nav-button {
    width: auto;
    min-width: 100px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .profile-header,
  .profile-details {
    padding: 20px;
  }
  
  .nav-button {
    padding: 10px 16px;
    font-size: 14px;
    min-width: 90px;
  }
  
  .button-icon {
    font-size: 16px;
  }
  
  /* 모바일에서 제목과 버튼 세로 배치 */
  .mb-10.flex {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

/* 애니메이션 */
.container {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #fbbf24;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #f59e0b;
}
</style>