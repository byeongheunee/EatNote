<template>
  <div class="p-6 space-y-10">
    <Header />
    <h1 class="text-2xl font-bold text-gray-800">트레이너 대시보드</h1>
    <TodayPendingMeals />
    <PendingFollowRequests />
    <FeedbackCalendar />
  </div>
</template>

<script setup>
import TodayPendingMeals from '@/components/Trainer/TodayPendingMeals.vue'
import PendingFollowRequests from '@/components/PendingFollowRequests.vue'
import FeedbackCalendar from '@/components/Trainer/FeedbackCalendar.vue'
import Header from '@/components/common/Header.vue'

</script>

<style scoped>
/* 원하는 레이아웃 스타일 추가 가능 */
</style>
