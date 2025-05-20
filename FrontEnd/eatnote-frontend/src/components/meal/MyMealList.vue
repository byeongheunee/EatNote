<!-- src/components/meal/MyMealList.vue -->
<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">나의 식단</h2>
    <div class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3">
      <div
        v-for="meal in meals"
        :key="meal.mealId"
        class="bg-white rounded shadow p-4 cursor-pointer"
        @click="goToDetail(meal.mealId)"
      >
        <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="w-full h-40 object-cover rounded" />
        <div class="mt-2">
          <h3 class="font-semibold">{{ meal.detectedFoods }}</h3>
          <p class="text-sm text-gray-500">{{ formatDate(meal.mealTime) }}</p>
          <p class="text-sm">총 칼로리: {{ meal.totalCalories }}kcal</p>
          <p class="text-sm">식사 유형: {{ mealTypeKor(meal.mealType) }}</p>
        </div>
        <div class="flex justify-between items-center mt-2 text-sm text-gray-600">
          <span>💬 {{ meal.feedbackCount }} · ❤️ {{ meal.likeCount }}</span>
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
const router = useRouter()

const getImageUrl = (path) => `http://localhost:8080${path}`
const formatDate = (datetime) => new Date(datetime).toLocaleDateString('ko-KR')
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
  const token = localStorage.getItem('accessToken')
  const user = JSON.parse(localStorage.getItem('user'))
  const userId = user?.userId
  if (!userId) return

  const res = await axios.get(`/api/users/user/${userId}/meals`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  meals.value = res.data.data
}

const goToDetail = (mealId) => router.push(`/meal/${mealId}`)

onMounted(loadMyMeals)
</script>
