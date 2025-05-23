<template>
  <div class="p-6 max-w-6xl mx-auto">
    <h2 class="text-2xl font-bold mb-4">📅 피드백 달력 통계</h2>

    <VCalendar mode="single" is-expanded @day-click="onDayClick" :attributes="calendarAttributes" :locale="'ko'"
      class="rounded border shadow">
      <template #day-content="{ day }">
        <div class="vc-day-box">
          <div class="date">{{ day.day }}</div>
          <div v-if="calendarStats[day.id]" class="indicator"
            :class="calendarStats[day.id].written === calendarStats[day.id].total ? 'green' : 'red'">
            {{ calendarStats[day.id].written }}/{{ calendarStats[day.id].total }}
          </div>
        </div>
      </template>
    </VCalendar>

    <div v-if="selectedDate" class="mt-6 p-4 border rounded bg-gray-50">
      <h3 class="font-semibold text-lg">📌 {{ selectedDate }} 통계</h3>
      <p>요청 수: {{ calendarStats[selectedDate]?.total ?? 0 }}</p>
      <p>완료 수: {{ calendarStats[selectedDate]?.written ?? 0 }}</p>
    </div>

    <div v-if="mealList.length > 0" class="mt-4">
      <h4 class="text-lg font-semibold mb-2">🍱 {{ selectedDate }} 식단 목록</h4>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="meal in mealList" :key="meal.mealId"
          class="border rounded p-4 shadow hover:bg-gray-50 cursor-pointer" @click="goToFeedback(meal.mealId)">
          <img :src="getImageUrl(meal.imageUrl)" class="w-full h-40 object-cover rounded mb-2" />
          <p><strong>음식:</strong> {{ meal.detectedFoods }}</p>
          <p><strong>칼로리:</strong> {{ meal.totalCalories }} kcal</p>
          <p><strong>종류:</strong> {{ meal.mealType }}</p>
          <p><strong>AI 점수:</strong> {{ meal.autoScore }}</p>
          <p><strong>🧠 AI 피드백:</strong><br />{{ meal.aiFeedback }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const calendarStats = ref({})
const selectedDate = ref(null)
const mealList = ref([])
const router = useRouter()

const today = new Date()
const currentMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`

const fetchStats = async () => {
  try {
    const auth = useAuthStore()
    const token = auth.accessToken
    console.log('현재 토큰:', token)

    const res = await axios.get('/api/trainer/feedback/statistics', {
      params: { month: currentMonth } // 헤더 생략
    })
    console.log(res)
    console.log(res.data)
    console.log(res.data.data)
    

    const map = {}
    res.data.data.forEach(item => {
      map[item.date] = {
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

const getImageUrl = (path) => (path ? `http://localhost:8080${path}` : '/images/default-meal.jpg')

const onDayClick = async ({ date }) => {
  console.log('✅ 날짜 클릭됨:', date)
  const d = new Date(date)
  const ymd = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  selectedDate.value = ymd
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/trainer/pending', {
      headers: { Authorization: `Bearer ${token}` }
    })
    mealList.value = (res.data.data || []).filter(meal => meal.mealTime?.startsWith(ymd))
  } catch (e) {
    console.error('식단 조회 실패', e)
    mealList.value = []
  }
}

const goToFeedback = (mealId) => {
  router.push(`/trainer/feedback/${mealId}`)
}
</script>

<style scoped>
.vc-container {
  min-width: 600px;
}

.vc-day-content {
  height: 85px !important;
  padding: 0 !important;
}

.vc-day-box {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
}

.vc-day-box .date {
  font-weight: bold;
  font-size: 1rem;
  line-height: 1.2;
}

.indicator {
  font-size: 0.7rem;
  padding: 2px 6px;
  border-radius: 10px;
  color: white;
  min-height: 18px;
  pointer-events: none;
}

.indicator.green {
  background-color: #38a169;
}

.indicator.red {
  background-color: #e53e3e;
}
</style>
