<template>
  <div class="board-slider-container">
    <div class="board-slider-wrapper">
      <!-- 왼쪽 화살표 버튼 -->
      <button
        v-show="showLeftArrow"
        @click="scrollLeft"
        class="nav-button nav-button-left"
        :disabled="!canScrollLeft"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
        </svg>
      </button>

      <!-- 슬라이더 콘텐츠 -->
      <div
        ref="sliderContainer"
        class="slider-content"
        @scroll="handleScroll"
      >
        <div class="slider-track">
          <div
            v-for="board in boards"
            :key="board.boardId"
            class="tooltip-container"
          >
            <button
              @click="selectBoard(board.boardId)"
              :class="[
                'board-tab',
                board.boardId === selectedBoardId ? 'board-tab-active' : 'board-tab-inactive'
              ]"
            >
              <span class="board-icon">{{ getBoardIcon(board.name) }}</span>
              <span class="board-name">{{ board.name }}</span>

              <!-- ✨ 툴팁: 버튼 내부로 이동 -->
              <div class="tooltip">
                <div class="tooltip-content">
                  <h4 class="tooltip-title">{{ board.name }}</h4>
                  <p class="tooltip-description">{{ board.description }}</p>
                </div>
                <div class="tooltip-arrow"></div>
              </div>
            </button>
          </div>
        </div>
      </div>

      <!-- 오른쪽 화살표 버튼 -->
      <button
        v-show="showRightArrow"
        @click="scrollRight"
        class="nav-button nav-button-right"
        :disabled="!canScrollRight"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </button>
    </div>

    <!-- 인디케이터 -->
    <div v-if="showIndicators" class="slider-indicators">
      <div
        v-for="(indicator, index) in indicators"
        :key="index"
        :class="[
          'indicator-dot',
          indicator.active ? 'indicator-active' : 'indicator-inactive'
        ]"
      ></div>
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

const hoveredBoardId = ref(null)

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
    '의학 게시판': '🏥'
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
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(245, 158, 11, 0.1);
  position: relative;
}

/* 슬라이더 래퍼 */
.board-slider-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 1rem;
}

/* 네비게이션 버튼 */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.25);
}

.nav-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-50%) scale(1.05);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.35);
}

.nav-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.nav-button-left {
  left: -20px;
}

.nav-button-right {
  right: -20px;
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
  gap: 1rem;
  padding: 0 1rem;
  min-width: max-content;
}

/* 툴팁 컨테이너 */
.tooltip-container {
  position: relative;
  display: inline-block;
}

/* 툴팁 */
.tooltip {
  position: absolute;
  bottom: calc(100% + 12px);
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  pointer-events: none;
  white-space: nowrap;
}

.tooltip-container:hover .tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(-4px);
}

.tooltip-content {
  background: rgba(17, 24, 39, 0.95);
  backdrop-filter: blur(12px);
  color: white;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  min-width: 200px;
  max-width: 280px;
}

.tooltip-title {
  font-weight: 600;
  font-size: 0.9rem;
  margin: 0 0 0.5rem 0;
  color: #f59e0b;
}

.tooltip-description {
  font-size: 0.85rem;
  line-height: 1.4;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
}

.tooltip-arrow {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid rgba(17, 24, 39, 0.95);
}

/* 게시판 탭 */
.board-tab {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.75rem;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  white-space: nowrap;
  font-weight: 600;
  font-size: 0.95rem;
  min-width: max-content;
  position: relative;
  backdrop-filter: blur(8px);
}

/* 활성 탭 */
.board-tab-active {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  border-color: rgba(245, 158, 11, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.25);
}

.board-tab-active:hover {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.35);
}

.board-tab-active .board-name {
  color: white;
  font-weight: 700;
}

/* 비활성 탭 */
.board-tab-inactive {
  background: rgba(255, 255, 255, 0.8);
  color: #374151;
  border-color: rgba(229, 231, 235, 0.6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.board-tab-inactive:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #111827;
  border-color: rgba(245, 158, 11, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.board-tab-inactive .board-name {
  color: #374151;
  font-weight: 600;
}

.board-tab-inactive:hover .board-name {
  color: #111827;
}

/* 게시판 아이콘 */
.board-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
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
  gap: 0.5rem;
  margin-top: 1.25rem;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.indicator-active {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  transform: scaleX(2);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.indicator-inactive {
  background: #d1d5db;
}

.indicator-inactive:hover {
  background: #9ca3af;
  transform: scale(1.2);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .board-slider-container {
    padding: 1rem;
  }

  .slider-track {
    gap: 0.5rem;
    padding: 0 1rem;
  }

  .board-tab {
    padding: 0.6rem 1.2rem;
    font-size: 0.85rem;
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

  /* 모바일에서 툴팁 조정 */
  .tooltip-content {
    min-width: 180px;
    max-width: 240px;
    padding: 0.6rem 0.8rem;
  }

  .tooltip-title {
    font-size: 0.85rem;
  }

  .tooltip-description {
    font-size: 0.8rem;
  }
}

@media (max-width: 640px) {
  .board-tab {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
  }

  .board-icon {
    font-size: 1rem;
  }

  .slider-track {
    padding: 0 0.5rem;
  }
}

/* 스크롤 페이드 효과 */
@supports (mask-image: linear-gradient(to right, black, black)) {
  .slider-content {
    mask-image: linear-gradient(to right, transparent 0px, black 20px, black calc(100% - 20px), transparent 100%);
  }
}

/* 애니메이션 */
@keyframes slideIn {
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
  animation: slideIn 0.6s ease-out;
}
</style>