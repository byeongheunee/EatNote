<template>
  <div class="w-48 p-2 shadow rounded bg-white">
    <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="w-full h-32 object-cover rounded" />
    <p class="mt-1 text-sm truncate">{{ meal.detectedFoods }}</p>
    <p class="text-xs text-gray-500">{{ formatDate(meal.mealTime) }} · {{ meal.nickname }}</p>
  </div>
</template>

<script setup>
defineProps(['meal'])

// 날짜 포맷
const formatDate = (datetime) => new Date(datetime).toLocaleDateString()

// 이미지 URL을 8080 서버 기준으로 절대 경로 조립
const getImageUrl = (path) => {
  return `http://localhost:8080${path}`
}
</script>