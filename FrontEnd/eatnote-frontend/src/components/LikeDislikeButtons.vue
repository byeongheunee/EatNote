<template>
  <div class="reaction-buttons">
    <!-- 좋아요 버튼 -->
    <button @click="submit('LIKE')" class="reaction-button like-button" :class="{ 'active': reaction === 'LIKE' }">
      <div class="icon-container">
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
      
      <span class="count-text">{{ likeCount }}</span>
    </button>

    <!-- 싫어요 버튼 -->
    <button @click="submit('DISLIKE')" class="reaction-button dislike-button" :class="{ 'active': reaction === 'DISLIKE' }">
      <div class="icon-container">
        <!-- 깨진 하트 아이콘 -->
        <div class="broken-heart-icon" :class="{ 'disliked': reaction === 'DISLIKE' }">
          <svg 
            viewBox="0 0 24 24" 
            class="broken-heart-svg"
            :class="{ 'filled': reaction === 'DISLIKE' }"
          >
            <path 
              d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
              fill="currentColor"
            />
            <!-- 깨진 효과를 위한 선들 -->
            <path 
              v-if="reaction === 'DISLIKE'"
              d="M8 8 L16 16 M8 16 L16 8"
              stroke="white"
              stroke-width="1.5"
              stroke-linecap="round"
            />
          </svg>
        </div>
        
        <!-- 플라잉 깨진 하트들 -->
        <div v-for="brokenHeart in flyingBrokenHearts" :key="brokenHeart.id" class="flying-broken-heart" :style="brokenHeart.style">
          💔
        </div>
      </div>
      
      <span class="count-text">{{ dislikeCount }}</span>
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
const flyingHearts = ref([])
const flyingBrokenHearts = ref([])

console.log('[초기 myReaction 값] →', props.myReaction)

watch(() => props.myReaction, (newVal) => {
  console.log('[myReaction 변경 감지] →', newVal)
  reaction.value = newVal
})

watch(reaction, (val) => {
  console.log('[reaction 값 변경됨] →', val)
})

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

// 플라잉 깨진 하트 생성 함수
const createFlyingBrokenHeart = () => {
  const id = Date.now() + Math.random()
  const brokenHeart = {
    id,
    style: {
      left: '50%',
      bottom: '100%',
      transform: 'translateX(-50%)',
      animationDelay: '0s'
    }
  }
  
  flyingBrokenHearts.value.push(brokenHeart)
  
  // 3초 후 제거
  setTimeout(() => {
    flyingBrokenHearts.value = flyingBrokenHearts.value.filter(h => h.id !== id)
  }, 3000)
}

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

    // 애니메이션 효과 추가
    if (type === 'LIKE' && reaction.value !== 'LIKE') {
      createFlyingHeart()
    } else if (type === 'DISLIKE' && reaction.value !== 'DISLIKE') {
      createFlyingBrokenHeart()
    }

    props.onUpdated() // 부모에서 fetchArticle() 등을 호출하여 최신 myReaction 전달
  } catch (e) {
    console.error('좋아요/싫어요 실패:', e)
  }
}
</script>

<style scoped>
.reaction-buttons {
  display: flex;
  gap: 16px;
  align-items: center;
}

.reaction-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border: 2px solid transparent;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  font-size: 18px;
  min-width: 80px;
  justify-content: center;
}

.reaction-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.reaction-button:active {
  transform: translateY(0) scale(0.95);
}

/* 좋아요 버튼 스타일 */
.like-button {
  border-color: rgba(239, 68, 68, 0.2);
}

.like-button:hover {
  background: rgba(254, 242, 242, 0.9);
  border-color: rgba(239, 68, 68, 0.3);
}

.like-button.active {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.05));
  border-color: #ec6060;
  box-shadow: 0 0 10px rgba(239, 68, 68, 0.3);
}

/* 싫어요 버튼 스타일 */
.dislike-button {
  border-color: rgba(107, 114, 128, 0.2);
}

