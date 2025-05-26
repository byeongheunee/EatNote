<template>
  <button @click.stop="toggleLike" class="like-button">
    <div class="heart-container">
      <!-- 메인 하트 아이콘 -->
      <div class="heart-icon" :class="{ 'liked': reaction === 'LIKE' }">
        <svg 
          viewBox="0 0 24 24" 
          class="heart-svg"
          :class="{ 'filled': reaction === 'LIKE' }"
        >
          <path 
            d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
            fill="currentColor"
          />
        </svg>
      </div>
      
      <!-- 플라잉 하트들 -->
      <div v-for="heart in flyingHearts" :key="heart.id" class="flying-heart" :style="heart.style">
        ❤️
      </div>
    </div>
    
    <span class="count-text">{{ count }}</span>
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
const flyingHearts = ref([])

// props가 바뀔 때 반영
watch(() => props.myReaction, (val) => reaction.value = val)
watch(() => props.likeCount, (val) => count.value = val)

// 플라잉 하트 생성 함수
const createFlyingHeart = () => {
  const id = Date.now() + Math.random()
  const heart = {
    id,
    style: {
      left: '50%',
      bottom: '100%',
      transform: 'translateX(-50%)',
      animationDelay: '0s'
    }
  }
  
  flyingHearts.value.push(heart)
  
  // 3초 후 제거
  setTimeout(() => {
    flyingHearts.value = flyingHearts.value.filter(h => h.id !== id)
  }, 3000)
}

// 좋아요 토글
const toggleLike = async () => {
  if (!auth.isLoggedIn) {
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
      // 좋아요 시에만 플라잉 하트 생성
      createFlyingHeart()
    }

    props.onUpdated()
  } catch (err) {
    console.error('좋아요 토글 실패:', err)
    toast.error('좋아요 처리에 실패했습니다. 다시 시도해주세요.')
  }
}
</script>

<style scoped>
.like-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: transparent;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.like-button:hover {
  background: rgba(255, 182, 193, 0.1);
  transform: translateY(-1px);
}

.like-button:active {
  transform: translateY(0) scale(0.95);
}

.heart-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.heart-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.heart-icon.liked {
  animation: heartBeat 0.6s ease-in-out;
  transform: scale(1.2);
}

.heart-svg {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
  color: #d1d5db;
  stroke: #9ca3af;
  stroke-width: 1.5;
}

.heart-svg.filled {
  color: #ef4444;
  stroke: #ef4444;
  filter: drop-shadow(0 0 8px rgba(239, 68, 68, 0.3));
}

.heart-svg:not(.filled) {
  fill: none;
}

.count-text {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  transition: color 0.2s ease;
  line-height: 1;
  min-width: 16px;
  text-align: left;
}

.like-button:hover .count-text {
  color: #374151;
}

/* 플라잉 하트 애니메이션 */
.flying-heart {
  position: absolute;
  font-size: 16px;
  pointer-events: none;
  animation: flyUp 3s ease-out forwards;
  z-index: 10;
}

/* 하트 비트 애니메이션 */
@keyframes heartBeat {
  0% {
    transform: scale(1);
  }
  14% {
    transform: scale(1.3);
  }
  28% {
    transform: scale(1);
  }
  42% {
    transform: scale(1.3);
  }
  70% {
    transform: scale(1);
  }
}

/* 플라잉 하트 애니메이션 */
@keyframes flyUp {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(0.8);
  }
  15% {
    opacity: 1;
    transform: translateX(-50%) translateY(-10px) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-60px) scale(0.3);
  }
}

/* 반응형 조정 */
@media (max-width: 640px) {
  .like-button {
    padding: 6px 10px;
    gap: 4px;
  }
  
  .heart-icon {
    width: 18px;
    height: 18px;
  }
  
  .count-text {
    font-size: 13px;
  }
  
  .flying-heart {
    font-size: 14px;
  }
}

/* 접근성 */
.like-button:focus {
  outline: 2px solid #ef4444;
  outline-offset: 2px;
}

.like-button:focus:not(:focus-visible) {
  outline: none;
}
</style>