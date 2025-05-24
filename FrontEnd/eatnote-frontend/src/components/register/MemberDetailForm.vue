<template>
  <div class="space-y-5">
    <!-- 필수 항목 안내 -->
    <div class="required-notice">
      <div class="notice-icon">⚠️</div>
      <div class="notice-text">
        <strong>필수 입력 항목</strong>을 모두 작성해주세요
        <div class="notice-items">키, 몸무게, 운동 목표</div>
      </div>
    </div>
    <!-- 신체 정보 섹션 -->
    <div class="section-header">
      <h3 class="section-title">
        <span class="section-icon">📊</span>
        신체 정보
      </h3>
      <p class="section-subtitle">정확한 분석을 위해 신체 정보를 입력해주세요</p>
    </div>

    <!-- 키 & 몸무게 -->
    <div class="grid grid-cols-2 gap-4">
      <div class="form-group">
        <label class="form-label required">
          <span class="label-icon">📏</span>
          키 (cm)
        </label>
        <input 
          v-model.number="model.height" 
          type="number" 
          class="form-input"
          :class="{ 'error': errors.height }"
          placeholder="170"
          min="100"
          max="250"
        />
        <p v-if="errors.height" class="error-message">키는 필수 입력 항목입니다.</p>
      </div>

      <div class="form-group">
        <label class="form-label required">
          <span class="label-icon">⚖️</span>
          몸무게 (kg)
        </label>
        <input 
          v-model.number="model.weight" 
          type="number" 
          class="form-input"
          :class="{ 'error': errors.weight }"
          placeholder="70"
          min="30"
          max="200"
        />
        <p v-if="errors.weight" class="error-message">몸무게는 필수 입력 항목입니다.</p>
      </div>
    </div>

    <!-- 체성분 정보 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">🔬</span>
        체성분 정보 (선택사항)
      </h4>
    </div>

    <!-- 체수분 & 단백질량 -->
    <div class="grid grid-cols-2 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">💧</span>
          체수분 (%)
        </label>
        <input 
          v-model.number="model.bodyWater" 
          type="number" 
          class="form-input"
          placeholder="60"
          min="0"
          max="100"
          step="0.1"
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">🥩</span>
          단백질량 (%)
        </label>
        <input 
          v-model.number="model.protein" 
          type="number" 
          class="form-input"
          placeholder="20"
          min="0"
          max="100"
          step="0.1"
        />
      </div>
    </div>

    <!-- 무기질 & 체지방 -->
    <div class="grid grid-cols-2 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">⚙️</span>
          무기질 (%)
        </label>
        <input 
          v-model.number="model.mineral" 
          type="number" 
          class="form-input"
          placeholder="5"
          min="0"
          max="100"
          step="0.1"
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">🔥</span>
          체지방 (kg)
        </label>
        <input 
          v-model.number="model.bodyFat" 
          type="number" 
          class="form-input"
          placeholder="15"
          min="0"
          max="100"
          step="0.1"
        />
      </div>
    </div>

    <!-- 골격근량 & 체지방량 -->
    <div class="grid grid-cols-2 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">💪</span>
          골격근량 (kg)
        </label>
        <input 
          v-model.number="model.skeletalMuscle" 
          type="number" 
          class="form-input"
          placeholder="30"
          min="0"
          max="100"
          step="0.1"
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">🧈</span>
          체지방량 (kg)
        </label>
        <input 
          v-model.number="model.bodyFatMass" 
          type="number" 
          class="form-input"
          placeholder="12"
          min="0"
          max="100"
          step="0.1"
        />
      </div>
    </div>

    <!-- BMI & 체지방률 -->
    <div class="grid grid-cols-2 gap-4">
      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">📊</span>
          BMI
        </label>
        <input 
          v-model.number="model.bmi" 
          type="number" 
          class="form-input"
          placeholder="22.5"
          min="10"
          max="50"
          step="0.1"
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          <span class="label-icon">📉</span>
          체지방률 (%)
        </label>
        <input 
          v-model.number="model.bodyFatPercentage" 
          type="number" 
          class="form-input"
          placeholder="18"
          min="0"
          max="100"
          step="0.1"
        />
      </div>
    </div>

    <!-- 목표 섹션 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">🎯</span>
        목표 설정
      </h4>
    </div>

    <!-- 목표 선택 -->
    <div class="form-group">
      <label class="form-label required">
        <span class="label-icon">🎯</span>
        운동 목표
      </label>
      <select v-model="model.goal" class="form-input" :class="{ 'error': errors.goal }">
        <option disabled value="">목표를 선택하세요</option>
        <option v-for="g in goalList" :key="g.code" :value="g.code">
          {{ g.label }}
        </option>
      </select>
      <p v-if="errors.goal" class="error-message">운동 목표는 필수 선택 항목입니다.</p>
    </div>

    <!-- 담당 트레이너 -->
    <div class="form-group">
      <label class="form-label">
        <span class="label-icon">👨‍💼</span>
        담당 트레이너 닉네임 (선택사항)
      </label>
      <div class="flex gap-2">
        <input
          v-model="model.trainerNickname"
          type="text"
          class="form-input flex-1"
          :class="{ 'error': errors.trainerNickname }"
          placeholder="트레이너 닉네임을 입력하세요"
          @input="handleTrainerNicknameChange"
        />
        <button 
          @click="checkTrainer" 
          class="trainer-check-btn"
          :disabled="!model.trainerNickname"
        >
          확인
        </button>
      </div>
      
      <div v-if="model.trainerNickname" class="mt-2">
        <p v-if="memberTrainerExists === true" class="success-message">
          ✅ 트레이너가 존재합니다.
        </p>
        <p v-else-if="memberTrainerExists === false" class="error-message">
          ❌ 존재하지 않는 트레이너입니다.
        </p>
        <p v-else-if="memberTrainerExists === null && !trainerCheckRequested" class="warning-message">
          ⚠️ 트레이너 닉네임 확인을 눌러주세요.
        </p>
      </div>
      <p v-if="errors.trainerNickname" class="error-message">
        트레이너 닉네임을 입력한 경우 반드시 확인 버튼을 눌러서 검증해주세요.
      </p>
    </div>

    <!-- 알레르기 섹션 -->
    <div class="section-divider">
      <h4 class="subsection-title">
        <span class="subsection-icon">🌰</span>
        알레르기 정보
      </h4>
    </div>

    <!-- 알레르기 카테고리 선택 -->
    <div class="form-group">
      <label class="form-label">
        <span class="label-icon">📂</span>
        알레르기 카테고리
      </label>
      <select v-model="selectedCategory" class="form-input">
        <option disabled value="">카테고리를 선택하세요</option>
        <option v-for="(list, category) in allergyMap" :key="category" :value="category">
          {{ category }}
        </option>
      </select>
    </div>

    <!-- 알레르기 체크박스 목록 -->
    <div v-if="selectedCategory" class="form-group">
      <label class="form-label">
        <span class="label-icon">✅</span>
        {{ selectedCategory }} 알레르기 선택
      </label>
      <div class="checkbox-grid">
        <label 
          v-for="a in allergyMap[selectedCategory]" 
          :key="a.allergyId" 
          class="checkbox-item"
        >
          <input
            type="checkbox"
            :value="a.allergyId"
            v-model="model.allergyIds"
            class="checkbox-input"
          />
          <span class="checkbox-label">{{ a.name }}</span>
        </label>
      </div>
    </div>

    <!-- 선택된 알레르기 표시 -->
    <div v-if="selectedAllergyNames.length > 0" class="form-group">
      <label class="form-label">
        <span class="label-icon">🏷️</span>
        선택된 알레르기
      </label>
      <div class="allergy-tags">
        <span
          v-for="(name, index) in selectedAllergyNames"
          :key="index"
          class="allergy-tag"
        >
          {{ name }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, watch } from 'vue'
