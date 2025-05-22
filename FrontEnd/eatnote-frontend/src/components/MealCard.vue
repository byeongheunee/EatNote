<template>
  <div class="w-48 p-2 shadow rounded bg-white cursor-pointer hover:shadow-md transition" @click="goToDetail">
    <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="w-full h-32 object-cover rounded" />
    <p class="mt-1 text-sm truncate">{{ meal.detectedFoods }}</p>
    <p class="text-xs text-gray-500">{{ formatDate(meal.mealTime) }} · {{ meal.nickname }}</p>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps(['meal'])
const router = useRouter()

// 날짜 포맷
const formatDate = (datetime) => new Date(datetime).toLocaleDateString()

// 이미지 URL
const getImageUrl = (path) => {
  return `http://localhost:8080${path}`
}

// 상세 페이지 이동
const goToDetail = () => {
  router.push(`/meal/${props.meal.mealId}`)
}
</script>
