<template>
  <div
    class="min-h-screen bg-gradient-to-br from-orange-50 via-yellow-50 to-amber-50 flex items-center justify-center p-4">
    <!-- 메인 컨테이너 -->
    <div class="w-full max-w-lg">
      <!-- 로고 및 제목 -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center gap-3 mb-4">
          <div
            class="w-12 h-12 bg-gradient-to-br from-yellow-400 to-orange-400 rounded-xl flex items-center justify-center shadow-lg">
            <span class="text-white text-2xl">🍽️</span>
          </div>
          <h1 @click="goHome"
            class="text-3xl font-bold bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent cursor-pointer hover:from-orange-700 hover:to-amber-700 transition-all duration-300">
            EatNote
          </h1>
        </div>
        <p class="text-gray-600 text-lg">새로운 시작을 위한 첫 걸음</p>
      </div>

      <!-- 프로그레스 바 -->
      <div class="mb-8">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-medium text-gray-600">진행 상황</span>
          <span class="text-sm font-medium text-gray-600">{{ step }}/3</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2">
          <div
            class="bg-gradient-to-r from-yellow-400 to-orange-400 h-2 rounded-full transition-all duration-500 ease-out"
            :style="`width: ${(step / 3) * 100}%`"></div>
        </div>
      </div>

      <!-- 메인 카드 -->
      <div class="bg-white/80 backdrop-blur-sm rounded-3xl shadow-xl border border-white/50 overflow-hidden">

        <!-- Step 1: 회원 유형 선택 -->
        <div v-if="step === 1" class="p-0 overflow-hidden">
          <div class="text-center mb-8 p-8 pb-4">
            <h2 class="text-2xl font-bold text-gray-800 mb-2">어떤 회원으로 가입하시나요?</h2>
            <p class="text-gray-600">목적에 맞는 서비스를 제공해드릴게요</p>
          </div>

          <!-- 좌우 분할 선택 영역 -->
          <div class="flex h-[22rem]">
            <!-- 트레이너 선택 -->
            <div @click="selectUserType(1)" class="user-type-split trainer-side group"
              :class="{ 'selected': selectedUserType === 1 }">
              <!-- 배경 이미지 -->
              <div class="split-background trainer-bg"></div>

              <!-- 오버레이 -->
              <div class="split-overlay"></div>

              <!-- 컨텐츠 -->
              <div class="split-content">
                <div class="split-icon">💪</div>
                <h3 class="split-title">트레이너</h3>
                <div class="split-description">
                  <div class="description-item">- 효율적인 회원의 식단 관리</div>
                  <div class="description-item">- 전문 피드백 제공</div>
                  <div class="description-item">- 트레이너간 커뮤니티 이용</div>
                </div>
                <div class="split-tags">
                  <span class="tag trainer-tag">전문가</span>
                  <span class="tag trainer-tag">피드백 제공</span>
                </div>
              </div>

              <!-- 선택 표시 -->
              <div class="selection-indicator trainer-indicator">
                <div class="check-circle"></div>
              </div>
            </div>

            <!-- 구분선 -->
            <div class="divider"></div>

            <!-- 일반 회원 선택 -->
            <div @click="selectUserType(2)" class="user-type-split member-side group"
              :class="{ 'selected': selectedUserType === 2 }">
              <!-- 배경 이미지 -->
              <div class="split-background member-bg"></div>

              <!-- 오버레이 -->
              <div class="split-overlay"></div>

              <!-- 컨텐츠 -->
              <div class="split-content">
                <div class="split-icon">🙋‍♂️</div>
                <h3 class="split-title">회원</h3>
                <div class="split-description">
                  <div class="description-item">- 손쉬운 식단 관리</div>
                  <div class="description-item">- AI 분석을 통해 건강 관리</div>
                  <div class="description-item">- 커뮤니티 이용</div>
                </div>
                <div class="split-tags">
                  <span class="tag member-tag">식단 관리</span>
                  <span class="tag member-tag">AI 분석</span>
                </div>
              </div>

              <!-- 선택 표시 -->
              <div class="selection-indicator member-indicator">
                <div class="check-circle"></div>
              </div>
            </div>
          </div>

          <!-- 홈으로 돌아가기 -->
          <div class="text-center p-6">
            <button @click="goHome"
              class="text-sm text-gray-500 hover:text-gray-700 transition-colors duration-300 flex items-center gap-2 mx-auto">
              🏠 홈으로 돌아가기
            </button>
          </div>
        </div>

        <!-- Step 2: 기본 정보 입력 -->
        <div v-else-if="step === 2" class="relative">

          <div class="p-8">
            <div class="text-center mb-8">
              <h2 class="text-2xl font-bold text-gray-800 mb-3">기본 정보를 입력해주세요</h2>
              <p class="text-gray-600">안전하고 개인화된 서비스를 위해 필요해요</p>
            </div>

            <div class="space-y-5">
              <!-- 이메일 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">📧</span>
                  이메일
                </label>
                <input v-model="form.email" type="email" class="form-input" :class="{ 'error': errors.email }"
                  placeholder="your@email.com" />
                <p v-if="errors.email" class="error-message">이메일은 필수 입력 항목입니다.</p>
              </div>

              <!-- 비밀번호 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">🔒</span>
                  비밀번호
                </label>
                <input v-model="form.password" type="password" class="form-input" :class="{ 'error': errors.password }"
                  placeholder="안전한 비밀번호를 입력하세요" />
                <p v-if="errors.password" class="error-message">비밀번호는 필수 입력 항목입니다.</p>
              </div>

              <!-- 비밀번호 확인 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">🔐</span>
                  비밀번호 확인
                </label>
                <input v-model="form.passwordConfirm" type="password" class="form-input"
                  :class="{ 'error': errors.passwordConfirm || passwordMismatch }" placeholder="비밀번호를 다시 입력하세요" />
                <p v-if="errors.passwordConfirm" class="error-message">비밀번호 확인은 필수입니다.</p>
                <p v-if="passwordMismatch" class="error-message">비밀번호가 일치하지 않습니다.</p>
              </div>

              <!-- 이름 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">👤</span>
                  이름
                </label>
                <input v-model="form.name" type="text" class="form-input" :class="{ 'error': errors.name }"
                  placeholder="실명을 입력해주세요" />
                <p v-if="errors.name" class="error-message">이름은 필수 입력 항목입니다.</p>
              </div>

              <!-- 닉네임 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">✨</span>
                  닉네임
                </label>
                <div class="flex gap-2">
                  <input v-model="form.nickname" type="text" class="form-input flex-1"
                    :class="{ 'error': errors.nickname }" placeholder="닉네임" />
                  <button type="button" @click="checkNickname" class="nickname-check-btn">
                    확인
                  </button>
                </div>
                <p v-if="errors.nickname" class="error-message">닉네임은 필수 입력 항목입니다.</p>
                <p v-if="nicknameMessage" :class="nicknameAvailable ? 'success-message' : 'error-message'">
                  {{ nicknameMessage }}
                </p>
              </div>

              <!-- 성별 & 나이 -->
              <div class="grid grid-cols-2 gap-4">
                <div class="form-group">
                  <label class="form-label">
                    <span class="label-icon">⚧️</span>
                    성별
                  </label>
                  <select v-model="form.gender" class="form-input">
                    <option value="M">남성</option>
                    <option value="F">여성</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">
                    <span class="label-icon">🎂</span>
                    나이
                  </label>
                  <input v-model="form.age" type="number" class="form-input" :class="{ 'error': errors.age }"
                    placeholder="나이" />
                  <p v-if="errors.age" class="error-message">나이는 필수 입력 항목입니다.</p>
                </div>
              </div>

              <!-- 프로필 사진 -->
              <div class="form-group">
                <label class="form-label">
                  <span class="label-icon">📸</span>
                  프로필 사진 (선택사항)
                </label>
                <div class="file-upload-area" @click="$refs.fileInput.click()">
                  <div v-if="!profileImageFile" class="file-upload-placeholder">
                    <div class="text-3xl mb-2">📷</div>
                    <p class="text-gray-600 font-medium">클릭해서 사진을 선택하세요</p>
                    <p class="text-xs text-gray-500 mt-1">JPG, PNG 파일 지원</p>
                  </div>
                  <div v-else class="file-upload-selected">
                    <div class="text-3xl mb-2">✅</div>
                    <p class="text-green-700 font-medium">{{ profileImageFile.name }}</p>
                    <p class="text-xs text-gray-500 mt-1">다른 파일을 선택하려면 클릭하세요</p>
                  </div>
                </div>
                <input ref="fileInput" type="file" @change="handleFileChange" class="hidden" accept="image/*" />
              </div>
            </div>
          </div>

          <!-- 네비게이션 버튼들 -->
          <div class="flex justify-between items-center mt-8 px-8 pb-6">
            <button @click="step = 1" class="nav-btn secondary flex items-center gap-2">
              ← 뒤로가기
            </button>

            <button @click="goToNextStep" class="nav-btn primary flex items-center gap-2">
              다음 단계 →
            </button>
          </div>
        </div>

        <!-- Step 3: 상세 정보 입력 -->
        <div v-else-if="step === 3" class="p-8">
          <div class="text-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800 mb-2">마지막 단계예요!</h2>
            <p class="text-gray-600">{{ userType === 1 ? '트레이너' : '회원' }} 전용 정보를 입력해주세요</p>
          </div>

          <component :is="detailComponent" v-model="formDetail" @goBack="step = 2"
            @validation-change="handleValidationChange" />

          <!-- 네비게이션 버튼 -->
          <div class="flex justify-between mt-8">
            <button @click="step = 2" class="nav-btn secondary">
              ← 뒤로가기
            </button>
            <button @click="submitForm" class="nav-btn success" :disabled="isSubmitting || !formDetailValid">
              <span v-if="isSubmitting">가입 중...</span>
              <span v-else-if="!formDetailValid">필수 항목을 입력하세요</span>
              <span v-else>🎉 회원가입 완료</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 하단 링크 -->
      <div class="text-center mt-6">
        <p class="text-gray-600">
          이미 계정이 있으신가요?
          <router-link to="/login"
            class="text-orange-600 hover:text-orange-700 font-medium transition-colors duration-300">
            로그인하기
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ref, computed } from 'vue'
import TrainerDetailForm from '@/components/register/TrainerDetailForm.vue'
import MemberDetailForm from '@/components/register/MemberDetailForm.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()
const router = useRouter()

