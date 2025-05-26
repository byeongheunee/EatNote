<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">📌 피드백 미완료 식단</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="meals.length === 0">피드백 미완료 식단이 없습니다.</div>

    <div v-else class="relative">
      <!-- 왼쪽 화살표 -->
      <button v-if="canScrollLeft"
        class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-white px-2 py-1 shadow rounded-full hover:bg-gray-100"
        @click="scrollLeft">
        ←
      </button>

      <!-- 카드 리스트 (가로 스크롤) -->
      <div ref="scrollContainer" class="overflow-x-auto scrollbar-hide" @scroll="checkScroll">
        <div class="flex gap-4 pb-2">
          <div v-for="meal in meals" :key="meal.mealId"
            class="w-[280px] flex-shrink-0 border rounded-lg p-3 shadow hover:bg-gray-50 cursor-pointer"
            @click="goToMeal(meal.mealId)">
            <img :src="getImageUrl(meal.imageUrl)" class="w-full h-40 object-cover rounded" />
            <div class="mt-2">
              <p class="font-semibold text-lg truncate">{{ meal.nickname }}</p>
              <p class="text-sm text-gray-600">{{ formatDate(meal.mealTime) }}</p>
              <p class="text-sm text-gray-800 line-clamp-2">🍽 {{ meal.detectedFoods }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 오른쪽 화살표 -->
      <button v-if="canScrollRight"
        class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-white px-2 py-1 shadow rounded-full hover:bg-gray-100"
        @click="scrollRight">
        →
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const scrollContainer = ref(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)

const scrollLeft = () => {
  scrollContainer.value?.scrollBy({ left: -1000, behavior: 'smooth' })
}

const scrollRight = () => {
  scrollContainer.value?.scrollBy({ left: 1000, behavior: 'smooth' })
}

const checkScroll = () => {
  const el = scrollContainer.value
  if (!el) return
  canScrollLeft.value = el.scrollLeft > 0
  canScrollRight.value = el.scrollLeft + el.clientWidth < el.scrollWidth - 1
}

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

    await nextTick()
    checkScroll()
    scrollContainer.value?.addEventListener('scroll', checkScroll)
  } catch (e) {
    console.error('피드백 식단 불러오기 실패', e)
  } finally {
    loading.value = false
  }
})

watch(meals, () => {
  nextTick(() => {
    checkScroll()
  })
})
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
