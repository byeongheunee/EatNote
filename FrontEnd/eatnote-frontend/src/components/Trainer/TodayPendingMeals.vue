<!-- src/components/trainer/TodayPendingMeals.vue -->
<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">📌 피드백 미완료 식단</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="meals.length === 0">피드백 미완료 식단이 없습니다.</div>
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="meal in meals" :key="meal.mealId" class="border rounded-lg p-3 shadow hover:bg-gray-50 cursor-pointer"
        @click="goToMeal(meal.mealId)">
        <img :src="getImageUrl(meal.imageUrl)" class="w-full h-40 object-cover rounded" />
        <div class="mt-2">
          <p class="font-semibold text-lg">{{ meal.nickname }}</p>
          <p class="text-sm text-gray-600">{{ formatDate(meal.mealTime) }}</p>
          <p class="text-sm text-gray-800">🍽 {{ meal.detectedFoods }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const meals = ref([])
const loading = ref(true)
const router = useRouter()

const getImageUrl = (path) => `http://localhost:8080${path}`
const formatDate = (datetime) => {
  const date = new Date(datetime)
  return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`
}
const goToMeal = (mealId) => router.push(`/trainer/feedback/${mealId}`)

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/trainer/pending', {
      headers: { Authorization: `Bearer ${token}` }
    })
    meals.value = res.data.data || []
  } catch (e) {
    console.error('피드백 식단 불러오기 실패', e)
  } finally {
    loading.value = false
  }
})
</script>
