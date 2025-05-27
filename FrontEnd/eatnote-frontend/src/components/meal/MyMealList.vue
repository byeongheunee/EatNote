<!-- src/components/meal/MyMealList.vue -->
<template>
  <div class="my-meal-list">
    <!-- 헤더 섹션 -->
    <div class="header-section">
      <div class="header-content">
        <h2 class="section-title">나의 식단 기록</h2>
        <button @click="router.push('/meal/upload')" class="upload-button">
          <span class="button-icon">📷</span>
          <span>새 식단 업로드</span>
        </button>
      </div>
      <p class="section-subtitle">내가 기록한 식단들을 확인하고 관리해보세요</p>
    </div>

    <!-- 필터 섹션 -->
    <div class="filter-section">
      <div class="filter-buttons">
        <button v-for="option in filterOptions" :key="option.label" @click="changeFilter(option.code)" :class="[
          'filter-button',
          selectedFilter === option.code ? 'active' : ''
        ]">
          <span class="filter-icon">{{ option.icon }}</span>
          <span>{{ option.label }}</span>
        </button>
      </div>
    </div>

    <!-- 식단 그리드 -->
    <div class="meals-grid" v-if="visibleMeals.length > 0">
      <div v-for="meal in visibleMeals" :key="meal.mealId" class="meal-card" @click="goToDetail(meal.mealId)">
        <div class="meal-image-container">
          <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="meal-image" />
          <div class="meal-type-badge">{{ getMealTypeEmoji(meal.mealType) }}</div>
        </div>

        <div class="meal-content">
          <h3 class="meal-title">{{ meal.detectedFoods }}</h3>
          <p class="meal-date">{{ formatDate(meal.mealTime) }}</p>
          <div class="meal-info">
            <div class="calorie-info">
              <span class="calorie-icon">🔥</span>
              <span class="calorie-text">{{ meal.totalCalories }}kcal</span>
            </div>
          </div>

          <div class="meal-actions">
            <div class="feedback-count">
              <span class="feedback-icon"> 피드백 💬</span>
              <span>{{ meal.feedbackCount }}</span>
            </div>
            <LikeButton contentType="MEAL" :contentId="meal.mealId" :likeCount="meal.likeCount"
              :myReaction="meal.myReaction || null" @onUpdated="loadMyMeals" />
          </div>
        </div>
      </div>
    </div>

    <!-- 빈 상태 -->
    <div v-else class="empty-state">
      <div class="empty-icon">🍽️</div>
      <h3 class="empty-title">아직 등록된 식단이 없어요</h3>
      <p class="empty-description">첫 번째 식단을 업로드해보세요!</p>
      <button @click="router.push('/meal/upload')" class="empty-upload-button">
        <span class="button-icon">📷</span>
        <span>식단 업로드하기</span>
      </button>
    </div>

    <!-- 더보기 버튼 -->
    <div class="load-more-section" v-if="filteredMeals.length > visibleCount">
      <button @click="visibleCount += 9" class="load-more-button">
        <span>더 많은 식단 보기</span>
        <span class="load-more-icon">📋</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LikeButton from '@/components/LikeButton.vue'

const meals = ref([])
const selectedFilter = ref('all')
const router = useRouter()
const visibleCount = ref(9)

const filterOptions = [
  { label: '모든 식단', code: 'all', icon: '🍽️' },
  { label: '오늘의 식단', code: 'today', icon: '📅' },
  { label: '이번 주 식단', code: 'week', icon: '📊' },
]

const changeFilter = (code) => {
  selectedFilter.value = code
  visibleCount.value = 9
}

const visibleMeals = computed(() => filteredMeals.value.slice(0, visibleCount.value))
const formatDate = (datetime) => new Date(datetime).toLocaleDateString('ko-KR')
const getImageUrl = (path) => `http://localhost:8080${path}`

// 식사 타입에 따른 이모티콘
const getMealTypeEmoji = (mealType) => {
  switch (mealType) {
    case 'breakfast': return '☀️' // 아침
    case 'lunch': return '🍽️' // 점심  
    case 'dinner': return '🌙' // 저녁
    case 'extra': return '🍩' // 간식
    default: return '🍽️'
  }
}

