<template>
  <div class="p-4 w-full min-h-screen bg-gray-50">
    <h2 class="text-3xl font-bold mb-6 text-center text-gray-800">📅 피드백 달력 통계</h2>

    <!-- 달력과 상세 정보를 나란히 배치 -->
    <div class="flex flex-col lg:flex-row gap-8 max-w-7xl mx-auto">
      <!-- 달력 컨테이너 -->
      <div class="flex-1 bg-white rounded-lg shadow-lg overflow-hidden">
        <VCalendar
          class="w-full h-full calendar-full"
          mode="single"
          @day-click="onDayClick"
          :attributes="calendarAttributes"
           :locale="'ko'"
          :masks="{ weekdays: 'WWW' }"
          v-model="selectedCalendarDate"
        >
          <template #day-content="{ day }">
            <div class="vc-day-box-circle" @click="handleDayClick(day, $event)">
              <div class="date-number">{{ day.day }}</div>
              <div v-if="getDayStats(day)" class="circle-indicator" :class="getFeedbackStatusClass(getDayStats(day))"></div>
            </div>
          </template>
        </VCalendar>
      </div>

      <!-- 선택된 날짜 상세 정보 - 달력 옆으로 이동 -->
      <div class="lg:w-96 w-full">
        <div v-if="selectedDate" class="p-6 border rounded-lg bg-white shadow-lg sticky top-4">
          <h3 class="font-bold text-2xl mb-4 text-gray-800">📌 {{ selectedDate }} 통계</h3>
          <div class="grid grid-cols-1 gap-4 mb-4">
            <div class="bg-blue-50 p-4 rounded-lg">
              <p class="text-lg"><span class="font-semibold text-blue-600">총 요청 수:</span> {{ calendarStats[selectedDate]?.total ?? 0 }}개</p>
            </div>
            <div class="bg-green-50 p-4 rounded-lg">
              <p class="text-lg"><span class="font-semibold text-green-600">완료 수:</span> {{ calendarStats[selectedDate]?.written ?? 0 }}개</p>
            </div>
          </div>

          <!-- 진행률 바 -->
          <div class="mb-4">
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm font-medium text-gray-700">완료율</span>
              <span class="text-sm font-medium text-gray-700">
                {{ getCompletionPercentage(selectedDate) }}%
              </span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-3">
              <div
                class="h-3 rounded-full transition-all duration-300"
                :class="getProgressBarClass(selectedDate)"
                :style="{ width: getCompletionPercentage(selectedDate) + '%' }"
              ></div>
            </div>
          </div>
        </div>

        <!-- 범례 추가 -->
        <div class="mt-4 p-4 bg-white rounded-lg shadow-lg">
          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 bg-green-500 rounded-full"></div>
              <span class="text-sm text-gray-700">모든 피드백 완료</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 bg-orange-500 rounded-full"></div>
              <span class="text-sm text-gray-700">일부 피드백 완료</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 bg-red-500 rounded-full"></div>
              <span class="text-sm text-gray-700">피드백 미완료</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-4 h-4 bg-gray-400 rounded-full"></div>
              <span class="text-sm text-gray-700">요청 없음</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const handleDayClick = (day, event) => {
  const ymd = formatDayId(day)
  const stats = calendarStats.value[ymd]
  if (!stats || stats.total === 0) return // 클릭 막기

  selectedDate.value = ymd
}


const calendarStats = ref({})
const selectedDate = ref(null)
const selectedCalendarDate = ref(null)
const router = useRouter()

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

onMounted(fetchStats)

const calendarAttributes = computed(() => [
  {
    key: 'feedback-stats',
    dates: Object.keys(calendarStats.value),
    customData: calendarStats.value
  }
])

const getDayStats = (day) => {
  const formats = [
    day.id,
    formatDayId(day),
    `${day.year}-${String(day.month).padStart(2, '0')}-${String(day.day).padStart(2, '0')}`
  ]
  for (const format of formats) {
    if (calendarStats.value[format]) {
      return calendarStats.value[format]
    }
  }
  return null
}

const formatDayId = (day) => {
  return `${day.year}-${String(day.month).padStart(2, '0')}-${String(day.day).padStart(2, '0')}`
}

const getFeedbackStatusClass = (stats) => {
  if (!stats || stats.total === 0) return 'bg-gray-400'
  if (stats.written === 0) return 'bg-red-500'
  if (stats.written === stats.total) return 'bg-green-500'
  return 'bg-orange-500'
}

const getCompletionPercentage = (date) => {
  const stats = calendarStats.value[date]
  if (!stats || stats.total === 0) return 0
  return Math.round((stats.written / stats.total) * 100)
}

const getProgressBarClass = (date) => {
  const percentage = getCompletionPercentage(date)
  if (percentage === 100) return 'bg-green-500'
  if (percentage === 0) return 'bg-red-500'
  return 'bg-orange-500'
}

const onDayClick = async ({ date }) => {
  const d = new Date(date)
  const ymd = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  selectedDate.value = ymd
}
watch(selectedCalendarDate, async (newDate) => {
  const ymd = formatDayId(newDate)
  const stats = calendarStats.value[ymd]
  if (!stats || stats.written !== stats.total) {
    await fetchStats()
  }
})

</script>

<style scoped>

.calendar-container {
  height: 500px; /* 또는 정확히 원하는 높이 */
  max-height: 500px;
  display: flex;
  flex-direction: column;
}

.calendar-full {
  flex-grow: 1;
  height: 100%;
  overflow: hidden;
}

:deep(.vc-weeks) {
  flex-grow: 1;
  height: 100%;
}

.vc-day-box-circle {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  position: relative;
  cursor: pointer;
}

.date-number {
  font-weight: bold;
  font-size: 1rem;
  margin-bottom: 4px;
  color: #374151;
}

.circle-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

/* VCalendar 내부 작은 날짜 텍스트 숨기기 */
:deep(.vc-day .vc-day-layer) {
  display: none;
}

/* 선택된 날짜 스타일 개선 */
:deep(.vc-day.is-selected .vc-day-box-circle) {
  background-color: rgba(59, 130, 246, 0.1);
  border-radius: 8px;
}

/* 호버 효과 */
.vc-day-box-circle:hover {
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

/* 반응형 조정 */
@media (max-width: 1024px) {
  .lg\:flex-row {
    flex-direction: column;
  }

  .lg\:w-96 {
    width: 100%;
  }
}

</style>