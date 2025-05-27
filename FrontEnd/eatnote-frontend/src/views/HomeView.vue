<template>
  <div class="home-page">
    <Header @go-feature="scrollToFeature" @go-usage="scrollToUsage" />

    <div class="home-container">
      <!-- 최근 등록 식단 -->
      <section class="my-12" ref="recentSection">
        <div class="flex items-center gap-3 mb-6">
          <h2 class="text-2xl font-bold text-gray-800">최근 등록 식단</h2>
          <div class="w-2 h-2 bg-orange-400 rounded-full animate-pulse"></div>
        </div>
        
        <div class="swiper-section">
          <Swiper 
            :modules="[Navigation]" 
            :slides-per-view="1"
            :space-between="10" 
            :breakpoints="{
              640: { slidesPerView: 2, spaceBetween: 8 },
              768: { slidesPerView: 3, spaceBetween: 10 },
              1024: { slidesPerView: 5, spaceBetween: 12 }
            }"
            navigation
            class="meal-swiper"
          >
            <SwiperSlide v-for="meal in recentMeals" :key="meal.mealId">
              <div class="meal-card-wrapper">
                <MealCard :meal="meal" />
              </div>
            </SwiperSlide>
          </Swiper>
        </div>
      </section>

      <!-- 핫한 식단 -->
      <section class="my-12" ref="popularSection">
        <div class="flex items-center gap-3 mb-6">
          <h2 class="text-2xl font-bold text-gray-800">핫한 식단</h2>
          <div class="text-2xl">🔥</div>
        </div>
        
        <div class="swiper-section">
          <Swiper 
            :modules="[Navigation]" 
            :slides-per-view="1"
            :space-between="10" 
            :breakpoints="{
              640: { slidesPerView: 2, spaceBetween: 8 },
              768: { slidesPerView: 3, spaceBetween: 10 },
              1024: { slidesPerView: 5, spaceBetween: 12 }
            }"
            navigation
            class="meal-swiper"
          >
            <SwiperSlide v-for="meal in popularMeals" :key="meal.mealId">
              <div class="meal-card-wrapper">
                <MealCard :meal="meal" />
              </div>
            </SwiperSlide>
          </Swiper>
        </div>
      </section>

      <!-- 주요 기능 -->
      <section class="my-16" ref="featureSection">
        <div class="text-center mb-12" ref="featureTitle">
          <h2 class="text-3xl font-bold text-gray-800 mb-4">주요 기능</h2>
          <p class="text-lg text-gray-600">EatNote가 제공하는 특별한 기능들을 확인해보세요</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div 
            v-for="(feature, index) in features" 
            :key="feature.title"
            class="feature-card group"
            :class="{ 'md:col-span-2 lg:col-span-1': index === 0 }"
          >
            <div class="feature-icon-container">
              <!-- 이모티콘 또는 이미지 -->
              <div v-if="feature.emoji" class="feature-emoji">{{ feature.emoji }}</div>
              <img v-else-if="feature.icon" :src="feature.icon" :alt="feature.title" class="feature-image" />
              <div v-else class="feature-placeholder">🎯</div>
            </div>
            
            <div class="feature-content">
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
            </div>
            
            <!-- 호버 효과 -->
            <div class="feature-hover-effect"></div>
          </div>
        </div>
      </section>

      <!-- 이용 방법 -->
      <section class="my-16 pb-16" ref="usageSection">
        <div class="text-center mb-12" ref="usageTitle">
          <h2 class="text-3xl font-bold text-gray-800 mb-4">이용 방법</h2>
          <p class="text-lg text-gray-600">간단한 4단계로 시작하는 건강한 식단 관리</p>
        </div>
        
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div 
            v-for="(step, index) in usageSteps" 
            :key="index"
            class="usage-step-card"
          >
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-icon">{{ step.emoji }}</div>
            <h3 class="step-title">{{ step.title }}</h3>
            <p class="step-description">{{ step.description }}</p>
            
            <!-- 화살표 (마지막 아닌 경우) -->
            <div v-if="index < usageSteps.length - 1" class="step-arrow">
              <svg class="w-6 h-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
              </svg>
            </div>
          </div>
        </div>
        
        <!-- CTA 버튼 -->
        <div class="text-center mt-12">
          <button 
            @click="$router.push('/register')"
            class="cta-button"
          >
            <span>🚀</span> 지금 시작하기
          </button>
        </div>
      </section>
    </div>
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

