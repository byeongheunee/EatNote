<template>
  <div class="space-y-5">
    <!-- 필수 항목 안내 -->
    <div class="required-notice">
      <div class="notice-icon">⚠️</div>
      <div class="notice-text">
        <strong>필수 입력 항목</strong>을 모두 작성해주세요
        <div class="notice-items">자기소개</div>
      </div>
    </div>

    <!-- 기본 정보 섹션 -->
    <div class="section-header">
      <h3 class="section-title">
        <span class="section-icon">📋</span>
        기본 정보
      </h3>
      <p class="section-subtitle">트레이너로 활동하기 위한 기본 정보를 입력해주세요</p>
    </div>

    <!-- 연락처 & 주소 -->
    <div class="grid grid-cols-1 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">📞</span>
          연락처 (선택사항)
        </label>
        <input 
          v-model="model.phone" 
          type="tel" 
          class="form-input"
          placeholder="010-1234-5678"
          maxlength="13"
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">📍</span>
          주소 (선택사항)
        </label>
        <input 
          v-model="model.address" 
          type="text" 
          class="form-input"
          placeholder="서울특별시 강남구..."
        />
      </div>
    </div>

    <!-- 헬스장 정보 섹션 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">🏋️</span>
        헬스장 정보
      </h4>
    </div>

    <!-- 헬스장명 & 웹사이트 -->
    <div class="grid grid-cols-1 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">🏢</span>
          헬스장명 (선택사항)
        </label>
        <input 
          v-model="model.gymName" 
          type="text" 
          class="form-input"
          placeholder="피트니스센터명을 입력하세요"
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">🌐</span>
          헬스장 웹사이트 (선택사항)
        </label>
        <input 
          v-model="model.gymWebsite" 
          type="url" 
          class="form-input"
          placeholder="https://..."
        />
      </div>
    </div>

    <!-- 자격 정보 섹션 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">🏆</span>
        자격 정보
      </h4>
    </div>

    <!-- 자격증 번호 & 사업자 번호 -->
    <div class="grid grid-cols-1 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">🎖️</span>
          자격증 번호 (선택사항)
        </label>
        <input 
          v-model="model.certificationNumber" 
          type="text" 
          class="form-input"
          placeholder="자격증 번호를 입력하세요"
        />
      </div>

      <!-- 자격증 사진 -->
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">📸</span>
          자격증 사진 (선택사항)
        </label>
        <div class="file-upload-area" @click="$refs.certImageInput.click()">
          <div v-if="!certificationImageFile" class="file-upload-placeholder">
            <div class="text-4xl mb-3">📜</div>
            <p class="text-gray-700 font-medium text-lg">클릭해서 자격증 사진을 선택하세요</p>
            <p class="text-sm text-gray-500 mt-2">JPG, PNG 파일 지원 (최대 10MB)</p>
            <p class="text-xs text-gray-400 mt-1">자격증 인증을 위해 사용됩니다</p>
          </div>
          <div v-else class="file-upload-selected">
            <div class="text-4xl mb-3">✅</div>
            <p class="text-green-700 font-medium text-lg">{{ certificationImageFile.name }}</p>
            <p class="text-sm text-gray-600 mt-2">파일 크기: {{ formatFileSize(certificationImageFile.size) }}</p>
            <p class="text-xs text-gray-500 mt-1">다른 파일을 선택하려면 클릭하세요</p>
          </div>
        </div>
        <input 
          ref="certImageInput"
          type="file" 
          @change="handleCertImageChange" 
          class="hidden"
          accept="image/*"
        />
        <p v-if="certImageError" class="error-message">{{ certImageError }}</p>
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">📊</span>
          사업자 번호 (선택사항)
        </label>
        <input 
          v-model="model.businessNumber" 
          type="text" 
          class="form-input"
          placeholder="000-00-00000"
          maxlength="12"
        />
      </div>
    </div>

    <!-- 프로필 정보 섹션 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">👤</span>
        프로필 정보
      </h4>
    </div>

    <!-- 자기소개 -->
    <div class="form-group">
      <label class="form-label required">
        <span class="label-icon">💬</span>
        자기소개
      </label>
      <textarea 
        v-model="model.introduction" 
        class="form-textarea"
        :class="{ 'error': errors.introduction }"
        placeholder="자신을 소개하고 트레이닝 철학을 알려주세요..."
        rows="4"
        maxlength="500"
      ></textarea>
      <div class="textarea-counter">
        {{ model.introduction?.length || 0 }}/500
      </div>
      <p v-if="errors.introduction" class="error-message">자기소개는 필수 입력 항목입니다.</p>
    </div>

    <!-- 경력 -->
    <div class="form-group">
      <label class="form-label">
        <span class="label-icon">📈</span>
        경력 (선택사항)
      </label>
      <textarea 
        v-model="model.career" 
        class="form-textarea"
        placeholder="관련 경력이나 경험을 작성해주세요..."
        rows="3"
        maxlength="300"
      ></textarea>
      <div class="textarea-counter">
        {{ model.career?.length || 0 }}/300
      </div>
    </div>

    <!-- SNS 정보 섹션 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">📱</span>
        SNS 정보
      </h4>
    </div>

    <!-- 인스타그램 URL -->
    <div class="form-group">
      <label class="form-label">
        <span class="label-icon">📸</span>
        인스타그램 URL (선택사항)
      </label>
      <input 
        v-model="model.instagramUrl" 
        type="url" 
        class="form-input"
        placeholder="https://instagram.com/your_account"
      />
      <p class="input-help">회원들이 더 쉽게 찾을 수 있도록 도움이 됩니다</p>
    </div>

    <!-- 입력 완료 안내 -->
    <div v-if="isFormValid" class="completion-notice">
      <div class="completion-icon">✅</div>
      <div class="completion-text">
        <strong>모든 필수 정보가 입력되었습니다!</strong>
        <div class="completion-subtitle">회원가입을 완료해주세요</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const model = defineModel()
