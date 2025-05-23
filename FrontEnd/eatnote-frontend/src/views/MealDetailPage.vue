<template>
  <div>
    <Header />
    <h2>식단 상세 보기</h2>

    <div v-if="loading">불러오는 중입니다...</div>
    <div v-else-if="error">에러가 발생했습니다: {{ error }}</div>

    <div v-else-if="meal">
      <button @click="goBackToMealList" class="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400">
        목록으로 돌아가기
      </button>
      <img :src="getImageUrl(meal.imageUrl)" alt="식단 이미지" class="w-96 mb-4" />

      <p><strong>식사 유형</strong>: {{
        { breakfast: '아침', lunch: '점심', dinner: '저녁', extra: '간식' }[meal.mealType]
        }}</p>
      <p>
        <strong>작성자</strong>:
        <router-link :to="`/profile/${meal.userId}`">{{ meal.userNickname }}</router-link>
      </p>

      <p><strong>감지된 음식:</strong> {{ meal.detectedFoods }}</p>
      <p><strong>총 열량:</strong> {{ meal.totalCalories }} kcal</p>
      <p><strong>탄수화물:</strong> {{ meal.carbohydrates }} g</p>
      <p><strong>단백질:</strong> {{ meal.protein }} g</p>
      <p><strong>지방:</strong> {{ meal.fat }} g</p>
      <p><strong>AI 점수:</strong> {{ meal.autoScore }} 점</p>
      <p><strong>AI 피드백:</strong> {{ meal.aiFeedback }}</p>

      <h3 class="mt-4 font-semibold">트레이너 피드백</h3>
      <div v-if="trainerFeedbacks.length > 0" class="space-y-3">
        <div v-for="feedback in trainerFeedbacks" :key="feedback.feedbackId" class="border rounded p-4" :class="{
          'border-blue-500 bg-blue-50': feedback.isWrittenByMe,
          'border-gray-200 bg-gray-50': !feedback.isWrittenByMe
        }">
          <div class="flex justify-between items-center mb-1">
            <p class="font-semibold">
              👤 {{ feedback.trainerNickname }}
              <span v-if="feedback.isWrittenByMe" class="text-sm text-blue-600">(내가 쓴 피드백)</span>
            </p>
            <div v-if="feedback.isWrittenByMe" class="space-x-2">
              <button @click="goToEditFeedback(feedback.feedbackId)" class="text-sm text-blue-600 hover:underline">✏️
                수정</button>
              <button @click="deleteFeedback(feedback.feedbackId)" class="text-sm text-red-500 hover:underline">🗑
                삭제</button>
            </div>
          </div>
          <p>📝 {{ feedback.comment }}</p>
          <p>⭐ 점수: {{ feedback.trainerScore }}/10</p>
          <p class="text-sm text-gray-500">🕒 {{ new Date(feedback.createdAt).toLocaleString() }}</p>
        </div>
      </div>
      <p v-else>작성된 트레이너 피드백이 없습니다.</p>

      <h3 class="mt-4 font-semibold">좋아요</h3>
      <LikeDislikeButtons contentType="MEAL" :contentId="meal.mealId" :likeCount="meal.likeCount"
        :dislikeCount="meal.dislikeCount" :myReaction="meal.myReaction" :onUpdated="loadMeal" />

      <h3 class="mt-4 font-semibold">댓글</h3>
      <CommentInput :parentCommentId="null" :onSubmit="loadComments" :targetType="targetType" :targetId="mealId" />
      <div v-if="comments.length">
        <CommentItem v-for="comment in comments" :key="comment.commentId" :comment="comment" :onReload="loadComments"
          :targetType="targetType" :targetId="mealId" />
      </div>
      <div v-else>
        <p>아직 댓글이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import CommentItem from '@/components/CommentItem.vue'
import CommentInput from '@/components/CommentInput.vue'
import LikeDislikeButtons from '@/components/LikeDislikeButtons.vue'
import Header from '@/components/common/Header.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()
const router = useRouter()
const route = useRoute()
const mealId = route.params.id

const meal = ref(null)
const comments = ref([])
const trainerFeedbacks = ref([])
const myUserId = ref(null)
const loading = ref(true)
const error = ref(null)
const targetType = "MEAL"

const getImageUrl = (path) => `http://localhost:8080${path}`
const goBackToMealList = () => router.push('/meals')

const extractUserIdFromToken = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1])).userId
  } catch {
    return null
  }
}

const loadMeal = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const response = await axios.get(`/api/meal/${mealId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    meal.value = response.data.data
  } catch (err) {
    console.error('식단 불러오기 실패:', err)
  }
}

const loadTrainerFeedbacks = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      console.warn('토큰이 없습니다. 피드백 요청에 실패할 수 있습니다.')
    }

    const res = await axios.get(`/api/meal/${mealId}/feedback`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    console.log('🥩 받은 피드백:', res.data.data)
    trainerFeedbacks.value = res.data.data || []

  } catch (e) {
    console.error('트레이너 피드백 불러오기 실패:', e)
  }
}

const loadComments = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/comments', {
      params: { targetType: 'MEAL', targetId: mealId },
      headers: { Authorization: `Bearer ${token}` }
    })
    comments.value = res.data.data
  } catch (e) {
    console.error('댓글 불러오기 실패:', e)
  }
}

const goToEditFeedback = (feedbackId) => {
  router.push(`/trainer/feedback/edit/${feedbackId}`)
}

const deleteFeedback = async (feedbackId) => {
  if (!confirm('정말로 이 피드백을 삭제하시겠습니까?')) return
  try {
    const token = localStorage.getItem('accessToken')
    console.log('🔐 token:', token)
    await axios.delete(`/api/trainer/feedback/${feedbackId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    // alert('삭제되었습니다.')
    toast.success('피드백이 삭제되었습니다!')

    await loadTrainerFeedbacks()
  } catch (e) {
    // alert('삭제 실패')
    toast.error('피드백 삭제에 실패했습니다.')
    console.error('삭제 오류:', e)
  }
}

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    myUserId.value = extractUserIdFromToken(token)

    await loadMeal()
    await loadComments()
    await loadTrainerFeedbacks()
  } catch (err) {
    error.value = err.message || '알 수 없는 오류'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}
</style>
