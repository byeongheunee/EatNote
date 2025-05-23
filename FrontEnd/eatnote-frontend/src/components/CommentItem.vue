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

    <div class="flex gap-2 mt-1">
      <button @click="showReply = !showReply" class="text-blue-500 text-xs">
        답글 달기
      </button>
      <button
        v-if="isAuthor"
        @click="handleDelete"
        class="text-red-600 text-xs"
      >
        삭제
      </button>
    </div>

    <CommentInput
      v-if="showReply"
      :parentCommentId="comment.commentId"
      :onSubmit="onReload"
      :targetType="targetType"
      :targetId="targetId"
    />

    <div v-if="comment.replies && comment.replies.length" class="ml-4 mt-2 border-l pl-3">
      <CommentItem v-for="reply in comment.replies" :key="reply.commentId" :comment="reply" :onReload="onReload"
        :targetType="targetType" :targetId="targetId" />
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import CommentInput from './CommentInput.vue'
import CommentItem from './CommentItem.vue'
import { useToast } from 'vue-toastification'
const toast = useToast()

const props = defineProps({
  comment: Object,
  onReload: Function,
  targetType: String,
  targetId: [String, Number]
})

const auth = useAuthStore()
const showReply = ref(false)

// 현재 로그인한 유저와 댓글 작성자가 동일한지 확인
const isAuthor = computed(() => {
  return auth.user?.userId === props.comment.userId
})

// 날짜 포맷 함수
const formatDate = (datetime) => {
  const date = new Date(datetime)
  return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`
}

// 좋아요/싫어요 처리
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
    toast.error('좋아요 처리에 실패했습니다. 다시 시도해주세요.')
  }
}

// 댓글 삭제 처리
const handleDelete = async () => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return

  try {
    const token = localStorage.getItem('accessToken')
    await axios.delete(`/api/comments/${props.comment.commentId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    props.onReload()
    toast.success('댓글이 삭제되었습니다. 🗑️')
  } catch (e) {
    console.error('댓글 삭제 실패:', e)
    // alert('댓글 삭제에 실패했습니다.')
    toast.error('댓글 삭제에 실패했습니다.')
  }
}
</script>

<style scoped>
/* 필요 시 스타일 추가 */
</style>