const emit = defineEmits(['validation-change'])

// 자격증 이미지 파일 관련
const certificationImageFile = ref(null)
const certImageError = ref('')

// 유효성 검사 에러 상태
const errors = ref({
  introduction: false
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  return model.value.introduction && 
         model.value.introduction.trim() !== ''
})

// 필수 항목 검증 함수
const validateForm = () => {
  errors.value.introduction = !model.value.introduction || model.value.introduction.trim() === ''
  
  return isFormValid.value
}

// 부모 컴포넌트에 유효성 상태 전달
watch(isFormValid, (valid) => {
  emit('validation-change', valid)
}, { immediate: true })

// 모델 값 변경 시 실시간 검증
watch(() => model.value.introduction, () => {
  validateForm()
}, { deep: true })

// 외부에서 호출할 수 있는 검증 함수 노출
defineExpose({
  validateForm,
  isFormValid,
  getCertificationImageFile: () => certificationImageFile.value
})

// 자격증 이미지 파일 변경 핸들러
const handleCertImageChange = (event) => {
  const file = event.target.files[0]
  certImageError.value = ''
  
  if (!file) {
    certificationImageFile.value = null
    return
  }
  
  // 파일 크기 검증 (10MB)
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    certImageError.value = '파일 크기는 10MB 이하여야 합니다.'
    event.target.value = '' // 파일 input 초기화
    return
  }
  
  // 파일 타입 검증
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']
  if (!allowedTypes.includes(file.type)) {
    certImageError.value = 'JPG, PNG 파일만 업로드 가능합니다.'
    event.target.value = '' // 파일 input 초기화
    return
  }
  
  certificationImageFile.value = file
}

// 파일 크기 포맷팅 함수
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 연락처 포맷팅 (하이픈 자동 추가)
watch(() => model.value.phone, (newPhone) => {
  if (newPhone) {
    // 숫자만 추출
    const numbers = newPhone.replace(/[^0-9]/g, '')
    
    // 하이픈 추가
    if (numbers.length <= 3) {
      model.value.phone = numbers
    } else if (numbers.length <= 7) {
      model.value.phone = numbers.slice(0, 3) + '-' + numbers.slice(3)
    } else {
      model.value.phone = numbers.slice(0, 3) + '-' + numbers.slice(3, 7) + '-' + numbers.slice(7, 11)
    }
  }
})

// 사업자 번호 포맷팅 (하이픈 자동 추가)
watch(() => model.value.businessNumber, (newNumber) => {
  if (newNumber) {
    // 숫자만 추출
    const numbers = newNumber.replace(/[^0-9]/g, '')
    
    // 하이픈 추가
    if (numbers.length <= 3) {
      model.value.businessNumber = numbers
    } else if (numbers.length <= 5) {
      model.value.businessNumber = numbers.slice(0, 3) + '-' + numbers.slice(3)
    } else {
      model.value.businessNumber = numbers.slice(0, 3) + '-' + numbers.slice(3, 5) + '-' + numbers.slice(5, 10)
    }
  }
})
</script>

