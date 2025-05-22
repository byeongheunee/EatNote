<template>
  <div>
    <h3 class="text-xl font-semibold mt-6 mb-4">👥 내가 팔로우한 사람들</h3>

    <div v-if="following.length === 0" class="text-gray-500">팔로우한 사용자가 없습니다.</div>

    <div v-else class="flex flex-wrap gap-6">
      <div
        v-for="user in following"
        :key="user.nickname"
        @click="$emit('open-profile', user)"
        class="flex flex-col items-center w-36 p-4 bg-white border rounded-2xl shadow hover:shadow-md cursor-pointer transition"
      >
        <!-- 프로필 이미지 -->
        <img
          :src="getProfileImage(user.profileImage)"
          alt="프로필"
          class="w-20 h-20 rounded-full object-cover mb-2 border"
        />

        <!-- 닉네임 -->
        <p class="font-semibold text-center text-base">{{ user.nickname }}</p>

        <!-- 유저 타입 -->
        <p class="text-sm text-gray-500">
          {{ user.userType === 1 ? '🏋️ 트레이너' : '👤 일반회원' }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const following = ref([])

// emits 정의
defineEmits(['open-profile'])

const getProfileImage = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchFollowing = async () => {
  try {
    const res = await axios.get('/api/follow/following', {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
    following.value = res.data.data
  } catch (err) {
    console.error('팔로우 목록 조회 실패', err)
  }
}

onMounted(fetchFollowing)
</script>
