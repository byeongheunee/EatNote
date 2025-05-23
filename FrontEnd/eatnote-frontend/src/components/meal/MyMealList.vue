<!-- src/components/meal/MyMealList.vue -->
<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">나의 식단</h2>
    <div class="flex gap-2 mb-4">
      <button v-for="option in filterOptions" :key="option.label" @click="selectedFilter = option.code" :class="[
        'px-4 py-1 rounded text-sm border',
        selectedFilter === option.code
          ? 'bg-green-600 text-white border-green-600'
          : 'bg-white text-gray-600 border-gray-300 hover:bg-gray-100'
      ]">
        {{ option.label }}
      </button>
    </div>
    <div class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3">
      <div v-for="meal in visibleMeals" :key="meal.mealId" class="bg-white rounded shadow p-4 cursor-pointer"
        @click="goToDetail(meal.mealId)">
        <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="w-full h-40 object-cover rounded" />
        <div class="mt-2">
          <h3 class="font-semibold">{{ meal.detectedFoods }}</h3>
          <p class="text-sm text-gray-500">{{ formatDate(meal.mealTime) }}</p>
          <p class="text-sm">총 칼로리: {{ meal.totalCalories }}kcal</p>
          <p class="text-sm">식사 유형: {{ mealTypeKor(meal.mealType) }}</p>
        </div>
        <div class="flex justify-between items-center mt-2 text-sm text-gray-600">
          <span>💬 {{ meal.feedbackCount }}</span>
          <LikeButton
            contentType="MEAL"
            :contentId="meal.mealId"
            :likeCount="meal.likeCount"
            :myReaction="meal.myReaction || null"
            @onUpdated="loadMyMeals"
          />
        </div>
      </div>
    </div>
    <div class="text-center mt-6" v-if="filteredMeals.length > visibleCount">
      <button @click="visibleCount += 9" class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300">
        더보기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LikeButton from '@/components/LikeButton.vue'


const meals = ref([])
const selectedFilter = ref('all')
const router = useRouter()
const visibleCount = ref(9)

const filterOptions = [
  { label: '모든 식단', code: 'all' },
  { label: '오늘의 식단', code: 'today' },
  { label: '이번 주 식단', code: 'week' },

]

const changeFilter = (code) => {
  selectedFilter.value = code
  visibleCount.value = 9   // ✅ 필터 바뀌면 개수 초기화
}
const visibleMeals = computed(() => filteredMeals.value.slice(0, visibleCount.value))
const formatDate = (datetime) => new Date(datetime).toLocaleDateString('ko-KR')
const getImageUrl = (path) => `http://localhost:8080${path}`
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
  const auth = useAuthStore()
  const userId = auth.user?.userId
  if (!userId) return

  try {
    const res = await axios.get(`/api/users/user/${userId}/meals`)
    meals.value = res.data.data
  } catch (e) {
    console.error('🍽️ 식단 목록 불러오기 실패:', e)
  }
}


const filteredMeals = computed(() => {
  const today = new Date()
  return meals.value.filter(meal => {
    const mealDate = new Date(meal.mealTime)
    switch (selectedFilter.value) {
      case 'today':
        return mealDate.toDateString() === today.toDateString()
      case 'week': {
        const diffDays = (today - mealDate) / (1000 * 60 * 60 * 24)
        return diffDays <= 7
      }
      default:
        return true
    }
  })
})

const goToDetail = (mealId) => router.push(`/meal/${mealId}`)

onMounted(loadMyMeals)
</script>