const step = ref(1)
const userType = ref(null)
const selectedUserType = ref(null)
const profileImageFile = ref(null)
const isSubmitting = ref(false)
const formDetail = ref({})

const form = ref({
  email: '',
  password: '',
  passwordConfirm: '',
  name: '',
  nickname: '',
  gender: 'M',
  userType: null,
  age: 0,
})

const passwordMismatch = computed(() =>
  form.value.password && form.value.passwordConfirm && form.value.password !== form.value.passwordConfirm
)

const formDetailValid = ref(false)

const handleValidationChange = (valid) => {
  formDetailValid.value = valid
}

const errors = ref({
  email: false,
  password: false,
  passwordConfirm: false,
  name: false,
  nickname: false,
  age: false,
})

const nicknameAvailable = ref(null)
const nicknameMessage = ref('')

const goHome = () => {
  router.push('/')
}

const handleFileChange = (event) => {
  profileImageFile.value = event.target.files[0]
}

const selectUserType = (type) => {
  selectedUserType.value = type
  setTimeout(() => {
    userType.value = type
    form.value.userType = type
    step.value = 2
  }, 300)
}

const goToNextStep = () => {
  // 비밀번호 불일치 체크
  if (passwordMismatch.value) {
    toast.warning('비밀번호가 일치하지 않습니다.')
    return
  }

  // 필수 항목 체크
  errors.value.email = !form.value.email
  errors.value.password = !form.value.password
  errors.value.passwordConfirm = !form.value.passwordConfirm
  errors.value.name = !form.value.name
  errors.value.nickname = !form.value.nickname
  errors.value.age = !form.value.age || form.value.age <= 0

  if (Object.values(errors.value).some(e => e)) {
    toast.error('필수 항목을 모두 입력해주세요.')
    return
  }

  // 닉네임 중복 확인 체크
  if (!form.value.nickname) {
    toast.error('닉네임을 입력해주세요.')
    return
  }

  if (nicknameAvailable.value !== true) {
    toast.warning('닉네임 중복 확인을 해주세요.')
    return
  }

  // 유저 타입 확인
  if (!userType.value) {
    toast.error('회원 유형을 선택해주세요.')
    return
  }

  // 다음 단계 데이터 초기화
  if (userType.value === 1) {
    formDetail.value = {
      phone: '', address: '', gymName: '', gymWebsite: '', certificationNumber: '',
      businessNumber: '', introduction: '', career: '', instagramUrl: ''
    }
  } else {
    formDetail.value = {
      height: 0, weight: 0, bodyWater: 0, protein: 0, mineral: 0, bodyFat: 0,
      skeletalMuscle: 0, bodyFatMass: 0, bmi: 0, bodyFatPercentage: 0,
      goal: '', trainerNickname: '', allergyIds: []
    }
  }

  console.log('Moving to step 3, userType:', userType.value) // 디버깅용
  step.value = 3
}

