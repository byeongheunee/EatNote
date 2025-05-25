<template>
  <div class="feedback-form-page">
    <Header />

    <div class="feedback-container">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <h1 class="page-title">
          {{ feedbackId ? '✏️ 피드백 수정' : '🍱 식단 피드백 작성' }}
        </h1>
      </section>

      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p class="loading-text">불러오는 중입니다...</p>
      </div>

      <div v-else class="content-layout">
        <!-- 왼쪽 열: 식단 및 회원 정보 -->
        <div class="info-column">
          <!-- 식단 정보 -->
          <section class="info-section">
            <div class="section-header">
              <h2 class="section-title">🥗 식단 정보</h2>
              <div class="meal-type-badge" :class="getMealTypeBadgeClass(meal.mealType)">
                <span class="meal-emoji">{{ getMealTypeEmoji(meal.mealType) }}</span>
                <span class="meal-type-text">{{ getMealTypeText(meal.mealType) }}</span>
              </div>
            </div>
            
            <div class="card-container">
              <div class="meal-image-section">
                <img :src="getImageUrl(meal.imageUrl)" class="meal-image" alt="식단 이미지" />
                <div class="meal-basic-info">
                  <div class="info-item">
                    <span class="info-label">📅 식사 시간</span>
                    <span class="info-value">{{ formatDate(meal.mealTime) }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">🤖 AI 점수</span>
                    <span class="info-value ai-score">{{ meal.autoScore }}/10</span>
                  </div>
                </div>
              </div>

              <div class="food-info">
                <div class="info-label-header">
                  <span class="label-icon">🍽️</span>
                  <span class="label-text">감지된 음식</span>
                </div>
                <p class="food-text">{{ meal.detectedFoods }}</p>
              </div>

              <div class="ai-feedback-section">
                <div class="info-label-header">
                  <span class="label-icon">🤖</span>
                  <span class="label-text">AI 피드백</span>
                </div>
                <p class="ai-feedback-text">{{ meal.aiFeedback }}</p>
              </div>

              <!-- 성분 정보 -->
              <div class="nutrition-section">
                <h3 class="nutrition-title">🧪 성분 정보</h3>
                <div class="nutrition-grid">
                  <div class="nutrition-item calories">
                    <span class="nutrition-label">총 칼로리</span>
                    <span class="nutrition-value">{{ meal.totalCalories }} kcal</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">탄수화물</span>
                    <span class="nutrition-value">{{ meal.carbohydrates }} g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">단백질</span>
                    <span class="nutrition-value">{{ meal.protein }} g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">지방</span>
                    <span class="nutrition-value">{{ meal.fat }} g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">당류</span>
                    <span class="nutrition-value">{{ meal.sugars }} g</span>
                  </div>
                  <div class="nutrition-item">
                    <span class="nutrition-label">나트륨</span>
                    <span class="nutrition-value">{{ meal.sodium }} mg</span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 회원 정보 -->
          <section class="info-section">
            <div class="section-header">
              <h2 class="section-title">👤 회원 정보</h2>
            </div>
            
            <div class="card-container">
              <div class="member-grid">
                <div v-if="memberDetails.nickname" class="member-item">
                  <span class="member-label">닉네임</span>
                  <span class="member-value">{{ memberDetails.nickname }}</span>
                </div>
                <div v-if="memberDetails.goal" class="member-item">
                  <span class="member-label">목표</span>
                  <span class="member-value">{{ memberDetails.goal }}</span>
                </div>
                <div v-if="memberDetails.height" class="member-item">
                  <span class="member-label">키</span>
                  <span class="member-value">{{ memberDetails.height }} cm</span>
                </div>
                <div v-if="memberDetails.weight" class="member-item">
                  <span class="member-label">몸무게</span>
                  <span class="member-value">{{ memberDetails.weight }} kg</span>
                </div>
                <div v-if="memberDetails.bodyFatPercentage" class="member-item">
                  <span class="member-label">체지방률</span>
                  <span class="member-value">{{ memberDetails.bodyFatPercentage }}%</span>
                </div>
                <div v-if="memberDetails.bmi" class="member-item">
                  <span class="member-label">BMI</span>
                  <span class="member-value">{{ memberDetails.bmi }}</span>
                </div>
              </div>
            </div>
          </section>

          <!-- 알레르기 정보 -->
          <section v-if="allergies.length > 0" class="info-section">
            <div class="section-header">
              <h2 class="section-title">⚠️ 알레르기 정보</h2>
            </div>
            
            <div class="card-container">
              <div class="allergy-list">
                <div v-for="allergy in allergies" :key="allergy.name" class="allergy-item">
                  <span class="allergy-icon">🚫</span>
                  <span class="allergy-name">{{ allergy.name }}</span>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- 오른쪽 열: 피드백 작성 -->
        <div class="feedback-column">
          <section class="feedback-section">
            <div class="section-header">
              <h2 class="section-title">📝 {{ feedbackId ? '피드백 수정' : '피드백 작성' }}</h2>
            </div>
            
            <div class="card-container feedback-card">
              <div class="feedback-form">
                <div class="textarea-wrapper">
                  <label class="form-label">피드백 내용</label>
                  <textarea 
                    v-model="feedbackContent" 
                    rows="8" 
                    class="feedback-textarea"
                    placeholder="회원에게 전달할 피드백 내용을 입력하세요.&#10;&#10;• 식단의 좋은 점&#10;• 개선이 필요한 부분&#10;• 추천 사항 등을 작성해주세요."
                  ></textarea>
                </div>

                <div class="score-wrapper">
                  <label class="form-label">트레이너 점수</label>
                  <div class="score-selector">
                    <select v-model.number="trainerScore" class="score-select">
                      <option disabled value="0">점수 선택</option>
                      <option v-for="n in 10" :key="n" :value="n">{{ n }}점</option>
                    </select>
                    <div class="score-display">
                      <span class="score-text">{{ trainerScore }}/10</span>
                      <div class="score-bar">
                        <div class="score-fill" :style="{ width: (trainerScore * 10) + '%' }"></div>
                      </div>
                    </div>
                  </div>
                </div>

                <button @click="submitFeedback" class="submit-button" :disabled="!feedbackContent || trainerScore === 0">
                  <span class="button-icon">{{ feedbackId ? '✏️' : '📝' }}</span>
                  <span>{{ feedbackId ? '수정 완료' : '피드백 제출' }}</span>
                </button>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'
const toast = useToast()

const route = useRoute()
const router = useRouter()

const mealId = route.params.mealId
const feedbackId = route.params.feedbackId || null

const loading = ref(true)
const meal = ref({})
const memberDetails = ref({})
const allergies = ref([])
const feedbackContent = ref('')
const trainerScore = ref(0)

const getImageUrl = (path) => path ? `http://localhost:8080${path}` : '/images/default-meal.jpg'

const formatDate = (datetime) => {
  const date = new Date(datetime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}.${month}.${day} ${hours}:${minutes}`
}

const getMealTypeText = (type) => {
  const typeMap = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
    extra: '간식'
  }
  return typeMap[type] || type
}

const getMealTypeEmoji = (type) => {
  const emojiMap = {
    breakfast: '☀️',
    lunch: '🍽️',
    dinner: '🌙',
    extra: '🍩'
  }
  return emojiMap[type] || '🍽️'
}

const getMealTypeBadgeClass = (type) => {
  const classMap = {
    breakfast: 'breakfast',
    lunch: 'lunch',
    dinner: 'dinner',
    extra: 'extra'
  }
  return classMap[type] || 'lunch'
}

const submitFeedback = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const payload = {
      content: feedbackContent.value,
      trainerScore: trainerScore.value
    }

    if (feedbackId) {
      await axios.put(`/api/trainer/feedback/${feedbackId}`, payload, {
        headers: { Authorization: `Bearer ${token}` }
      })
      toast.success('피드백이 수정되었습니다.')
    } else {
      await axios.post(`/api/trainer/feedback/meal/${mealId}`, payload, {
        headers: { Authorization: `Bearer ${token}` }
      })
      toast.success('피드백이 등록되었습니다!')
    }

    router.push('/trainer')
  } catch (e) {
    console.error('피드백 처리 실패', e)
    toast.error('피드백 요청에 실패했습니다.')
  }
}

onMounted(async () => {
  console.log('🚨 feedbackId:', feedbackId)
  try {
    const token = localStorage.getItem('accessToken')

    if (feedbackId) {
      const fbRes = await axios.get(`/api/trainer/feedback/${feedbackId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const fb = fbRes.data.data
      feedbackContent.value = fb.content
      trainerScore.value = fb.trainerScore

      const res = await axios.get(`/api/trainer/meals/${fb.mealId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = res.data.data
      meal.value = data.meal
      memberDetails.value = data.memberDetails
      allergies.value = data.allergies

    } else if (mealId) {
      const res = await axios.get(`/api/trainer/meals/${mealId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = res.data.data
      meal.value = data.meal
      memberDetails.value = data.memberDetails
      allergies.value = data.allergies
    } else {
      throw new Error('mealId 또는 feedbackId가 유효하지 않습니다.')
    }

  } catch (e) {
    console.error('데이터 로딩 실패', e)
    toast.error('잘못된 요청입니다. \n 페이지를 다시 확인해주세요. ⚠️')
    router.push('/trainer')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 페이지 전체 배경 */
.feedback-form-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

.feedback-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.page-title {
  font-size: 2.25rem;
  font-weight: 700;
  color: #374151;
  margin: 0;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 로딩 상태 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

.loading-spinner {
  font-size: 3rem;
  margin-bottom: 1rem;
  animation: pulse 2s infinite;
}

.loading-text {
  font-size: 1.1rem;
  color: #6b7280;
  font-weight: 500;
}

/* 콘텐츠 레이아웃 */
.content-layout {
  display: grid;
  grid-template-columns: 7fr 5fr;
  gap: 2rem;
  align-items: start;
}

/* 정보 열 */
.info-column {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 피드백 열 */
.feedback-column {
  position: sticky;
  top: 2rem;
}

/* 섹션 공통 */
.info-section, .feedback-section {
  animation: fadeInUp 0.6s ease-out;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 카드 컨테이너 */
.card-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
}

.card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.12);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 식사 타입 뱃지 */
.meal-type-badge {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.meal-type-badge.breakfast {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #d97706;
  border: 1px solid rgba(251, 191, 36, 0.3);
}

.meal-type-badge.lunch {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #2563eb;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.meal-type-badge.dinner {
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #4338ca;
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.meal-type-badge.extra {
  background: linear-gradient(135deg, #fce7f3, #fbcfe8);
  color: #be185d;
  border: 1px solid rgba(236, 72, 153, 0.3);
}

/* 식단 이미지 섹션 */
.meal-image-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.meal-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.meal-basic-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 600;
}

.info-value {
  font-size: 0.95rem;
  color: #374151;
  font-weight: 700;
}

.ai-score {
  color: #f59e0b;
  font-size: 1.1rem;
}

/* 정보 라벨 헤더 */
.info-label-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.label-icon {
  font-size: 1rem;
}

.label-text {
  font-size: 0.9rem;
  font-weight: 600;
  color: #6b7280;
}

/* 음식 정보 */
.food-info {
  margin-bottom: 1.5rem;
}

.food-text {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 1rem;
  font-size: 0.95rem;
  color: #374151;
  line-height: 1.5;
  margin: 0;
}

/* AI 피드백 섹션 */
.ai-feedback-section {
  margin-bottom: 1.5rem;
}

.ai-feedback-text {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(37, 99, 235, 0.05));
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  padding: 1rem;
  font-size: 0.95rem;
  color: #374151;
  line-height: 1.5;
  margin: 0;
}

/* 영양 정보 */
.nutrition-section {
  margin-top: 1.5rem;
}

.nutrition-title {
  font-size: 1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nutrition-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem;
}

.nutrition-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 0.75rem;
  text-align: center;
}

.nutrition-item.calories {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.05));
  border-color: rgba(245, 158, 11, 0.2);
}

.nutrition-label {
  display: block;
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.nutrition-value {
  display: block;
  font-size: 0.9rem;
  color: #374151;
  font-weight: 700;
}

/* 회원 정보 */
.member-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.member-item {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.member-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 600;
}

.member-value {
  font-size: 0.9rem;
  color: #374151;
  font-weight: 700;
}

/* 알레르기 정보 */
.allergy-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.allergy-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.05));
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 12px;
  padding: 0.5rem 0.75rem;
}

.allergy-icon {
  font-size: 0.9rem;
}

.allergy-name {
  font-size: 0.85rem;
  color: #dc2626;
  font-weight: 600;
}

/* 피드백 폼 */
.feedback-card {
  position: sticky;
  top: 2rem;
}

.feedback-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.textarea-wrapper {
  display: flex;
  flex-direction: column;
}

.feedback-textarea {
  width: 100%;
  min-height: 200px;
  padding: 1rem;
  border: 2px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  font-size: 0.9rem;
  line-height: 1.6;
  resize: vertical;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
}

.feedback-textarea:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.score-wrapper {
  display: flex;
  flex-direction: column;
}

.score-selector {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.score-select {
  padding: 0.75rem;
  border: 2px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
  min-width: 120px;
}

.score-select:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.score-display {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.score-text {
  font-size: 1.1rem;
  font-weight: 700;
  color: #f59e0b;
  min-width: 3rem;
}

.score-bar {
  flex: 1;
  height: 8px;
  background: rgba(229, 231, 235, 0.8);
  border-radius: 4px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b, #d97706);
  border-radius: 4px;
  transition: width 0.3s ease;
}

/* 제출 버튼 */
.submit-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 1rem 1.5rem;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.submit-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

.submit-button:disabled {
  background: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.button-icon {
  font-size: 1rem;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .feedback-column {
    position: static;
  }
  
  .feedback-card {
    position: static;
  }
}

@media (max-width: 768px) {
  .feedback-container {
    padding: 1rem;
  }
  
  .page-title {
    font-size: 1.75rem;
  }
  
  .meal-image-section {
    grid-template-columns: 1fr;
  }
  
  .nutrition-grid,
  .member-grid {
    grid-template-columns: 1fr;
  }
  
  .score-selector {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }
}

/* 애니메이션 */
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

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 섹션별 애니메이션 지연 */
.info-section:nth-child(1) { animation-delay: 0.1s; }
.info-section:nth-child(2) { animation-delay: 0.2s; }
.info-section:nth-child(3) { animation-delay: 0.3s; }
.feedback-section { animation-delay: 0.4s; }
</style>