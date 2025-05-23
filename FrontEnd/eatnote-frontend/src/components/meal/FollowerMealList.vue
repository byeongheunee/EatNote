<!-- src/components/meal/FollowerMealView.vue -->
<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">팔로워 식단</h2>

    <!-- 상단 프로필 목록 -->
    <div class="flex overflow-x-auto mb-6 gap-4">
      <div v-for="user in filteredFollowings" :key="user.userId" class="text-center cursor-pointer"
        @click="selectUser(user.userId)">
        <img :src="getImageUrl(user.profileImage)" alt="profile"
          class="w-16 h-16 object-cover rounded-full mx-auto border-2"
          :class="{ 'border-orange-400': selectedUserId === user.userId }" />
        <p class="mt-1 text-sm">{{ user.nickname }}</p>
      </div>
    </div>

    <!-- 식단 목록 -->
    <div v-if="meals.length > 0" class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3">
      <div v-for="meal in meals" :key="meal.mealId" class="bg-white rounded shadow p-4 cursor-pointer"
        @click="goToDetail(meal.mealId)">
        <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="w-full h-40 object-cover rounded" />
        <div class="mt-2">
          <h3 class="font-semibold">{{ meal.detectedFoods }}</h3>
          <p class="text-sm text-gray-500">{{ formatDate(meal.mealTime) }}</p>
          <p class="text-sm">총 칼로리: {{ meal.totalCalories }}kcal</p>
          <p class="text-sm">식사 유형: {{ mealTypeKor(meal.mealType) }}</p>
        </div>
        <div class="flex justify-between items-center mt-2 text-sm text-gray-600">
          <span>💬 {{ meal.feedbackCount }} · ❤️ {{ meal.likeCount }}</span>
        </div>
      </div>
    </div>
    <div v-else class="text-center text-gray-500 mt-6">
      해당 유저는 아직 식단을 올리지 않았습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import { useRouter } from 'vue-router'

const meals = ref([])
const followings = ref([])
const selectedUserId = ref(null)
const router = useRouter()
const auth = useAuthStore()

const filteredFollowings = computed(() => followings.value.filter(user => user.userType === 2))

const getImageUrl = (path) => {
  if (!path) return '/images/default-profile.png'
  return `http://localhost:8080${path.startsWith('/') ? path : '/' + path}`
}

const formatDate = (datetime) => new Date(datetime).toLocaleDateString('ko-KR')
const mealTypeKor = (type) => {
  switch (type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}

const loadAllMeals = async () => {
  try {
    const res = await axios.get('/api/meal/followings')
    meals.value = res.data.data
  } catch (e) {
    console.error('팔로워 식단 로딩 실패:', e)
  }
}

const loadFollowings = async () => {
  try {
    const res = await axios.get('/api/follow/following')
    followings.value = res.data.data
  } catch (e) {
    console.error('팔로우 목록 로딩 실패:', e)
  }
}

const selectUser = async (userId) => {
  if (selectedUserId.value === userId) {
    selectedUserId.value = null
    await loadAllMeals()
    return
  }

  selectedUserId.value = userId
  try {
    const res = await axios.get(`/api/users/user/${userId}/meals`)
    meals.value = res.data.data
  } catch (e) {
    console.error('선택 유저 식단 조회 실패:', e)
  }
}

const goToDetail = (mealId) => router.push(`/meal/${mealId}`)

onMounted(async () => {
  await loadFollowings()
  await loadAllMeals()
})
</script>
