<template>
  <div class="video-recommend-page">
    <Header />

    <!-- 메인 컨테이너 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 페이지 헤더 -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">
          <span class="bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent">
            추천 운동 영상
          </span>
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          당신의 목표에 맞는 맞춤형 운동 영상을 추천해드립니다
        </p>
      </div>

      <!-- 좌우 분할 레이아웃 -->
      <div class="grid lg:grid-cols-2 gap-8 items-start">
        <!-- 왼쪽: 사용자 목표 기반 추천 -->
        <div class="goal-section">
          <div class="section-card">
            <!-- 섹션 헤더 -->
            <div class="section-header1">
              <div class="header-icon">🎯</div>
              <div class="header-content">
                <h2 class="section-title">목표 기반 추천</h2>
                <p class="section-subtitle">나의 목표: <span class="goal-highlight">{{ userGoalLabel }}</span></p>
              </div>
            </div>

            <!-- 목표 선택 버튼 -->
            <div class="goal-selector">
              <h3 class="selector-title">목표 변경</h3>
              <div class="goal-buttons">
                <button 
                  v-for="item in goals" 
                  :key="item.code" 
                  @click="selectGoal(item)"
                  :class="[
                    'goal-btn',
                    selectedGoal.code === item.code ? 'goal-btn-active' : 'goal-btn-inactive'
                  ]"
                >
                  {{ item.label }}
                </button>
              </div>
            </div>

            <!-- 목표 기반 영상 리스트 -->
            <div class="videos-container">
              <div class="videos-grid">
                <div 
                  v-for="video in visibleGoalVideos" 
                  :key="video.videoId"
                  class="video-card"
                >
                  <div class="video-thumbnail">
                    <iframe 
                      :src="`https://www.youtube.com/embed/${video.videoId}`" 
                      title="YouTube video"
                      frameborder="0" 
                      allowfullscreen
                      class="video-iframe"
                    ></iframe>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <p class="video-meta">{{ video.channelTitle }} · {{ formatDate(video.publishedAt) }}</p>
                  </div>
                </div>
              </div>
              
              <!-- 더보기 버튼 -->
              <div class="show-more-container">
                <button 
                  v-if="goalBasedVideos.length > 4 && !showAllGoalVideos"
                  @click="showAllGoalVideos = true" 
                  class="show-more-btn"
                >
                  <svg class="show-more-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                  더 많은 영상 보기
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽: AI 추천 운동 -->
        <div class="ai-section">
          <div class="section-card">
            <!-- 섹션 헤더 -->
            <div class="section-header">
              <div class="header-icon nomi-icon">
                <NomiBasic size="medium" />
              </div>
              <div class="header-content">
                <h2 class="section-title">AI 맞춤 추천</h2>
                <p class="section-subtitle">개인 정보 기반 AI 운동 추천</p>
              </div>
            </div>

            <!-- AI 추천 운동 선택 -->
            <div class="exercise-selector">
              <h3 class="selector-title">추천 운동 목록</h3>
              <div class="exercise-buttons">
                <button 
                  v-for="exercise in gptExercises" 
                  :key="exercise" 
                  @click="searchExerciseVideo(exercise)"
                  :class="[
                    'exercise-btn',
                    selectedExercise === exercise ? 'exercise-btn-active' : 'exercise-btn-inactive'
                  ]"
                >
                  {{ exercise }}
                </button>
              </div>
            </div>

            <!-- AI 추천 영상 리스트 -->
            <div class="videos-container">
              <div class="videos-grid">
                <div 
                  v-for="video in visibleExerciseVideos" 
                  :key="video.videoId"
                  class="video-card"
                >
                  <div class="video-thumbnail">
                    <iframe 
                      :src="`https://www.youtube.com/embed/${video.videoId}`" 
                      title="YouTube video"
                      frameborder="0" 
                      allowfullscreen
                      class="video-iframe"
                    ></iframe>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <p class="video-meta">{{ video.channelTitle }} · {{ formatDate(video.publishedAt) }}</p>
                  </div>
                </div>
              </div>
              
              <!-- 더보기 버튼 -->
              <div class="show-more-container">
                <button 
                  v-if="exerciseVideos.length > 4 && !showAllExerciseVideos"
                  @click="showAllExerciseVideos = true" 
                  class="show-more-btn"
                >
                  <svg class="show-more-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                  더 많은 영상 보기
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import NomiBasic from '@/components/NomiBasic.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const goals = [
  { code: '감량', label: '체중 감량' },
  { code: '유지', label: '체중 유지' },
  { code: '증량', label: '근육 증량' },
  { code: '체력향상', label: '체력 향상' }
]

