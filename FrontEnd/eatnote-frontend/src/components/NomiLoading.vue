<template>
  <div class="nomi-loading-container">
    <div class="nomi-wrapper">
      <svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg" :class="sizeClass">
        <!-- 배경 -->
        <circle cx="200" cy="200" r="180" fill="#f0fdf4" opacity="0.3"/>
        
        <!-- 노미의 몸체 (노트 모양) -->
        <rect x="140" y="160" width="120" height="140" rx="15" ry="15" 
              fill="#fef3c7" stroke="#fbbf24" stroke-width="3" 
              class="body-bounce"/>
        
        <!-- 노트 구멍들 -->
        <circle cx="155" cy="180" r="3" fill="#ffffff"/>
        <circle cx="155" cy="200" r="3" fill="#ffffff"/>
        <circle cx="155" cy="220" r="3" fill="#ffffff"/>
        <circle cx="155" cy="240" r="3" fill="#ffffff"/>
        <circle cx="155" cy="260" r="3" fill="#ffffff"/>
        
        <!-- 노트 선들 -->
        <line x1="170" y1="190" x2="240" y2="190" stroke="#fed7aa" stroke-width="1.5"/>
        <line x1="170" y1="205" x2="240" y2="205" stroke="#fed7aa" stroke-width="1.5"/>
        <line x1="170" y1="220" x2="240" y2="220" stroke="#fed7aa" stroke-width="1.5"/>
        <line x1="170" y1="235" x2="240" y2="235" stroke="#fed7aa" stroke-width="1.5"/>
        <line x1="170" y1="250" x2="240" y2="250" stroke="#fed7aa" stroke-width="1.5"/>
        <line x1="170" y1="265" x2="240" y2="265" stroke="#fed7aa" stroke-width="1.5"/>
        
        <!-- 노미의 얼굴 -->
        <!-- 눈 (깜빡이는 애니메이션) -->
        <circle cx="180" cy="200" r="12" fill="#1f2937" class="eye-blink"/>
        <circle cx="220" cy="200" r="12" fill="#1f2937" class="eye-blink"/>
        
        <!-- 눈의 하이라이트 -->
        <circle cx="184" cy="196" r="4" fill="#ffffff" class="eye-blink"/>
        <circle cx="224" cy="196" r="4" fill="#ffffff" class="eye-blink"/>
        
        <!-- 입 (미소) -->
        <path d="M 175 225 Q 200 245 225 225" stroke="#1f2937" stroke-width="4" 
              fill="none" stroke-linecap="round"/>
        
        <!-- 볼 홍조 -->
        <circle cx="160" cy="215" r="8" fill="#fca5a5" opacity="0.6"/>
        <circle cx="240" cy="215" r="8" fill="#fca5a5" opacity="0.6"/>
        
        <!-- 연필 안테나 (회전 애니메이션) -->
        <g class="antenna-spin">
          <line x1="200" y1="160" x2="200" y2="130" stroke="#fbbf24" stroke-width="4" stroke-linecap="round"/>
          <polygon points="198,125 200,115 202,125" fill="#ef4444"/>
          <circle cx="200" cy="128" r="3" fill="#6b7280"/>
        </g>
        
        <!-- 연필 안테나 회전 효과선들 -->
        <path d="M 190 120 Q 200 115 210 120" stroke="#fbbf24" stroke-width="1" 
              fill="none" opacity="0.5" class="effect-lines"/>
        <path d="M 192 115 Q 200 110 208 115" stroke="#fbbf24" stroke-width="1" 
              fill="none" opacity="0.3" class="effect-lines"/>
        
        <!-- 목걸이 (숟가락 모양) -->
        <ellipse cx="200" cy="155" rx="15" ry="3" fill="#f59e0b"/>
        <ellipse cx="200" cy="145" rx="4" ry="8" fill="#f59e0b"/>
        <rect x="198" y="140" width="4" height="8" fill="#f59e0b"/>
        
        <!-- 분석 중 표시 점들 -->
        <g class="thinking-dots">
          <circle cx="180" cy="280" r="3" fill="#fbbf24" class="dot-1"/>
          <circle cx="200" cy="280" r="3" fill="#fbbf24" class="dot-2"/>
          <circle cx="220" cy="280" r="3" fill="#fbbf24" class="dot-3"/>
        </g>
        
        <!-- 반짝이 효과 -->
        <g opacity="0.7" class="sparkles">
          <polygon points="150,120 152,125 157,123 152,128 150,133 148,128 143,123 148,125" 
                   fill="#fbbf24" class="sparkle-1"/>
          <polygon points="250,150 251,153 254,152 251,155 250,158 249,155 246,152 249,153" 
                   fill="#fbbf24" class="sparkle-2"/>
          <polygon points="300,220 301,222 303,221 301,223 300,225 299,223 297,221 299,222" 
                   fill="#fbbf24" class="sparkle-3"/>
          <polygon points="100,250 101,252 103,251 101,253 100,255 99,253 97,251 99,252" 
                   fill="#fbbf24" class="sparkle-4"/>
        </g>
        
        <!-- 바닥 그림자 -->
        <ellipse cx="200" cy="320" rx="40" ry="12" fill="#000000" opacity="0.1"/>
      </svg>
    </div>
    
    <!-- 로딩 메시지 -->
    <div v-if="showMessage" class="loading-message">
      <p class="message-text">{{ currentMessage }}</p>
      <div v-if="showProgress" class="progress-bar">
        <div class="progress-fill" :style="`width: ${progress}%`"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large', 'xl'].includes(value)
  },
  isLoading: {
    type: Boolean,
    default: true
  },
  showMessage: {
    type: Boolean,
    default: true
  },
  showProgress: {
    type: Boolean,
    default: true
  },
  messages: {
    type: Array,
    default: () => [
      '사진을 분석하고 있어요! 🔍',
      '음식을 찾고 있어요! 🍽️',
      '영양 정보를 계산하고 있어요! 📊',
      '거의 다 끝났어요! ✨',
      '완료되었어요! 🎉'
    ]
  }
})

