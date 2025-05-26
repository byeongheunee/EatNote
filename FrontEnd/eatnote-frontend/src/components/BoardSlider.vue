<template>
  <div class="board-slider-container">
    <div class="board-slider-wrapper">
      <!-- 왼쪽 화살표 버튼 -->
      <button v-show="showLeftArrow" @click="scrollLeft" class="nav-button nav-button-left" :disabled="!canScrollLeft">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
        </svg>
      </button>

      <!-- 슬라이더 콘텐츠 -->
      <div ref="sliderContainer" class="slider-content" @scroll="handleScroll">
        <div class="slider-track">
          <div v-for="board in boards" :key="board.boardId" class="board-item">
            <button @click="selectBoard(board.boardId)" :class="[
              'board-tab',
              board.boardId === selectedBoardId ? 'board-tab-active' : 'board-tab-inactive'
            ]">
              <span class="board-icon">{{ getBoardIcon(board.name) }}</span>
              <span class="board-name">{{ board.name }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 오른쪽 화살표 버튼 -->
      <button v-show="showRightArrow" @click="scrollRight" class="nav-button nav-button-right"
        :disabled="!canScrollRight">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </button>
    </div>

    <!-- 인디케이터 -->
    <div v-if="showIndicators" class="slider-indicators">
      <div v-for="(indicator, index) in indicators" :key="index" :class="[
        'indicator-dot',
        indicator.active ? 'indicator-active' : 'indicator-inactive'
      ]"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'

const props = defineProps({
  boards: Array,
  selectedBoardId: Number
})

const emit = defineEmits(['selectBoard'])

const sliderContainer = ref(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)
const showLeftArrow = ref(false)
const showRightArrow = ref(false)

const scrollAmount = 200

const showIndicators = computed(() => {
  return props.boards && props.boards.length > 4
})

const indicators = computed(() => {
  if (!props.boards || props.boards.length <= 4) return []
  const totalPages = Math.ceil(props.boards.length / 4)
  const currentPage = Math.floor(getCurrentScrollPage())
  return Array.from({ length: totalPages }, (_, index) => ({
    active: index === currentPage
  }))
})

const getCurrentScrollPage = () => {
  if (!sliderContainer.value) return 0
  const scrollLeft = sliderContainer.value.scrollLeft
  const containerWidth = sliderContainer.value.clientWidth
  return scrollLeft / (containerWidth * 0.8)
}

const updateScrollState = () => {
  if (!sliderContainer.value) return
  const container = sliderContainer.value
  const scrollLeft = container.scrollLeft
  const maxScrollLeft = container.scrollWidth - container.clientWidth
  canScrollLeft.value = scrollLeft > 5
  canScrollRight.value = scrollLeft < maxScrollLeft - 5
  const hasOverflow = container.scrollWidth > container.clientWidth
  showLeftArrow.value = hasOverflow && canScrollLeft.value
  showRightArrow.value = hasOverflow && canScrollRight.value
}

const checkInitialOverflow = () => {
  if (!sliderContainer.value) return
  const container = sliderContainer.value
  const hasOverflow = container.scrollWidth > container.clientWidth
  if (hasOverflow) {
    showRightArrow.value = true
    showLeftArrow.value = false
  }
}

const handleScroll = () => {
  updateScrollState()
}

const scrollLeft = () => {
  if (!sliderContainer.value) return
  sliderContainer.value.scrollBy({
    left: -scrollAmount,
    behavior: 'smooth'
  })
}

const scrollRight = () => {
  if (!sliderContainer.value) return
  sliderContainer.value.scrollBy({
    left: scrollAmount,
    behavior: 'smooth'
  })
}

const selectBoard = (boardId) => {
  emit('selectBoard', boardId)
  nextTick(() => {
    scrollToActiveTab()
  })
}

const scrollToActiveTab = () => {
  if (!sliderContainer.value) return
  const activeTab = sliderContainer.value.querySelector('.board-tab-active')
  if (!activeTab) return
  const container = sliderContainer.value
  const tabRect = activeTab.getBoundingClientRect()
  const containerRect = container.getBoundingClientRect()
  if (tabRect.right > containerRect.right) {
    container.scrollBy({
      left: tabRect.right - containerRect.right + 20,
      behavior: 'smooth'
    })
  } else if (tabRect.left < containerRect.left) {
    container.scrollBy({
      left: tabRect.left - containerRect.left - 20,
      behavior: 'smooth'
    })
  }
}

const handleResize = () => {
  checkInitialOverflow()
  updateScrollState()
}

const getBoardIcon = (boardName) => {
  const iconMap = {
    '설문조사': '📊',
    '열린게시판': '💬',
    '의학게시판': '🏥',
    '우리밥보기': '🍽️',
    '자유게시판': '✨',
    '공지사항': '📢',
    '질문답변': '❓',
    '후기': '⭐',
    '팁공유': '💡',
    '식단 공유 게시판': '🍽️',
    '트레이너 전용 게시판': '💪',
    'Q&A 게시판': '❓',
  }
  return iconMap[boardName] || '📝'
}

