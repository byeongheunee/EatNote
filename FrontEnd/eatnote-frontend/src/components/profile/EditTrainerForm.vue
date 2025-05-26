<template>
  <div>
    <!-- 프로필 사진 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">프로필 사진</h3>
      <div class="profile-image-container">
        <div v-if="!profileDeleted" class="current-image">
          <img :src="getImageUrl(user.profileImage)" alt="프로필 이미지" class="profile-edit-image" />
          <button @click="deleteProfileImage" class="delete-button">
            🗑 삭제하기
          </button>
        </div>
        <div v-else class="upload-new">
          <input type="file" accept="image/*" @change="handleProfileImageUpload" class="file-input" />
          <p class="upload-notice">새 프로필 사진을 선택하세요</p>
        </div>
      </div>
    </div>

    <!-- 자격증 이미지 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">자격증 이미지</h3>
      <div class="cert-image-container">
        <div v-if="!certDeleted" class="current-cert">
          <div class="cert-image-wrapper">
            <img :src="getImageUrl(trainer.certificationImage)" alt="자격증 이미지" class="cert-edit-image" />
          </div>
          <button @click="deleteCertImage" class="delete-button">
            🗑 삭제하기
          </button>
        </div>
        <div v-else class="upload-new">
          <input type="file" accept="image/*" @change="handleCertUpload" class="file-input" />
          <p class="upload-notice">새 자격증 이미지를 선택하세요</p>
        </div>
      </div>
    </div>

    <!-- 기본 정보 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">기본 정보</h3>
      <div class="basic-info-container">
        <!-- 닉네임 필드 -->
        <div class="info-item full-width">
          <label class="info-label">닉네임</label>
          <div class="nickname-container">
            <input 
              v-model="form.nickname" 
              placeholder="닉네임을 입력하세요" 
              class="info-input"
              :class="{ 'error': nicknameError, 'success': nicknameChecked }"
              @input="onNicknameChange"
            />
            <button 
              @click="checkNickname" 
              :disabled="!canCheckNickname"
              class="check-button"
              :class="{ 'checking': isCheckingNickname }"
            >
              {{ isCheckingNickname ? '확인중...' : '중복확인' }}
            </button>
          </div>
          <p v-if="nicknameMessage" class="validation-message" :class="{ 'error': nicknameError, 'success': !nicknameError }">
            {{ nicknameMessage }}
          </p>
        </div>

        <!-- 비밀번호 섹션 -->
        <div class="password-section">
          <!-- 비밀번호 필드 -->
          <div class="info-item">
            <label class="info-label">비밀번호</label>
            <input 
              v-model="form.password" 
              type="password" 
              placeholder="새 비밀번호 (선택사항)" 
              class="info-input"
              @input="onPasswordChange"
            />
          </div>

          <!-- 비밀번호 확인 필드 (비밀번호 입력 시에만 표시) -->
          <div v-if="form.password" class="info-item">
            <label class="info-label">비밀번호 확인</label>
            <input 
              v-model="form.passwordConfirm" 
              type="password" 
              placeholder="비밀번호를 다시 입력하세요" 
              class="info-input"
              :class="{ 'error': passwordMismatch && form.passwordConfirm }"
              @input="checkPasswordMatch"
            />
            <p v-if="passwordMismatch && form.passwordConfirm" class="validation-message error">
              비밀번호가 일치하지 않습니다
            </p>
            <p v-if="!passwordMismatch && form.passwordConfirm && form.password" class="validation-message success">
              비밀번호가 일치합니다
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 트레이너 정보 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">트레이너 정보</h3>
      <div class="trainer-grid">
        <div class="trainer-basic">
          <div class="info-item">
            <label class="info-label">연락처</label>
            <input v-model="form.phone" placeholder="연락처를 입력하세요" class="info-input" />
          </div>
          <div class="info-item">
            <label class="info-label">인스타그램 URL</label>
            <input v-model="form.instagramUrl" placeholder="인스타그램 URL을 입력하세요" class="info-input" />
          </div>
          <div class="info-item">
            <label class="info-label">주소</label>
            <input v-model="form.address" placeholder="주소를 입력하세요" class="info-input" />
          </div>
          <div class="info-item">
            <label class="info-label">소속 헬스장</label>
            <input v-model="form.gymName" placeholder="소속 헬스장을 입력하세요" class="info-input" />
          </div>
          <div class="info-item">
            <label class="info-label">헬스장 홈페이지</label>
            <input v-model="form.gymWebsite" placeholder="헬스장 홈페이지 URL" class="info-input" />
          </div>
        </div>

        <div class="trainer-cert">
          <div class="info-item">
            <label class="info-label">자격증 번호</label>
            <input v-model="form.certificationNumber" placeholder="자격증 번호를 입력하세요" class="info-input" />
          </div>
          <div class="info-item">
            <label class="info-label">사업자 등록번호</label>
            <input v-model="form.businessNumber" placeholder="사업자 등록번호를 입력하세요" class="info-input" />
          </div>
          <div class="info-item">
            <label class="info-label">자기소개</label>
            <textarea 
              v-model="form.introduction" 
              placeholder="자기소개를 입력하세요" 
              class="info-textarea"
              rows="3"
            ></textarea>
          </div>
          <div class="info-item">
            <label class="info-label">경력 요약</label>
            <textarea 
              v-model="form.career" 
              placeholder="경력 요약을 입력하세요" 
              class="info-textarea"
              rows="3"
            ></textarea>
          </div>
        </div>
      </div>
    </div>

    <!-- 저장 버튼 -->
    <div class="save-section">
      <button @click="submitForm" :disabled="!canSubmit" class="save-button" :class="{ 'disabled': !canSubmit }">
        <span class="save-icon">💾</span>
        <span>정보 저장</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'

