<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 헤더 -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-gray-900 mb-2">📊 나의 식단 통계</h1>
        <p class="text-gray-600">건강한 식습관을 위한 데이터 분석</p>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="flex justify-center items-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        <span class="ml-3 text-gray-600">데이터를 불러오는 중...</span>
      </div>

      <!-- 메인 컨텐츠 -->
      <div v-else class="space-y-8">
        <!-- 주차 선택 카드 -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div class="p-2 bg-indigo-100 rounded-lg">
                <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
              </div>
              <h2 class="text-xl font-semibold text-gray-800">기간 선택</h2>
            </div>
            <select
              v-model="selectedWeek"
              class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent bg-white shadow-sm"
            >
              <option v-for="w in weekList" :key="w.week" :value="w.week">
                {{ w.week }}
              </option>
            </select>
          </div>
        </div>

        <!-- 주간 요약 카드 -->
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
          <div class="bg-gradient-to-r from-indigo-600 to-purple-600 px-6 py-4">
            <h2 class="text-xl font-semibold text-white flex items-center">
              <span class="mr-2">📈</span>
              주간 요약
            </h2>
          </div>

          <div class="p-6">
            <div v-if="dailyStats.length === 0" class="text-center py-8">
              <div class="text-gray-400 text-6xl mb-4">📝</div>
              <p class="text-gray-500 text-lg">이번 주에는 아직 식사 기록이 없습니다</p>
              <p class="text-gray-400 text-sm mt-2">식사를 기록하면 통계를 확인할 수 있어요!</p>
            </div>

            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div class="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-blue-600 font-medium">주차</p>
                    <p class="text-2xl font-bold text-blue-800">{{ weekly.week }}</p>
                  </div>
                  <div class="text-blue-500 text-2xl">🗓️</div>
                </div>
              </div>

              <div class="bg-gradient-to-br from-green-50 to-green-100 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-green-600 font-medium">평균 AI 점수</p>
                    <p class="text-2xl font-bold text-green-800">{{ weekly.autoScore?.toFixed(1) ?? '-' }}</p>
                  </div>
                  <div class="text-green-500 text-2xl">🧠</div>
                </div>
              </div>

              <div class="bg-gradient-to-br from-orange-50 to-orange-100 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-orange-600 font-medium">평균 칼로리</p>
                    <p class="text-2xl font-bold text-orange-800">{{ weekly.avgCalories }}</p>
                    <p class="text-xs text-orange-500">kcal</p>
                  </div>
                  <div class="text-orange-500 text-2xl">🔥</div>
                </div>
              </div>

              <div class="bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-yellow-600 font-medium">평균 탄수화물</p>
                    <p class="text-2xl font-bold text-yellow-800">{{ weekly.avgCarbohydrates }}</p>
                    <p class="text-xs text-yellow-500">g</p>
                  </div>
                  <div class="text-yellow-500 text-2xl">🍞</div>
                </div>
              </div>

              <div class="bg-gradient-to-br from-red-50 to-red-100 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-red-600 font-medium">평균 단백질</p>
                    <p class="text-2xl font-bold text-red-800">{{ weekly.avgProtein }}</p>
                    <p class="text-xs text-red-500">g</p>
                  </div>
                  <div class="text-red-500 text-2xl">🥩</div>
                </div>
              </div>

              <div class="bg-gradient-to-br from-purple-50 to-purple-100 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-purple-600 font-medium">평균 지방</p>
                    <p class="text-2xl font-bold text-purple-800">{{ weekly.avgFat }}</p>
                    <p class="text-xs text-purple-500">g</p>
                  </div>
                  <div class="text-purple-500 text-2xl">🥑</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- AI 피드백 카드 -->
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
          <div class="bg-gradient-to-r from-green-600 to-teal-600 px-6 py-4">
            <h2 class="text-xl font-semibold text-white flex items-center">
              <span class="mr-2">🤖</span>
              AI 피드백
            </h2>
          </div>

          <div class="p-6 space-y-4">
            <div class="bg-red-50 border-l-4 border-red-400 p-4 rounded-r-lg">
              <div class="flex">
                <div class="flex-shrink-0">
                  <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="ml-3">
                  <h3 class="text-sm font-medium text-red-800">주의사항</h3>
                  <p class="text-sm text-red-700 mt-1">{{ aiFeedback.warning }}</p>
                </div>
              </div>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-400 p-4 rounded-r-lg">
              <div class="flex">
                <div class="flex-shrink-0">
                  <svg class="h-5 w-5 text-blue-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="ml-3">
                  <h3 class="text-sm font-medium text-blue-800">건강 팁</h3>
                  <p class="text-sm text-blue-700 mt-1">{{ aiFeedback.tip }}</p>
                </div>
              </div>
            </div>

            <div v-if="aiFeedback.recommendedFoods?.length" class="bg-green-50 border-l-4 border-green-400 p-4 rounded-r-lg">
              <h3 class="text-sm font-medium text-green-800 mb-2">🍎 추천 식품</h3>
              <div class="space-y-2">
                <div
                  v-for="(food, i) in aiFeedback.recommendedFoods"
                  :key="i"
                  class="bg-white p-3 rounded-lg border border-green-200"
                >
                  <p class="font-medium text-green-800">{{ food.name }}</p>
                  <p class="text-sm text-green-600">{{ food.reason }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 차트 섹션 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- 일별 점수 차트 -->
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="bg-gradient-to-r from-purple-600 to-pink-600 px-6 py-4">
              <h2 class="text-xl font-semibold text-white flex items-center">
                <span class="mr-2">📊</span>
                일자별 점수 추이
              </h2>
            </div>
            <div class="p-6">
              <canvas ref="dailyChartRef" class="w-full h-64"></canvas>
            </div>
          </div>

          <!-- 영양소 비율 차트 -->
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="bg-gradient-to-r from-teal-600 to-cyan-600 px-6 py-4">
              <h2 class="text-xl font-semibold text-white flex items-center">
                <span class="mr-2">🥘</span>
                영양소 비율
              </h2>
            </div>
            <div class="p-6">
              <div class="mb-6">
                <div class="bg-amber-50 border border-amber-200 rounded-lg p-3 mb-4">
                  <p class="text-sm text-amber-800">
                    💡 <strong>이상적인 비율:</strong> 탄수화물 50~60%, 단백질 20~30%, 지방 20~25%
                  </p>
                </div>
                <select
                  v-model="selectedDay"
                  class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                >
                  <option v-for="row in dailyStats" :key="row.day" :value="row.day">
                    {{ row.day }}
                  </option>
                </select>
              </div>
              <div class="flex justify-center">
                <div class="w-64 h-64">
                  <canvas ref="pieChartRef" class="w-full h-full"></canvas>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 상세 통계 테이블 -->
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
          <div class="bg-gradient-to-r from-gray-700 to-gray-800 px-6 py-4">
            <h2 class="text-xl font-semibold text-white flex items-center">
              <span class="mr-2">📋</span>
              상세 일별 데이터
            </h2>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">날짜</th>
                  <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">AI 점수</th>
                  <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">트레이너 점수</th>
                  <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">식사 수</th>
                  <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">평균 칼로리</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="row in dailyStats" :key="row.day" class="hover:bg-gray-50 transition-colors">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ row.day }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      {{ row.autoScore ?? '-' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {{ row.trainerScore ?? '-' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ row.mealCount }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ row.avgCalories }} kcal</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import axios from 'axios'
import { Chart } from 'chart.js/auto'

const weekly = ref({})
const aiFeedback = ref({})
const dailyStats = ref([])
const weekList = ref([])
const selectedWeek = ref('')
const selectedDay = ref('')
const loading = ref(true)
const dailyChartRef = ref(null)
const pieChartRef = ref(null)
let dailyChartInstance = null
let pieChartInstance = null

function getCurrentWeekString() {
  const now = new Date()
  const year = now.getFullYear()
  const jan4 = new Date(year, 0, 4)
  const jan4Day = jan4.getDay() || 7
  const firstWeekStart = new Date(jan4)
  firstWeekStart.setDate(jan4.getDate() - jan4Day + 1)

  const diffInMs = now - firstWeekStart
  const week = Math.ceil(diffInMs / (1000 * 60 * 60 * 24 * 7))

  return `${year}-W${String(week).padStart(2, '0')}`
}

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const headers = { Authorization: `Bearer ${token}` }

    const [weeklyRes, aiRes] = await Promise.all([
      axios.get('/api/users/statistics/weekly', { headers }),
      axios.get('/api/users/statistics/weekly/ai-feedback', { headers })
    ])

    let weeks = weeklyRes.data.data || []
    weeks.sort((a, b) => b.week.localeCompare(a.week))
    weekList.value = weeks

    const currentWeek = getCurrentWeekString()
    const matched = weeks.find(w => w.week === currentWeek)
    selectedWeek.value = matched?.week || weeks[0]?.week || ''
    weekly.value = matched || weeks[0] || {}
    aiFeedback.value = aiRes.data.data || {}

    await fetchDailyStats(headers)
  } catch (err) {
    console.error('주간 초기 통계 불러오기 실패:', err)
  } finally {
    loading.value = false
  }
})

