<template>
  <div class="bg-white p-4 rounded shadow space-y-3 border cursor-pointer hover:bg-gray-50"
    :class="highlightPending ? 'border-orange-400' : 'border-gray-300'" @click="handleCardClick">
    <!-- 헤더 -->
    <div class="flex justify-between items-center">
      <h3 class="font-semibold">{{ meal.nickname }}님의 식사</h3>
      <span class="text-sm text-gray-500">{{ formatDate(meal.mealTime) }}</span>
    </div>

    <!-- 이미지 -->
    <img :src="getMealImage(meal.imageUrl)" class="w-full h-48 object-cover rounded" />

    <!-- 정보 -->
    <ul class="text-sm space-y-1">
      <li><strong>음식:</strong> {{ meal.detectedFoods || '정보 없음' }}</li>
      <li><strong>칼로리:</strong> {{ meal.totalCalories ?? '-' }} kcal</li>
      <li><strong>종류:</strong> {{ mealTypeMap[meal.mealType] || meal.mealType }}</li>
      <li><strong>AI 점수:</strong> {{ meal.autoScore ?? '-' }}</li>
    </ul>

    <!-- 하단 피드백 상태 -->
    <div class="pt-2">
      <!-- ❗ 미작성 시 입력 버튼 -->
      <button v-if="!meal.isFeedbackedByMe && highlightPending" @click.stop="$emit('feedback', meal.mealId)"
        class="w-full bg-orange-500 text-white py-2 rounded hover:bg-orange-600">
        피드백 입력
      </button>

      <div v-else class="flex justify-between items-center mt-2 text-sm text-green-600">
        <span>✔️ 피드백 완료 (점수: {{ meal.trainerScore }}/10)</span>
        <div class="flex gap-2">
          <button @click="$emit('view', meal.mealId)">상세 보기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps(['meal', 'highlightPending'])
const emit = defineEmits(['feedback', 'view', 'delete'])

const handleCardClick = () => {
  // 피드백 완료된 식단은 클릭해도 아무 동작 안함
  if (props.meal.isFeedbackedByMe) return
  emit('feedback', props.meal.mealId)
}

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
