<template>
  <div class="flex overflow-x-auto gap-4 py-2">
    <div v-for="user in users.filter(u => u !== null)" :key="user.userId" @click="selectUser(user.userId)"
      class="cursor-pointer text-center">
      <img :src="getProfileImage(user.profileImage)" alt="profile"
        class="w-16 h-16 object-cover rounded-full border-2 mx-auto"
        :class="{ 'border-orange-400': user.userId === selectedUserId }" />
      <p class="mt-1 text-sm">{{ user.nickname }}</p>
      <p class="text-sm text-gray-600">
  🟡 {{ user.pendingCount }}개 대기 | 🟢 {{ user.writtenCount }}개 완료
</p>
    </div>
  </div>
</template>

<script setup>
const props = defineProps(['users', 'selectedUserId'])

console.log('👥 받은 users:', props.users)
const emit = defineEmits(['select'])

const selectUser = (userId) => {
  console.log('👆 유저 선택됨:', userId)
  emit('select', userId)
}

const getProfileImage = (path) =>
  path ? `http://localhost:8080${path}` : '/images/default-profile.png'
</script>

<style scoped>
::-webkit-scrollbar {
  display: none;
}
</style>
