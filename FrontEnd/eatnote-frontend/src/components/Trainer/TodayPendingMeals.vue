<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">📌 피드백 미완료 식단</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="meals.length === 0">피드백 미완료 식단이 없습니다.</div>

    <div v-else class="relative">
      <!-- 왼쪽 화살표 -->
      <button v-if="canScrollLeft && meals.length > 3" class="arrow-left" @click="scrollLeft">
        ←
      </button>

      <!-- 카드 리스트 (가로 스크롤 + 슬라이드) -->
      <div ref="scrollContainer" class="scroll-container" @scroll="checkScroll">
        <div class="slide-track">
          <div v-for="meal in meals" :key="meal.mealId" class="slide-card" @click="goToMeal(meal.mealId)">
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
      <button v-if="canScrollRight && meals.length > 3" class="arrow-right" @click="scrollRight">
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
const meals = ref([])
const loading = ref(true)
const router = useRouter()

const emit = defineEmits(['update-count'])

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
    emit('update-count', meals.value.length)

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
.scroll-container {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scroll-snap-type: x mandatory;
  scrollbar-width: none;
}

.scroll-container::-webkit-scrollbar {
  display: none;
}

.slide-track {
  display: flex;
  gap: 1rem;
  padding-bottom: 1rem;
}

.slide-card {
  scroll-snap-align: start;
  flex: 0 0 auto;
  width: 280px;
  border-radius: 12px;
  padding: 0.75rem;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
  border: 1px solid #e5e7eb;
  cursor: pointer;
}

.slide-card:hover {
  transform: scale(1.02);
  background-color: #f9fafb;
}

/* 화살표 버튼 */
.arrow-left,
.arrow-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  background: white;
  padding: 0.5rem 0.75rem;
  border-radius: 9999px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  font-size: 1.2rem;
  font-weight: bold;
}

.arrow-left {
  left: -12px;
}

.arrow-right {
  right: -12px;
}
</style>