const toast = useToast()
const props = defineProps(['userData'])
const router = useRouter()

const user = props.userData.user
const trainer = props.userData.trainerDetails

const form = reactive({
  password: '',
  passwordConfirm: '',
  nickname: user.nickname,
  phone: trainer.phone,
  address: trainer.address,
  gymName: trainer.gymName,
  gymWebsite: trainer.gymWebsite,
  certificationNumber: trainer.certificationNumber,
  businessNumber: trainer.businessNumber,
  introduction: trainer.introduction,
  career: trainer.career,
  instagramUrl: trainer.instagramUrl
})

const profileDeleted = ref(false)
const certDeleted = ref(false)
const profileFile = ref(null)
const certFile = ref(null)

// 닉네임 관련 상태
const originalNickname = user.nickname
const isCheckingNickname = ref(false)
const nicknameChecked = ref(false)
const nicknameError = ref(false)
const nicknameMessage = ref('')

// 비밀번호 관련 상태
const passwordMismatch = ref(false)

// 닉네임 변경 감지
const onNicknameChange = () => {
  nicknameChecked.value = false
  nicknameError.value = false
  nicknameMessage.value = ''
}

// 닉네임 중복 확인 가능 여부
const canCheckNickname = computed(() => {
  return form.nickname && 
         form.nickname.trim() !== '' && 
         form.nickname !== originalNickname && 
         !isCheckingNickname.value
})

// 닉네임 중복 확인
const checkNickname = async () => {
  if (!canCheckNickname.value) return

  isCheckingNickname.value = true
  nicknameError.value = false
  nicknameMessage.value = ''

  try {
    const response = await axios.get('/api/users/check-nickname', {
      params: { nickname: form.nickname },
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })

    if (response.data.success) {
      nicknameChecked.value = true
      nicknameError.value = false
      nicknameMessage.value = response.data.message
      toast.success('사용 가능한 닉네임입니다!')
    }
  } catch (err) {
    nicknameChecked.value = false
    nicknameError.value = true
    nicknameMessage.value = err.response?.data?.message || '닉네임 확인 중 오류가 발생했습니다'
    toast.error(nicknameMessage.value)
  } finally {
    isCheckingNickname.value = false
  }
}

// 비밀번호 변경 감지
const onPasswordChange = () => {
  form.passwordConfirm = ''
  passwordMismatch.value = false
}

// 비밀번호 일치 확인
const checkPasswordMatch = () => {
  if (form.passwordConfirm) {
    passwordMismatch.value = form.password !== form.passwordConfirm
  }
}

// 폼 제출 가능 여부
const canSubmit = computed(() => {
  // 닉네임이 변경되었다면 중복 확인이 완료되어야 함
  const nicknameValid = form.nickname === originalNickname || nicknameChecked.value

  // 비밀번호가 입력되었다면 일치해야 함
  const passwordValid = !form.password || (!passwordMismatch.value && form.passwordConfirm)

  return nicknameValid && passwordValid
})

