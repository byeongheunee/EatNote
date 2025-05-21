<template>
  <div class="mt-8">
    <h2 class="text-lg font-bold mb-2">빠른 메뉴</h2>
    <ul class="flex flex-col gap-2 text-base">
      <li>
        <RouterLink to="/meal/upload" class="hover:underline hover:text-green-600">
          식단 등록하기
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/meals" class="hover:underline hover:text-green-600">
          피드백 확인하기
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/videos" class="hover:underline hover:text-green-600">
          추천 운동 보기
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/community" class="hover:underline hover:text-green-600">
          커뮤니티 방문하기
        </RouterLink>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
ul {
  list-style: none;
  padding: 0;
}
</style>