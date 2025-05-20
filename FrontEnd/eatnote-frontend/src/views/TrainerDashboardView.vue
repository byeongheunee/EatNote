<template>
  <div class="p-6 space-y-10">
    <h1 class="text-2xl font-bold text-gray-800">트레이너 대시보드</h1>

    <TodayPendingMeals />
    <FolloweeList />
    <FeedbackCalendar />
  </div>
</template>

<script setup>
import TodayPendingMeals from '@/components/Trainer/TodayPendingMeals.vue'
import FolloweeList from '@/components/Trainer/FolloweeList.vue'
import FeedbackCalendar from '@/components/Trainer/FeedbackCalendar.vue'


</script>

<style scoped>
/* 원하는 레이아웃 스타일 추가 가능 */
</style>
