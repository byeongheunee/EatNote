<template>
  <div class="video-view px-6 pb-16">
    <!-- 공통 헤더 -->
    <Header
      @go-feature="scrollToFeature"
      @go-usage="scrollToUsage"
    />

    <div class="max-w-4xl mx-auto mt-10">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">나의 목표 · {{ goal }}</h2>

      <div class="mb-6">
        <h3 class="text-lg font-semibold mb-2">관련 영상</h3>

        <!-- 검색창 -->
        <div class="mb-4 flex items-center gap-2">
          <input
            v-model="keyword"
            type="text"
            placeholder="영상 검색..."
            class="border px-3 py-2 rounded w-full"
            @keydown.enter="searchVideos"
          />
          <button class="bg-green-600 text-white px-4 py-2 rounded" @click="searchVideos">
            검색
          </button>
        </div>

        <!-- 추천 영상 리스트 -->
        <div class="grid md:grid-cols-2 gap-6">
          <div
            v-for="video in videos"
            :key="video.videoId"
            class="border rounded overflow-hidden shadow-sm hover:shadow-md transition bg-white"
            >
            <iframe
                class="w-full h-60"
                :src="`https://www.youtube.com/embed/${video.videoId}`"
                title="YouTube video player"
                frameborder="0"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen
            ></iframe>
            <div class="p-3">
                <h4 class="font-bold text-sm mb-1">{{ video.title }}</h4>
                <p class="text-xs text-gray-500">{{ video.channelTitle }} · {{ formatDate(video.publishedAt) }}</p>
            </div>
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const goal = ref('')
const keyword = ref('')
const videos = ref([])

const fetchGoal = async () => {
  try {
    const res = await axios.get('/api/users/me/goal', {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
    goal.value = res.data.data
    await fetchRecommendedVideos()
  } catch (err) {
    console.error('목표 조회 실패:', err)
  }
}

const fetchRecommendedVideos = async () => {
  try {
    const res = await axios.get(`/api/youtube/recommend?goal=${goal.value}`)
    videos.value = res.data.data
  } catch (err) {
    console.error('추천 영상 조회 실패:', err)
  }
}

const searchVideos = async () => {
  if (!keyword.value) return
  try {
    const res = await axios.get(`/api/youtube/search?keyword=${encodeURIComponent(keyword.value)}`)
    videos.value = res.data.data
  } catch (err) {
    console.error('검색 실패:', err)
  }
}

const formatDate = (iso) => {
  const date = new Date(iso)
  return date.toLocaleDateString()
}

const logout = () => {
  auth.logout()
  router.push('/')
}

onMounted(fetchGoal)
</script>

<style scoped>
.video-view input:focus {
  outline: none;
  border-color: #4ade80;
  box-shadow: 0 0 0 1px #4ade80;
}
</style>