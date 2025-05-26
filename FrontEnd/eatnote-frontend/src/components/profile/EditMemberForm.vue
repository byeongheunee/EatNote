<template>
  <div>
    <!-- 프로필 사진 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">프로필 사진</h3>
      <div class="profile-image-container">
        <div v-if="!profileDeleted" class="current-image">
          <img :src="previewImageUrl" alt="프로필 이미지" class="profile-edit-image" />
          <button @click="deleteProfileImage" class="delete-button">
            🗑 삭제하기
          </button>
        </div>
        <div v-else class="upload-new">
          <input type="file" accept="image/*" @change="handleImageUpload" class="file-input" />
          <p class="upload-notice">새 프로필 사진을 선택하세요</p>
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

    <!-- 신체 정보 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">신체 정보</h3>
      <div class="info-grid">
        <div class="info-item">
          <label class="info-label">키</label>
          <input v-model.number="form.height" placeholder="키(cm)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">몸무게</label>
          <input v-model.number="form.weight" placeholder="몸무게(kg)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">BMI</label>
          <input v-model.number="form.bmi" placeholder="BMI" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">체수분</label>
          <input v-model.number="form.bodyWater" placeholder="체수분(%)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">단백질</label>
          <input v-model.number="form.protein" placeholder="단백질(%)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">무기질</label>
          <input v-model.number="form.mineral" placeholder="무기질(%)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">체지방</label>
          <input v-model.number="form.bodyFat" placeholder="체지방(kg)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">골격근</label>
          <input v-model.number="form.skeletalMuscle" placeholder="골격근(kg)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">체지방량</label>
          <input v-model.number="form.bodyFatMass" placeholder="체지방량(kg)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">체지방률</label>
          <input v-model.number="form.bodyFatPercentage" placeholder="체지방률(%)" class="info-input" />
        </div>
        <div class="info-item">
          <label class="info-label">목표</label>
          <select v-model="form.goal" class="info-select">
            <option disabled value="">목표를 선택하세요</option>
            <option v-for="g in goalList" :key="g.code" :value="g.code">
              {{ g.label }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- 알레르기 정보 섹션 -->
    <div class="info-section mb-6">
      <h3 class="section-title">알레르기 정보</h3>
      
      <!-- 카테고리 선택 -->
      <div class="allergy-selector mb-4">
        <label class="info-label">카테고리 선택</label>
        <select v-model="selectedCategory" class="info-select">
          <option disabled value="">카테고리를 선택하세요</option>
          <option v-for="(list, category) in allergyMap" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>

      <!-- 알레르기 체크박스 -->
      <div v-if="selectedCategory" class="allergy-checkboxes mb-4">
        <h4 class="subsection-title">{{ selectedCategory }} 알레르기 목록</h4>
        <div class="checkbox-grid">
          <label v-for="a in allergyMap[selectedCategory]" :key="a.allergyId" class="checkbox-item">
            <input
              type="checkbox"
              :value="a.allergyId"
              v-model="form.allergyIds"
              class="checkbox-input"
            />
            <span class="checkbox-label">{{ a.name }}</span>
          </label>
        </div>
      </div>

      <!-- 선택된 알레르기 표시 -->
      <div class="selected-allergies">
        <h4 class="subsection-title">선택된 알레르기</h4>
        <div v-if="selectedAllergyNames.length" class="allergy-tags">
          <span
            v-for="(name, index) in selectedAllergyNames"
            :key="index"
            class="allergy-tag"
          >
            {{ name }}
          </span>
        </div>
        <div v-else class="no-allergy">
          <span class="no-allergy-text">선택된 알레르기가 없습니다</span>
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
import { ref, reactive, watchEffect, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

const toast = useToast()
const router = useRouter()

const props = defineProps(['userData'])

const form = reactive({
  password: '',
  passwordConfirm: '',
  nickname: '',
  height: 0,
  weight: 0,
  bodyWater: 0,
  protein: 0,
  mineral: 0,
  bodyFat: 0,
  skeletalMuscle: 0,
  bodyFatMass: 0,
  bmi: 0,
  bodyFatPercentage: 0,
  goal: '',
  allergyIds: []
})

// 알레르기 관련
const allergyMap = ref({})
const selectedCategory = ref('')

// 목표 관련
const goalList = ref([])

// 프로필 관련
const profileDeleted = ref(false)
const file = ref(null)
const previewImageUrl = ref(getImageUrl(props.userData.user?.profileImage))

// 닉네임 관련 상태
const originalNickname = props.userData.user?.nickname || ''
const isCheckingNickname = ref(false)
const nicknameChecked = ref(false)
const nicknameError = ref(false)
const nicknameMessage = ref('')

// 비밀번호 관련 상태
const passwordMismatch = ref(false)

function getImageUrl(path) {
  return path ? `http://localhost:8080${path}` : '/images/default-profile.png'
}

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

// 선택된 알레르기 이름들
const selectedAllergyNames = computed(() => {
  const selected = []
  for (const category in allergyMap.value) {
    allergyMap.value[category].forEach((a) => {
      if (form.allergyIds.includes(a.allergyId)) {
        selected.push(a.name)
      }
    })
  }
  return selected
})

// 사용자 데이터 초기 세팅
watchEffect(() => {
  if (props.userData.user && props.userData.memberDetails) {
    const u = props.userData.user
    const m = props.userData.memberDetails

    form.nickname = u.nickname
    form.height = m.height
    form.weight = m.weight
    form.bodyWater = m.bodyWater
    form.protein = m.protein
    form.mineral = m.mineral
    form.bodyFat = m.bodyFat
    form.skeletalMuscle = m.skeletalMuscle
    form.bodyFatMass = m.bodyFatMass
    form.bmi = m.bmi
    form.bodyFatPercentage = m.bodyFatPercentage
    form.goal = m.goal
    form.allergyIds = props.userData.allergies?.map(a => a.allergyId) || []
  }
})

// 알레르기 목록 불러오기
const loadAllergies = async () => {
  try {
    const res = await axios.get('/api/allergies/grouped')
    allergyMap.value = res.data.data
  } catch (err) {
    console.error('알레르기 목록 조회 실패:', err)
  }
}

// 목표 목록 불러오기
const loadGoalList = async () => {
  try {
    const res = await axios.get('/api/common/goal')
    goalList.value = res.data.data
  } catch (err) {
    console.error('목표 목록 조회 실패:', err)
  }
}

// 프로필 사진 삭제
const deleteProfileImage = () => {
  profileDeleted.value = true
  file.value = null
}

// 프로필 사진 업로드
const handleImageUpload = (e) => {
  file.value = e.target.files[0]
}

// 폼 제출
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

  const memberPayload = {
    height: form.height,
    weight: form.weight,
    bodyWater: form.bodyWater,
    protein: form.protein,
    mineral: form.mineral,
    bodyFat: form.bodyFat,
    skeletalMuscle: form.skeletalMuscle,
    bodyFatMass: form.bodyFatMass,
    bmi: form.bmi,
    bodyFatPercentage: form.bodyFatPercentage,
    goal: form.goal,
    allergyIds: form.allergyIds
  }

  formData.append('common', JSON.stringify(commonPayload))
  formData.append('member', JSON.stringify(memberPayload))

  if (profileDeleted.value && file.value) {
    formData.append('file', file.value)
  }

  try {
    const res = await axios.put('/api/users/member', formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    toast.success('회원 정보가 성공적으로 수정되었습니다!')
    router.replace('/profile')
  } catch (err) {
    console.error('수정 실패', err)
    toast.error('수정 중 오류가 발생했습니다: ' + (err?.response?.data?.message || '서버 오류'))
  }
}

// 컴포넌트 마운트 시 초기 데이터 불러오기
onMounted(() => {
  loadAllergies()
  loadGoalList()
})
</script>

<style scoped>
/* 정보 섹션 */
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

.subsection-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
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

/* 정보 그리드 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.info-input,
.info-select {
  font-size: 16px;
  font-weight: 500;
  color: #111827;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  transition: all 0.2s ease;
}

.info-input:focus,
.info-select:focus {
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

/* 알레르기 관련 */
.allergy-selector {
  margin-bottom: 24px;
}

.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  padding: 20px;
  background: rgba(249, 250, 251, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(229, 231, 235, 0.3);
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background 0.2s ease;
}

.checkbox-item:hover {
  background: rgba(245, 158, 11, 0.1);
}

.checkbox-input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.checkbox-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

/* 선택된 알레르기 */
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
@media (max-width: 768px) {
  .password-section {
    grid-template-columns: 1fr;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .checkbox-grid {
    grid-template-columns: 1fr;
  }
  
  .info-section {
    padding: 20px;
  }
  
  .nickname-container {
    flex-direction: column;
  }
  
  .check-button {
    width: 100%;
  }
}
</style>