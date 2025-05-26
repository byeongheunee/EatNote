<template>
  <div class="my-page">
    <!-- 로그인 상태 전용 헤더 -->
    <Header @go-feature="scrollToFeature" @go-usage="scrollToUsage" />

    <div class="container mx-auto px-6 py-12 max-w-7xl">
      
      <!-- 페이지 제목 -->
      <div class="mb-10">
        <h1 class="text-4xl font-bold text-gray-900 mb-3">마이페이지</h1>
        <p class="text-lg text-gray-600">나의 정보를 확인하고 관리하세요</p>
      </div>

      <!-- 메인 레이아웃: 왼쪽 프로필, 오른쪽 상세정보 -->
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-8 mb-3">
        
        <!-- 왼쪽: 프로필 카드 -->
        <div class="xl:col-span-1">
          <div class="profile-card">
            <div class="profile-header">
              <img 
                :src="getProfileImage(user.profileImage)" 
                alt="프로필" 
                class="profile-image"
              />
              <div class="profile-info">
                <h2 class="profile-name">{{ user.name }}</h2>
                <p class="profile-nickname">@{{ user.nickname }}</p>
                <div class="profile-badges">
                  <span class="badge-type">
                    {{ user.userType === 1 ? '트레이너' : '회원' }}
                  </span>
                  <span class="badge-join">
                    {{ user.createdAt ? new Date(user.createdAt).getFullYear() + '년 가입' : '' }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="profile-actions">
              <button @click="showModal = true" class="edit-button">
                정보 수정
              </button>
            </div>

            <!-- 기본 정보 -->
            <div class="profile-details">
              <div class="detail-row">
                <span class="detail-label">이메일</span>
                <span class="detail-value">{{ user.email }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">성별</span>
                <span class="detail-value">{{ user.gender === 'M' ? '남자' : user.gender === 'F' ? '여자' : user.gender }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">나이</span>
                <span class="detail-value">{{ user.age }}세</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 상세 정보 -->
        <div class="xl:col-span-2">
          
          <!-- 회원 정보 -->
          <div v-if="memberDetails" class="info-section">
            <h3 class="section-title">신체 정보</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">키</span>
                <span class="info-value">{{ memberDetails.height }} cm</span>
              </div>
              <div class="info-item">
                <span class="info-label">몸무게</span>
                <span class="info-value">{{ memberDetails.weight }} kg</span>
              </div>
              <div class="info-item">
                <span class="info-label">BMI</span>
                <span class="info-value">{{ memberDetails.bmi || '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">체수분</span>
                <span class="info-value">{{ memberDetails.bodyWater ? memberDetails.bodyWater + '%' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">단백질</span>
                <span class="info-value">{{ memberDetails.protein ? memberDetails.protein + '%' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">무기질</span>
                <span class="info-value">{{ memberDetails.mineral ? memberDetails.mineral + '%' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">체지방</span>
                <span class="info-value">{{ memberDetails.bodyFat ? memberDetails.bodyFat + 'kg' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">골격근</span>
                <span class="info-value">{{ memberDetails.skeletalMuscle ? memberDetails.skeletalMuscle + 'kg' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">체지방량</span>
                <span class="info-value">{{ memberDetails.bodyFatMass ? memberDetails.bodyFatMass + 'kg' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">체지방률</span>
                <span class="info-value">{{ memberDetails.bodyFatPercentage ? memberDetails.bodyFatPercentage + '%' : '미입력' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">목표</span>
                <span class="info-value">{{ memberDetails.goal }}</span>
              </div>
            </div>

            <!-- 담당 트레이너 -->
            <!-- <div v-if="memberDetails.trainerNickname" class="trainer-info">
              <h4 class="subsection-title">담당 트레이너</h4>
              <p class="trainer-name">{{ memberDetails.trainerNickname }}</p>
            </div> -->

            <!-- 알레르기 정보 -->
            <div class="allergy-section">
              <h4 class="subsection-title">알레르기</h4>
              <div v-if="allergies.length > 0" class="allergy-tags">
                <span 
                  v-for="allergy in allergies" 
                  :key="allergy.allergyId"
                  class="allergy-tag"
                >
                  {{ allergy.name }}
                </span>
              </div>
              <div v-else class="no-allergy">
                <span class="no-allergy-text">등록된 알레르기가 없습니다</span>
              </div>
            </div>
          </div>

          <!-- 트레이너 정보 -->
          <div v-if="trainerDetails" class="info-section">
            <h3 class="section-title">트레이너 정보</h3>
            
            <div class="trainer-grid">
              <div class="trainer-basic">
                <div class="info-item">
                  <span class="info-label">연락처</span>
                  <span class="info-value">{{ trainerDetails.phone }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">주소</span>
                  <span class="info-value">{{ trainerDetails.address }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">헬스장</span>
                  <span class="info-value">{{ trainerDetails.gymName }}</span>
                </div>
                <div v-if="trainerDetails.gymWebsite" class="info-item">
                  <span class="info-label">홈페이지</span>
                  <div class="website-links">
                    <a :href="trainerDetails.gymWebsite" target="_blank" class="website-link">
                      바로가기
                    </a>
                    <button @click="showGymPreview = true" class="preview-button">
                      미리보기
                    </button>
                  </div>
                </div>
              </div>

              <div class="trainer-cert">
                <div class="info-item">
                  <span class="info-label">자격증 번호</span>
                  <div class="cert-links">
                    <span class="info-value">{{ trainerDetails.certificationNumber }}</span>
                    <button 
                      v-if="trainerDetails.certificationImage" 
                      @click="showCertPreview = true" 
                      class="preview-button"
                    >
                      사진보기
                    </button>
                  </div>
                </div>
                <div class="info-item">
                  <span class="info-label">사업자번호</span>
                  <span class="info-value">{{ trainerDetails.businessNumber }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">인증 상태</span>
                  <span class="info-value" :class="trainerDetails.isVerified ? 'verified' : 'unverified'">
                    {{ trainerDetails.isVerified ? '인증 완료' : '미인증' }}
                  </span>
                </div>
                <div v-if="trainerDetails.instagramUrl" class="info-item">
                  <span class="info-label">인스타그램</span>
                  <a :href="trainerDetails.instagramUrl" target="_blank" class="instagram-link">
                    @{{ trainerDetails.instagramUrl.split('/').pop() }}
                  </a>
                </div>
              </div>
            </div>

            <!-- 자격증 이미지 (기존 섹션 제거) -->
            <!-- <div v-if="trainerDetails.certificationImage" class="cert-image-section">
              <h4 class="subsection-title">자격증</h4>
              <img 
                :src="getProfileImage(trainerDetails.certificationImage)" 
                alt="자격증" 
                class="cert-image"
              />
            </div> -->

            <!-- 소개 -->
            <div class="intro-section">
              <div v-if="trainerDetails.introduction" class="intro-item">
                <h4 class="subsection-title">자기소개</h4>
                <p class="intro-text">{{ trainerDetails.introduction }}</p>
              </div>
              <div v-if="trainerDetails.career" class="intro-item">
                <h4 class="subsection-title">경력</h4>
                <p class="intro-text">{{ trainerDetails.career }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 팔로우 리스트 (전체 폭 사용) -->
      <div v-if="memberDetails || trainerDetails" class="follow-section-full">
        <MyFollowList 
          v-if="user.userType === 2" 
          ref="memberFollowListRef" 
          @open-profile="openProfileModal" 
        />
        <MyFollowList 
          v-if="user.userType === 1" 
          ref="trainerFollowListRef" 
          @open-profile="openProfileModal" 
        />
      </div>

      <!-- 최근 게시글 -->
      <div class="articles-section">
        <h3 class="section-title">최근 작성한 게시글</h3>
        <div class="articles-container">
          <div v-if="myArticles.length === 0" class="empty-articles">
            <div class="empty-articles-icon">📝</div>
            <h4 class="empty-articles-title">작성한 게시글이 없습니다</h4>
            <p class="empty-articles-text">첫 번째 게시글을 작성해보세요!</p>
          </div>
          <ArticleList v-else :articles="myArticles.slice(0, 10)" @open-detail="goToDetail" />
        </div>
      </div>
    </div>

    <!-- 자격증 미리보기 모달 -->
    <div v-if="showCertPreview" class="modal-overlay" @click="showCertPreview = false">
      <div class="cert-modal-content" @click.stop>
        <div class="modal-header">
          <h3>자격증 사진</h3>
          <button @click="showCertPreview = false" class="modal-close">✕</button>
        </div>
        <div class="cert-image-modal">
          <img
            :src="getProfileImage(trainerDetails.certificationImage)"
            alt="자격증"
            class="cert-modal-image"
          />
        </div>
      </div>
    </div>

    <!-- 헬스장 미리보기 모달 -->
    <div v-if="showGymPreview" class="modal-overlay" @click="showGymPreview = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>헬스장 홈페이지</h3>
          <button @click="showGymPreview = false" class="modal-close">✕</button>
        </div>
        <iframe
          :src="trainerDetails.gymWebsite"
          class="modal-iframe"
          frameborder="0"
        ></iframe>
      </div>
    </div>

    <!-- 비밀번호 확인 모달 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="password-modal" @click.stop>
        <h3 class="modal-title">비밀번호 확인</h3>
        <input 
          v-model="password" 
          type="password" 
          placeholder="비밀번호를 입력하세요" 
          class="password-input"
        />
        <div class="modal-buttons">
          <button @click="closeModal" class="cancel-button">취소</button>
          <button @click="checkPassword" class="confirm-button">확인</button>
        </div>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </div>
    </div>

    <!-- 프로필 모달 -->
    <UserProfileModal 
      :visible="profileModalVisible" 
      :profile="selectedProfile" 
      @close="profileModalVisible = false"
      @follow-requested="handleFollowRequested" 
    />
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import ArticleList from '@/components/ArticleList.vue'
import MyFollowList from '@/components/common/MyFollowList.vue'
import UserProfileModal from '@/components/UserProfileModal.vue'
import { useToast } from 'vue-toastification'

const showGymPreview = ref(false)
const showCertPreview = ref(false)  // 자격증 미리보기 상태 추가
const toast = useToast()
const myArticles = ref([])
const auth = useAuthStore()
const router = useRouter()
const user = ref({})
const memberDetails = ref(null)
const trainerDetails = ref(null)
const allergies = ref([])

const showModal = ref(false)
const password = ref('')
const errorMessage = ref('')

const memberFollowListRef = ref(null)
const trainerFollowListRef = ref(null)

const profileModalVisible = ref(false)
const selectedProfile = ref(null)

const handleFollowRequested = async () => {
  if (selectedProfile.value?.followStatus === 'ACCEPTED') {
    selectedProfile.value.followStatus = 'NONE'
  } else {
    selectedProfile.value.followStatus = 'PENDING'
  }

  if (user.value.userType === 1 && trainerFollowListRef.value?.refresh) {
    await trainerFollowListRef.value.refresh()
  } else if (user.value.userType === 2 && memberFollowListRef.value?.refresh) {
    await memberFollowListRef.value.refresh()
  }

  profileModalVisible.value = false
}

const openProfileModal = async (otherUser) => {
  try {
    const token = auth.accessToken
    const res = await axios.get(`/api/users/${otherUser.userId}/profile`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    if (!res.data.success) {
      const code = res.data.code
      if (code === 'FORBIDDEN_ADMIN_PROFILE') {
        toast.warning('❌ 관리자는 프로필을 조회할 수 없습니다.')
      } else if (code === 'USER_NOT_FOUND') {
        toast.warning('❌ 해당 사용자가 존재하지 않습니다.')
      } else if (code === 'VALIDATION_FAILED') {
        toast.error('⚠️ 알 수 없는 사용자 유형입니다.')
      } else {
        toast.error(res.data.message || '🚨 알 수 없는 오류가 발생했습니다.')
      }
      return
    }
    selectedProfile.value = res.data.data
    profileModalVisible.value = true
  } catch (e) {
    console.error('상대방 프로필 조회 실패:', e)
    toast.error('프로필 정보를 불러오지 못했습니다.')
  }
}

const fetchMyArticles = async () => {
  try {
    const res = await axios.get('/api/articles', {
      headers: { Authorization: `Bearer ${auth.accessToken}` },
      params: {
        keyword: user.value.nickname,
        searchField: 'NICKNAME',
        sort: 'createdAt'
      }
    })
    myArticles.value = res.data.data
  } catch (err) {
    console.error('내 게시글 조회 실패', err)
  }
}

const goToDetail = (articleId) => {
  const article = myArticles.value.find(a => a.articleId === articleId)
  if (!article) return
  router.push({
    name: 'ArticleDetail',
    params: { boardId: article.boardId, articleId: article.articleId }
  })
}

const getProfileImage = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchMyInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me', {
      headers: { Authorization: `Bearer ${auth.accessToken}` }
    })
    const data = res.data.data
    user.value = data.user
    memberDetails.value = data.memberDetails || null
    trainerDetails.value = data.trainerDetails || null
    allergies.value = data.allergies || []
    auth.setUser(data.user)
  } catch (err) {
    console.error('회원정보 조회 실패', err)
  }
}

const closeModal = () => {
  showModal.value = false
  password.value = ''
  errorMessage.value = ''
}

const checkPassword = async () => {
  try {
    const res = await axios.post('/api/users/check-password', {
      password: password.value
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    })

    if (res.data.success) {
      closeModal()
      router.push('/profile/edit')
    } else {
      errorMessage.value = '비밀번호가 일치하지 않습니다.'
    }
  } catch (err) {
    errorMessage.value = '서버 오류 또는 인증 실패입니다.'
  }
}

const scrollToFeature = () => {}
const scrollToUsage = () => {}

onMounted(async () => {
  await fetchMyInfo()
  await fetchMyArticles()
})
</script>

<style scoped>
/* 기본 레이아웃 */
.my-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

.container {
  max-width: 1400px;
}

/* 프로필 카드 */
.profile-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.profile-header {
  padding: 38px;
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
  padding: 30px 32px;
  border-bottom: 1px solid #f3f4f6;
}

.edit-button {
  width: 100%;
  background: #f59e0b;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-button:hover {
  background: #d97706;
  transform: translateY(-1px);
}

.profile-details {
  padding: 27px 32px;
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

/* 정보 섹션 */
.info-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 32px;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f59e0b;
}

.subsection-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

/* 정보 그리드 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;  /* 기존 20px에서 24px로 증가 */
  margin-bottom: 40px;  /* 기존 32px에서 40px로 증가 */
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;  /* 기존 4px에서 8px로 증가 */
  margin-bottom: 20px;  /* 항목 간 간격 추가 */
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  line-height: 1.5;  /* 줄 간격 추가 */
}

.verified {
  color: #059669;
}

.unverified {
  color: #dc2626;
}

/* 트레이너 그리드 */
.trainer-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;  /* 기존 32px에서 40px로 증가 */
  margin-bottom: 40px;  /* 기존 32px에서 40px로 증가 */
}

/* 트레이너 기본 정보와 자격증 정보 */
.trainer-basic,
.trainer-cert {
  padding: 24px;  /* 내부 패딩 추가 */
  background: rgba(249, 250, 251, 0.5);  /* 배경색으로 구분 */
  border-radius: 12px;  /* 둥근 모서리 */
  border: 1px solid rgba(229, 231, 235, 0.3);  /* 테두리 추가 */
}

/* 자격증 링크 스타일 */
.cert-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 자격증 모달 전용 스타일 */
.cert-modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 900px;  /* 600px에서 900px로 증가 */
  max-height: 90vh;  /* 80vh에서 90vh로 증가 */
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

/* 자격증 모달 스타일 */
.cert-image-modal {
  padding: 20px;
  text-align: center;
  background: #f9fafb;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 600px;  /* 400px에서 600px로 증가 */
}

.cert-modal-image {
  max-width: 100%;
  max-height: 75vh;  /* 60vh에서 75vh로 증가 */
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  object-fit: contain;
  background: white;  /* 이미지 배경 */
}

/* 웹사이트 링크 */
.website-links {
  display: flex;
  gap: 12px;
}

.website-link,
.instagram-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
}

.website-link:hover,
.instagram-link:hover {
  text-decoration: underline;
}

.preview-button {
  background: none;
  border: 1px solid #d1d5db;
  color: #6b7280;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.preview-button:hover {
  background: #f9fafb;
}

/* 자격증 이미지 */
.cert-image-section {
  margin-bottom: 40px;  /* 기존 32px에서 40px로 증가 */
  padding: 24px;  /* 패딩 추가 */
  background: rgba(249, 250, 251, 0.3);  /* 배경색 추가 */
  border-radius: 12px;  /* 둥근 모서리 */
}

.cert-image {
  max-width: 300px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 12px;  /* 제목과 이미지 사이 간격 */
}

/* 소개 섹션 */
.intro-section {
  margin-top: 40px;  /* 상단 간격 증가 */
}

.intro-item {
  margin-bottom: 32px;  /* 기존 24px에서 32px로 증가 */
  padding: 24px;  /* 패딩 추가 */
  background: rgba(249, 250, 251, 0.3);  /* 배경색 추가 */
  border-radius: 12px;  /* 둥근 모서리 */
  border: 1px solid rgba(229, 231, 235, 0.3);  /* 테두리 추가 */
}

.intro-text {
  color: #374151;
  line-height: 1.8;  /* 기존 1.6에서 1.8로 증가 */
  margin-top: 12px;  /* 제목과 내용 사이 간격 */
  font-size: 15px;  /* 폰트 크기 약간 증가 */
}

/* 트레이너 정보 */
.trainer-info {
  margin-bottom: 24px;
}

.trainer-name {
  font-weight: 600;
  color: #111827;
}

/* 알레르기 */
.allergy-section {
  margin-bottom: 24px;
}

.allergy-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.allergy-tag {
  background: #fef2f2;
  color: #dc2626;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #fecaca;
}

/* 팔로우 섹션 (전체 폭) */
.follow-section-full {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 32px;
  margin-bottom: 24px;
}

/* 알레르기 섹션 스타일 */
.no-allergy {
  text-align: center;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px dashed #d1d5db;
}

.no-allergy-text {
  color: #6b7280;
  font-style: italic;
}

/* 빈 게시글 상태 */
.empty-articles {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, rgba(249, 250, 251, 0.8) 0%, rgba(243, 244, 246, 0.6) 100%);
  border-radius: 16px;
  border: 2px dashed #d1d5db;
}

.empty-articles-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.6;
}

.empty-articles-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.75rem;
}

.empty-articles-text {
  color: #6b7280;
  font-size: 1rem;
  font-weight: 500;
}

/* 게시글 섹션 */
.articles-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 32px;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 1200px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #6b7280;
}

.modal-iframe {
  width: 100%;
  height: 70vh;
}

.password-modal {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 24px;
  color: #111827;
}

.password-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  margin-bottom: 24px;
  font-size: 16px;
}

.password-input:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.modal-buttons {
  display: flex;
  gap: 12px;
}

.cancel-button {
  flex: 1;
  padding: 12px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.confirm-button {
  flex: 1;
  padding: 12px;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.confirm-button:hover {
  background: #d97706;
}

.error-message {
  color: #dc2626;
  text-align: center;
  margin-top: 12px;
  font-size: 14px;
}

/* 반응형 */
@media (max-width: 1280px) {
  .trainer-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .profile-header,
  .profile-actions,
  .profile-details,
  .info-section,
  .articles-section {
    padding: 20px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>