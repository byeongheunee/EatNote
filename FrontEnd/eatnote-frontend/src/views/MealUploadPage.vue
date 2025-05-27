<template>
  <div class="meal-upload-page">
    <Header />

    <!-- 메인 컨테이너 -->
    <div class="main-container">
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="main-title">식단 업로드</h1>
          <p class="main-subtitle">{{ suggestedMessage }}</p>
          <div class="time-badge">
            <div class="status-dot"></div>
            <span>현재 시간 기준: {{ mealTypeLabel }} 식사</span>
          </div>
        </div>
      </div>

      <!-- 메인 업로드 카드 -->
      <div class="upload-card">
        <!-- 이미지 미리보기 섹션 -->
        <div v-if="imagePreviewUrl" class="image-preview-section">
          <div class="image-container">
            <img :src="imagePreviewUrl" alt="미리보기" class="preview-image" />
          </div>
          <button @click="clearImage" class="remove-button">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- 업로드 폼 -->
        <div class="form-content">
          <form v-if="!uploadComplete" @submit.prevent="handleUpload" class="upload-form">

            <!-- 시간 설정 모드 선택 -->
            <div class="form-section">
              <label class="section-label">
                <span class="label-icon">🕓</span>
                식사 시간 설정
              </label>

              <!-- 모드 선택 토글 -->
              <div class="time-mode-selector">
                <label class="mode-option">
                  <input type="radio" :value="false" v-model="useCustomTime" class="mode-radio" />
                  <div class="mode-indicator" :class="{ active: !useCustomTime }"></div>
                  <span class="mode-text">🔄 현재 시간으로 자동 등록</span>
                </label>

                <label class="mode-option">
                  <input type="radio" :value="true" v-model="useCustomTime" class="mode-radio" />
                  <div class="mode-indicator" :class="{ active: useCustomTime }"></div>
                  <span class="mode-text">✏️ 시간 직접 입력</span>
                </label>
              </div>

              <!-- 직접 시간 입력 -->
              <div v-if="useCustomTime" class="custom-time-section">
                <div class="datetime-input-wrapper">
                  <input 
                    type="datetime-local" 
                    v-model="customMealTime" 
                    :max="maxDateTime" 
                    required
                    class="datetime-input"
                  />
                  <div class="input-icon">📅</div>
                </div>
                <p class="input-hint">💡 원하는 날짜와 시간을 선택하세요</p>
              </div>

              <!-- 현재 시간 정보 -->
              <div v-else class="current-time-info">
                <div class="time-info-content">
                  <span class="time-icon">⏰</span>
                  <span><strong>{{ currentTimeDisplay }}</strong>로 등록됩니다</span>
                </div>
              </div>
            </div>

            <!-- 파일 업로드 영역 -->
            <div class="form-section">
              <label class="section-label">
                <span class="label-icon">📸</span>
                식단 사진 선택
              </label>

              <!-- 드래그 앤 드롭 영역 -->
              <div
                class="upload-zone"
                :class="{ 'has-image': imagePreviewUrl }"
                @click="$refs.fileInput.click()"
                @drop.prevent="handleDrop"
                @dragover.prevent
                @dragenter.prevent
              >
                <input 
                  ref="fileInput" 
                  type="file" 
                  @change="onFileChange" 
                  accept="image/*" 
                  required
                  class="file-input"
                />

                <div v-if="!imagePreviewUrl" class="upload-placeholder">
                  <div class="upload-icon">📷</div>
                  <div class="upload-text">
                    <p class="upload-title">사진을 선택하거나 여기로 드래그하세요</p>
                    <p class="upload-subtitle">JPG, PNG 파일을 지원합니다</p>
                  </div>
                </div>

                <div v-else class="upload-success">
                  <div class="success-icon">✅</div>
                  <p class="success-title">사진이 선택되었습니다!</p>
                  <p class="success-subtitle">다른 사진을 선택하려면 클릭하세요</p>
                </div>
              </div>
            </div>

            <!-- 업로드 버튼 -->
            <button 
              type="submit" 
              :disabled="!imageFile || uploading"
              class="upload-button"
            >
              <span class="button-icon">🚀</span>
              AI 분석 시작하기
            </button>
          </form>

          <!-- 분석 결과 -->
          <div v-if="result" class="result-section">
            <div class="result-card">
              <div class="result-header">
                <div class="result-icon">🔍</div>
                <h3 class="result-title">분석 완료!</h3>
              </div>

              <div class="result-content">
                <div class="result-stats">
                  <div class="stat-item">
                    <div class="stat-label">감지된 음식</div>
                    <div class="stat-value">{{ result.detectedFoods }}</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">총 칼로리</div>
                    <div class="stat-value">{{ result.totalCalories }} kcal</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-label">AI 점수</div>
                    <div class="stat-value">{{ result.autoScore }} / 10</div>
                  </div>
                </div>

                <div class="feedback-section">
                  <div class="feedback-label">AI 피드백</div>
                  <div class="feedback-content">{{ result.aiFeedback }}</div>
                </div>
              </div>

              <button @click="goToMealDetail(result.mealId)" class="detail-button">
                📊 상세 분석 결과 보기
              </button>
            </div>

            <!-- AI 점수 설명 -->
            <div class="info-card">
              <div class="info-content">
                <div class="info-icon">💡</div>
                <div class="info-text">
                  <strong>EatNote AI 건강 점수란?</strong><br>
                  개인정보(성별, 연령, 신체지수)를 바탕으로 권장 섭취량 대비 평가됩니다.
                  열량 적정성, 영양소 균형, 나트륨·당류 등을 종합 분석하여 10점 만점으로 산정합니다.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 하단 네비게이션 -->
      <div class="bottom-navigation">
        <button @click="goToDashboard" class="nav-button secondary">
          📋 식단 목록으로
        </button>

        <button v-if="uploadComplete" @click="resetForm" class="nav-button primary">
          ➕ 새 식단 등록
        </button>
      </div>
    </div>

    <!-- 업로드 중 모달 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="uploading" class="modal-overlay" @click="cancelUpload">
          <div class="modal-container" @click.stop>
            <!-- 모달 닫기 버튼 -->
            <button @click="cancelUpload" class="modal-close">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>

            <!-- 노미 로딩 컴포넌트 -->
            <div class="loading-content">
              <NomiLoading size="xl" :is-loading="uploading" :messages="[
                '사진을 분석하고 있어요! 🔍',
                `${nickname}님을\n위한 피드백 작성 중이에요! ✏️`,
                '맛있는 음식을 찾고 있어요! 🍽️',
                '영양 정보를 계산하고 있어요! 📊',
                '거의 다 끝났어요! ✨'
              ]" />
              <div class="loading-text">
                <p class="loading-title">AI가 식단을 분석하고 있어요</p>
                <p class="loading-subtitle">잠시만 기다려주세요...</p>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import NomiLoading from '@/components/NomiLoading.vue'
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const nickname = computed(() => authStore.user?.nickname || '회원')