const detailComponent = computed(() =>
  userType.value === 1 ? TrainerDetailForm : MemberDetailForm
)

const submitForm = async () => {
  if (isSubmitting.value || !formDetailValid.value) {
    if (!formDetailValid.value) {
      toast.error('필수 항목을 모두 입력해주세요.')
    }
    return
  }

  isSubmitting.value = true

  try {
    const userJson = {
      ...form.value,
      ...(userType.value === 1
        ? { trainerDetails: formDetail.value }
        : { memberDetails: formDetail.value }),
    }
    delete userJson.passwordConfirm

    const formData = new FormData()
    formData.append('user', JSON.stringify(userJson))
    if (profileImageFile.value) {
      formData.append('file', profileImageFile.value)
    }

    const response = await axios.post('/api/users/register', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    toast.success('🎉 회원가입이 완료되었습니다! 🎉')
    console.log('서버 응답:', response.data)
    router.push('/login')

  } catch (error) {
    console.error('회원가입 실패:', error)
    toast.error('회원가입 중 오류가 발생했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

const checkNickname = async () => {
  if (!form.value.nickname) {
    nicknameMessage.value = '닉네임을 입력해주세요.'
    nicknameAvailable.value = false
    return
  }

  try {
    const response = await axios.get(`/api/users/check-nickname?nickname=${form.value.nickname}`)
    console.log(response)
    nicknameAvailable.value = true
    nicknameMessage.value = '사용 가능한 닉네임입니다.'
  } catch (err) {
    nicknameAvailable.value = false
    nicknameMessage.value = '이미 사용 중인 닉네임입니다.'
  }
}

import { onMounted } from 'vue'

onMounted(() => {
  if (step.value === 1) {
    selectedUserType.value = null
    userType.value = null
  }
})

</script>

<style scoped>
/* 좌우 분할 선택 스타일 */
.user-type-split {
  flex: 1;
  position: relative;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 320px;
  border-radius: 0;
}

.user-type-split:hover {
  flex-grow: 1.02;
}

.user-type-split.selected {
  flex-grow: 1.05;
}

/* 배경 이미지 */
.split-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  transition: all 0.4s ease;
  opacity: 0.15;
}

.trainer-bg {
  background: linear-gradient(135deg,
      rgba(59, 130, 246, 0.4) 0%,
      rgba(99, 102, 241, 0.6) 50%,
      rgba(139, 69, 197, 0.4) 100%);
}

.member-bg {
  background: linear-gradient(135deg,
      rgba(34, 197, 94, 0.4) 0%,
      rgba(16, 185, 129, 0.6) 50%,
      rgba(5, 150, 105, 0.4) 100%);
}

.user-type-split:hover .split-background {
  opacity: 0.25;
  transform: scale(1.02);
}

.user-type-split.selected .split-background {
  opacity: 0.35;
  transform: scale(1.05);
}

/* 오버레이 */
.split-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg,
      rgba(255, 255, 255, 0.95) 0%,
      rgba(255, 255, 255, 0.85) 40%,
      rgba(255, 255, 255, 0.75) 100%);
  backdrop-filter: blur(1px);
  transition: all 0.4s ease;
}

.user-type-split:hover .split-overlay {
  background: linear-gradient(180deg,
      rgba(255, 255, 255, 0.9) 0%,
      rgba(255, 255, 255, 0.75) 40%,
      rgba(255, 255, 255, 0.65) 100%);
  backdrop-filter: blur(0.5px);
}

.user-type-split.selected .split-overlay {
  background: linear-gradient(180deg,
      rgba(255, 255, 255, 0.85) 0%,
      rgba(255, 255, 255, 0.65) 40%,
      rgba(255, 255, 255, 0.5) 100%);
  backdrop-filter: blur(0px);
}

/* 컨텐츠 */
.split-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 2.5rem 1.5rem;
  z-index: 10;
  transition: all 0.4s ease;
}

