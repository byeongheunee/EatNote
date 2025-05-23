<template>
  <div class="video-view px-6 pb-16">
    <Header />

    <div class="max-w-4xl mx-auto mt-10">
      <!-- 사용자 목표 -->
      <h2 class="text-xl font-semibold text-gray-800 mb-4">나의 목표 : {{ userGoalLabel }}</h2>

      <!-- 목표 선택 버튼 -->
      <div class="flex flex-wrap gap-2 mb-6">
        <button v-for="item in goals" :key="item.code" @click="selectGoal(item)" :class="[
          'px-4 py-2 rounded border text-sm',
          selectedGoal.code === item.code
            ? 'bg-green-600 text-white border-green-600'
            : 'bg-white text-gray-700 hover:bg-gray-100 border-gray-300'
        ]">
          {{ item.label }}
        </button>
      </div>

      <!-- 목표 기반 추천 영상 -->
      <div class="grid md:grid-cols-2 gap-6 mb-6">
        <div v-for="video in visibleGoalVideos" :key="video.videoId"
          class="border rounded overflow-hidden shadow-sm hover:shadow-md transition bg-white">
          <iframe class="w-full h-60" :src="`https://www.youtube.com/embed/${video.videoId}`" title="YouTube video"
            frameborder="0" allowfullscreen></iframe>
          <div class="p-3">
            <h4 class="font-bold text-sm mb-1">{{ video.title }}</h4>
            <p class="text-xs text-gray-500">{{ video.channelTitle }} · {{ video.channelTitle }} · {{
              formatDate(video.publishedAt) }}</p>
          </div>
        </div>
      </div>
      <button v-if="goalBasedVideos.length > 4 && !showAllGoalVideos" @click="showAllGoalVideos = true"
        class="block mx-auto mb-10 text-sm text-blue-600 hover:underline">
        더보기
      </button>

      <!-- GPT 추천 운동 3가지 -->
      <h3 class="text-lg font-semibold text-gray-800 mb-2">AI 추천 운동 (나의 정보를 바탕으로 AI가 추천한 운동 리스트 입니다.)</h3>
      <div class="flex flex-wrap gap-2 mb-4">
        <button v-for="exercise in gptExercises" :key="exercise" @click="searchExerciseVideo(exercise)" :class="[
          'px-4 py-2 rounded text-white text-sm',
          selectedExercise === exercise
            ? 'bg-blue-600'
            : 'bg-blue-400 hover:bg-blue-500'
        ]">
          {{ exercise }}
        </button>
      </div>

      <!-- AI 추천 영상 -->
      <div class="grid md:grid-cols-2 gap-6 mb-6">
        <div v-for="video in visibleExerciseVideos" :key="video.videoId"
          class="border rounded overflow-hidden shadow-sm hover:shadow-md transition bg-white">
          <iframe class="w-full h-60" :src="`https://www.youtube.com/embed/${video.videoId}`" title="YouTube video"
            frameborder="0" allowfullscreen></iframe>
          <div class="p-3">
            <h4 class="font-bold text-sm mb-1">{{ video.title }}</h4>
            <p class="text-xs text-gray-500">{{ video.channelTitle }} · {{ video.channelTitle }} · {{
              formatDate(video.publishedAt) }}</p>
          </div>
        </div>
      </div>
      <button v-if="exerciseVideos.length > 4 && !showAllExerciseVideos" @click="showAllExerciseVideos = true"
        class="block mx-auto text-sm text-blue-600 hover:underline">
        더보기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
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

const formatDate = (iso) => new Date(iso).toLocaleDateString()

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
.video-view iframe {
  border-radius: 4px;
}
</style>
