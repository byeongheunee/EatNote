<template>
  <div class="mt-2">
    <textarea v-model="content" rows="2" class="w-full border rounded p-2 text-sm" placeholder="댓글을 입력하세요"></textarea>
    <div class="flex justify-end mt-1">
      <button @click="submitComment" class="bg-green-500 text-white px-3 py-1 rounded text-sm">등록</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  parentCommentId: { type: Number, default: null },
  onSubmit: { type: Function, default: () => { } },
  targetType: { type: String, required: true },
  targetId: { type: [String, Number], required: true }
})

const content = ref('')

const auth = useAuthStore()
const router = useRouter()

const submitComment = async () => {
  if (!content.value.trim()) return

  if (!auth.isLogin) {
    alert('댓글을 작성하려면 로그인해야 합니다.')
    router.push({ path: '/login', query: { redirect: `/meal/${props.targetId}` } })
    return
  }

  try {
    const token = localStorage.getItem('accessToken')

    await axios.post('/api/comments', {
      content: content.value,
      parentCommentId: props.parentCommentId,
      targetType: props.targetType,
      targetId: props.targetId
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    content.value = ''
    props.onSubmit()
  } catch (err) {
    console.error('댓글 등록 실패:', err)
    if (err.response) {
      console.error('응답 상태코드:', err.response.status)
      console.error('응답 메시지:', err.response.data)
    }
  }
}
</script>


<style scoped>
/* 필요 시 스타일 추가 */
</style>
