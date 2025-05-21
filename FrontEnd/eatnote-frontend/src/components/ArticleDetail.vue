<template>
  <div>
    <!-- 닫기 버튼 -->
    <div class="text-right mb-4">
      <button class="px-4 py-1 text-sm text-gray-600 hover:underline" @click="$emit('close')">
        ← 목록으로 돌아가기
      </button>
    </div>

    <div v-if="article" class="bg-white p-6 rounded shadow mb-10">
      <!-- 제목 + 작성자 -->
      <div class="flex justify-between items-center mb-2">
        <h2 class="text-2xl font-bold">{{ article.title }}</h2>
        <span class="text-gray-500 text-sm">작성자 : {{ article.userNickname }}</span>
      </div>

      <!-- 내용 + 대표 이미지 -->
      <div class="flex flex-col md:flex-row gap-4 mt-4">
        <div class="flex-1 whitespace-pre-line text-gray-800">
          내용 : {{ article.content }}
        </div>
        <img v-if="article.imageUrl" :src="getImageUrl(article.imageUrl)" alt="대표 이미지"
          class="w-full md:w-80 h-auto rounded object-cover shadow" />
      </div>

      <!-- 추가 첨부 이미지 -->
      <div v-if="additionalImages.length" class="mt-6">
        <h4 class="font-semibold mb-2">첨부 이미지</h4>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
          <img v-for="(img, index) in additionalImages" :key="index" :src="getImageUrl(img.filePath)"
            :alt="img.originalName" class="w-full h-40 object-cover rounded shadow" />
        </div>
      </div>

      <!-- 좋아요/싫어요/조회수 -->
      <div class="flex items-center gap-3 text-gray-500 text-sm mt-4">
        <LikeDislikeButtons contentType="ARTICLE" :contentId="props.articleId" :likeCount="article.likeCount"
          :dislikeCount="article.dislikeCount" :myReaction="article.myReaction" :onUpdated="fetchArticle" />
        <span class="flex items-center gap-1"> 👁️ 조회수 {{ article.viewCnt }} </span>
      </div>

      <!-- 댓글 입력 -->
      <h3 class="mt-8 text-lg font-semibold">댓글</h3>
      <CommentInput :parentCommentId="null" :onSubmit="loadComments" targetType="ARTICLE" :targetId="props.articleId" />

      <!-- 댓글 목록 -->
      <div v-if="comments.length">
        <CommentItem v-for="comment in comments" :key="comment.commentId" :comment="comment" :onReload="loadComments"
          targetType="ARTICLE" :targetId="props.articleId" />
      </div>
      <div v-else>
        <p class="text-sm text-gray-500 mt-2">아직 댓글이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import CommentItem from '@/components/CommentItem.vue'
import CommentInput from '@/components/CommentInput.vue'
import LikeDislikeButtons from '@/components/LikeDislikeButtons.vue'

const props = defineProps({
  articleId: Number
})
const emit = defineEmits(['close'])

const article = ref(null)
const additionalImages = ref([])
const comments = ref([])

const fetchArticle = async () => {
  try {
    const token = localStorage.getItem('accessToken') // ✅ 여기 추가해야 함
    const res = await axios.get(`/api/articles/${props.articleId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    article.value = res.data.data
    if (article.value.attachments?.length > 1) {
      additionalImages.value = article.value.attachments.slice(1)
    } else {
      additionalImages.value = []
    }
  } catch (err) {
    console.error('게시글 조회 실패:', err)
  }
}

const loadComments = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/comments', {
      params: {
        targetType: 'ARTICLE',
        targetId: props.articleId
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

const getImageUrl = (path) => `http://localhost:8080${path}`

const increaseViewCount = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.patch(`/api/articles/${props.articleId}/view-count`, null, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    console.log('✅ 조회수 증가 API 호출 완료')
  } catch (e) {
    console.error('❌ 조회수 증가 실패:', e)
  }
}

watch(() => props.articleId, async () => {
  await fetchArticle()
  await loadComments()
})

onMounted(async () => {
  await increaseViewCount()
  await fetchArticle()
  await loadComments()
})
</script>

<style scoped>
/* 필요시 스타일 추가 */
</style>