const getImageUrl = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const deleteProfileImage = () => {
  profileDeleted.value = true
  profileFile.value = null
}

const handleProfileImageUpload = (e) => {
  profileFile.value = e.target.files[0]
}

const deleteCertImage = () => {
  certDeleted.value = true
  certFile.value = null
}

const handleCertUpload = (e) => {
  certFile.value = e.target.files[0]
}

const submitForm = async () => {
  if (!canSubmit.value) {
    toast.error('입력 정보를 확인해주세요')
    return
  }

  const formData = new FormData()

  const commonPayload = {
    password: form.password,
    nickname: form.nickname
  }
  const trainerPayload = {
    phone: form.phone,
    address: form.address,
    gymName: form.gymName,
    gymWebsite: form.gymWebsite,
    certificationNumber: form.certificationNumber,
    businessNumber: form.businessNumber,
    introduction: form.introduction,
    career: form.career,
    instagramUrl: form.instagramUrl
  }

  formData.append('common', JSON.stringify(commonPayload))
  formData.append('trainer', JSON.stringify(trainerPayload))
  if (profileDeleted.value && profileFile.value) formData.append('file', profileFile.value)
  if (certDeleted.value && certFile.value) formData.append('certFile', certFile.value)

  try {
    await axios.put('/api/users/trainer', formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    toast.success('트레이너 정보가 성공적으로 수정되었습니다!')
    router.replace('/profile')
  } catch (err) {
    console.error('수정 실패', err)
    toast.error('수정 중 오류가 발생했습니다: ' + (err?.response?.data?.message || '서버 오류'))
  }
}
</script>

<style scoped>
/* 기존 스타일 유지 */
.info-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f59e0b;
}

/* 기본 정보 컨테이너 - 레이아웃 고정 */
.basic-info-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.full-width {
  width: 100%;
}

.password-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  align-items: start;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.trainer-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.trainer-basic,
.trainer-cert {
  padding: 24px;
  background: rgba(249, 250, 251, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(229, 231, 235, 0.3);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.info-input,
.info-textarea {
  font-size: 16px;
  font-weight: 500;
  color: #111827;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  transition: all 0.2s ease;
  line-height: 1.5;
}

.info-input:focus,
.info-textarea:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.info-input.error {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.info-input.success {
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.info-textarea {
  resize: vertical;
  min-height: 100px;
}

/* 닉네임 컨테이너 */
.nickname-container {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.nickname-container .info-input {
  flex: 1;
}

.check-button {
  background: #3b82f6;
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  min-width: 80px;
}

.check-button:hover:not(:disabled) {
  background: #2563eb;
}

.check-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.check-button.checking {
  background: #6b7280;
}

/* 검증 메시지 */
.validation-message {
  font-size: 12px;
  font-weight: 500;
  margin-top: 4px;
}

.validation-message.error {
  color: #ef4444;
}

.validation-message.success {
  color: #10b981;
}

/* 프로필 이미지 */
.profile-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.profile-edit-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #f3f4f6;
}

/* 자격증 이미지 */
.cert-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.current-cert {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.cert-image-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
}

.cert-edit-image {
  max-width: 300px;
  width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: block;
}

.delete-button {
  background: #ef4444;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  align-self: center;
}

.delete-button:hover {
  background: #dc2626;
  transform: translateY(-1px);
}

.file-input {
  padding: 12px;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  background: #f9fafb;
  width: 100%;
  cursor: pointer;
}

.upload-notice {
  font-size: 14px;
  color: #6b7280;
  text-align: center;
}

/* 저장 버튼 */
.save-section {
  text-align: center;
}

.save-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #f59e0b;
  color: white;
  padding: 16px 32px;
  border-radius: 12px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.save-button:hover:not(.disabled) {
  background: #d97706;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

.save-button.disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.save-icon {
  font-size: 18px;
}

/* 반응형 */
@media (max-width: 1280px) {
  .trainer-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .info-section {
    padding: 20px;
  }
  
  .trainer-basic,
  .trainer-cert {
    padding: 16px;
  }
  
  .password-section {
    grid-template-columns: 1fr;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .nickname-container {
    flex-direction: column;
  }
  
  .check-button {
    width: 100%;
  }
}
</style>