import MealCard from '@/components/MealCard.vue'
import Header from '@/components/common/Header.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const recentMeals = ref([])
const popularMeals = ref([])

const featureSection = ref(null)
const usageSection = ref(null)
const featureTitle = ref(null)
const usageTitle = ref(null)

const features = [
  { 
    title: '간편한 식단 등록', 
    description: '사진 업로드 만으로도 쉽게 식단을 기록하고 관리할 수 있습니다. 번거로운 입력 과정 없이 간편하게 식단을 기록하세요.',
    emoji: '📱',
    // icon: '/images/features/upload.png' // PNG 파일 사용 시
  },
  { 
    title: 'AI 식단 분석', 
    description: '인공지능이 업로드된 식단 사진을 분석하여 영양소와 칼로리 정보를 자동으로 계산해 드립니다.',
    emoji: '🤖'
  },
  { 
    title: '전문 트레이너 피드백', 
    description: '실제 전문 트레이너가 AI 분석을 바탕으로 개인화된 식단 피드백과 조언을 제공합니다.',
    emoji: '💪'
  },
  { 
    title: '맞춤형 운동 추천', 
    description: '식단과 체형 정보 및 개인 목표에 맞는 운동영상도 추천해드려요.',
    emoji: '🏃‍♂️'
  },
  { 
    title: '커뮤니티 참여', 
    description: '다른 회원들과 식단을 공유하고 자유롭게 소통하며 동기부여를 받을 수 있습니다.',
    emoji: '👥'
  },
  { 
    title: '상세한 식단 리포트', 
    description: '주간, 월간 식단 리포트를 통해 식습관의 변화와 개선점을 한눈에 확인할 수 있습니다.',
    emoji: '📊'
  }
]

const usageSteps = [
  {
    title: '회원가입',
    description: '간단한 정보로 쉽게 가입하고 맞춤형 서비스를 시작하세요',
    emoji: '✍️'
  },
  {
    title: '식단 업로드',
    description: '식사 사진을 촬영하거나 업로드하여 식단을 기록하세요',
    emoji: '📸'
  },
  {
    title: 'AI 분석',
    description: '인공지능이 자동으로 음식을 인식하고 영양정보를 분석합니다',
    emoji: '🔍'
  },
  {
    title: '피드백 확인',
    description: '전문가의 맞춤형 피드백과 개선사항을 확인하세요',
    emoji: '💬'
  }
]

const fetchMeals = async () => {
  try {
    const [recentRes, popularRes] = await Promise.all([
      axios.get('/api/meal/home/recent-meals'),
      axios.get('/api/meal/home/popular-meals')
    ])
    recentMeals.value = recentRes.data.data
    popularMeals.value = popularRes.data.data
  } catch (error) {
    console.error('식단 데이터 로딩 실패:', error)
  }
}

