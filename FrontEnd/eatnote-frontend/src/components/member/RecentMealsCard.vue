<template>
  <div>
    <div class="flex justify-between items-center mb-2">
      <h2 class="text-lg font-bold">최근 식단</h2>
      <RouterLink to="/meals" class="text-sm text-green-600 hover:underline">전체보기</RouterLink>
    </div>

    <div class="relative">
      <!-- 왼쪽 화살표 -->
      <button
        v-if="meals.length >= 4 && canScrollLeft"
        class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-white px-2 py-1 shadow rounded-full hover:bg-gray-100"
        @click="scrollLeft"
      >
        ←
      </button>

      <!-- 가로 슬라이드 카드 리스트 -->
      <div ref="scrollContainer" class="overflow-x-auto scrollbar-hide" @scroll="checkScroll">
        <div class="flex gap-4 pb-2">
          <div
            v-for="meal in meals"
            :key="meal.mealId"
            class="w-1/4 flex-shrink-0 h-[250px] border rounded p-2 shadow-sm cursor-pointer hover:bg-gray-50 flex flex-col justify-between"
            @click="goToMealDetail(meal.mealId)"
          >

            <img
              :src="getImageUrl(meal.imageUrl)"
              alt="식단 이미지"
              class="w-full h-40 object-cover rounded"
            />
            <div class="mt-2 text-sm text-gray-600">
              <p class="text-xs">{{ formatDate(meal.mealTime) }} · {{ translateMealType(meal.mealType) }}</p>
              <p class="font-semibold mt-1 truncate">{{ meal.detectedFoods }}</p>
              <p class="text-xs mt-1 text-gray-500">총 열량: {{ Math.round(meal.totalCalories) }} kcal</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 오른쪽 화살표 -->
      <button
        v-if="meals.length >= 4 && canScrollRight"
        class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-white px-2 py-1 shadow rounded-full hover:bg-gray-100"
        @click="scrollRight"
      >
        →
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, RouterLink } from 'vue-router'

const props = defineProps({
  meals: Array
})

const router = useRouter()
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

onMounted(() => {
  nextTick(() => {
    if (props.meals.length >= 4) {
      checkScroll()
    }
  })
})

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
  switch (type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}
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
