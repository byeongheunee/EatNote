<template>
  <button @click.stop="toggleLike" class="flex items-center gap-1">
    <span v-if="reaction === 'LIKE'">💖</span>
    <span v-else>🤍</span>
    <span class="text-sm text-gray-600">{{ count }}</span>
  </button>
</template>

<script setup>
import { ref, watch, toRefs } from 'vue'
import axios from 'axios'

// Props 정의
const props = defineProps({
  contentType: { type: String, required: true },       // 예: 'ARTICLE', 'MEAL'
  contentId: { type: [Number, String], required: true },
  likeCount: { type: Number, default: 0 },
  myReaction: { type: [String, null], default: null, required: true },         // 'LIKE' 또는 null
  onUpdated: { type: Function, default: () => {} }     // 상위에서 fetch 함수 전달
})
console.log(props)

// // 반응형 상태 설정
const reaction = ref(props.myReaction)
const count = ref(props.likeCount)


// // 💡 초기 mount 시도 반영
// // onMounted(() => {
// //   reaction.value = props.myReaction
// //   count.value = props.likeCount
// // })

// props 값이 바뀔 경우 반영
watch(() => props.myReaction, (val) => reaction.value = val)
watch(() => props.likeCount, (val) => count.value = val)

// 좋아요 토글 함수
const toggleLike = async () => {
  try {
    const token = localStorage.getItem('accessToken')

    await axios.post('/api/likes', {
      contentType: props.contentType,
      contentId: props.contentId,
      likeType: 'LIKE' // 항상 'LIKE'로 보내기 (같은 값 → 서버에서 삭제 처리)
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })

    // 프론트에서 즉시 UI 반영
    if (reaction.value === 'LIKE') {
      reaction.value = null
      count.value--
    } else {
      reaction.value = 'LIKE'
      count.value++
    }

    props.onUpdated() // 서버 최신값 동기화 요청
  } catch (err) {
    console.error('좋아요 토글 실패:', err)
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
