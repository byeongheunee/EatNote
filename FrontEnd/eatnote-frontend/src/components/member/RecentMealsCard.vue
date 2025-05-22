<template>
  <div>
    <div class="flex justify-between items-center mb-2">
      <h2 class="text-lg font-bold">최근 식단</h2>
      <RouterLink to="/meals" class="text-sm text-green-600 hover:underline">전체보기</RouterLink>
    </div>

    <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="meal in meals" :key="meal.mealId" class="border rounded p-2 shadow-sm cursor-pointer hover:bg-gray-50" @click="goToMealDetail(meal.mealId)" >
        <img
          :src="getImageUrl(meal.imageUrl)"
          alt="식단 이미지"
          class="w-full h-32 object-cover rounded"
        />
        <div class="mt-2 text-sm text-gray-600">
          <p class="text-xs">{{ formatDate(meal.mealTime) }} · {{ translateMealType(meal.mealType) }}</p>
          <p class="font-semibold mt-1 truncate">{{ meal.detectedFoods }}</p>
          <p class="text-xs mt-1 text-gray-500">총 열량: {{ Math.round(meal.totalCalories) }} kcal</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter, RouterLink } from 'vue-router'

const props = defineProps({
  meals: Array
})

const router = useRouter()

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}`)
}

const getImageUrl = (url) => {
  return url ? `http://localhost:8080${url}` : '/images/default-meal.png'
}

const formatDate = (datetime) => {
  const date = new Date(datetime)
  return `${date.getFullYear()}.${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`
}

const translateMealType = (type) => {
  switch(type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}
</script>

<style scoped>
img {
  background-color: #f4f4f4;
}
</style>