const scrollToFeature = () => {
  if (featureTitle.value) {
    const element = featureTitle.value
    const headerHeight = 100 // 헤더 높이
    const elementPosition = element.getBoundingClientRect().top + window.pageYOffset
    const offsetPosition = elementPosition - headerHeight
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}

const scrollToUsage = () => {
  if (usageTitle.value) {
    const element = usageTitle.value
    const headerHeight = 100 // 헤더 높이
    const elementPosition = element.getBoundingClientRect().top + window.pageYOffset
    const offsetPosition = elementPosition - headerHeight
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}

onMounted(fetchMeals)
</script>

<style scoped>
/* 페이지 전체 배경 */
.home-page {
  padding-top: 80px; /* 헤더 높이만큼 여백 */
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

.home-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

/* 스위퍼 섹션 컨테이너 */
.swiper-section {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 10px 10px; /* 좌우에 공간 확보 */
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
  overflow: hidden;
}

/* 식단 슬라이더 스타일 */
.meal-swiper {
  padding: 0; /* 패딩 제거 */
  margin: 0;
}

.meal-swiper :deep(.swiper-button-prev),
.meal-swiper :deep(.swiper-button-next) {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border-radius: 50%;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  color: #f59e0b;
  transition: all 0.3s ease;
  border: 1px solid rgba(251, 191, 36, 0.3);
  z-index: 10;
}

.meal-swiper :deep(.swiper-button-prev:hover),
.meal-swiper :deep(.swiper-button-next:hover) {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  transform: scale(1.15);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.5);
}

.meal-swiper :deep(.swiper-button-prev) {
  left: 15px; /* 컨테이너 안쪽에서 15px */
}

.meal-swiper :deep(.swiper-button-next) {
  right: 15px; /* 컨테이너 안쪽에서 15px */
}

.meal-swiper :deep(.swiper-button-prev::after),
.meal-swiper :deep(.swiper-button-next::after) {
  font-size: 14px;
  font-weight: bold;
}

.meal-card-wrapper {
  padding: 0; /* 패딩 완전 제거 */
}

/* 주요 기능 카드 스타일 */
.feature-card {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  padding: 2rem;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(251, 191, 36, 0.2);
  border-color: rgba(251, 191, 36, 0.5);
}

.feature-icon-container {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(251, 191, 36, 0.3);
}

.feature-card:hover .feature-icon-container {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 35px rgba(251, 191, 36, 0.4);
}

.feature-emoji {
  font-size: 2.5rem;
  line-height: 1;
}

.feature-image {
  width: 50px;
  height: 50px;
  object-fit: contain;
}

.feature-placeholder {
  font-size: 2.5rem;
  color: white;
}

.feature-content {
  position: relative;
  z-index: 2;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 1rem;
  transition: color 0.3s ease;
}

.feature-description {
  color: #6b7280;
  line-height: 1.6;
  font-size: 0.95rem;
}

.feature-hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.1) 0%, rgba(245, 158, 11, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 24px;
}

.feature-card:hover .feature-hover-effect {
  opacity: 1;
}

/* 이용 방법 스타일 */
.usage-step-card {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 20px;
  padding: 2rem 1.5rem;
  text-align: center;
  transition: all 0.3s ease;
  height: 100%;
}

.usage-step-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(34, 197, 94, 0.15);
  border-color: rgba(34, 197, 94, 0.3);
}

.step-number {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.step-icon {
  font-size: 3rem;
  margin: 1rem 0;
  transition: transform 0.3s ease;
}

.usage-step-card:hover .step-icon {
  transform: scale(1.2);
}

.step-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 0.75rem;
}

.step-description {
  color: #6b7280;
  line-height: 1.5;
  font-size: 0.9rem;
}

.step-arrow {
  position: absolute;
  top: 50%;
  right: -15px;
  transform: translateY(-50%);
  z-index: 10;
}

@media (max-width: 1024px) {
  .step-arrow {
    display: none;
  }
}

/* CTA 버튼 */
.cta-button {
  background: linear-gradient(135deg, #ecad3e, #eb9b41);
  color: white;
  font-size: 1.2rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.4);
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.cta-button:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.5);
  background: linear-gradient(135deg, #d97706, #b45309);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .home-container {
    padding: 16px;
  }
  
  .feature-card,
  .usage-step-card {
    padding: 1.5rem;
  }
  
  .feature-icon-container {
    width: 60px;
    height: 60px;
  }
  
  .feature-emoji {
    font-size: 2rem;
  }
  
  .step-icon {
    font-size: 2.5rem;
  }
}

/* 스크롤 애니메이션 */
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

.feature-card,
.usage-step-card {
  animation: fadeInUp 0.6s ease-out;
}

.feature-card:nth-child(even) {
  animation-delay: 0.1s;
}

.usage-step-card:nth-child(2) { animation-delay: 0.1s; }
.usage-step-card:nth-child(3) { animation-delay: 0.2s; }
.usage-step-card:nth-child(4) { animation-delay: 0.3s; }
</style>