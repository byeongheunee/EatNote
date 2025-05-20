<template>
  <div>
    <h2>식단 상세 보기</h2>

    <div v-if="loading">불러오는 중입니다...</div>
    <div v-else-if="error">에러가 발생했습니다: {{ error }}</div>
    <div v-else-if="meal">
      <img :src="getImageUrl(meal.imageUrl)" alt="식단 이미지" class="w-96 mb-4" />

      <p><strong>식사 유형</strong>: {{
      {
      breakfast: '아침',
      lunch: '점심',
      dinner: '저녁',
      extra: '간식'
    }[meal.mealType]
      }}</p>
      <p>
      <strong>작성자</strong>:
      <router-link :to="`/profile/${meal.userId}`">{{ meal.userNickname }}</router-link>
      </p>

      <p><strong>감지된 음식:</strong> {{ meal.detectedFoods }}</p>
      <p><strong>총 칼로리:</strong> {{ meal.totalCalories }} kcal</p>
      <p><strong>탄수화물:</strong> {{ meal.carbohydrates }} g</p>
      <p><strong>단백질:</strong> {{ meal.protein }} g</p>
      <p><strong>지방:</strong> {{ meal.fat }} g</p>
      <p><strong>자동 점수:</strong> {{ meal.autoScore }} 점</p>
      <p><strong>AI 피드백:</strong> {{ meal.aiFeedback }}</p>

      <h3 class="mt-4 font-semibold">트레이너 피드백</h3>
      <p v-if="meal.trainerFeedback">{{ meal.trainerFeedback }}</p>
      <p v-else>트레이너가 피드백 작성중입니다...</p>

      <h3 class="mt-4 font-semibold">좋아요</h3>
      <!-- 좋아요/싫어요 영역 -->
      <LikeDislikeButtons
        contentType="MEAL"
        :contentId="meal.mealId"
        :likeCount="meal.likeCount"
        :dislikeCount="meal.dislikeCount"
        :myReaction="meal.myReaction"
        :onUpdated="loadMeal"
      />

      <h3 class="mt-4 font-semibold">댓글</h3>
      <CommentInput
        :parentCommentId="null"
        :onSubmit="loadComments"
        :targetType="targetType"
        :targetId="mealId"
      />
      <div v-if="comments.length">
        <CommentItem
          v-for="comment in comments"
          :key="comment.commentId"
          :comment="comment"
          :onReload="loadComments"
          :targetType="targetType"
          :targetId="mealId"
        />
      </div>
      <div v-else>
        <p>아직 댓글이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import CommentItem from '@/components/CommentItem.vue'
import CommentInput from '@/components/CommentInput.vue'
import LikeDislikeButtons from '@/components/LikeDislikeButtons.vue'


const route = useRoute()
const mealId = route.params.id

const meal = ref(null)
const comments = ref([])
const loading = ref(true)
const error = ref(null)
const targetType = "MEAL"

const getImageUrl = (path) => `http://localhost:8080${path}`

const loadMeal = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const response = await axios.get(`/api/meal/${mealId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    meal.value = response.data.data
  } catch (err) {
    console.error('식단 불러오기 실패:', err)
  }
}

const loadComments = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/comments', {
      params: {
        targetType: 'MEAL',
        targetId: mealId
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    comments.value = res.data.data
  } catch (e) {
    console.error('댓글 불러오기 실패:', e)
  }
}

onMounted(async () => {
  try {
    await loadMeal()
    await loadComments()
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