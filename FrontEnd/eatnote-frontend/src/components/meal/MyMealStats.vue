<template>
  <div class="p-6">
    <h2 class="text-2xl font-bold mb-4">나의 식단 통계</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else>
      <!-- 주간 통계 -->
      <div class="mb-6">
        <h3 class="text-xl font-semibold mb-2">📊 주간 요약</h3>
        <p><strong>주차:</strong> {{ weekly.week }}</p>
        <p><strong>평균 자동 점수:</strong> {{ weekly.autoScore?.toFixed(1) ?? '-' }}</p>
        <p><strong>총 섭취 칼로리:</strong> {{ weekly.totalCalories }} kcal</p>
        <p><strong>총 섭취 탄수화물:</strong> {{ weekly.totalCarbohydrates }} g</p>
        <p><strong>총 섭취 단백질:</strong> {{ weekly.totalProtein }} g</p>
        <p><strong>총 섭취 지방:</strong> {{ weekly.totalFat }} g</p>
      </div>

      <!-- AI 피드백 -->
      <div class="mb-6">
        <h3 class="text-xl font-semibold mb-2">🧠 AI 피드백</h3>
        <p><strong>주의:</strong> {{ aiFeedback.warning }}</p>
        <p><strong>텍:</strong> {{ aiFeedback.tip }}</p>
        <div v-if="aiFeedback.recommendedFoods?.length">
          <h4 class="font-medium mt-2">추천 식품:</h4>
          <ul class="list-disc list-inside">
            <li v-for="(food, i) in aiFeedback.recommendedFoods" :key="i">
              {{ food.name }} - {{ food.reason }}
            </li>
          </ul>
        </div>
      </div>

      <!-- 일간 통계 -->
      <div class="mb-6">
        <h3 class="text-xl font-semibold mb-2">🗓️ 일자별 자동 점수</h3>
        <canvas ref="dailyChartRef" height="200"></canvas>

        <table class="w-full mt-4 text-sm border">
          <thead class="bg-gray-100">
            <tr>
              <th class="p-2 border">날짜</th>
              <th class="p-2 border">자동 점수</th>
              <th class="p-2 border">트레이너 점수</th>
              <th class="p-2 border">식사 수</th>
              <th class="p-2 border">청 칼로리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in dailyStats" :key="row.day">
              <td class="p-2 border">{{ row.day }}</td>
              <td class="p-2 border">{{ row.autoScore }}</td>
              <td class="p-2 border">{{ row.trainerScore }}</td>
              <td class="p-2 border">{{ row.mealCount }}</td>
              <td class="p-2 border">{{ row.totalCalories }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { Chart } from 'chart.js/auto'

const weekly = ref({})
const aiFeedback = ref({})
const dailyStats = ref([])
const loading = ref(true)
const dailyChartRef = ref(null)

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const headers = { Authorization: `Bearer ${token}` }

    const [weeklyRes, aiRes, dailyRes] = await Promise.all([
      axios.get('/api/users/statistics/weekly', { headers }),
      axios.get('/api/users/statistics/weekly/ai-feedback', { headers }),
      axios.get('/api/users/statistics/daily', { headers })
    ])

    weekly.value = weeklyRes.data.data[0] || {}
    aiFeedback.value = aiRes.data.data || {}
    dailyStats.value = dailyRes.data.data || []

    await nextTick()
    drawDailyChart()
  } catch (err) {
    console.error('주간/일간 통계 불러오기 실패:', err)
  } finally {
    loading.value = false
  }
})

function drawDailyChart() {
  const ctx = dailyChartRef.value?.getContext('2d')
  if (!ctx || dailyStats.value.length === 0) return

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: dailyStats.value.map(d => d.day),
      datasets: [
        {
          label: '자동 점수',
          data: dailyStats.value.map(d => d.autoScore),
          borderColor: 'rgba(75, 192, 192, 1)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          tension: 0.4,
          fill: true,
          pointRadius: 4
        }
      ]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: true } }
    }
  })
}
</script>

<style scoped>
/* 필요 시 스타일 추가 */
</style>