const currentMessage = ref('')
const progress = ref(0)

const sizeClass = computed(() => `nomi-loading-${props.size}`)

let messageInterval = null
let progressInterval = null

const startLoading = () => {
  let messageIndex = 0
  let currentProgress = 0
  
  // 메시지 변경 (3초마다)
  if (props.showMessage && props.messages.length > 1) {
    messageInterval = setInterval(() => {
      currentMessage.value = props.messages[messageIndex]
      messageIndex = (messageIndex + 1) % props.messages.length
    }, 3000)
  }
  
  // 프로그레스 바 진행 (600ms마다)
  if (props.showProgress) {
    progressInterval = setInterval(() => {
      if (currentProgress < 85) { // 85%까지만 먼저 진행
        currentProgress += Math.random() * 0.8 // 더 작은 증가량
        progress.value = Math.min(currentProgress, 85)
      }
    }, 600) // 100ms → 600ms로 변경
  }
  
  // 초기 메시지 설정
  if (props.showMessage) {
    currentMessage.value = props.messages[0] || '로딩 중...'
  }
}

const stopLoading = () => {
  if (messageInterval) {
    clearInterval(messageInterval)
    messageInterval = null
  }
  if (progressInterval) {
    clearInterval(progressInterval)
    progressInterval = null
  }
  if (props.showProgress) {
    progress.value = 100
  }
  if (props.showMessage) {
    currentMessage.value = '완료되었어요! 🎉'
  }
}

onMounted(() => {
  if (props.isLoading) {
    startLoading()
  }
})

onUnmounted(() => {
  stopLoading()
})

// props 변화 감지
watch(() => props.isLoading, (newValue) => {
  if (newValue) {
    startLoading()
  } else {
    stopLoading()
  }
})
</script>

<style scoped>
.nomi-loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.nomi-wrapper {
  display: inline-block;
}

.nomi-loading-small {
  width: 120px;
  height: 120px;
}

.nomi-loading-medium {
  width: 180px;
  height: 180px;
}

.nomi-loading-large {
  width: 240px;
  height: 240px;
}

.nomi-loading-xl {
  width: 300px;
  height: 300px;
}

/* 몸체 바운스 애니메이션 */
.body-bounce {
  animation: bounce 2s ease-in-out infinite;
  transform-origin: center bottom;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 연필 안테나 회전 */
.antenna-spin {
  animation: spin 3s linear infinite;
  transform-origin: 200px 145px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 눈 깜빡임 */
.eye-blink {
  animation: blink 3s ease-in-out infinite;
}

@keyframes blink {
  0%, 90%, 100% { opacity: 1; }
  95% { opacity: 0; }
}

/* 생각 중 점들 */
.thinking-dots .dot-1 {
  animation: dot-bounce 1.4s ease-in-out infinite;
}

.thinking-dots .dot-2 {
  animation: dot-bounce 1.4s ease-in-out infinite 0.2s;
}

.thinking-dots .dot-3 {
  animation: dot-bounce 1.4s ease-in-out infinite 0.4s;
}

@keyframes dot-bounce {
  0%, 60%, 100% { transform: translateY(0px); opacity: 0.4; }
  30% { transform: translateY(-15px); opacity: 1; }
}

/* 반짝이 효과 */
.sparkles .sparkle-1 {
  animation: sparkle 2s ease-in-out infinite;
}

.sparkles .sparkle-2 {
  animation: sparkle 2s ease-in-out infinite 0.5s;
}

.sparkles .sparkle-3 {
  animation: sparkle 2s ease-in-out infinite 1s;
}

.sparkles .sparkle-4 {
  animation: sparkle 2s ease-in-out infinite 1.5s;
}

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0); }
  50% { opacity: 1; transform: scale(1); }
}

/* 효과선 애니메이션 */
.effect-lines {
  animation: fade-in-out 2s ease-in-out infinite;
}

@keyframes fade-in-out {
  0%, 100% { opacity: 0.2; }
  50% { opacity: 0.8; }
}

/* 로딩 메시지 스타일 */
.loading-message {
  text-align: center;
  max-width: 300px;
}

.message-text {
  font-size: 1.1rem;
  color: #374151;
  margin-bottom: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #f3f4f6;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #fbbf24, #f59e0b);
  border-radius: 4px;
  transition: width 0.3s ease;
}

/* 반응형 */
@media (max-width: 768px) {
  .message-text {
    font-size: 1rem;
  }
}
</style>