.split-icon {
  font-size: 3.5rem;
  margin-bottom: 1.2rem;
  transition: all 0.4s ease;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.trainer-side .split-icon {
  color: #3b82f6;
}

.member-side .split-icon {
  color: #22c55e;
}

.user-type-split:hover .split-icon {
  font-size: 4rem;
  transform: translateY(-4px);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
}

.user-type-split.selected .split-icon {
  font-size: 4.5rem;
  transform: translateY(-6px);
  filter: drop-shadow(0 6px 12px rgba(0, 0, 0, 0.2));
}

.split-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 0.8rem;
  transition: all 0.4s ease;
  color: #374151;
}

.trainer-side:hover .split-title,
.trainer-side.selected .split-title {
  color: #1e40af;
}

.member-side:hover .split-title,
.member-side.selected .split-title {
  color: #059669;
}

.user-type-split:hover .split-title {
  font-size: 2rem;
  transform: translateY(-2px);
}

.user-type-split.selected .split-title {
  font-size: 2.2rem;
  transform: translateY(-4px);
}

.split-description {
  margin-bottom: 1.2rem;
  transition: all 0.4s ease;
  max-width: 220px;
}

.description-item {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 0.4rem;
  text-align: left;
  line-height: 1.4;
  transition: all 0.4s ease;
}

