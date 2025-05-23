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
      <div v-if="profile.userType === 1" class="space-y-2 text-sm text-gray-700">
        <p>트레이너 정보</p>
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
      <div v-else-if="profile.userType === 2" class="space-y-2 text-sm text-gray-700">
        <p>회원 정보</p>
        <p><strong>목표:</strong> {{ profile.goal || '미설정' }}</p>
        <p><strong>알레르기:</strong> {{ profile.allergyIds?.length ? profile.allergyIds.join(', ') : '없음' }}</p>
        <p><strong>총 식단 기록:</strong> {{ profile.totalMeals }}</p>
        <p><strong>평균 식단 점수:</strong> {{ profile.averageScore ?? '-' }}</p>
        <p><strong>연속 식단 기록일:</strong> {{ profile.consecutiveDays }}일</p>
        <p><strong>팔로잉 수:</strong> {{ profile.followingCount }}</p>
      </div>

      <!-- 팔로우 버튼 영역 -->
      <div class="mt-6 text-center">
        <!-- 로그인 유저가 트레이너라면: 무조건 팔로우 끊기만 보여줌 -->
        <template v-if="isTrainer">
          <div class="mt-4 flex flex-col items-center space-y-2">
            <template v-if="profile.followedByOtherStatus === 'ACCEPTED'">
              <p class="text-green-600 font-semibold">👥 {{ profile.nickname }}님이 나를 팔로우 중입니다.</p>
              <button @click="cancelFollow"
                class="px-3 py-1 text-sm bg-red-100 text-red-600 border border-red-300 rounded hover:bg-red-200">
                ❌ 팔로우 끊기
              </button>
            </template>

            <template v-else-if="profile.followedByOtherStatus === 'PENDING'">
              <p class="text-blue-500 font-semibold">📩 {{ profile.nickname }}님의 팔로우 요청</p>
              <div class="flex gap-2">
                <button @click="acceptFollowRequest"
                  class="px-3 py-1 text-sm bg-green-100 text-green-700 border border-green-300 rounded hover:bg-green-200">
                  ✅ 수락
                </button>
                <button @click="rejectFollowRequest"
                  class="px-3 py-1 text-sm bg-red-100 text-red-600 border border-red-300 rounded hover:bg-red-200">
                  ❌ 거절
                </button>
              </div>
            </template>

            <template v-else-if="profile.followedByOtherStatus === 'REJECTED'">
              <p class="text-gray-500 font-semibold">🙅 {{ profile.nickname }}님의 요청을 거절하였습니다.</p>
            </template>

            <template v-else>
              <p class="text-gray-400">🙈 {{ profile.nickname }}님의 팔로우 요청이 없습니다.</p>
            </template>
          </div>
        </template>

        <!-- 일반 사용자일 경우 -->
        <template v-else>
          <!-- 내가 보낸 팔로우 상태 -->
          <template v-if="profile.followStatus === 'ACCEPTED'">
            <div class="flex flex-col items-center space-y-2">
              <p class="text-green-600 font-semibold">✅ 팔로우 중</p>
              <button @click="cancelFollow"
                class="px-3 py-1 text-sm bg-red-100 text-red-600 border border-red-300 rounded hover:bg-red-200">
                ❌ 팔로우 취소
              </button>
            </div>
          </template>
          <template v-else-if="profile.followStatus === 'PENDING'">
            <p class="text-blue-500 font-semibold">⏳ 팔로우 요청 중</p>
          </template>
          <template v-else-if="profile.followStatus === 'REJECTED'">
            <p class="text-red-500 font-semibold">❌ 팔로우 요청이 거절되었습니다</p>
          </template>
          <template v-else>
            <button @click="sendFollowRequest"
              class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
              ➕ 팔로우 요청
            </button>
          </template>

          <!-- 상대방이 나를 팔로우하는 상태 -->
          <div class="mt-4">
            <template v-if="profile.followedByOtherStatus === 'ACCEPTED'">
              <p class="text-green-600 font-semibold">👀 상대방이 나를 팔로우하고 있습니다.</p>
              <!-- <button @click="cancelFollow"
                class="px-3 py-1 text-sm bg-red-100 text-red-600 border border-red-300 rounded hover:bg-red-200">
                ❌ 팔로우 취소
              </button> -->
            </template>
            <template v-else-if="profile.followedByOtherStatus === 'PENDING'">
              <div class="flex flex-col items-center space-y-2">
                <p class="text-blue-500 font-semibold">📩 팔로우 요청이 도착했습니다.</p>
                <div class="flex gap-2">
                  <button @click="acceptFollowRequest"
                    class="px-3 py-1 text-sm bg-green-100 text-green-700 border border-green-300 rounded hover:bg-green-200">
                    ✅ 수락
                  </button>
                  <button @click="rejectFollowRequest"
                    class="px-3 py-1 text-sm bg-red-100 text-red-600 border border-red-300 rounded hover:bg-red-200">
                    ❌ 거절
                  </button>
                </div>
              </div>
            </template>
            <template v-else-if="profile.followedByOtherStatus === 'REJECTED'">
              <p class="text-gray-500 font-semibold">🙅 상대방의 요청을 거절하였습니다.</p>
            </template>
            <template v-else>
              <p class="text-gray-400">🙈 상대방의 팔로우 요청이 없습니다.</p>
            </template>
          </div>


        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const isTrainer = computed(() => auth.user.userType === 1)

