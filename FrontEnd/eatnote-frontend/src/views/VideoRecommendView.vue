<template>
  <div class="video-recommend-page">
    <Header />

    <!-- 메인 컨테이너 -->
    <div class="main-container">
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="main-title">추천 운동 영상</h1>
          <p class="main-subtitle">{{ userNickname }}님을 위한 맞춤형 운동영상을 추천해 드립니다</p>
        </div>
      </div>

      <!-- 좌우 분할 레이아웃 -->
      <div class="content-grid">
        <!-- 왼쪽: 사용자 목표 기반 추천 -->
        <div class="goal-section">
          <div class="content-card">
            <!-- 섹션 헤더 -->
            <div class="section-header">
              <div class="header-icon">🎯</div>
              <div class="header-content">
                <h2 class="section-title">목표 기반 추천</h2>
                <p class="section-subtitle">나의 목표: <span class="goal-highlight">{{ userGoalLabel }}</span></p>
              </div>
            </div>

            <!-- 목표 선택 버튼 -->
            <div class="selector-section">
              <h3 class="selector-title">목표 변경</h3>
              <div class="button-grid">
                <button v-for="item in goals" :key="item.code" @click="selectGoal(item)" :class="[
                  'selector-button',
                  selectedGoal.code === item.code ? 'selector-button-active' : 'selector-button-inactive'
                ]">
                  {{ item.label }}
                </button>
              </div>
            </div>

            <!-- 목표 기반 영상 리스트 -->
            <div class="videos-section">
              <div class="videos-grid">
                <div v-for="video in visibleGoalVideos" :key="video.videoId" class="video-card">
                  <div class="video-thumbnail">
                    <iframe :src="`https://www.youtube.com/embed/${video.videoId}`" title="YouTube video"
                      frameborder="0" allowfullscreen class="video-iframe"></iframe>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <p class="video-meta">{{ video.channelTitle }} · {{ formatDate(video.publishedAt) }}</p>
                  </div>
                </div>
              </div>

              <!-- 더보기 버튼 -->
              <div class="show-more-container">
                <button v-if="goalBasedVideos.length > 4 && !showAllGoalVideos" @click="showAllGoalVideos = true"
                  class="show-more-btn">
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
          <div class="content-card">
            <!-- 섹션 헤더 -->
            <div class="section-header">
              <div class="header-icon nomi-icon">
                <NomiBasic size="medium" />
              </div>
              <div class="header-content">
                <h2 class="section-title">AI 맞춤 추천</h2>
                <p class="section-subtitle">건강 정보 기반 추천</p>
              </div>
            </div>

            <!-- AI 추천 운동 선택 -->
            <div class="selector-section">
              <h3 class="selector-title">추천 운동 목록</h3>
              <div class="button-grid">
                <button v-for="exercise in gptExercises" :key="exercise" @click="searchExerciseVideo(exercise)" :class="[
                  'selector-button exercise-button',
                  selectedExercise === exercise ? 'selector-button-active' : 'selector-button-inactive'
                ]">
                  {{ exercise }}
                </button>
              </div>
            </div>

            <!-- AI 추천 영상 리스트 -->
            <div class="videos-section">
              <div class="videos-grid">
                <div v-for="video in visibleExerciseVideos" :key="video.videoId" class="video-card">
                  <div class="video-thumbnail">
                    <iframe :src="`https://www.youtube.com/embed/${video.videoId}`" title="YouTube video"
                      frameborder="0" allowfullscreen class="video-iframe"></iframe>
                  </div>
                  <div class="video-info">
                    <h4 class="video-title">{{ video.title }}</h4>
                    <p class="video-meta">{{ video.channelTitle }} · {{ formatDate(video.publishedAt) }}</p>
                  </div>
                </div>
              </div>

              <!-- 더보기 버튼 -->
              <div class="show-more-container">
                <button v-if="exerciseVideos.length > 4 && !showAllExerciseVideos" @click="showAllExerciseVideos = true"
                  class="show-more-btn">
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