.user-type-split:hover .description-item {
  font-size: 0.9rem;
  color: #4b5563;
  transform: translateY(-1px);
}

.user-type-split.selected .description-item {
  font-size: 0.95rem;
  color: #374151;
  transform: translateY(-2px);
}

/* 태그 */
.split-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  justify-content: center;
}

.tag {
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.trainer-tag {
  background-color: rgba(59, 130, 246, 0.15);
  color: #1e40af;
  border-color: rgba(59, 130, 246, 0.3);
}

.member-tag {
  background-color: rgba(34, 197, 94, 0.15);
  color: #059669;
  border-color: rgba(34, 197, 94, 0.3);
}

.trainer-side:hover .trainer-tag,
.trainer-side.selected .trainer-tag {
  background-color: rgba(59, 130, 246, 0.25);
  border-color: rgba(59, 130, 246, 0.5);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
}

.member-side:hover .member-tag,
.member-side.selected .member-tag {
  background-color: rgba(34, 197, 94, 0.25);
  border-color: rgba(34, 197, 94, 0.5);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.2);
}

/* 구분선 */
.divider {
  width: 1px;
  background: linear-gradient(to bottom,
      transparent 20%,
      rgba(156, 163, 175, 0.4) 50%,
      transparent 80%);
  z-index: 5;
}

/* 선택 표시 */
.selection-indicator {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  z-index: 20;
  transition: all 0.3s ease;
}