const toast = useToast()
const customMealTime = ref('')
const showTimeInput = ref(false)

const toggleTimeInput = () => {
  showTimeInput.value = !showTimeInput.value
}

// 상태 변수
const mealType = ref('breakfast')
const imageFile = ref(null)
const uploading = ref(false)
const result = ref(null)
const router = useRouter()
const uploadComplete = ref(false)
const imagePreviewUrl = ref(null)

// 시간 관련 상태
const useCustomTime = ref(false)  // true: 직접입력, false: 현재시간

// AbortController for canceling requests
let abortController = null

// 업로드 취소 함수
const cancelUpload = () => {
  if (abortController) {
    abortController.abort()
  }
  uploading.value = false
  toast.info('업로드가 취소되었습니다.')
}

// 현재 시간을 기준으로 한 최대 날짜시간 (미래 입력 방지)
const maxDateTime = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const date = String(now.getDate()).padStart(2, '0')
  const hour = String(now.getHours()).padStart(2, '0')
  const minute = String(now.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${date}T${hour}:${minute}`
})

// 현재 시간 표시용
const currentTimeDisplay = computed(() => {
  const now = new Date()
  const month = now.getMonth() + 1
  const date = now.getDate()
  const hour = now.getHours()
  const minute = now.getMinutes()
  return `${month}월 ${date}일 ${hour}:${minute.toString().padStart(2, '0')}`
})

const onFileChange = (e) => {
  const file = e.target.files[0]
  imageFile.value = file
  if (file) {
    imagePreviewUrl.value = URL.createObjectURL(file)
  }
}

const handleDrop = (e) => {
  const files = e.dataTransfer.files
  if (files.length > 0) {
    const file = files[0]
    if (file.type.startsWith('image/')) {
      imageFile.value = file
      imagePreviewUrl.value = URL.createObjectURL(file)
    }
  }
}

const clearImage = () => {
  imageFile.value = null
  imagePreviewUrl.value = null
  if (URL.revokeObjectURL && imagePreviewUrl.value) {
    URL.revokeObjectURL(imagePreviewUrl.value)
  }
}

const resetForm = () => {
  imageFile.value = null
  imagePreviewUrl.value = null
  result.value = null
  uploadComplete.value = false
}

const goToDashboard = () => {
  router.push('/meals')
}

const goToMealDetail = (mealId) => {
  if (!mealId) return
  router.replace(`/meal/${mealId}`)
}

onMounted(() => {
  const now = new Date()
  const hour = now.getHours()
  if (hour >= 4 && hour < 11) mealType.value = 'breakfast'
  else if (hour < 16) mealType.value = 'lunch'
  else if (hour < 23) mealType.value = 'dinner'
  else mealType.value = 'extra'
})

const mealTypeLabel = computed(() => {
  switch (mealType.value) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    default: return '간식'
  }
})

const suggestedMessage = computed(() => {
  const now = new Date()
  const month = now.getMonth() + 1
  const date = now.getDate()

  let type = ''
  let emoji = ''

  switch (mealType.value) {
    case 'breakfast': type = '아침'; emoji = '☀️'; break
    case 'lunch': type = '점심'; emoji = '🍽️'; break
    case 'dinner': type = '저녁'; emoji = '🌙'; break
    default: type = '간식'; emoji = '🍩'
  }

  return `${month}월 ${date}일의 ${type} 식사를 올려주세요 ${emoji}`
})

const handleUpload = async () => {
  if (!imageFile.value) return;

  uploading.value = true;

  // AbortController 생성
  abortController = new AbortController()

  const formData = new FormData();
  formData.append('file', imageFile.value);

  if (customMealTime.value) {
    const localTime = new Date(customMealTime.value);
    const offset = localTime.getTimezoneOffset();
    const corrected = new Date(localTime.getTime() - offset * 60000);
    const localISOString = corrected.toISOString().slice(0, 19);
    formData.append('mealTime', localISOString);
  }

  try {
    const token = localStorage.getItem('accessToken');
    const response = await axios.post('/api/meal/upload', formData, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      signal: abortController.signal  // AbortController 신호 추가
    });

    result.value = response.data.data;
    uploadComplete.value = true;
    toast.success('식단이 성공적으로 업로드되었습니다.')
  } catch (error) {
    // 사용자가 취소한 경우
    if (axios.isCancel(error)) {
      console.log('업로드가 사용자에 의해 취소되었습니다.');
      return;
    }

    console.error('[❌ 업로드 에러 발생]', error);

    const msg = error?.response?.data?.message || '';
    if (msg.includes('감지된 음식이 없습니다')) {
      toast.warning('😥 음식이 감지되지 않았어요.\n더 선명한 사진으로 다시 시도해보세요!')
    } else {
      toast.error('🚨 식단 업로드 중 문제가 발생했습니다.')
    }
  } finally {
    uploading.value = false;
    abortController = null;
  }
}
</script>

<style scoped>
/* 페이지 전체 배경 */
.meal-upload-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 설정 */
.main-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f59e0b;
}

.header-content {
  text-align: center;
}

.main-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
  margin-bottom: 12px;
}

.main-subtitle {
  font-size: 18px;
  color: #5D4037;
  line-height: 1.6;
  margin-bottom: 16px;
}

.time-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 20px;
  border: 1px solid rgba(245, 158, 11, 0.2);
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 업로드 카드 */
.upload-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
  margin-bottom: 32px;
}

/* 이미지 미리보기 섹션 */
.image-preview-section {
  position: relative;
}

.image-container {
  width: 100%;
  height: 256px;
  background: #f9fafb;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-button {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-button:hover {
  background: #dc2626;
  transform: scale(1.1);
}

.remove-button svg {
  width: 16px;
  height: 16px;
}

/* 폼 콘텐츠 */
.form-content {
  padding: 32px;
}

.upload-form {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 폼 섹션 */
.form-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #374151;
}

.label-icon {
  font-size: 20px;
}

/* 시간 모드 선택자 */
.time-mode-selector {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: rgba(249, 250, 251, 0.5);
  border-radius: 16px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.mode-option {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.mode-radio {
  display: none;
}

.mode-indicator {
  width: 16px;
  height: 16px;
  border: 2px solid #f59e0b;
  border-radius: 50%;
  background: white;
  position: relative;
  transition: all 0.3s ease;
}

.mode-indicator.active {
  background: #f59e0b;
}

.mode-indicator.active::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 6px;
  background: white;
  border-radius: 50%;
}

.mode-text {
  font-weight: 500;
  color: #374151;
}

/* 커스텀 시간 섹션 */
.custom-time-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.datetime-input-wrapper {
  position: relative;
}

.datetime-input {
  width: 100%;
  padding: 12px 48px 12px 16px;
  border: 2px solid #f59e0b;
  border-radius: 12px;
  font-size: 16px;
  color: #374151;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
}

.datetime-input:focus {
  outline: none;
  border-color: #d97706;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.input-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  pointer-events: none;
}

.input-hint {
  font-size: 14px;
  color: #6b7280;
}

/* 현재 시간 정보 */
.current-time-info {
  padding: 16px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.time-info-content {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #1e40af;
}

.time-icon {
  font-size: 16px;
}

/* 업로드 영역 */
.upload-zone {
  border: 2px dashed #f59e0b;
  border-radius: 16px;
  padding: 48px 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(249, 250, 251, 0.5);
}

.upload-zone:hover {
  border-color: #d97706;
  background: rgba(245, 158, 11, 0.05);
}

.upload-zone.has-image {
  background: rgba(16, 185, 129, 0.05);
  border-color: #10b981;
}

.file-input {
  display: none;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  font-size: 64px;
  transition: transform 0.3s ease;
}

.upload-zone:hover .upload-icon {
  transform: scale(1.1);
}

.upload-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.upload-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.upload-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.success-icon {
  font-size: 48px;
}

.success-title {
  font-size: 18px;
  font-weight: 600;
  color: #059669;
  margin: 0;
}

.success-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

/* 업로드 버튼 */
.upload-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.upload-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

.upload-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.button-icon {
  font-size: 20px;
}

/* 결과 섹션 */
.result-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.result-card {
  background: rgba(16, 185, 129, 0.1);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.result-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.result-icon {
  width: 40px;
  height: 40px;
  background: #10b981;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.result-title {
  font-size: 20px;
  font-weight: 700;
  color: #374151;
  margin: 0;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.stat-item {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #374151;
}

.feedback-section {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 16px;
}

.feedback-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 8px;
}

.feedback-content {
  color: #374151;
  line-height: 1.6;
}

.detail-button {
  width: 100%;
  padding: 12px 24px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.detail-button:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.4);
}

.info-card {
  background: rgba(59, 130, 246, 0.1);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.info-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.info-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.info-text {
  font-size: 14px;
  color: #1e40af;
  line-height: 1.6;
}

/* 하단 네비게이션 */
.bottom-navigation {
  display: flex;
  gap: 16px;
}

.nav-button {
  flex: 1;
  padding: 16px 24px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-button.secondary {
  background: rgba(255, 255, 255, 0.8);
  color: #374151;
  border: 1px solid #e5e7eb;
}

.nav-button.secondary:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: #d1d5db;
  transform: translateY(-1px);
}

.nav-button.primary {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.nav-button.primary:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  padding: 32px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(245, 158, 11, 0.1);
  position: relative;
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
}

.modal-close:hover {
  background: #dc2626;
  transform: scale(1.1);
}

.modal-close svg {
  width: 16px;
  height: 16px;
}

.loading-content {
  text-align: center;
}

.loading-text {
  margin-top: 24px;
}

.loading-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.loading-subtitle {
  font-size: 14px;
  color: #6b7280;
}

/* 모달 트랜지션 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  backdrop-filter: blur(0px);
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}

/* 애니메이션 - 헤더 제외 */
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

/* 반응형 디자인 */
@media (max-width: 768px) {
  .main-container {
    padding: 16px 12px;
  }
  
  .page-header {
    margin-bottom: 32px;
  }
  
  .main-title {
    font-size: 32px;
  }
  
  .main-subtitle {
    font-size: 16px;
  }

  .form-content {
    padding: 24px;
  }

  .upload-form {
    gap: 24px;
  }

  .upload-zone {
    padding: 32px 24px;
  }

  .upload-icon {
    font-size: 48px;
  }

  .upload-title {
    font-size: 16px;
  }

  .result-stats {
    grid-template-columns: 1fr;
  }

  .bottom-navigation {
    flex-direction: column;
  }

  .modal-container {
    padding: 24px;
    margin: 16px;
  }
}

@media (max-width: 640px) {
  .main-title {
    font-size: 28px;
  }
  
  .main-subtitle {
    font-size: 16px;
  }

  .form-content {
    padding: 20px;
  }

  .time-mode-selector {
    padding: 16px;
  }

  .upload-zone {
    padding: 24px 16px;
  }

  .section-label {
    font-size: 16px;
  }

  .modal-container {
    padding: 20px;
  }
}

/* 접근성 */
.upload-button:focus,
.nav-button:focus,
.detail-button:focus {
  outline: 2px solid #f59e0b;
  outline-offset: 2px;
}

.mode-option:focus-within .mode-indicator {
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.3);
}
</style>