<template>
  <div class="mb-3 border p-2 rounded">
    <p class="text-sm text-gray-600">
      <strong>{{ comment.userNickname }}</strong> · {{ formatDate(comment.createdAt) }}
    </p>
    <p class="text-base">{{ comment.content }}</p>
    <div class="flex gap-2 text-sm text-gray-500 mt-1">
      <button @click="handleLike('LIKE')" class="text-green-600">👍 {{ comment.likeCount }}</button>
      <button @click="handleLike('DISLIKE')" class="text-red-500">👎 {{ comment.dislikeCount }}</button>
    </div>

    <button @click="showReply = !showReply" class="text-blue-500 text-xs mt-1">
      답글 달기
    </button>
    <CommentInput v-if="showReply" :parentCommentId="comment.commentId" :onSubmit="onReload" :targetType="targetType"
      :targetId="targetId" />

    <div v-if="comment.replies && comment.replies.length" class="ml-4 mt-2 border-l pl-3">
      <CommentItem v-for="reply in comment.replies" :key="reply.commentId" :comment="reply" :onReload="onReload"
        :targetType="targetType" :targetId="targetId" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import CommentInput from './CommentInput.vue'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  comment: Object,
  onReload: Function,
  targetType: String,
  targetId: [String, Number]
})

const showReply = ref(false)

const formatDate = (datetime) => {
  const date = new Date(datetime)
  return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`
}

const handleLike = async (type) => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.post('/api/likes', {
      contentType: 'COMMENT',
      contentId: props.comment.commentId,
      likeType: type
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    props.onReload()
  } catch (e) {
    console.error('댓글 좋아요 실패:', e)
  }
}
</script>

<style scoped>
/* 필요 시 스타일 추가 */
</style>
