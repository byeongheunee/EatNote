<template>
  <div class="flex overflow-x-auto gap-4 pb-4">

    <!-- 🟢 전체 보기 (맨 앞) -->
    <div class="text-center cursor-pointer" :class="{ 'border-orange-400 border-2 rounded-full': !selectedUserId }"
      @click="$emit('select', null)">
      <img :src="defaultProfile" class="w-16 h-16 rounded-full mx-auto" />
      <p class="text-sm mt-1 font-bold">전체</p>
      <p class="text-xs text-gray-600">
        🟡 {{ totalPending || 0 }}개 대기 | 🟢 {{ totalDone || 0 }}개 완료
      </p>
    </div>

    <!-- 👤 유저들 -->
    <div v-for="user in users" :key="user.userId" class="text-center cursor-pointer"
      :class="{ 'border-orange-400 border-2 rounded-full': selectedUserId === user.userId }"
      @click="$emit('select', user.userId)">
      <img :src="user.profileImage || defaultProfile" class="w-16 h-16 rounded-full mx-auto" />
      <p class="text-sm mt-1">{{ user.nickname }}</p>
      <p class="text-xs text-gray-600">
        🟡 {{ user.pendingCount || 0 }}개 대기 | 🟢 {{ user.writtenCount || 0 }}개 완료
      </p>
    </div>
  </div>
</template>

<script setup>
import defaultProfile from '@/default-profile.png'

defineProps({
  users: Array,
  selectedUserId: [Number, null],
  totalPending: Number,
  totalDone: Number
})
</script>
