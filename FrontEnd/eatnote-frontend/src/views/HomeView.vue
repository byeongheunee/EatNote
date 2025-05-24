<template>
  <div class="home"> 
    <Header @go-feature="scrollToFeature" @go-usage="scrollToUsage" />

    <!-- 최근 등록 식단 -->
    <section class="my-6">
      <h2 class="text-lg font-bold">최근 등록 식단</h2>
      <div class="relative">
        <Swiper :modules="[Navigation]" :slides-per-view="4" :space-between="20" navigation
          class="swiper-container-centered" style="padding-left: 60px;">
          <SwiperSlide v-for="meal in recentMeals" :key="meal.mealId">
            <MealCard :meal="meal" />
          </SwiperSlide>
        </Swiper>
      </div>
    </section>

    <!-- 핫한 식단 -->
    <section class="my-6">
      <h2 class="text-lg font-bold">핫한 식단</h2>
      <div class="relative">
        <Swiper :modules="[Navigation]" :slides-per-view="4" :space-between="20" navigation
          class="swiper-container-centered" style="padding-left: 60px;">
          <SwiperSlide v-for="meal in popularMeals" :key="meal.mealId">
            <MealCard :meal="meal" />
          </SwiperSlide>
        </Swiper>
      </div>
    </section>

    <!-- 주요 기능 -->
    <section class="grid grid-cols-2 md:grid-cols-3 gap-4 my-10">
      <FeatureCard v-for="feature in features" :key="feature.title" :feature="feature" />
    </section>

    <!-- 이용 방법 -->
    <section class="my-10 text-center">
      <h2 class="text-xl font-bold mb-4">이용 방법</h2>
      <ul class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <li v-for="(step, i) in usageSteps" :key="i" class="bg-gray-100 p-4 rounded shadow">
          {{ step }}
        </li>
      </ul>
    </section>
  </div>
</template>







<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'

import MealCard from '@/components/MealCard.vue'
import FeatureCard from '@/components/FeatureCard.vue'
import Header from '@/components/common/Header.vue'
import { useAuthStore } from '@/stores/auth' // ✅ 추가됨

const auth = useAuthStore() // ✅ 추가됨
const router = useRouter()

const recentMeals = ref([])
const popularMeals = ref([])

const featureSection = ref(null) // ✅ 추가됨
const usageSection = ref(null)   // ✅ 추가됨

const features = [
  { title: '간편한 식단 등록', description: '사진 업로드 만으로도 쉽게 식단을 기록하고 관리할 수 있습니다. \n 번거로운 입력 과정 없이 간편하게 식단을 기록하세요.'},
  { title: 'AI 식단 분석', description: '인공지능이 업로드된 식단 사진을 분석하여 영양소와 칼로리 정보를 자동으로 계산해 드립니다.' },
  { title: '전문 트레이너 피드백', description: '실제 전문 트레이너가 AI 분석을 바탕으로 개인화된 식단 피드백과 조언을 제공합니다. ' },
  { title: '맞춤형 운동 추천', description: '식단과 체형 정보 및 개인 목표에 맞는 운동영상도 추천해드려요.' },
  { title: '커뮤니티 참여', description: '다른 회원들과 식단을 공유하고 자유롭게  소통하며 동기부여를 받을 수 있습니다.' },
  { title: '상세한 식단 리포트', description: '주간, 월간 식단 리포트를 통해 식습관의 변화와 개선점을 한눈에 확인할 수 있습니다.' }
]

const usageSteps = ['회원가입', '식단 업로드', 'AI 분석', '피드백 확인']

const fetchMeals = async () => {
  const [recentRes, popularRes] = await Promise.all([
    axios.get('/api/meal/home/recent-meals'),
    axios.get('/api/meal/home/popular-meals')
  ])
  recentMeals.value = recentRes.data.data
  popularMeals.value = popularRes.data.data
}

const scrollToFeature = () => featureSection.value?.scrollIntoView({ behavior: 'smooth' })
const scrollToUsage = () => usageSection.value?.scrollIntoView({ behavior: 'smooth' })

const handleLogout = () => {
  auth.logout()
  router.push('/')
}

onMounted(fetchMeals)
</script>




<style>
/* scoped 가능 */
.swiper-meal-section {
  padding: 0 60px;
  /* 양옆 버튼 공간 확보 */
  overflow: visible;
  /* 내부는 보이되, 외부에서 자름 */
  position: relative;
}

/* 좌우 버튼을 Swiper 밖으로 빼고 정확히 위치 */
.swiper-meal-section .swiper-button-prev,
.swiper-meal-section .swiper-button-next {
  color: black;
  top: 40%;
  width: 36px;
  height: 36px;
  background-color: white;
  border-radius: 9999px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

/* 안보이던 문제 해결: 버튼 위치 조정 */
.swiper-meal-section .swiper-button-prev {
  left: -30px;
}

.swiper-meal-section .swiper-button-next {
  right: -30px;
}



.home {
  padding: 20px;
  font-family: 'Noto Sans KR', sans-serif;
  max-width: 1200px;
  margin: 0 auto;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #ccc;
}

header h1 {
  font-size: 28px;
  color: #2ecc71;
}

header button {
  margin-left: 10px;
  padding: 8px 16px;
  border: 1px solid #aaa;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

header button:hover {
  background-color: #f0f0f0;
}

section {
  margin-top: 40px;
}

section h2 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 12px;
}

.meal-list {
  display: flex;
  overflow-x: auto;
  gap: 12px;
}

.meal-card {
  width: 180px;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  background-color: #fff;
}

.meal-card img {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.meal-card .meta {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.usage-steps {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  margin-top: 20px;
}

.usage-steps li {
  background-color: #f7f7f7;
  padding: 10px 15px;
  border-radius: 8px;
  margin: 5px;
}
</style>
