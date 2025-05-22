<template>
  <div class="bg-white p-4 rounded shadow space-y-3 border"
    :class="highlightPending ? 'border-orange-400' : 'border-gray-300'">
    <div class="flex justify-between items-center">
      <h3 class="font-semibold">{{ meal.nickname }}님의 식사</h3>
      <span class="text-sm text-gray-500">{{ formatDate(meal.mealTime) }}</span>
    </div>

    <img :src="getMealImage(meal.imageUrl)" class="w-full h-48 object-cover rounded" />

    <ul class="text-sm space-y-1">
      <li><strong>음식:</strong> {{ meal.detectedFoods || '정보 없음' }}</li>
      <li><strong>칼로리:</strong> {{ meal.totalCalories ?? '-' }} kcal</li>
      <li>
        <strong>종류:</strong> {{ mealTypeMap[meal.mealType] || meal.mealType }}
      </li>
      <li><strong>AI 점수:</strong> {{ meal.autoScore ?? '-' }}</li>
    </ul>

    <div class="pt-2">
      <button v-if="!meal.isFeedbackedByMe && highlightPending" @click="$emit('feedback', meal.mealId)"
        class="w-full bg-orange-500 text-white py-2 rounded hover:bg-orange-600">
        피드백 입력
      </button>
      <span v-else class="block text-green-600 text-sm text-center">✔️ 피드백 완료</span>
    </div>
  </div>
</template>

<script setup>
const props = defineProps(['meal', 'highlightPending'])

const getMealImage = (path) =>
  path ? `http://localhost:8080${path}` : '/images/default-meal.jpg'

const formatDate = (datetime) =>
  new Date(datetime).toLocaleString('ko-KR')

const mealTypeMap = {
  breakfast: '아침',
  lunch: '점심',
  dinner: '저녁',
  extra: '간식'
}
</script>

<style scoped></style>