const model = defineModel()
const emit = defineEmits(['validation-change'])

const trainerExists = ref(null)

// 유효성 검사 에러 상태
const errors = ref({
  height: false,
  weight: false,
  goal: false,
  trainerNickname: false
})

// 트레이너 닉네임 관련 상태
const memberTrainerExists = ref(null)
const trainerCheckRequested = ref(false) // 확인 버튼을 눌렀는지 여부

// 트레이너 닉네임 검증 함수
const isTrainerNicknameValid = computed(() => {
  // 트레이너 닉네임을 입력하지 않은 경우: 유효 (선택사항이므로)
  if (!model.value.trainerNickname || model.value.trainerNickname.trim() === '') {
    return true
  }
  
  // 트레이너 닉네임을 입력한 경우: 확인 버튼을 누르고 존재하는 트레이너여야 함
  return trainerCheckRequested.value && memberTrainerExists.value === true
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  return model.value.height && 
         model.value.weight && 
         model.value.goal &&
         model.value.height > 0 &&
         model.value.weight > 0 &&
         isTrainerNicknameValid.value // 트레이너 닉네임 검증 추가
})

// 필수 항목 검증 함수
const validateForm = () => {
  errors.value.height = !model.value.height || model.value.height <= 0
  errors.value.weight = !model.value.weight || model.value.weight <= 0
  errors.value.goal = !model.value.goal
  
  // 트레이너 닉네임 검증
  if (model.value.trainerNickname && model.value.trainerNickname.trim() !== '') {
    errors.value.trainerNickname = !isTrainerNicknameValid.value
  } else {
    errors.value.trainerNickname = false
  }
  
  return isFormValid.value
}

