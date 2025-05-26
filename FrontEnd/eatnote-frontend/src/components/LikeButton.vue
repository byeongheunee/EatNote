<template>
  <button @click.stop="toggleLike" class="flex items-center gap-1">
    <span v-if="reaction === 'LIKE'">👍</span>
    <span v-else>👍</span>
    <span class="text-sm text-gray-600">{{ count }}</span>
  </button>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
const toast = useToast()
const auth = useAuthStore()
const router = useRouter()

// Props 정의
const props = defineProps({
  contentType: { type: String, required: true },
  contentId: { type: [Number, String], required: true },
  likeCount: { type: Number, default: 0 },
  myReaction: { type: [String, null], default: null, required: true },
  onUpdated: { type: Function, default: () => { } }
})

// 반응형 상태
const reaction = ref(props.myReaction)
const count = ref(props.likeCount)

// props가 바뀔 때 반영
watch(() => props.myReaction, (val) => reaction.value = val)
watch(() => props.likeCount, (val) => count.value = val)

// 좋아요 토글
const toggleLike = async () => {
  if (!auth.isLoggedIn) {
    // alert('좋아요를 누르려면 로그인해야 합니다.')
    toast.warning('좋아요를 누르려면 로그인해야 합니다. 💡')
    router.push({ path: '/login', query: { redirect: `/meal/${props.contentId}` } })
    return
  }

  try {
    const token = localStorage.getItem('accessToken')

    await axios.post('/api/likes', {
      contentType: props.contentType,
      contentId: props.contentId,
      likeType: 'LIKE'
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })

    // UI 즉시 반영
    if (reaction.value === 'LIKE') {
      reaction.value = null
      count.value--
    } else {
      reaction.value = 'LIKE'
      count.value++
    }

    props.onUpdated()
  } catch (err) {
    console.error('좋아요 토글 실패:', err)
    toast.error('좋아요 처리에 실패했습니다. 다시 시도해주세요.')
  }
}
</script>

<style scoped>
button {
  transition: transform 0.1s ease;
}

button:active {
  transform: scale(1.05);
}
</style>