.check-circle {
  width: 2.2rem;
  height: 2.2rem;
  border-radius: 50%;
  border: 2px solid rgba(156, 163, 175, 0.4);
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trainer-side:hover .check-circle {
  border-color: rgba(59, 130, 246, 0.6);
  background-color: rgba(59, 130, 246, 0.1);
  transform: scale(1.1);
}

.member-side:hover .check-circle {
  border-color: rgba(34, 197, 94, 0.6);
  background-color: rgba(34, 197, 94, 0.1);
  transform: scale(1.1);
}

.trainer-side.selected .check-circle {
  background-color: #3b82f6;
  border-color: #3b82f6;
  transform: scale(1.15);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.member-side.selected .check-circle {
  background-color: #22c55e;
  border-color: #22c55e;
  transform: scale(1.15);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.user-type-split.selected .check-circle::after {
  content: "✓";
  color: white;
  font-size: 1rem;
  font-weight: bold;
}

/* 반응형 */
@media (max-width: 768px) {
  .user-type-split {
    min-height: 280px;
  }

  .split-content {
    padding: 1.5rem;
  }

  .split-icon {
    font-size: 2.8rem;
    margin-bottom: 1rem;
  }

  .user-type-split:hover .split-icon {
    font-size: 3.2rem;
  }

  .split-title {
    font-size: 1.5rem;
    margin-bottom: 0.6rem;
  }

  .user-type-split:hover .split-title {
    font-size: 1.7rem;
  }

  .split-description {
    margin-bottom: 1rem;
    max-width: 180px;
  }

  .description-item {
    font-size: 0.8rem;
    margin-bottom: 0.3rem;
  }

  .user-type-split:hover .description-item {
    font-size: 0.85rem;
  }

  .tag {
    padding: 0.3rem 0.6rem;
    font-size: 0.75rem;
  }
}

/* 삼각형 네비게이션 버튼 제거 - 일반 버튼으로 교체됨 */

/* 입력 카드 스타일 */
.input-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.input-card:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(245, 158, 11, 0.3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.input-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.75rem;
}

.label-icon {
  font-size: 1.1rem;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.75rem;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem;
  background-color: rgba(255, 255, 255, 0.9);
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  outline: none;
}

.form-input:focus {
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
  background-color: #ffffff;
}

.form-input.error {
  border-color: #ef4444;
  background-color: rgba(254, 242, 242, 0.9);
}

.form-input.error:focus {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

/* 에러 및 성공 메시지 */
.error-message {
  color: #dc2626;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.error-message::before {
  content: "⚠️";
  font-size: 0.75rem;
}

.success-message {
  color: #059669;
  font-size: 0.85rem;
  margin-top: 0.5rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.success-message::before {
  content: "✅";
  font-size: 0.75rem;
}

/* 중복확인 버튼 */
.nickname-check-btn {
  padding: 0.875rem 1rem;
  background-color: #f3f4f6;
  border: 2px solid #e5e7eb;
  color: #374151;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 70px;
}

.nickname-check-btn:hover {
  background-color: #e5e7eb;
  border-color: #d1d5db;
  transform: translateY(-1px);
}

/* 파일 업로드 영역 */
.file-upload-area {
  width: 100%;
  padding: 1.5rem;
  background-color: rgba(255, 255, 255, 0.8);
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s ease;
}

.file-upload-area:hover {
  border-color: #f59e0b;
  background-color: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
}

.file-upload-placeholder,
.file-upload-selected {
  transition: all 0.3s ease;
}

/* 반응형 - 삼각형 버튼 관련 코드 제거됨 */

/* 네비게이션 버튼 */
.nav-btn {
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  border-radius: 0.75rem;
  transition: all 0.3s ease;
  transform: scale(1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.nav-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.nav-btn.secondary {
  background-color: #f3f4f6;
  color: #374151;
}

.nav-btn.secondary:hover {
  background-color: #e5e7eb;
}

.nav-btn.primary {
  background: linear-gradient(to right, #f59e0b, #f6a832);
  color: white;
}

.nav-btn.primary:hover {
  background: linear-gradient(to right, #d97706, #dc7726);
}

.nav-btn.success {
  background: linear-gradient(to right, #10b981, #34d399);
  color: white;
}

.nav-btn.success:hover {
  background: linear-gradient(to right, #059669, #10b981);
}

.nav-btn.success:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: scale(1);
}

.nav-btn.success:disabled:hover {
  transform: scale(1);
}

/* 그라데이션 텍스트 */
.bg-clip-text {
  background-clip: text;
  -webkit-background-clip: text;
}

/* 애니메이션 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-group {
  animation: slideInUp 0.4s ease-out;
}

.form-group:nth-child(odd) {
  animation-delay: 0.1s;
}

.form-group:nth-child(even) {
  animation-delay: 0.2s;
}

/* 반응형 */
@media (max-width: 640px) {
  .user-type-card {
    padding: 1rem;
  }

  .user-type-card .w-16 {
    width: 3rem;
    height: 3rem;
  }

  .form-input {
    font-size: 1rem;
  }
}
</style>