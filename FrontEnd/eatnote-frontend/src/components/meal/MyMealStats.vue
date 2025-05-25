<!-- src/components/meal/MyMealStats.vue -->
<template>
  <div class="my-meal-stats">
    <!-- 헤더 섹션 -->
    <div class="header-section">
      <h2 class="section-title">나의 식단 통계</h2>
      <p class="section-subtitle">건강한 식습관을 위한 데이터 분석</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <span class="loading-text">데이터를 불러오는 중...</span>
    </div>

    <!-- 메인 컨텐츠 -->
    <div v-else class="main-content">
      <!-- 주차 선택 카드 -->
      <div class="period-selector-card">
        <div class="selector-header">
          <div class="selector-icon-wrapper">
            <span class="selector-icon">📅</span>
          </div>
          <h3 class="selector-title">기간 선택</h3>
        </div>
        <select
          v-model="selectedWeek"
          class="week-selector"
        >
          <option v-for="w in weekList" :key="w.week" :value="w.week">
            {{ w.week }}
          </option>
        </select>
      </div>

      <!-- 주간 요약 카드 -->
      <div class="summary-card">
        <div class="card-header">
          <h3 class="card-title">
            <span class="card-icon">📈</span>
            주간 요약
          </h3>
        </div>

        <div class="summary-content">
          <div v-if="dailyStats.length === 0" class="empty-stats">
            <div class="empty-icon">📝</div>
            <h4 class="empty-title">이번 주에는 아직 식사 기록이 없습니다</h4>
            <p class="empty-description">식사를 기록하면 통계를 확인할 수 있어요!</p>
          </div>

          <div v-else class="stats-grid">
            <div class="stat-item week-stat">
              <div class="stat-content">
                <p class="stat-label">주차</p>
                <p class="stat-value">{{ weekly.week }}</p>
              </div>
              <div class="stat-emoji">🗓️</div>
            </div>

            <div class="stat-item score-stat">
              <div class="stat-content">
                <p class="stat-label">평균 AI 점수</p>
                <p class="stat-value">{{ weekly.autoScore?.toFixed(1) ?? '-' }}</p>
              </div>
              <div class="stat-emoji">🧠</div>
            </div>

            <div class="stat-item calorie-stat">
              <div class="stat-content">
                <p class="stat-label">평균 칼로리</p>
                <p class="stat-value">{{ weekly.avgCalories }}</p>
                <p class="stat-unit">kcal</p>
              </div>
              <div class="stat-emoji">🔥</div>
            </div>

            <div class="stat-item carb-stat">
              <div class="stat-content">
                <p class="stat-label">평균 탄수화물</p>
                <p class="stat-value">{{ weekly.avgCarbohydrates }}</p>
                <p class="stat-unit">g</p>
              </div>
              <div class="stat-emoji">🍞</div>
            </div>

            <div class="stat-item protein-stat">
              <div class="stat-content">
                <p class="stat-label">평균 단백질</p>
                <p class="stat-value">{{ weekly.avgProtein }}</p>
                <p class="stat-unit">g</p>
              </div>
              <div class="stat-emoji">🥩</div>
            </div>

            <div class="stat-item fat-stat">
              <div class="stat-content">
                <p class="stat-label">평균 지방</p>
                <p class="stat-value">{{ weekly.avgFat }}</p>
                <p class="stat-unit">g</p>
              </div>
              <div class="stat-emoji">🥑</div>
            </div>
          </div>
        </div>
      </div>

      <!-- AI 피드백 카드 -->
      <div class="feedback-card">
        <div class="card-header">
          <h3 class="card-title">
            <span class="card-icon">🤖</span>
            AI 피드백
          </h3>
        </div>

        <div class="feedback-content">
          <div class="feedback-item warning-feedback">
            <div class="feedback-icon-wrapper">
              <span class="feedback-icon">⚠️</span>
            </div>
            <div class="feedback-text">
              <h4 class="feedback-title">주의사항</h4>
              <p class="feedback-description">{{ aiFeedback.warning }}</p>
            </div>
          </div>

          <div class="feedback-item tip-feedback">
            <div class="feedback-icon-wrapper">
              <span class="feedback-icon">💡</span>
            </div>
            <div class="feedback-text">
              <h4 class="feedback-title">건강 팁</h4>
              <p class="feedback-description">{{ aiFeedback.tip }}</p>
            </div>
          </div>

          <div v-if="aiFeedback.recommendedFoods?.length" class="feedback-item recommendation-feedback">
            <div class="feedback-icon-wrapper">
              <span class="feedback-icon">🍎</span>
            </div>
            <div class="feedback-text">
              <h4 class="feedback-title">추천 식품</h4>
              <div class="recommended-foods">
                <div
                  v-for="(food, i) in aiFeedback.recommendedFoods"
                  :key="i"
                  class="food-recommendation"
                >
                  <p class="food-name">{{ food.name }}</p>
                  <p class="food-reason">{{ food.reason }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 차트 섹션 -->
      <div class="charts-section">
        <!-- 일별 점수 차트 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <span class="chart-icon">📊</span>
              일자별 점수 추이
            </h3>
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

        <!-- 영양소 비율 차트 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <span class="chart-icon">🥘</span>
              영양소 비율
            </h3>
          </div>
          <div class="chart-content">
            <div class="nutrition-info">
              <p class="nutrition-guide">
                💡 <strong>이상적인 비율:</strong> 탄수화물 50~60%, 단백질 20~30%, 지방 20~25%
              </p>
            </div>
            <div class="day-selector-wrapper">
              <select
                v-model="selectedDay"
                class="day-selector"
              >
                <option disabled value="">날짜를 선택하세요</option>
                <option v-for="row in dailyStats" :key="row.day" :value="row.day">
                  {{ row.day }}
                </option>
              </select>
            </div>
            <div class="pie-chart-wrapper">
              <canvas ref="pieChartRef" class="pie-chart"></canvas>
            </div>
          </div>
        </div>
      </div>

      <!-- 상세 통계 테이블 -->
      <div class="table-card">
        <div class="card-header">
          <h3 class="card-title">
            <span class="card-icon">📋</span>
            상세 일별 데이터
          </h3>
        </div>
        <div class="table-wrapper">
          <table class="stats-table">
            <thead class="table-header">
              <tr>
                <th class="table-th">날짜</th>
                <th class="table-th">AI 점수</th>
                <th class="table-th">트레이너 점수</th>
                <th class="table-th">식사 수</th>
                <th class="table-th">평균 칼로리</th>
              </tr>
            </thead>
            <tbody class="table-body">
              <tr v-for="row in dailyStats" :key="row.day" class="table-row">
                <td class="table-td font-medium">{{ row.day }}</td>
                <td class="table-td">
                  <span class="score-badge ai-score">
                    {{ row.autoScore ?? '-' }}
                  </span>
                </td>
                <td class="table-td">
                  <span class="score-badge trainer-score">
                    {{ row.trainerScore ?? '-' }}
                  </span>
                </td>
                <td class="table-td">{{ row.mealCount }}</td>
                <td class="table-td">{{ row.avgCalories }} kcal</td>
              </tr>
            </tbody>
          </table>
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
          borderColor: '#f59e0b',
          backgroundColor: 'rgba(245, 158, 11, 0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: '#f59e0b',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2
        },
        {
          label: '트레이너 점수',
          data: dailyStats.value.map(d => d.trainerScore),
          borderColor: '#10b981',
          backgroundColor: 'rgba(16, 185, 129, 0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: '#10b981',
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
            padding: 20,
            font: {
              size: 12,
              weight: '600'
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(229, 231, 235, 0.5)'
          },
          ticks: {
            font: {
              size: 11
            }
          }
        },
        x: {
          grid: {
            color: 'rgba(229, 231, 235, 0.5)'
          },
          ticks: {
            font: {
              size: 11
            }
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
      backgroundColor: ['#f59e0b', '#10b981', '#ef4444'],
      borderColor: ['#d97706', '#059669', '#dc2626'],
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
          font: { size: 11, weight: '600' },
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
.my-meal-stats {
  width: 100%;
  animation: fadeInUp 0.6s ease-out;
}

/* 헤더 섹션 */
.header-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
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

/* 로딩 상태 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3rem 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.loading-spinner {
  width: 2rem;
  height: 2rem;
  border: 2px solid rgba(245, 158, 11, 0.2);
  border-top: 2px solid #f59e0b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 0.75rem;
}

.loading-text {
  color: #6b7280;
  font-weight: 500;
}

/* 메인 컨텐츠 */
.main-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 기간 선택 카드 */
.period-selector-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selector-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.selector-icon-wrapper {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.1));
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 12px;
  padding: 0.5rem;
}

