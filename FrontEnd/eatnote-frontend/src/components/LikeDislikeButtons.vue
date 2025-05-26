<template>
  <div class="flex gap-4 text-xl">
    <!-- 하트 버튼 -->
    <button @click="submit('LIKE')">
      <span v-if="reaction === 'LIKE'">👍</span>
      <span v-else>👍</span>
      <span class="ml-1 text-base text-gray-600">{{ likeCount }}</span>
    </button>

    <!-- 싫어요 버튼은 필요 시 유지 -->
    <button @click="submit('DISLIKE')">
      <span v-if="reaction === 'DISLIKE'">👎</span>
      <span v-else>👎
      </span>
      <span class="ml-1 text-base text-gray-600">{{ dislikeCount }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  contentType: { type: String, required: true },
  contentId: { type: [Number, String], required: true },
  likeCount: { type: Number, default: 0 },
  dislikeCount: { type: Number, default: 0 },
  myReaction: { type: String, default: null },
  onUpdated: { type: Function, default: () => { } }
})

// 내부 반응형 상태로 따로 관리
const reaction = ref(props.myReaction)
console.log('[초기 myReaction 값] →', props.myReaction)

watch(() => props.myReaction, (newVal) => {
  console.log('[myReaction 변경 감지] →', newVal)
  reaction.value = newVal
})

watch(reaction, (val) => {
  console.log('[reaction 값 변경됨] →', val)
})

const submit = async (type) => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.post('/api/likes', {
      contentType: props.contentType,
      contentId: props.contentId,
      likeType: type
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    props.onUpdated() // 부모에서 fetchArticle() 등을 호출하여 최신 myReaction 전달
  } catch (e) {
    console.error('좋아요/싫어요 실패:', e)
  }
}
</script>
