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
/* 슬라이더 컨테이너 */
.board-slider-container {
  background: white;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  position: relative;
}

/* 슬라이더 래퍼 */
.board-slider-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 네비게이션 버튼 */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  width: 36px;
  height: 36px;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-button:hover:not(:disabled) {
  background: #d97706;
  transform: translateY(-50%) translateY(-1px);
}

.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: translateY(-50%);
}

.nav-button-left {
  left: -18px;
}

.nav-button-right {
  right: -18px;
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
  gap: 12px;
  padding: 4px 12px;
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
  gap: 10px;
  padding: 12px 20px;
  border-radius: 6px;
  border: 1px solid transparent;
  transition: all 0.2s ease;
  cursor: pointer;
  white-space: nowrap;
  font-weight: 500;
  font-size: 14px;
  min-width: max-content;
  position: relative;
}

/* 활성 탭 */
.board-tab-active {
  background: #f59e0b;
  color: white;
  border-color: #d97706;
}

.board-tab-active:hover {
  background: #d97706;
}

.board-tab-active .board-name {
  color: white;
  font-weight: 600;
}

/* 비활성 탭 */
.board-tab-inactive {
  background: #f9fafb;
  color: #374151;
  border-color: #e5e7eb;
}

.board-tab-inactive:hover {
  background: #f3f4f6;
  color: #111827;
  border-color: #d1d5db;
}

.board-tab-inactive .board-name {
  color: #374151;
  font-weight: 500;
}

.board-tab-inactive:hover .board-name {
  color: #111827;
}

/* 게시판 아이콘 */
.board-icon {
  font-size: 16px;
  flex-shrink: 0;
}

/* 게시판 이름 */
.board-name {
  font-weight: inherit;
  transition: color 0.2s ease;
}

/* 인디케이터 */
.slider-indicators {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.indicator-active {
  background: #f59e0b;
  transform: scaleX(1.5);
}

.indicator-inactive {
  background: #d1d5db;
}

.indicator-inactive:hover {
  background: #9ca3af;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
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

@media (max-width: 768px) {
  .board-slider-container {
    padding: 16px;
  }

  .slider-track {
    gap: 8px;
    padding: 4px 8px;
  }

  .board-tab {
    padding: 10px 16px;
    font-size: 13px;
    gap: 8px;
  }

  .board-icon {
    font-size: 14px;
  }

  .nav-button {
    width: 28px;
    height: 28px;
  }

  .nav-button-left {
    left: -14px;
  }

  .nav-button-right {
    right: -14px;
  }
}

@media (max-width: 640px) {
  .board-slider-container {
    padding: 12px;
  }

  .board-tab {
    padding: 8px 12px;
    font-size: 12px;
    gap: 6px;
  }

  .board-icon {
    font-size: 13px;
  }

  .slider-track {
    gap: 6px;
    padding: 4px 6px;
  }
}

/* 접근성 */
.board-tab:focus {
  outline: 2px solid #f59e0b;
  outline-offset: 2px;
}
</style>