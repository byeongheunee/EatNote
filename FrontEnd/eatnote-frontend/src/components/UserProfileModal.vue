<template>
  <div v-if="visible" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-xl p-6 w-[90%] max-w-md relative">
      <!-- 닫기 버튼 -->
      <button @click="$emit('close')" class="absolute top-3 right-3 text-gray-400 text-xl font-bold">×</button>

      <div class="text-center">
        <img :src="getProfileImage(profile.profileImage)" class="w-20 h-20 rounded-full mx-auto mb-2" />
        <h2 class="text-xl font-bold">{{ profile.nickname }}</h2>
        <p class="text-sm text-gray-500 mb-4">{{ profile.gender === 'M' ? '남성' : '여성' }}</p>
      </div>

      <!-- 트레이너 정보 -->
      <div v-if="isTrainer" class="space-y-2 text-sm text-gray-700">
        <p><strong>소개:</strong> {{ profile.introduction || '소개 없음' }}</p>
        <p><strong>헬스장:</strong> {{ profile.gymName || '정보 없음' }}</p>
        <p><strong>홈페이지:</strong> 
          <a :href="profile.gymWebsite" target="_blank" class="text-blue-500 underline">
            {{ profile.gymWebsite || '없음' }}
          </a>
        </p>
        <p><strong>인스타그램:</strong>
          <a :href="profile.instagramUrl" target="_blank" class="text-pink-500 underline">
            {{ profile.instagramUrl || '없음' }}
          </a>
        </p>
        <p><strong>총 피드백 수:</strong> {{ profile.totalFeedbacks }}</p>
        <p><strong>팔로잉 수:</strong> {{ profile.followingCount }}</p>
      </div>

      <!-- 일반 회원 정보 -->
      <div v-else class="space-y-2 text-sm text-gray-700">
        <p><strong>목표:</strong> {{ profile.goal || '미설정' }}</p>
        <p><strong>알레르기:</strong> {{ profile.allergyIds?.length ? profile.allergyIds.join(', ') : '없음' }}</p>
        <p><strong>총 식단 기록:</strong> {{ profile.totalMeals }}</p>
        <p><strong>평균 식단 점수:</strong> {{ profile.averageScore ?? '-' }}</p>
        <p><strong>연속 식단 기록일:</strong> {{ profile.consecutiveDays }}일</p>
        <p><strong>팔로잉 수:</strong> {{ profile.followingCount }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: Boolean,
  profile: Object,
  isTrainer: Boolean,
})

const defaultProfile = '/images/default-profile.png'
const getProfileImage = (path) => path ? `http://localhost:8080${path}` : defaultProfile
</script>
