<template>
  <div class="bg-gray-100 p-4 rounded shadow flex flex-col lg:flex-row justify-between items-center gap-4">
    <div class="flex-1 text-center" v-for="stat in statsList" :key="stat.label">
      <p class="text-sm text-gray-600">{{ stat.label }}</p>
      <p class="text-xl font-bold text-green-700">{{ stat.value }}</p>
    </div>
    <RouterLink
      to="/meal/upload"
      class="self-start lg:self-center px-4 py-2 border border-purple-400 rounded-full text-sm text-purple-700 hover:bg-purple-100"
    >
      ✔ 새 식단 등록
    </RouterLink>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps({
  stats: {
    type: Object,
    required: true
  }
})


const statsList = computed(() => [
  { label: '연속 기록일', value: `${props.stats.consecutiveDays}일` },
  { label: '총 식단 기록', value: props.stats.totalMeals },
  { label: '피드백 받은 식단', value: props.stats.feedbackReceived },
  { label: '평균 칼로리', value: props.stats.averageCalories.toLocaleString() },
])
</script>

<style scoped>
.text-green-700 {
  color: #15803d;
}
</style>