.selector-icon {
  font-size: 1.25rem;
}

.selector-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.week-selector {
  padding: 0.75rem 1rem;
  border: 2px solid rgba(229, 231, 235, 0.8);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  color: #374151;
  font-weight: 500;
  transition: all 0.3s ease;
}

.week-selector:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

/* 카드 공통 스타일 */
.summary-card,
.feedback-card,
.chart-card,
.table-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: all 0.3s ease;
}

.summary-card:hover,
.feedback-card:hover,
.chart-card:hover,
.table-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.2);
}

/* 카드 헤더 */
.card-header {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  padding: 1.25rem 1.5rem;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.25rem;
  font-weight: 600;
  color: white;
  margin: 0;
}

.card-icon {
  font-size: 1.25rem;
}

/* 주간 요약 */
.summary-content {
  padding: 1.5rem;
}

.empty-stats {
  text-align: center;
  padding: 2rem;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.7;
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.empty-description {
  color: #6b7280;
  font-size: 0.9rem;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.stat-item {
  background: rgba(249, 250, 251, 0.8);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid rgba(229, 231, 235, 0.5);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
  margin: 0 0 0.25rem 0;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  margin: 0;
  line-height: 1;
}

.stat-unit {
  font-size: 0.7rem;
  color: #6b7280;
  margin: 0;
}

.stat-emoji {
  font-size: 1.5rem;
  opacity: 0.8;
}

/* 통계별 색상 */
.week-stat {
  border-left: 4px solid #3b82f6;
}

.score-stat {
  border-left: 4px solid #10b981;
}

.calorie-stat {
  border-left: 4px solid #f59e0b;
}

.carb-stat {
  border-left: 4px solid #eab308;
}

.protein-stat {
  border-left: 4px solid #ef4444;
}

.fat-stat {
  border-left: 4px solid #8b5cf6;
}

/* AI 피드백 */
.feedback-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feedback-item {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem;
  border-radius: 12px;
  border-left: 4px solid;
}

.warning-feedback {
  background: rgba(254, 242, 242, 0.8);
  border-left-color: #ef4444;
}

.tip-feedback {
  background: rgba(239, 246, 255, 0.8);
  border-left-color: #3b82f6;
}

.recommendation-feedback {
  background: rgba(240, 253, 244, 0.8);
  border-left-color: #10b981;
}

.feedback-icon-wrapper {
  flex-shrink: 0;
}

.feedback-icon {
  font-size: 1.25rem;
}

.feedback-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.25rem 0;
}

.feedback-description {
  font-size: 0.85rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

.recommended-foods {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 0.75rem;
}

.food-recommendation {
  background: rgba(255, 255, 255, 0.8);
  padding: 0.75rem;
  border-radius: 8px;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.food-name {
  font-weight: 600;
  color: #047857;
  margin: 0 0 0.25rem 0;
  font-size: 0.9rem;
}

.food-reason {
  font-size: 0.8rem;
  color: #059669;
  margin: 0;
}

/* 차트 섹션 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
}

.chart-header {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  padding: 1.25rem 1.5rem;
}

.chart-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: white;
  margin: 0;
}

.chart-icon {
  font-size: 1.1rem;
}

.chart-content {
  padding: 1.5rem;
}

.chart-canvas {
  width: 100%;
  height: 250px;
}

.nutrition-info {
  margin-bottom: 1rem;
}

.nutrition-guide {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 8px;
  padding: 0.75rem;
  font-size: 0.85rem;
  color: #d97706;
  margin: 0;
}

.day-selector-wrapper {
  margin-bottom: 1rem;
}

.day-selector {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid rgba(229, 231, 235, 0.8);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  color: #374151;
  font-weight: 500;
  transition: all 0.3s ease;
}

.day-selector:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.pie-chart-wrapper {
  display: flex;
  justify-content: center;
}

.pie-chart {
  max-width: 300px;
  max-height: 300px;
}

/* 테이블 */
.table-wrapper {
  overflow-x: auto;
}

.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.table-header {
  background: rgba(249, 250, 251, 0.8);
}

.table-th {
  padding: 1rem 1.5rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid rgba(229, 231, 235, 0.5);
}

.table-body {
  background: rgba(255, 255, 255, 0.5);
}

.table-row {
  transition: background-color 0.2s ease;
}

.table-row:hover {
  background: rgba(249, 250, 251, 0.8);
}

.table-td {
  padding: 1rem 1.5rem;
  font-size: 0.9rem;
  color: #374151;
  border-bottom: 1px solid rgba(229, 231, 235, 0.3);
}

.font-medium {
  font-weight: 600;
}

.score-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.ai-score {
  background: rgba(16, 185, 129, 0.1);
  color: #047857;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.trainer-score {
  background: rgba(59, 130, 246, 0.1);
  color: #1d4ed8;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
  
  .period-selector-card {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .feedback-item {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .recommended-foods {
    gap: 0.5rem;
  }
  
  .chart-content {
    padding: 1rem;
  }
  
  .chart-canvas {
    height: 200px;
  }
  
  .table-th,
  .table-td {
    padding: 0.75rem 1rem;
    font-size: 0.8rem;
  }
}

@media (max-width: 640px) {
  .summary-content,
  .feedback-content,
  .chart-content {
    padding: 1rem;
  }
  
  .period-selector-card {
    padding: 1rem;
  }
  
  .stat-item {
    padding: 1rem;
  }
  
  .stat-value {
    font-size: 1.25rem;
  }
  
  .stat-emoji {
    font-size: 1.25rem;
  }
  
  .pie-chart {
    max-width: 250px;
    max-height: 250px;
  }
  
  .table-th,
  .table-td {
    padding: 0.5rem 0.75rem;
    font-size: 0.75rem;
  }
  
  .score-badge {
    padding: 0.25rem 0.5rem;
    font-size: 0.7rem;
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

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 스크롤바 스타일링 */
.table-wrapper::-webkit-scrollbar {
  height: 6px;
}

.table-wrapper::-webkit-scrollbar-track {
  background: rgba(249, 250, 251, 0.5);
  border-radius: 3px;
}

.table-wrapper::-webkit-scrollbar-thumb {
  background: rgba(156, 163, 175, 0.5);
  border-radius: 3px;
}

.table-wrapper::-webkit-scrollbar-thumb:hover {
  background: rgba(156, 163, 175, 0.7);
}

/* 차트 반응형 조정 */
@media (max-width: 480px) {
  .chart-canvas {
    height: 180px;
  }
  
  .pie-chart {
    max-width: 200px;
    max-height: 200px;
  }
  
  .nutrition-guide {
    font-size: 0.8rem;
    padding: 0.5rem;
  }
}
</style>