// 트레이너 닉네임 입력값 변경 시 호출
const handleTrainerNicknameChange = () => {
  // 닉네임이 변경되면 이전 검증 결과 초기화
  memberTrainerExists.value = null
  trainerCheckRequested.value = false
  
  // 실시간 검증
  validateForm()
}

// 부모 컴포넌트에 유효성 상태 전달
watch(isFormValid, (valid) => {
  emit('validation-change', valid)
}, { immediate: true })

// 모델 값 변경 시 실시간 검증
watch(() => [model.value.height, model.value.weight, model.value.goal], () => {
  validateForm()
}, { deep: true })

// 트레이너 닉네임 유효성 변경 시 검증
watch(isTrainerNicknameValid, () => {
  validateForm()
})

// 외부에서 호출할 수 있는 검증 함수 노출
defineExpose({
  validateForm,
  isFormValid
})

const checkTrainer = async () => {
  if (!model.value.trainerNickname) {
    memberTrainerExists.value = null
    trainerCheckRequested.value = false
    return
  }

  try {
    trainerCheckRequested.value = true // 확인 버튼을 눌렀음을 표시
    const res = await fetch(`/api/users/check-trainer-nickname?nickname=${encodeURIComponent(model.value.trainerNickname)}`)
    const data = await res.json()
    memberTrainerExists.value = data.data
    
    // 검증 후 폼 유효성 재검사
    validateForm()
  } catch (e) {
    console.error('트레이너 닉네임 확인 실패:', e)
    memberTrainerExists.value = false
    validateForm()
  }
}

// 알레르기 관련
const allergyMap = ref({})
const selectedCategory = ref('')

// 목표 관련
const goalList = ref([])

// 선택된 알레르기 이름 리스트
const selectedAllergyNames = computed(() => {
  const result = []
  for (const category in allergyMap.value) {
    for (const a of allergyMap.value[category]) {
      if (model.value.allergyIds?.includes(a.allergyId)) {
        result.push(a.name)
      }
    }
  }
  return result
})

// API 호출
const loadAllergies = async () => {
  try {
    const res = await fetch('/api/allergies/grouped')
    const data = await res.json()
    allergyMap.value = data.data
  } catch (e) {
    console.error('알레르기 목록 로딩 실패:', e)
  }
}

const loadGoals = async () => {
  try {
    const res = await fetch('/api/common/goal')
    const data = await res.json()
    goalList.value = data.data
  } catch (e) {
    console.error('목표 목록 로딩 실패:', e)
  }
}

onMounted(() => {
  model.value.allergyIds = model.value.allergyIds || []
  loadAllergies()
  loadGoals()
})
</script>

<style scoped>
/* 필수 항목 안내 */
.required-notice {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background: linear-gradient(135deg, #fef3c7, #fed7aa);
  border: 1px solid #f59e0b;
  border-radius: 12px;
  margin-bottom: 1.5rem;
}

.notice-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notice-text {
  color: #92400e;
  font-size: 0.9rem;
}

.notice-text strong {
  color: #78350f;
}

.notice-items {
  font-size: 0.8rem;
  color: #a16207;
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

.form-input::placeholder {
  color: #9ca3af;
}

/* 트레이너 확인 버튼 */
.trainer-check-btn {
  padding: 0.875rem 1rem;
  background-color: #3b82f6;
  border: 2px solid #3b82f6;
  color: white;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 70px;
}

.trainer-check-btn:hover:not(:disabled) {
  background-color: #2563eb;
  border-color: #2563eb;
  transform: translateY(-1px);
}

.trainer-check-btn:disabled {
  background-color: #9ca3af;
  border-color: #9ca3af;
  cursor: not-allowed;
}

/* 체크박스 그리드 */
.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background-color: rgba(255, 255, 255, 0.8);
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.checkbox-item:hover {
  background-color: rgba(245, 158, 11, 0.05);
  border-color: rgba(245, 158, 11, 0.3);
}

.checkbox-input {
  width: 1.2rem;
  height: 1.2rem;
  accent-color: #f59e0b;
}

.checkbox-label {
  font-size: 0.9rem;
  color: #374151;
  cursor: pointer;
}

/* 알레르기 태그 */
.allergy-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.allergy-tag {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(245, 158, 11, 0.2);
  transition: all 0.3s ease;
}

.allergy-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(245, 158, 11, 0.3);
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

.success-message {
  color: #059669;
  font-size: 0.85rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.warning-message {
  color: #d97706;
  font-size: 0.85rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.25rem;
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
  .checkbox-grid {
    grid-template-columns: 1fr;
  }
  
  .form-input {
    font-size: 16px; /* iOS zoom 방지 */
  }
  
  .section-title {
    font-size: 1.3rem;
  }
  
  .subsection-title {
    font-size: 1.1rem;
  }
}
</style>