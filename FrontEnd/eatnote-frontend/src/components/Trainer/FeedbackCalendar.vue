<template>
  <div class="feedback-calendar-container">
    <div class="calendar-layout">
      <!-- 1열: 캘린더 + 범례 -->
      <div class="calendar-column">
        <!-- FullCalendar -->
        <div class="calendar-wrapper">
          <FullCalendar ref="calendar" :options="calendarOptions" class="custom-fullcalendar" />
        </div>

        <!-- 범례 -->
        <div class="legend-card">
          <div class="legend-item">
            <div class="legend-dot complete"></div>
            <span class="legend-text">완료</span>
          </div>
          <div class="legend-item">
            <div class="legend-dot partial"></div>
            <span class="legend-text">일부완료</span>
          </div>
          <div class="legend-item">
            <div class="legend-dot pending"></div>
            <span class="legend-text">미완료</span>
          </div>
          <div class="legend-item">
            <div class="legend-dot empty"></div>
            <span class="legend-text">요청없음</span>
          </div>
        </div>
      </div>

      <!-- 2열: 통계 정보 -->
      <div class="stats-column">
        <!-- 선택된 날짜 정보 -->
        <div v-if="selectedDate" class="date-info-card">
          <h3 class="date-title">📌 {{ selectedDate }} 통계</h3>
          <div class="stats-grid">
            <div class="stat-item total">
              <div class="stat-number">{{ calendarStats[selectedDate]?.total ?? 0 }}</div>
              <div class="stat-label">총 요청</div>
            </div>
            <div class="stat-item completed">
              <div class="stat-number">{{ calendarStats[selectedDate]?.written ?? 0 }}</div>
              <div class="stat-label">완료</div>
            </div>
          </div>

          <!-- 진행률 바 -->
          <div class="progress-section">
            <div class="progress-header">
              <span class="progress-label">완료율</span>
              <span class="progress-percentage">{{ getCompletionPercentage(selectedDate) }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :class="getProgressBarClass(selectedDate)"
                :style="{ width: getCompletionPercentage(selectedDate) + '%' }"></div>
            </div>
          </div>

          <!-- 미완료 식단 목록 -->
          <div v-if="selectedDateMeals.length > 0" class="pending-meals-section">
            <h4 class="section-title">🍽️ 미완료 식단 ({{ selectedDateMeals.length }}개)</h4>
            <div class="meals-container">
              <div v-for="meal in selectedDateMeals" :key="meal.mealId" class="meal-card"
                @click="goToMeal(meal.mealId)">
                <div class="meal-image">
                  <img :src="getFullImageUrl(meal.imageUrl)" :alt="meal.detectedFoods" />
                </div>
                <div class="meal-info">
                  <div class="meal-header">
                    <span class="user-nickname">{{ meal.nickname }}</span>
                    <span class="meal-calories">{{ meal.totalCalories }}kcal</span>
                  </div>
                  <div class="meal-type">{{ getMealTypeText(meal.mealType) }}</div>
                  <div class="meal-foods">{{ meal.detectedFoods }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 날짜 선택 안내 -->
        <div v-else class="select-date-guide">
          <div class="guide-icon">📅</div>
          <p class="guide-text">날짜를 선택하면</p>
          <p class="guide-subtext">상세 통계를 확인할 수 있습니다</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const calendarStats = ref({})
const selectedDate = ref(null)
const calendar = ref(null)
const router = useRouter()
const pendingMeals = ref([])
const selectedDateMeals = ref([])

const today = new Date()
const currentMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`

const fetchStats = async () => {
  try {
    const auth = useAuthStore()
    const token = auth.accessToken

    const res = await axios.get('/api/trainer/feedback/statistics', {
      params: { month: currentMonth }
    })

    const map = {}
    res.data.data.forEach(item => {
      const dateKey = item.date
      map[dateKey] = {
        total: item.pendingCount,
        written: item.completedCount
      }
    })

    calendarStats.value = map
  } catch (e) {
    console.error('통계 불러오기 실패', e)
  }
}

const fetchPendingMeals = async () => {
  try {
    const auth = useAuthStore()
    const token = auth.accessToken

    const res = await axios.get('/api/trainer/pending', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    pendingMeals.value = res.data.data
  } catch (e) {
    console.error('미완료 식단 불러오기 실패', e)
  }
}

const filterMealsByDate = (date) => {
  if (!date || !pendingMeals.value.length) return []

  return pendingMeals.value.filter(meal => {
    // 로컬 시간대 기준으로 날짜 추출
    const mealDate = new Date(meal.mealTime)
    const year = mealDate.getFullYear()
    const month = String(mealDate.getMonth() + 1).padStart(2, '0')
    const day = String(mealDate.getDate()).padStart(2, '0')
    const localDateStr = `${year}-${month}-${day}`

    return localDateStr === date
  })
}

const getMealTypeText = (mealType) => {
  const types = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁'
  }
  return types[mealType] || mealType
}

const getStatusClass = (stats) => {
  if (!stats || stats.total === 0) return 'status-empty'
  if (stats.written === 0) return 'status-pending'
  if (stats.written === stats.total) return 'status-complete'
  return 'status-partial'
}

const getStatusColor = (stats) => {
  if (!stats || stats.total === 0) return '#9ca3af'
  if (stats.written === 0) return '#ef4444'
  if (stats.written === stats.total) return '#10b981'
  return '#f59e0b'
}

// 식단 카드 클릭 시 피드백 페이지로 이동
const goToMeal = (mealId) => {
  router.push(`/trainer/feedback/${mealId}`)
}

const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locale: 'ko',
  headerToolbar: {
    left: 'prev,next',
    center: 'title',
    right: ''
  },
  height: 'auto',
  aspectRatio: 1.8,
  fixedWeekCount: false,
  showNonCurrentDates: false,
  dayMaxEvents: false,
  events: Object.keys(calendarStats.value).map(date => {
    const stats = calendarStats.value[date]
    return {
      id: date,
      start: date,
      display: 'background',
      backgroundColor: getStatusColor(stats),
      borderColor: getStatusColor(stats),
      classNames: [getStatusClass(stats)]
    }
  }),
  dateClick: (info) => {
    const stats = calendarStats.value[info.dateStr]
    if (!stats || stats.total === 0) return
    selectedDate.value = info.dateStr
    selectedDateMeals.value = filterMealsByDate(info.dateStr)
  },
  dayCellContent: (info) => {
    const dateStr = info.date.toISOString().split('T')[0]
    const stats = calendarStats.value[dateStr]

    return {
      html: `
        <div class="custom-day-cell">
          <div class="day-number">${info.dayNumberText}</div>
        </div>
      `
    }
  }
}))

const getCompletionPercentage = (date) => {
  const stats = calendarStats.value[date]
  if (!stats || stats.total === 0) return 0
  return Math.round((stats.written / stats.total) * 100)
}

const getProgressBarClass = (date) => {
  const percentage = getCompletionPercentage(date)
  if (percentage === 100) return 'progress-complete'
  if (percentage === 0) return 'progress-pending'
  return 'progress-partial'
}

const getFullImageUrl = (relativePath) => {
  return `http://localhost:8080${relativePath}`
}

onMounted(() => {
  fetchStats()
  fetchPendingMeals()
})
</script>

<style scoped>
.feedback-calendar-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 2열 레이아웃 */
.calendar-layout {
  display: grid;
  grid-template-columns: 6fr 4fr;
  gap: 1.5rem;
  height: 100%;
  min-height: 0;
}

/* 1열: 캘린더 영역 */
.calendar-column {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-height: 0;
}

/* 2열: 통계 영역 - 높이를 왼쪽 캘린더+범례와 맞춤 */
.stats-column {
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: 100%;
}

/* 날짜 선택 안내 */
.select-date-guide {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.guide-icon {
  font-size: 3.5rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.guide-text {
  font-size: 1.3rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.guide-subtext {
  font-size: 1.1rem;
  color: #6b7280;
}

/* 선택된 날짜 정보 카드 - 높이를 100%로 설정 */
.date-info-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  padding: 1.25rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.date-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.stat-item {
  text-align: center;
  padding: 0.75rem;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-item.total {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.stat-item.completed {
  background: linear-gradient(135deg, #dcfce7, #bbf7d0);
  border: 1px solid rgba(34, 197, 94, 0.2);
}

.stat-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 1rem;
  font-weight: 500;
  color: #6b7280;
}

/* 진행률 섹션 */
.progress-section {
  margin-top: 1rem;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.progress-label {
  font-size: 1rem;
  font-weight: 600;
  color: #374151;
}

.progress-percentage {
  font-size: 1rem;
  font-weight: 600;
  color: #f59e0b;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background: rgba(229, 231, 235, 0.8);
  border-radius: 5px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 5px;
  transition: all 0.3s ease;
}

.progress-complete {
  background: linear-gradient(90deg, #10b981, #059669);
}

.progress-partial {
  background: linear-gradient(90deg, #f59e0b, #d97706);
}

.progress-pending {
  background: linear-gradient(90deg, #ef4444, #dc2626);
}

/* 미완료 식단 섹션 - flex를 사용해 남은 공간 차지 */
.pending-meals-section {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(229, 231, 235, 0.3);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.75rem;
}

.meals-container {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
  min-height: 0;
}

.meals-container::-webkit-scrollbar {
  width: 4px;
}

.meals-container::-webkit-scrollbar-track {
  background: transparent;
}

.meals-container::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.5);
  border-radius: 2px;
}

.meals-container::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.7);
}

/* 미완료 식단 카드 크기 축소 */
.meal-card {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  background: rgba(249, 250, 251, 0.7);
  border: 1px solid rgba(229, 231, 235, 0.3);
  border-radius: 10px;
  transition: all 0.2s ease;
  cursor: pointer;
  min-height: 70px;
}

.meal-card:hover {
  background: rgba(243, 244, 246, 0.8);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.meal-card:last-child {
  margin-bottom: 0;
}

/* 이미지 크기 축소 */
.meal-image {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f3f4f6;
}

.meal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.meal-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  min-width: 0;
  justify-content: center;
}

.meal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-nickname {
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
}

.meal-calories {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

.meal-type {
  font-size: 0.8rem;
  color: #f59e0b;
  font-weight: 600;
  margin-top: -0.1rem;
}

.meal-foods {
  font-size: 0.85rem;
  color: #374151;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.calendar-wrapper {
  flex: 1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  padding: 1rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 0;
}

.custom-fullcalendar {
  width: 100%;
  height: 100%;
  font-family: inherit;
}

/* FullCalendar 커스텀 스타일 */
:deep(.fc-header-toolbar) {
  margin-bottom: 1rem;
  padding: 0;
}

:deep(.fc-toolbar-title) {
  font-size: 1.2rem;
  font-weight: 700;
  color: #374151;
}

:deep(.fc-button) {
  background: linear-gradient(135deg, #f7a77f, #f79d70);
  border: none;
  color: white;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
  font-weight: 600;
  font-size: 0.9rem;
}

:deep(.fc-button:hover) {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-1px);
}

:deep(.fc-button:disabled) {
  background: #d1d5db;
  color: #9ca3af;
  transform: none;
}

:deep(.fc-col-header-cell) {
  background: rgba(249, 250, 251, 0.8);
  border: 1px solid rgba(229, 231, 235, 0.5);
  padding: 0.5rem;
}

:deep(.fc-col-header-cell-cushion) {
  color: #6b7280;
  font-weight: 600;
  font-size: 0.9rem;
}

:deep(.fc-daygrid-day) {
  border: 1px solid rgba(229, 231, 235, 0.3);
  cursor: pointer;
  transition: all 0.2s ease;
}

:deep(.fc-daygrid-day:hover) {
  background: rgba(245, 158, 11, 0.05);
}

:deep(.fc-daygrid-day.fc-day-today) {
  background: rgba(245, 158, 11, 0.1);
  border-color: #f59e0b;
}

/* 커스텀 날짜 셀 */
:deep(.custom-day-cell) {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0.25rem;
}

:deep(.custom-day-cell .day-number) {
  font-weight: 600;
  font-size: 1rem;
  color: #374151;
  margin-bottom: 0.25rem;
  z-index: 2;
  position: relative;
}

:deep(.custom-day-cell .stats-indicator) {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  z-index: 2;
  position: relative;
}

:deep(.stats-indicator.status-complete) {
  background: linear-gradient(135deg, #10b981, #059669);
}

:deep(.stats-indicator.status-partial) {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

:deep(.stats-indicator.status-pending) {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

:deep(.stats-indicator.status-empty) {
  background: #9ca3af;
}

/* 범례 카드 */
.legend-card {
  display: flex;
  justify-content: space-between;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.legend-dot.complete {
  background: linear-gradient(135deg, #10b981, #059669);
}

.legend-dot.partial {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.legend-dot.pending {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.legend-dot.empty {
  background: #9ca3af;
}

.legend-text {
  font-size: 0.85rem;
  font-weight: 500;
  color: #6b7280;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .calendar-layout {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .stats-column {
    order: -1;
  }

  .meal-image {
    width: 55px;
    height: 55px;
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .legend-card {
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  :deep(.custom-day-cell .day-number) {
    font-size: 0.9rem;
  }

  :deep(.custom-day-cell .stats-indicator) {
    width: 8px;
    height: 8px;
  }

  .meal-card {
    gap: 0.5rem;
    padding: 0.6rem;
    min-height: 60px;
  }

  .meal-image {
    width: 50px;
    height: 50px;
  }

  .date-title {
    font-size: 1.1rem;
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .guide-text {
    font-size: 1.1rem;
  }

  .guide-subtext {
    font-size: 0.95rem;
  }
}
</style>