onMounted(() => {
  nextTick(() => {
    checkInitialOverflow()
    updateScrollState()
    scrollToActiveTab()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 색상 변수 정의 - 일관된 색상 팔레트 */
:root {
  --primary-coral: #FF6B47;
  --primary-salmon: #FF8C69;
  --accent-gold: #FFD700;
  --accent-amber: #FFA500;
  --warm-brown: #8B4513;
  --cream: #FFF8E7;
  --warm-white: #FFFBF0;
  --text-dark: #2D1810;
  --text-medium: #5D4037;
  --text-light: #8D6E63;
  --gradient-primary: linear-gradient(135deg, #FF6B47 0%, #FF8C69 100%);
  --gradient-gold: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  --gradient-warm: linear-gradient(135deg, #FFF8E7 0%, #FFFBF0 100%);
}

/* 슬라이더 컨테이너 */
.board-slider-container {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 
    0 8px 30px rgba(255, 107, 71, 0.1),
    0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 140, 105, 0.2);
  position: relative;
  overflow: hidden;
}

.board-slider-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--gradient-gold);
}

/* 슬라이더 래퍼 */
.board-slider-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 네비게이션 버튼 */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  width: 44px;
  height: 44px;
  background: var(--gradient-primary);
  color: white;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(255, 107, 71, 0.3);
  border: 2px solid var(--accent-gold);
}

.nav-button:hover:not(:disabled) {
  background: var(--gradient-gold);
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 12px 35px rgba(255, 215, 0, 0.4);
}

.nav-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: translateY(-50%);
}

.nav-button-left {
  left: -22px;
}

.nav-button-right {
  right: -22px;
}

/* 슬라이더 콘텐츠 */
.slider-content {
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  scroll-behavior: smooth;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.slider-content::-webkit-scrollbar {
  display: none;
}

/* 슬라이더 트랙 */
.slider-track {
  display: flex;
  gap: 16px;
  padding: 8px 16px;
  min-width: max-content;
}

/* 게시판 아이템 */
.board-item {
  position: relative;
}

/* 게시판 탭 */
.board-tab {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  border-radius: 16px;
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  white-space: nowrap;
  font-weight: 600;
  font-size: 16px;
  min-width: max-content;
  position: relative;
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.board-tab::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.board-tab:hover::before {
  left: 100%;
}

/* 활성 탭 */
.board-tab-active {
  background: var(--gradient-primary);
  color: white !important;
  border-color: var(--accent-gold);
  transform: translateY(-4px);
  box-shadow: 
    0 12px 30px rgba(255, 107, 71, 0.3),
    0 4px 12px rgba(255, 215, 0, 0.2);
  position: relative;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.board-tab-active::after {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: var(--gradient-gold);
  border-radius: 18px;
  z-index: -1;
}

.board-tab-active:hover {
  transform: translateY(-6px);
  box-shadow: 
    0 16px 40px rgba(255, 107, 71, 0.4),
    0 6px 16px rgba(255, 215, 0, 0.3);
  color: white !important;
}

.board-tab-active .board-name {
  color: rgb(20, 20, 20) !important;
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.board-tab-active .board-icon {
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
}

/* 비활성 탭 */
.board-tab-inactive {
  background: rgba(255, 255, 255, 0.95);
  color: var(--text-medium);
  border-color: rgba(255, 140, 105, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.board-tab-inactive:hover {
  background: rgba(255, 255, 255, 1);
  color: var(--text-dark);
  border-color: var(--primary-salmon);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 140, 105, 0.2);
}

.board-tab-inactive .board-name {
  color: var(--text-medium);
  font-weight: 600;
}

.board-tab-inactive:hover .board-name {
  color: var(--text-dark);
}

/* 게시판 아이콘 */
.board-icon {
  font-size: 20px;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  transition: transform 0.3s ease;
}

.board-tab:hover .board-icon {
  transform: scale(1.1) rotate(5deg);
}

/* 게시판 이름 */
.board-name {
  font-weight: inherit;
  letter-spacing: -0.01em;
  transition: color 0.3s ease;
}

/* 인디케이터 */
.slider-indicators {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 140, 105, 0.2);
}

.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 5px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.indicator-active {
  background: var(--gradient-primary);
  transform: scaleX(2);
  box-shadow: 0 2px 8px rgba(255, 107, 71, 0.3);
}

.indicator-inactive {
  background: #d1d5db;
}

.indicator-inactive:hover {
  background: var(--primary-salmon);
  transform: scale(1.2);
}

/* 애니메이션 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.board-slider-container {
  animation: slideInUp 0.6s ease-out;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .nav-button {
    width: 40px;
    height: 40px;
  }
  
  .nav-button-left {
    left: -20px;
  }
  
  .nav-button-right {
    right: -20px;
  }
}

@media (max-width: 768px) {
  .board-slider-container {
    padding: 20px;
  }

  .slider-track {
    gap: 12px;
    padding: 4px 12px;
  }

  .board-tab {
    padding: 12px 20px;
    font-size: 14px;
    gap: 10px;
  }

  .board-icon {
    font-size: 18px;
  }

  .nav-button {
    width: 36px;
    height: 36px;
  }

  .nav-button-left {
    left: -18px;
  }

  .nav-button-right {
    right: -18px;
  }
}

@media (max-width: 640px) {
  .board-slider-container {
    padding: 16px;
  }

  .board-tab {
    padding: 10px 16px;
    font-size: 13px;
    gap: 8px;
  }

  .board-icon {
    font-size: 16px;
  }

  .slider-track {
    gap: 8px;
    padding: 4px 8px;
  }

  .nav-button {
    width: 32px;
    height: 32px;
  }

  .nav-button-left {
    left: -16px;
  }

  .nav-button-right {
    right: -16px;
  }
}

/* 스크롤 페이드 효과 */
@supports (mask-image: linear-gradient(to right, black, black)) {
  .slider-content {
    mask-image: linear-gradient(to right, transparent 0px, black 20px, black calc(100% - 20px), transparent 100%);
  }
}

/* 접근성 */
.board-tab:focus {
  outline: none;
  ring: 3px;
  ring-color: rgba(255, 107, 71, 0.3);
  ring-offset: 2px;
}

/* 터치 디바이스 최적화 */
@media (hover: none) and (pointer: coarse) {
  .board-tab {
    padding: 14px 22px;
  }
  
  .nav-button {
    width: 48px;
    height: 48px;
  }
}
</style>