<template>
  <div class="p-6 max-w-6xl mx-auto">
    <Header />
    <h1 class="text-2xl font-bold mb-6">🥗 트레이너 피드백 관리</h1>

    <!-- 👤 유저 목록 -->
    <TrainerFolloweeList :users="users" :selected-user-id="selectedUserId" :total-pending="pendingMeals.length"
      :total-done="calendarStats.reduce((sum, s) => sum + s.written, 0)" @select="selectUser" />

    <!-- 🟡 초기: 전체 미작성 식단 -->
    <div v-if="!selectedUserId && pendingMeals.length > 0" class="mt-6">
      <h2 class="text-lg font-semibold mb-2">🟡 전체 피드백 미작성 식단</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <TrainerMealCard v-for="meal in pendingMeals" :key="meal.mealId" :meal="meal" :highlightPending="true"
          @feedback="goToFeedbackForm" @edit="editFeedback" />
      </div>
    </div>

    <!-- 👤 선택된 유저의 전체 식단 목록 -->
    <div v-if="selectedUserId" class="mt-6">
      <h2 class="text-lg font-semibold mb-2">👤 {{ selectedUserNickname }}님의 식단</h2>
      <div v-if="meals.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <TrainerMealCard v-for="meal in visibleMeals" :key="meal.mealId" :meal="meal"
          :highlightPending="!meal.isFeedbackedByMe" @feedback="goToFeedbackForm" @view="goToMealDetail" />
      </div>
      <div v-if="visibleMeals.length < meals.length" class="text-center mt-4">
        <button @click="showMore" class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">
          더보기
        </button>
      </div>
      <div v-if="meals.length === 0" class="text-gray-500 mt-4 text-center">식단 데이터가 없습니다.</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import TrainerFolloweeList from '@/components/Trainer/TrainerFolloweeList.vue'
import TrainerMealCard from '@/components/Trainer/TrainerMealCard.vue'
import Header from '@/components/common/Header.vue'
import { useToast } from 'vue-toastification'
const toast = useToast()

const visibleCount = ref(4)

const visibleMeals = computed(() => {
  return meals.value.slice(0, visibleCount.value)
})

const showMore = () => {
  visibleCount.value = Math.min(visibleCount.value + 4, meals.value.length)
}

const users = ref([])
const pendingMeals = ref([])
const selectedUserId = ref(null)
const selectedUserNickname = ref('')
const meals = ref([])
const calendarStats = ref([])
const selectedMonth = ref(new Date().toISOString().slice(0, 7))
const router = useRouter()

const editFeedback = (feedbackId) => {
  router.push(`/trainer/feedback/${feedbackId}/edit`)
}


const authHeader = {
  headers: {
    Authorization: `Bearer ${localStorage.getItem('accessToken')}`
  }
}

const goToFeedbackForm = (mealId) => {
  router.push(`/trainer/feedback/${mealId}`)
}

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}`)
}


const selectUser = async (userId) => {
  if (userId === null) {
    selectedUserId.value = null
    selectedUserNickname.value = ''
    return
  }

  selectedUserId.value = userId
  const selected = users.value.find(u => u.userId === userId)
  selectedUserNickname.value = selected?.nickname || ''

  try {
    const res = await axios.get(`/api/users/user/${userId}/meals`, authHeader)

    const sortedMeals = (res.data.data || [])
      .filter(m => m !== null)
      .sort((a, b) => {
        const aPending = !(a.isFeedbackedByMe === true || a.isFeedbackedByMe === 'true')
        const bPending = !(b.isFeedbackedByMe === true || b.isFeedbackedByMe === 'true')

        if (aPending && !bPending) return -1
        if (!aPending && bPending) return 1

        return new Date(b.mealTime) - new Date(a.mealTime)
      })

    meals.value = sortedMeals
    visibleCount.value = 4
  } catch (e) {
    console.error('식단 조회 실패', e)
    // alert('선택한 회원의 식단을 불러올 수 없습니다.')
    toast.error('해당 회원의 식단을 불러오는 데 실패했습니다.')
  }
}

const fetchCalendarStats = async () => {
  try {
    const res = await axios.get('/api/trainer/feedback/statistics', {
      ...authHeader,
      params: { month: selectedMonth.value }
    })
    calendarStats.value = res.data.data || []
    console.log('📅 calendarStats:', calendarStats.value)
  } catch (e) {
    // console.error('달력 통계 조회 실패', e)
    toast.warning('달력 통계를 불러오지 못했어요. \n 화면을 새로고침 해보세요. 🔁')
  }
}

onMounted(async () => {
  try {
    const pendingRes = await axios.get('/api/trainer/pending', authHeader)
    pendingMeals.value = pendingRes.data.data || []

    const userRes = await axios.get('/api/trainer/feedback/followings', authHeader)
    users.value = (userRes.data.data || [])
      .filter(u => u !== null)
      .sort((a, b) => (b.pendingCount || 0) - (a.pendingCount || 0))

    await fetchCalendarStats()
  } catch (e) {
    console.error('초기 데이터 로딩 실패', e)
  }
})
</script>

<style scoped></style>
