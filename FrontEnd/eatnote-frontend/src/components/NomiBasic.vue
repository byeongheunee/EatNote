<template>
  <div class="nomi-container">
    <svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg" :class="sizeClass">
      <!-- 배경 -->
      <circle cx="200" cy="200" r="180" fill="#f0fdf4" opacity="0.3"/>
      
      <!-- 노미의 몸체 (노트 모양) -->
      <rect x="140" y="160" width="120" height="140" rx="15" ry="15" 
            fill="#fef3c7" stroke="#fbbf24" stroke-width="3"/>
      
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
      <!-- 눈 -->
      <circle cx="180" cy="200" r="12" fill="#1f2937"/>
      <circle cx="220" cy="200" r="12" fill="#1f2937"/>
      
      <!-- 눈의 하이라이트 -->
      <circle cx="184" cy="196" r="4" fill="#ffffff"/>
      <circle cx="224" cy="196" r="4" fill="#ffffff"/>
      
      <!-- 입 (미소) -->
      <path d="M 175 225 Q 200 245 225 225" stroke="#1f2937" stroke-width="4" 
            fill="none" stroke-linecap="round"/>
      
      <!-- 볼 홍조 -->
      <circle cx="160" cy="215" r="8" fill="#fca5a5" opacity="0.6"/>
      <circle cx="240" cy="215" r="8" fill="#fca5a5" opacity="0.6"/>
      
      <!-- 연필 안테나 -->
      <line x1="200" y1="160" x2="200" y2="130" stroke="#fbbf24" stroke-width="4" stroke-linecap="round"/>
      <polygon points="198,125 200,115 202,125" fill="#ef4444"/>
      <circle cx="200" cy="128" r="3" fill="#6b7280"/>
      
      <!-- 연필 안테나 효과선들 -->
      <path d="M 190 120 Q 200 115 210 120" stroke="#fbbf24" stroke-width="1" 
            fill="none" opacity="0.5"/>
      <path d="M 192 115 Q 200 110 208 115" stroke="#fbbf24" stroke-width="1" 
            fill="none" opacity="0.3"/>
      
      <!-- 목걸이 (숟가락 모양) -->
      <ellipse cx="200" cy="155" rx="15" ry="3" fill="#f59e0b"/>
      <ellipse cx="200" cy="145" rx="4" ry="8" fill="#f59e0b"/>
      <rect x="198" y="140" width="4" height="8" fill="#f59e0b"/>
      
      <!-- 반짝이 효과 -->
      <g opacity="0.7">
        <polygon points="150,120 152,125 157,123 152,128 150,133 148,128 143,123 148,125" 
                 fill="#fbbf24"/>
        <polygon points="250,150 251,153 254,152 251,155 250,158 249,155 246,152 249,153" 
                 fill="#fbbf24"/>
        <polygon points="300,220 301,222 303,221 301,223 300,225 299,223 297,221 299,222" 
                 fill="#fbbf24"/>
        <polygon points="100,250 101,252 103,251 101,253 100,255 99,253 97,251 99,252" 
                 fill="#fbbf24"/>
      </g>
      
      <!-- 말풍선 (옵션) -->
      <g v-if="showMessage">
        <ellipse cx="320" cy="120" rx="60" ry="35" fill="#fef3c7" stroke="#fbbf24" stroke-width="2"/>
        <polygon points="280,135 270,145 285,140" fill="#fef3c7"/>
        <text x="320" y="115" text-anchor="middle" font-family="Arial, sans-serif" 
              font-size="12" fill="#1f2937">{{ message.line1 }}</text>
        <text x="320" y="130" text-anchor="middle" font-family="Arial, sans-serif" 
              font-size="12" fill="#1f2937">{{ message.line2 }}</text>
      </g>
      
      <!-- 바닥 그림자 -->
      <ellipse cx="200" cy="320" rx="40" ry="12" fill="#000000" opacity="0.1"/>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'medium', // small, medium, large, xl
    validator: (value) => ['small', 'medium', 'large', 'xl'].includes(value)
  },
  showMessage: {
    type: Boolean,
    default: false
  },
  message: {
    type: Object,
    default: () => ({
      line1: '안녕!',
      line2: '오늘은 뭐 먹었어? 😊'
    })
  }
})

const sizeClass = computed(() => `nomi-${props.size}`)
</script>

<style scoped>
.nomi-container {
  display: inline-block;
}

.nomi-small {
  width: 80px;
  height: 80px;
}

.nomi-medium {
  width: 120px;
  height: 120px;
}

.nomi-large {
  width: 180px;
  height: 180px;
}

.nomi-xl {
  width: 240px;
  height: 240px;
}

/* 호버 효과 (옵션) */
.nomi-container:hover svg {
  transform: scale(1.05);
  transition: transform 0.3s ease;
}
</style>