const userGoalLabel = ref('')
const selectedGoal = ref(goals[0])
const goalBasedVideos = ref([])
const exerciseVideos = ref([])
const gptExercises = ref([])
const selectedExercise = ref('')

const showAllGoalVideos = ref(false)
const showAllExerciseVideos = ref(false)

const visibleGoalVideos = computed(() =>
  showAllGoalVideos.value ? goalBasedVideos.value : goalBasedVideos.value.slice(0, 4)
)
const visibleExerciseVideos = computed(() =>
  showAllExerciseVideos.value ? exerciseVideos.value : exerciseVideos.value.slice(0, 4)
)

const formatDate = (iso) => {
  const date = new Date(iso)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}.${month}.${day}`
}

const fetchUserGoal = async () => {
  try {
    const res = await axios.get('/api/users/me/goal', {
      headers: { Authorization: `Bearer ${auth.accessToken}` }
    })
    const userGoalCode = res.data.data
    const matched = goals.find(g => g.code === userGoalCode)
    selectedGoal.value = matched || goals[0]
    userGoalLabel.value = matched ? matched.label : userGoalCode
    await fetchGoalBasedVideos()
  } catch (err) {
    console.error('목표 조회 실패:', err)
  }
}

const fetchGoalBasedVideos = async () => {
  try {
    const res = await axios.get(`/api/youtube/recommend?goal=${selectedGoal.value.code}`)
    goalBasedVideos.value = res.data.data
    showAllGoalVideos.value = false
  } catch (err) {
    console.error('목표 기반 영상 실패:', err)
  }
}

const selectGoal = async (item) => {
  selectedGoal.value = item
  await fetchGoalBasedVideos()
}

const fetchGptExercises = async () => {
  try {
    const res = await axios.get('/api/youtube/recommendExercise', {
      headers: { Authorization: `Bearer ${auth.accessToken}` }
    })
    gptExercises.value = res.data.data

    if (gptExercises.value.length > 0) {
      selectedExercise.value = gptExercises.value[0]
      await searchExerciseVideo(selectedExercise.value)
    }
  } catch (err) {
    console.error('GPT 운동 추천 실패:', err)
  }
}

const searchExerciseVideo = async (exerciseName) => {
  try {
    selectedExercise.value = exerciseName
    const res = await axios.get(`/api/youtube/recommend?goal=${encodeURIComponent(exerciseName)}`)
    exerciseVideos.value = res.data.data
    showAllExerciseVideos.value = false
  } catch (err) {
    console.error('운동 영상 검색 실패:', err)
  }
}

onMounted(async () => {
  await fetchUserGoal()
  await fetchGptExercises()
})
</script>

<style scoped>
/* 페이지 전체 배경 */
.video-recommend-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

/* 그라데이션 텍스트 */
.bg-clip-text {
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 섹션 카드 */
.section-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(245, 158, 11, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 600px;
}

/* 섹션 헤더 */
.section-header1 {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-top: 2rem;
  padding-bottom: 3rem;
  padding-left: 1rem;
  border-bottom: 2px solid rgba(245, 158, 11, 0.1);
  min-height: 120px; /* 최소 높이 설정으로 두 섹션 헤더 높이 맞춤 */
}

/* 섹션 헤더 */
.section-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid rgba(245, 158, 11, 0.1);
  min-height: 120px; /* 최소 높이 설정으로 두 섹션 헤더 높이 맞춤 */
}

.header-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.nomi-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  animation: nomiFloat 3s ease-in-out infinite;
}

@keyframes nomiFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

.header-content {
  flex: 1;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-subtitle {
  font-size: 0.95rem;
  color: #6b7280;
  font-weight: 500;
}

.goal-highlight {
  color: #f59e0b;
  font-weight: 700;
}

/* 선택자 */
.goal-selector,
.exercise-selector {
  margin-bottom: 2rem;
}

.selector-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 1rem;
}

.goal-buttons,
.exercise-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

/* 목표 버튼 */
.goal-btn {
  padding: 0.75rem 1.25rem;
  border-radius: 12px;
  border: 2px solid transparent;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
}

.goal-btn-active {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border-color: rgba(16, 185, 129, 0.3);
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
  transform: translateY(-1px);
}

.goal-btn-inactive {
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  border-color: rgba(229, 231, 235, 0.5);
}

.goal-btn-inactive:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #374151;
  border-color: rgba(16, 185, 129, 0.4);
  transform: translateY(-1px);
}

/* 운동 버튼 */
.exercise-btn {
  padding: 0.75rem 1.25rem;
  border-radius: 12px;
  border: 2px solid transparent;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
}

.exercise-btn-active {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border-color: rgba(59, 130, 246, 0.3);
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.exercise-btn-inactive {
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  border-color: rgba(229, 231, 235, 0.5);
}

.exercise-btn-inactive:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #374151;
  border-color: rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

/* 비디오 컨테이너 */
.videos-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.videos-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

/* 비디오 카드 */
.video-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px; /* 모서리 둥근 정도 */
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(245, 158, 11, 0.1);
  transition: all 0.3s ease;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.2);
}

.video-thumbnail {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.video-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 0;
}

.video-info {
  padding: 1rem;
}

.video-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
  line-height: 1.4;
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  font-size: 0.8rem;
  color: #9ca3af;
  font-weight: 500;
}

/* 더보기 버튼 */
.show-more-container {
  text-align: center;
  margin-top: auto;
  padding-top: 1rem;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.show-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.show-more-btn:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

.show-more-icon {
  width: 16px;
  height: 16px;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .grid.lg\\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
  
  .section-card {
    padding: 1.5rem;
  }
  
  .videos-grid {
    gap: 1.25rem;
  }
}

@media (max-width: 768px) {
  .video-recommend-page {
    padding: 1rem;
  }

  .max-w-7xl {
    padding: 0 1rem;
  }

  .text-4xl {
    font-size: 2rem;
  }

  .section-card {
    padding: 1.25rem;
  }

  .section-header {
    flex-direction: column;
    text-align: center;
    gap: 0.75rem;
  }

  .header-icon {
    font-size: 2rem;
  }

  .section-title {
    font-size: 1.3rem;
  }

  .goal-buttons,
  .exercise-buttons {
    gap: 0.5rem;
  }

  .goal-btn,
  .exercise-btn {
    padding: 0.6rem 1rem;
    font-size: 0.85rem;
  }

  .video-thumbnail {
    height: 180px;
  }

  .video-info {
    padding: 0.875rem;
  }
}

@media (max-width: 640px) {
  .py-8 {
    padding-top: 1.5rem;
    padding-bottom: 1.5rem;
  }

  .section-header {
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
  }

  .goal-buttons,
  .exercise-buttons {
    flex-direction: column;
  }

  .goal-btn,
  .exercise-btn {
    width: 100%;
    text-align: center;
  }

  .video-thumbnail {
    height: 160px;
  }
}

/* 애니메이션 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-card {
  animation: fadeInUp 0.6s ease-out;
}

.ai-section .section-card {
  animation-delay: 0.1s;
}

.video-card {
  animation: fadeInUp 0.4s ease-out;
}

.video-card:nth-child(even) {
  animation-delay: 0.1s;
}
</style>