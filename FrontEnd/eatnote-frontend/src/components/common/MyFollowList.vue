<template>
  <div>
    <h3 class="text-xl font-semibold mt-6 mb-4">
      {{ isTrainer ? '관리중인 회원 목록' : '👥 팔로잉' }}
    </h3>

    <div v-if="users.length === 0" class="text-gray-500">
      {{ isTrainer ? '관리중인 회원이 없습니다.' : '팔로우한 사용자가 없습니다.' }}
    </div>

    <div v-else class="flex flex-wrap gap-6">
      <div
        v-for="otherUser in users"
        :key="otherUser.nickname"
        @click="$emit('open-profile', otherUser)"
        class="flex flex-col items-center w-36 p-4 bg-white border rounded-2xl shadow hover:shadow-md cursor-pointer transition"
      >
        <!-- 프로필 이미지 -->
        <img
          :src="getProfileImage(otherUser.profileImage)"
          alt="프로필"
          class="w-20 h-20 rounded-full object-cover mb-2 border"
        />

        <!-- 닉네임 -->
        <p class="font-semibold text-center text-base">{{ otherUser.nickname }}</p>

        <!-- 유저 타입 -->
        <p class="text-sm text-gray-500">
          {{ otherUser.userType === 1 ? '🏋️ 트레이너' : '👤 일반회원' }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const userType = computed(() => auth.user.userType)
const isTrainer = computed(() => userType.value === 1)

const users = ref([])

defineEmits(['open-profile'])

const getProfileImage = (path) =>
  path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchUsers = async () => {
  try {
    const url = isTrainer.value ? '/api/follow/followers' : '/api/follow/following'
    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
    users.value = res.data.data
  } catch (err) {
    console.error('팔로우 목록 조회 실패', err)
  }
}

const refresh = async () => {
  await fetchUsers()
}
defineExpose({ refresh })

onMounted(fetchUsers)
</script>