const emit = defineEmits(['follow-requested'])

const toast = useToast?.()
const auth = useAuthStore()

const defaultProfile = '/images/default-profile.png'
const getProfileImage = (path) => path ? `http://localhost:8080${path}` : defaultProfile

const props = defineProps({
  visible: Boolean,
  profile: Object,
})

const sendFollowRequest = async () => {
  try {
    const res = await axios.post('/api/follow', {
      nickname: props.profile.nickname,
    }, {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })

    if (res.data?.success) {
      const msg = `${props.profile.nickname}님에게 팔로우를 요청하였습니다.`
      if (toast) toast.success(msg)
      else alert(msg)

      // 버튼 상태를 즉시 반영하고 싶다면:
      // props.profile.followStatus = 'PENDING'
      emit('follow-requested')
    } else {
      const fallback = res.data?.message || '팔로우 요청에 실패했습니다.'
      toast?.error(fallback) ?? alert(fallback)
    }
  } catch (e) {
    console.error('팔로우 요청 실패:', e)
    toast?.error('서버 오류로 요청을 처리할 수 없습니다.') ?? alert('서버 오류 발생')
  }
}

const cancelFollow = async () => {
  try {
    const isTrainer = auth.user.userType === 1
    const url = isTrainer ? '/api/follow/follower' : '/api/follow'

    const res = await axios.delete(url, {
      data: {
        nickname: props.profile.nickname,
      },
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    });

    if (res.data?.success) {
      const msg = isTrainer
        ? `${props.profile.nickname}님을 팔로우 목록에서 제거했습니다.`
        : `${props.profile.nickname}님과의 팔로우가 취소되었습니다.`
      toast?.success(msg) ?? alert(msg)

      emit('follow-requested')

      // 모달 닫기
      emit('close')
    } else {
      const fallback = res.data?.message || '팔로우 취소에 실패했습니다.'
      toast?.error(fallback) ?? alert(fallback)
    }
  } catch (e) {
    console.error('팔로우 취소 실패:', e)
    toast?.error('서버 오류로 요청을 처리할 수 없습니다.') ?? alert('서버 오류 발생')
  }
}


const respondToFollowRequest = async (action) => {
  try {
    const res = await axios.post('/api/follow/respond', {
      followId: props.profile.followId,
      action: action // 'ACCEPT' or 'REJECT'
    })

    if (res.data?.success) {
      const msg = action === 'ACCEPT'
        ? `${props.profile.nickname}님의 팔로우 요청을 수락했습니다.`
        : `${props.profile.nickname}님의 팔로우 요청을 거절했습니다.`
      toast?.success(msg) ?? alert(msg)
      emit('follow-requested')
      emit('close') // 모달 닫기
    } else {
      const fallback = res.data?.message || '요청 처리에 실패했습니다.'
      toast?.error(fallback) ?? alert(fallback)
    }
  } catch (e) {
    console.error(`팔로우 ${action} 실패:`, e)
    toast?.error('서버 오류로 요청을 처리할 수 없습니다.') ?? alert('서버 오류 발생')
  }
}

const acceptFollowRequest = () => respondToFollowRequest('ACCEPT')
const rejectFollowRequest = () => respondToFollowRequest('REJECT')


</script>