watch(selectedWeek, async (newWeek) => {
  const token = localStorage.getItem('accessToken')
  const headers = { Authorization: `Bearer ${token}` }

  const selected = weekList.value.find(w => w.week === newWeek)
  weekly.value = selected || {}

  try {
    const aiRes = await axios.get('/api/users/statistics/weekly/ai-feedback', { headers })
    aiFeedback.value = aiRes.data.data || {}
  } catch (err) {
    console.error('AI 피드백 불러오기 실패:', err)
  }

  await fetchDailyStats(headers)
})

watch(selectedDay, (newDay) => {
  const stat = dailyStats.value.find(d => d.day === newDay)
  if (stat) drawPieChart(stat)
})

async function fetchDailyStats(headers) {
  try {
    const res = await axios.get('/api/users/statistics/daily', {
      headers,
      params: { week: selectedWeek.value }
    })
    dailyStats.value = res.data.data || []

    // 가장 최근 날짜로 자동 선택
    if (dailyStats.value.length > 0) {
      selectedDay.value = dailyStats.value[0].day
    }

    await nextTick()
    if (dailyStats.value.length > 0) {
      drawDailyChart()
      // 자동으로 첫 번째 데이터의 파이차트 그리기
      drawPieChart(dailyStats.value[0])
    }
  } catch (err) {
    console.error('일별 통계 불러오기 실패:', err)
    dailyStats.value = []
  }
}

