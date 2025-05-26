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
          <div class="meal-type-badge">{{ mealTypeKor(meal.mealType) }}</div>
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
.my-meal-list {
  width: 100%;
  animation: fadeInUp 0.6s ease-out;
}

/* 헤더 섹션 */
.header-section {
  margin-bottom: 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  margin: 0;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-subtitle {
  color: #6b7280;
  font-size: 0.95rem;
  margin: 0;
}

.upload-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 10px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  white-space: nowrap;
}

.upload-button:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

.button-icon {
  font-size: 1rem;
}

/* 필터 섹션 */
.filter-section {
  margin-bottom: 2rem;
}

.filter-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  border: 2px solid rgba(229, 231, 235, 0.8);
  background: rgba(255, 255, 255, 0.9);
  color: #6b7280;
  font-weight: 600;
  font-size: 0.9rem;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(5px);
}

.filter-button:hover {
  border-color: rgba(245, 158, 11, 0.5);
  background: rgba(255, 255, 255, 1);
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.filter-button.active {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-color: #f59e0b;
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  transform: translateY(-1px);
}

.filter-icon {
  font-size: 1rem;
}

/* 식단 그리드 */
.meals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.meal-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.meal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.3);
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
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.9), rgba(5, 150, 105, 0.9));
  color: white;
  padding: 0.375rem 0.75rem;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 600;
  backdrop-filter: blur(5px);
}

.meal-content {
  padding: 1.25rem;
}

.meal-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
  line-height: 1.4;
}

.meal-date {
  color: #6b7280;
  font-size: 0.85rem;
  margin: 0 0 0.75rem 0;
}

.meal-info {
  margin-bottom: 1rem;
}

.calorie-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.1));
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.calorie-icon {
  font-size: 1rem;
}

.calorie-text {
  font-weight: 600;
  color: #d97706;
  font-size: 0.9rem;
}

.meal-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(229, 231, 235, 0.5);
}

.feedback-count {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  color: #6b7280;
  font-size: 0.85rem;
  font-weight: 500;
}

.feedback-icon {
  font-size: 0.9rem;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 2px dashed rgba(245, 158, 11, 0.3);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.7;
}

.empty-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.empty-description {
  color: #6b7280;
  margin: 0 0 1.5rem 0;
}

.empty-upload-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.875rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.empty-upload-button:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

/* 더보기 버튼 */
.load-more-section {
  text-align: center;
  padding-top: 1rem;
}

.load-more-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  color: #6b7280;
  border: 2px solid rgba(229, 231, 235, 0.8);
  border-radius: 12px;
  padding: 0.875rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-button:hover {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border-color: #f59e0b;
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.3);
}

.load-more-icon {
  font-size: 1rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .meals-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
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
    padding: 0.625rem 1rem;
    font-size: 0.85rem;
  }

  .filter-button {
    padding: 0.625rem 1rem;
    font-size: 0.85rem;
  }

  .meal-content {
    padding: 1rem;
  }

  .empty-state {
    padding: 2rem 1rem;
  }

  .empty-icon {
    font-size: 3rem;
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