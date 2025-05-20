<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">👥 팔로잉 회원 목록</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="users.length === 0">팔로우한 회원이 없습니다.</div>
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div
        v-for="user in users"
        :key="user.userId"
        class="border rounded-lg p-3 shadow hover:bg-gray-50 cursor-pointer flex items-center space-x-4"
        @click="goToMeals(user.userId)"
      >
        <img :src="getProfileImage(user.profileImage)" class="w-12 h-12 rounded-full object-cover" />
        <div>
          <p class="font-semibold text-lg">{{ user.nickname }}</p>
          <p class="text-sm text-gray-600">요청: {{ user.pendingCount }} / 완료: {{ user.writtenCount }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const users = ref([])
const loading = ref(true)
const router = useRouter()

const getProfileImage = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const goToMeals = (userId) => router.push(`/meals/${userId}`)

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/trainer/feedback/followings', {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = res.data.data
  } catch (e) {
    console.error('팔로우 회원 조회 실패', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 스타일은 필요에 따라 추가 가능 */
</style>