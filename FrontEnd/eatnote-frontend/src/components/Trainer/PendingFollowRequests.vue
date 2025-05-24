<template>
  <div class="p-4 bg-yellow-50 rounded">
    <h2 class="text-xl font-bold mb-4">📥 팔로우 요청 목록</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else-if="users.length === 0">팔로우 요청이 없습니다.</div>

    <div v-else-if="users.length >= 4" class="relative">
      <!-- 왼쪽 화살표 -->
      <button
        v-if="canScrollLeft"
        class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-white px-2 py-1 shadow rounded-full hover:bg-gray-100"
        @click="scrollLeft"
      >←</button>

      <!-- 슬라이드 카드 -->
      <div ref="scrollContainer" class="overflow-x-auto scrollbar-hide" @scroll="checkScroll">
        <div class="flex gap-4 pb-2">
          <div v-for="user in users" :key="user.followId"
            class="min-w-[280px] flex-shrink-0 bg-white rounded-lg p-3 shadow hover:bg-gray-50 flex flex-col items-center space-y-2">
            <img :src="getProfileImage(user.profileImage)" class="w-20 h-20 rounded-full object-cover" />
            <p class="font-semibold text-lg">{{ user.nickname }}</p>
            <p class="text-sm text-gray-600">{{ user.requestedAt.slice(0, 10) }}</p>
            <div class="flex space-x-2 mt-2">
              <button class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
                @click="acceptRequest(user.followId)">수락</button>
              <button class="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                @click="rejectRequest(user.followId)">거절</button>
            </div>
          </div>
        </div>
      </div>

      <button
        v-if="canScrollRight"
        class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-white px-2 py-1 shadow rounded-full hover:bg-gray-100"
        @click="scrollRight"
      >→</button>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
      <div v-for="user in users" :key="user.followId"
        class="bg-white rounded-lg p-3 shadow hover:bg-gray-50 flex flex-col items-center space-y-2">
        <img :src="getProfileImage(user.profileImage)" class="w-20 h-20 rounded-full object-cover" />
        <p class="font-semibold text-lg">{{ user.nickname }}</p>
        <p class="text-sm text-gray-600">{{ user.requestedAt.slice(0, 10)}}</p>
        <div class="flex space-x-2 mt-2">
          <button class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
            @click="acceptRequest(user.followId)">수락</button>
          <button class="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
            @click="rejectRequest(user.followId)">거절</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import axios from 'axios'

const users = ref([])
const loading = ref(true)

const scrollContainer = ref(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)

const scrollLeft = () => {
  scrollContainer.value?.scrollBy({ left: -1000, behavior: 'smooth' })
}
const scrollRight = () => {
  scrollContainer.value?.scrollBy({ left: 1000, behavior: 'smooth' })
}
const checkScroll = () => {
  const el = scrollContainer.value
  if (!el) return
  canScrollLeft.value = el.scrollLeft > 0
  canScrollRight.value = el.scrollLeft + el.clientWidth < el.scrollWidth - 1
}

const getProfileImage = (path) =>
  path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchRequests = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/trainer/follow/requests', {
      headers: { Authorization: `Bearer ${token}` }
    })
    users.value = res.data.data || []
    await nextTick()
    if (users.value.length >= 4) checkScroll()
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
    await nextTick()
    if (users.value.length >= 4) checkScroll()
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
    await nextTick()
    if (users.value.length >= 4) checkScroll()
  } catch (e) {
    console.error('팔로우 거절 실패', e)
  }
}

onMounted(fetchRequests)

watch(users, () => {
  nextTick(() => {
    if (users.value.length >= 4) checkScroll()
  })
})
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
