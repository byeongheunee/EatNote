<template>
  <div class="mt-10 bg-gray-50 p-4 rounded shadow">
    <h2 class="text-lg font-bold mb-4 text-gray-800">빠른 메뉴</h2>
    <ul class="flex flex-wrap gap-6 justify-center text-base font-medium text-gray-700">
      <li>
        <RouterLink to="/meal/upload" class="quick-link">식단 등록하기</RouterLink>
      </li>
      <li>
        <RouterLink to="/meals" class="quick-link">피드백 확인하기</RouterLink>
      </li>
      <li>
        <RouterLink to="/videos" class="quick-link">추천 운동 보기</RouterLink>
      </li>
      <li>
        <RouterLink to="/community" class="quick-link">커뮤니티 방문하기</RouterLink>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.quick-link {
  font-size: 1.125rem;
  /* text-lg */
  padding: 0.5rem 1rem;
  background-color: #e6f4ea;
  border-radius: 0.5rem;
  transition: background-color 0.3s ease;
}

.quick-link:hover {
  background-color: #c0ebd1;
  text-decoration: underline;
}
</style>
