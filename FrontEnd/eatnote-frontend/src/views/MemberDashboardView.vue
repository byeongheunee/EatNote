<template>
  <div class="MemberDashboard-container">
    <Header />

    <div class="mt-6 text-xl font-semibold">안녕하세요, {{ userNickname }}님!!</div>

    <MealStatsCard v-if="mealStats" :stats="mealStats" class="my-6" />

    <div class="flex flex-col lg:flex-row gap-6">
      <div class="flex-1">
        <RecentMealsCard :meals="recentMeals" />
        <PendingFollowRequests/>
        <QuickMenuCard class="mt-6" />
      </div>
      <div class="w-full lg:w-1/3">
        <RecentFeedbackCard :feedbacks="recentFeedbacks" />
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import Header from '@/components/common/Header.vue'
import MealStatsCard from '@/components/member/MealStatsCard.vue'
import RecentMealsCard from '@/components/member/RecentMealsCard.vue'
import RecentFeedbackCard from '@/components/member/RecentFeedbackCard.vue'
import QuickMenuCard from '@/components/member/QuickMenuCard.vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import PendingFollowRequests from '@/components/Trainer/PendingFollowRequests.vue'

const auth = useAuthStore()
const token = computed(() => auth.accessToken)
const userNickname = computed(() => auth.user?.nickname || '회원')

const mealStats = ref(null)
const recentMeals = ref([])
const recentFeedbacks = ref([])

const fetchDashboardData = async () => {
  try {
    const headers = {
      Authorization: `Bearer ${token.value}`
    }

    const [statsRes, mealsRes, feedbacksRes] = await Promise.all([
      axios.get('/api/meal/stats', { headers }),
      axios.get('/api/meal/my/recent', { headers }),
      axios.get('/api/meal/my/recent-feedbacks', { headers }),
    ])

    mealStats.value = statsRes.data.data
    recentMeals.value = mealsRes.data.data
    recentFeedbacks.value = feedbacksRes.data.data

    console.log('mealStats:', mealStats.value)
  } catch (e) {
    console.error('대시보드 데이터 로딩 실패:', e.response?.data || e)
  }
}

onMounted(fetchDashboardData)
</script>

<style scoped>
.MemberDashboard-container {
  max-width: 1500px;
  margin: 0 auto;
  padding: 20px;
}
</style>
