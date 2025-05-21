<!-- src/components/Trainer/PendingFollowRequests.vue -->
<template>
  <div class="p-4 bg-yellow-50 rounded">
    <h2 class="text-xl font-bold mb-4">📥 팔로우 요청 목록</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="users.length === 0">팔로우 요청이 없습니다.</div>
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="user in users" :key="user.followId"
        class="bg-white rounded-lg p-3 shadow hover:bg-gray-50 flex flex-col items-center space-y-2">
        <img :src="getProfileImage(user.profileImage)" class="w-20 h-20 rounded-full object-cover" />
        <p class="font-semibold text-lg">{{ user.nickname }}</p>
        <p class="text-sm text-gray-600">{{ user.gender }}</p>
        <div class="flex space-x-2 mt-2">
          <button class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
            @click="acceptRequest(user.followId)">
            수락
          </button>
          <button class="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
            @click="rejectRequest(user.followId)">
            거절
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const users = ref([])
const loading = ref(true)

const getProfileImage = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchRequests = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/trainer/follow/requests', {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = res.data.data || []
  } catch (e) {
    console.error('팔로우 요청 조회 실패', e)
  } finally {
    loading.value = false
  }
}

const acceptRequest = async (followId) => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.post('/api/follow/respond', {
      followId,
      action: 'ACCEPT'
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = users.value.filter(u => u.followId !== followId)
  } catch (e) {
    console.error('팔로우 수락 실패', e)
  }
}

const rejectRequest = async (followId) => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.post('/api/follow/respond', {
      followId,
      action: 'REJECT'
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = users.value.filter(u => u.followId !== followId)
  } catch (e) {
    console.error('팔로우 거절 실패', e)
  }
}

onMounted(fetchRequests)
</script>

<style scoped>
/* 필요시 커스터마이징 */
</style>