const userNickname = ref(auth.user?.nickname || '회원')
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
    showAllGoalVideos.value = false // 목표 변경 시 접기
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
    showAllExerciseVideos.value = false // 운동 변경 시 접기
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
  padding-top: 80px; /* 헤더 높이만큼 여백 */
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 설정 */
.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f59e0b;
}

.header-content {
  text-align: center;
}

.main-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
  margin-bottom: 12px;
}

.main-subtitle {
  font-size: 18px;
  color: #5D4037;
  line-height: 1.6;
}

/* 콘텐츠 그리드 */
.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
}

/* 콘텐츠 카드 */
.content-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  padding: 32px;
  display: flex;
  flex-direction: column;
}

/* 섹션 헤더 */
.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 2px solid rgba(245, 158, 11, 0.1);
}

.header-icon {
  width: 56px;
  height: 56px;
  font-size: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.nomi-icon {
  animation: nomiFloat 3s ease-in-out infinite;
}

@keyframes nomiFloat {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-5px);
  }
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-subtitle {
  font-size: 15px;
  color: #6b7280;
  font-weight: 500;
}

.goal-highlight {
  color: #f59e0b;
  font-weight: 700;
}

/* 선택자 섹션 */
.selector-section {
  margin-bottom: 32px;
}

.selector-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 16px;
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

/* 선택 버튼 */
.selector-button {
  padding: 12px 20px;
  border-radius: 12px;
  border: 2px solid transparent;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
  text-align: center;
}

.selector-button-active {
  background: linear-gradient(135deg, #93d48f, #93d48f);
  color: white;
  border-color: #83ce7f;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
  transform: translateY(-1px);
}

.selector-button-inactive {
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  border-color: rgba(229, 231, 235, 0.5);
}

.selector-button-inactive:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #374151;
  border-color: rgba(16, 185, 129, 0.4);
  transform: translateY(-1px);
}

/* 운동 버튼 (AI 추천용) */
.exercise-button.selector-button-active {
  background: linear-gradient(135deg, 	#54aedb, 	#54aedb);
  border-color: #3f97c4;
  box-shadow: 0 4px 15px rgba(109, 161, 245, 0.3);
}

.exercise-button.selector-button-inactive:hover {
  border-color: rgba(59, 130, 246, 0.4);
}

/* 비디오 섹션 */
.videos-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.videos-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

/* 비디오 카드 */
.video-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
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
  padding: 16px;
}

.video-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

/* 더보기 버튼 */
.show-more-container {
  text-align: center;
  margin-top: auto;
  padding-top: 16px;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.show-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
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

/* 애니메이션 - 헤더 제외 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.content-card {
  animation: fadeInUp 0.5s ease-out;
}

.content-card:nth-child(2) {
  animation-delay: 0.1s;
}

.video-card {
  animation: fadeInUp 0.4s ease-out;
}

.video-card:nth-child(2) {
  animation-delay: 0.1s;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .main-container {
    padding: 24px 16px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .content-card {
    padding: 24px;
    min-height: 600px;
  }

  .button-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .main-container {
    padding: 16px 12px;
  }

  .page-header {
    margin-bottom: 32px;
  }

  .main-title {
    font-size: 32px;
  }

  .main-subtitle {
    font-size: 16px;
  }

  .content-card {
    padding: 20px;
  }

  .section-header {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .header-icon {
    font-size: 32px;
  }

  .section-title {
    font-size: 20px;
  }

  .video-thumbnail {
    height: 180px;
  }

  .video-info {
    padding: 14px;
  }
}

@media (max-width: 640px) {
  .main-title {
    font-size: 28px;
  }

  .main-subtitle {
    font-size: 16px;
  }

  .section-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
  }

  .selector-button {
    padding: 10px 16px;
    font-size: 13px;
  }

  .video-thumbnail {
    height: 160px;
  }
}

/* 접근성 */
.selector-button:focus {
  outline: 2px solid #f59e0b;
  outline-offset: 2px;
}

.show-more-btn:focus {
  outline: 2px solid #f59e0b;
  outline-offset: 2px;
}
</style>