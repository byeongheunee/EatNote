<!-- src/components/trainer/FeedbackCalendar.vue -->
<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">📅 피드백 달력 통계</h2>
    <VueDatePicker
      v-model="selectedMonth"
      :month-picker="true"
      @update:model-value="fetchStats"
      :format="formatMonth"
    />

    <div v-if="Object.keys(stats).length > 0" class="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4">
      <div v-for="(stat, date) in stats" :key="date" class="border rounded p-3 shadow-sm">
        <p class="font-semibold text-gray-800">{{ date }}</p>
        <p class="text-sm text-gray-700">요청: {{ stat.total }}건</p>
        <p class="text-sm text-gray-700">작성: {{ stat.written }}건</p>
      </div>
    </div>
    <div v-else class="text-gray-500 mt-4">조회된 피드백 통계가 없습니다.</div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref } from 'vue'
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'

const selectedMonth = ref(new Date())
const stats = ref({})

const formatMonth = (date) => `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`

const fetchStats = async () => {
  const ym = formatMonth(selectedMonth.value)
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get(`/api/trainer/feedback/calendar?month=${ym}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    stats.value = res.data.data
  } catch (e) {
    console.error('달력 통계 불러오기 실패', e)
  }
}

fetchStats()
</script>