function drawDailyChart() {
  const ctx = dailyChartRef.value?.getContext('2d')
  if (!ctx || dailyStats.value.length === 0) return

  // 기존 차트 인스턴스 제거
  if (dailyChartInstance) {
    dailyChartInstance.destroy()
    dailyChartInstance = null
  }

  dailyChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: dailyStats.value.map(d => d.day),
      datasets: [
        {
          label: 'AI 점수',
          data: dailyStats.value.map(d => d.autoScore),
          borderColor: '#8b5cf6',
          backgroundColor: 'rgba(139, 92, 246, 0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: '#8b5cf6',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2
        },
        {
          label: '트레이너 점수',
          data: dailyStats.value.map(d => d.trainerScore),
          borderColor: '#ec4899',
          backgroundColor: 'rgba(236, 72, 153, 0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: '#ec4899',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 20
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(0, 0, 0, 0.1)'
          }
        },
        x: {
          grid: {
            color: 'rgba(0, 0, 0, 0.1)'
          }
        }
      }
    }
  })
}

function drawPieChart(stat) {
  const ctx = pieChartRef.value?.getContext('2d')
  if (!ctx) return

  // 기존 차트 인스턴스 제거
  if (pieChartInstance) {
    pieChartInstance.destroy()
    pieChartInstance = null
  }

  const total = stat.avgCarbohydrates + stat.avgProtein + stat.avgFat

  const data = {
    labels: [
      `탄수화물 (${((stat.avgCarbohydrates / total) * 100).toFixed(1)}%)`,
      `단백질 (${((stat.avgProtein / total) * 100).toFixed(1)}%)`,
      `지방 (${((stat.avgFat / total) * 100).toFixed(1)}%)`
    ],
    datasets: [{
      data: [stat.avgCarbohydrates, stat.avgProtein, stat.avgFat],
      backgroundColor: ['#fbbf24', '#60a5fa', '#f87171'],
      borderColor: ['#f59e0b', '#3b82f6', '#ef4444'],
      borderWidth: 2,
      hoverOffset: 4
    }]
  }

  const options = {
    responsive: true,
    maintainAspectRatio: true,
    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          font: { size: 11 },
          usePointStyle: true,
          padding: 12
        }
      },
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const value = ctx.parsed
            const percent = ((value / total) * 100).toFixed(1)
            return `${ctx.label}: ${value}g (${percent}%)`
          }
        }
      }
    }
  }

  pieChartInstance = new Chart(ctx, {
    type: 'pie',
    data,
    options
  })
}
</script>

<style scoped>
/* 필요한 경우 추가 스타일 */
</style>