const mealTypeKor = (type) => {
  switch (type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}

const loadMyMeals = async () => {
  const auth = useAuthStore()
  const userId = auth.user?.userId
  if (!userId) return

  try {
    const res = await axios.get(`/api/users/user/${userId}/meals`)
    meals.value = res.data.data
  } catch (e) {
    console.error('🍽️ 식단 목록 불러오기 실패:', e)
  }
}

const filteredMeals = computed(() => {
  const today = resetTime(new Date())
  const dayOfWeek = today.getDay() === 0 ? 7 : today.getDay()
  const startOfWeek = new Date(today)
  startOfWeek.setDate(today.getDate() - (dayOfWeek - 1))

  const endOfWeek = new Date(startOfWeek)
  endOfWeek.setDate(startOfWeek.getDate() + 6)

  return meals.value.filter(meal => {
    const mealDate = resetTime(new Date(meal.mealTime))

    switch (selectedFilter.value) {
      case 'today':
        return mealDate.getTime() === today.getTime()
      case 'week':
        return mealDate >= startOfWeek && mealDate <= endOfWeek
      default:
        return true
    }
  })
})

function resetTime(date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}

const goToDetail = (mealId) => router.push(`/meal/${mealId}`)

onMounted(loadMyMeals)
</script>

<style scoped>
/* 페이지 전체 */
.my-meal-list {
  width: 100%;
  animation: fadeInUp 0.6s ease-out;
}

/* 헤더 섹션 */
.header-section {
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2D1810;
  margin: 0;
}

.section-subtitle {
  color: #5D4037;
  font-size: 15px;
  margin: 0;
  line-height: 1.5;
}

.upload-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #7fc7ae, #5b9c88);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  white-space: nowrap;
}

.upload-button:hover {
  background: linear-gradient(135deg, #11946a, #0f7a5c);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

.button-icon {
  font-size: 16px;
}

/* 필터 섹션 */
.filter-section {
  margin-bottom: 32px;
}

.filter-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  font-weight: 600;
  font-size: 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-button:hover {
  border-color: #f59e0b;
  background: white;
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.filter-button.active {
  background: linear-gradient(135deg, #f3ae36, #ee9024);
  border-color: #f59e0b;
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  transform: translateY(-1px);
}

.filter-icon {
  font-size: 16px;
}

/* 식단 그리드 */
.meals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.meal-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.meal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.2);
  border-color: #f59e0b;
}

.meal-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.meal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.meal-card:hover .meal-image {
  transform: scale(1.05);
}

.meal-type-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  font-size: 1rem;
}

.meal-card:hover .meal-type-badge {
  transform: scale(1.1);
}

.meal-content {
  padding: 20px;
}

.meal-title {
  font-size: 18px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.meal-date {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 12px 0;
}

.meal-info {
  margin-bottom: 16px;
}

.calorie-info {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(245, 158, 11, 0.1);
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.calorie-icon {
  font-size: 16px;
}

.calorie-text {
  font-weight: 600;
  color: #d97706;
  font-size: 14px;
}

.meal-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.feedback-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
}

.feedback-icon {
  font-size: 14px;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 48px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  border: 2px dashed rgba(245, 158, 11, 0.3);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.7;
}

.empty-title {
  font-size: 20px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-description {
  color: #6b7280;
  margin: 0 0 24px 0;
}

.empty-upload-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 14px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.empty-upload-button:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

/* 더보기 버튼 */
.load-more-section {
  text-align: center;
  padding-top: 16px;
}

.load-more-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-button:hover {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border-color: #f59e0b;
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.3);
}

.load-more-icon {
  font-size: 16px;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .section-title {
    font-size: 20px;
  }

  .meals-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .filter-buttons {
    justify-content: center;
  }

  .filter-button {
    flex: 1;
    justify-content: center;
  }
}

@media (max-width: 640px) {
  .upload-button {
    padding: 10px 16px;
    font-size: 14px;
  }

  .filter-button {
    padding: 10px 16px;
    font-size: 14px;
  }

  .meal-content {
    padding: 16px;
  }

  .empty-state {
    padding: 32px 16px;
  }

  .empty-icon {
    font-size: 48px;
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
</style>