.dislike-button:hover {
  background: rgba(249, 250, 251, 0.9);
  border-color: rgba(107, 114, 128, 0.3);
}

.dislike-button.active {
  background: linear-gradient(135deg, rgba(107, 114, 128, 0.1), rgba(75, 85, 99, 0.05));
  border-color: #6b7280;
  box-shadow: 0 0 10px rgba(107, 114, 128, 0.3);
}

.icon-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 하트 아이콘 */
.heart-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.heart-icon.liked {
  animation: heartBeat 0.6s ease-in-out;
}

.heart-svg {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
  color: #d1d5db;
  stroke: #d1d5db;
  stroke-width: 1.5;
}

.heart-svg.filled {
  color: #ec6060;
  stroke: #ec6060;
  filter: drop-shadow(0 0 8px rgba(239, 68, 68, 0.4));
}

.heart-svg:not(.filled) {
  fill: none;
}

/* 깨진 하트 아이콘 */
.broken-heart-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.broken-heart-icon.disliked {
  animation: heartBreak 0.6s ease-in-out;
}

.broken-heart-svg {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
  color: #d1d5db;
  stroke: #d1d5db;
  stroke-width: 1.5;
}

.broken-heart-svg.filled {
  color: #6b7280;
  stroke: #6b7280;
  filter: drop-shadow(0 0 8px rgba(107, 114, 128, 0.4));
}

.broken-heart-svg:not(.filled) {
  fill: none;
}

.count-text {
  font-size: 16px;
  font-weight: 600;
  color: #6b7280;
  transition: color 0.2s ease;
  line-height: 1;
  min-width: 20px;
  text-align: left;
}

.like-button.active .count-text {
  color: #ec6060;
}

.dislike-button.active .count-text {
  color: #6b7280;
}

/* 플라잉 이모지 애니메이션 */
.flying-heart,
.flying-broken-heart {
  position: absolute;
  font-size: 20px;
  pointer-events: none;
  animation: flyUp 3s ease-out forwards;
  z-index: 10;
}

.flying-broken-heart {
  animation: flyUpShake 3s ease-out forwards;
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

/* 하트 브레이크 애니메이션 */
@keyframes heartBreak {
  0% {
    transform: scale(1) rotate(0deg);
  }
  25% {
    transform: scale(1.2) rotate(-5deg);
  }
  50% {
    transform: scale(1.1) rotate(5deg);
  }
  75% {
    transform: scale(1.2) rotate(-3deg);
  }
  100% {
    transform: scale(1) rotate(0deg);
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
    transform: translateX(-50%) translateY(-15px) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-80px) scale(0.3);
  }
}

/* 플라잉 깨진 하트 애니메이션 (흔들림 효과) */
@keyframes flyUpShake {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(0.8) rotate(0deg);
  }
  15% {
    opacity: 1;
    transform: translateX(-50%) translateY(-15px) scale(1) rotate(-10deg);
  }
  30% {
    transform: translateX(-50%) translateY(-30px) scale(0.9) rotate(10deg);
  }
  45% {
    transform: translateX(-50%) translateY(-45px) scale(0.7) rotate(-5deg);
  }
  60% {
    transform: translateX(-50%) translateY(-60px) scale(0.5) rotate(5deg);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-80px) scale(0.3) rotate(0deg);
  }
}

/* 반응형 조정 */
@media (max-width: 640px) {
  .reaction-buttons {
    gap: 12px;
  }
  
  .reaction-button {
    padding: 10px 14px;
    gap: 6px;
    min-width: 70px;
    font-size: 16px;
  }
  
  .heart-icon,
  .broken-heart-icon {
    width: 20px;
    height: 20px;
  }
  
  .count-text {
    font-size: 14px;
  }
  
  .flying-heart,
  .flying-broken-heart {
    font-size: 16px;
  }
}

/* 접근성 */
.reaction-button:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

.reaction-button:focus:not(:focus-visible) {
  outline: none;
}
</style>