<style scoped>
/* 필수 항목 안내 */
.required-notice {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  border: 1px solid #3b82f6;
  border-radius: 12px;
  margin-bottom: 1.5rem;
}

.notice-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notice-text {
  color: #1e40af;
  font-size: 0.9rem;
}

.notice-text strong {
  color: #1e3a8a;
}

.notice-items {
  font-size: 0.8rem;
  color: #2563eb;
  margin-top: 0.25rem;
}

/* 섹션 헤더 */
.section-header {
  text-align: center;
  margin-bottom: 2rem;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.5rem;
}

.section-icon {
  font-size: 1.75rem;
}

.section-subtitle {
  color: #6b7280;
  font-size: 0.95rem;
}

/* 섹션 구분선 */
.section-divider {
  margin: 2rem 0 1.5rem 0;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.subsection-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.2rem;
  font-weight: 600;
  color: #4b5563;
  margin-bottom: 1rem;
}

.subsection-icon {
  font-size: 1.25rem;
}

/* 폼 그룹 */
.form-group {
  animation: slideInUp 0.4s ease-out;
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

.form-label.required::after {
  content: "*";
  color: #ef4444;
  margin-left: 0.25rem;
}

.label-icon {
  font-size: 1.1rem;
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
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  background-color: #ffffff;
}

.form-input.error {
  border-color: #ef4444;
  background-color: rgba(254, 242, 242, 0.9);
}

.form-input.error:focus {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.form-input::placeholder {
  color: #9ca3af;
}

/* 텍스트 에리어 */
.form-textarea {
  width: 100%;
  padding: 0.875rem 1rem;
  background-color: rgba(255, 255, 255, 0.9);
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  outline: none;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.6;
}

.form-textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  background-color: #ffffff;
}

.form-textarea::placeholder {
  color: #9ca3af;
}

/* 텍스트 카운터 */
.textarea-counter {
  text-align: right;
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 0.5rem;
}

/* 입력 도움말 */
.input-help {
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 0.5rem;
  font-style: italic;
}

/* 완료 안내 */
.completion-notice {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  border: 1px solid #10b981;
  border-radius: 12px;
  margin-top: 1.5rem;
}

.completion-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.completion-text {
  color: #065f46;
  font-size: 0.9rem;
}

.completion-text strong {
  color: #064e3b;
}

.completion-subtitle {
  font-size: 0.8rem;
  color: #047857;
  margin-top: 0.25rem;
}

/* 메시지 */
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

/* 파일 업로드 영역 */
.file-upload-area {
  width: 100%;
  padding: 2rem;
  background-color: rgba(255, 255, 255, 0.8);
  border: 2px dashed #d1d5db;
  border-radius: 16px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s ease;
  min-height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-upload-area:hover {
  border-color: #3b82f6;
  background-color: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.file-upload-placeholder,
.file-upload-selected {
  transition: all 0.3s ease;
  width: 100%;
}

.file-upload-placeholder .text-4xl,
.file-upload-selected .text-4xl {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.file-upload-area:hover .file-upload-placeholder .text-4xl,
.file-upload-area:hover .file-upload-selected .text-4xl {
  transform: scale(1.1);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
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

/* 반응형 */
@media (max-width: 768px) {
  .form-input, .form-textarea {
    font-size: 16px; /* iOS zoom 방지 */
  }
  
  .section-title {
    font-size: 1.3rem;
  }
  
  .subsection-title {
    font-size: 1.1rem;
  }
  
  .grid.grid-cols-2 {
    grid-template-columns: 1fr;
  }
  
  .file-upload-area {
    padding: 1.5rem;
    min-height: 120px;
  }
  
  .file-upload-placeholder .text-lg,
  .file-upload-selected .text-lg {
    font-size: 1rem;
  }
}

/* 포커스 링 제거 */
*:focus {
  outline: none;
}

/* 스크롤바 스타일링 */
.form-textarea::-webkit-scrollbar {
  width: 6px;
}

.form-textarea::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.form-textarea::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.form-textarea::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>