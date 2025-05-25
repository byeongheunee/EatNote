<template>
  <div class="min-h-screen bg-gradient-to-br from-orange-50 via-yellow-50 to-amber-50">
    <Header />

    <div class="container mx-auto px-4 py-8 max-w-2xl">
      <!-- 노미 캐릭터 섹션 -->
      <div class="text-center mb-8">
        <!-- 로딩 중일 때는 애니메이션 노미, 아닐 때는 기본 노미 -->
        <NomiLoading
          v-if="uploading"
          size="xl"
          :is-loading="uploading"
          :messages="[
            '사진을 분석하고 있어요! 🔍',
            '맛있는 음식을 찾고 있어요! 🍽️',
            '영양 정보를 계산하고 있어요! 📊',
            '거의 다 끝났어요! ✨'
          ]"
        />
        <NomiBasic
          v-else
          size="xl"
          :show-message="true"
          :message="{ line1: '환영해요!', line2: '식단을 기록해봐요 📝' }"
        />
      </div>

      <!-- 페이지 제목 -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-2">
          <span class="text-4xl">🍽️</span> 식단 업로드
        </h1>
        <p class="text-lg text-gray-600 font-medium">{{ suggestedMessage }}</p>
        <div class="inline-flex items-center gap-2 mt-3 px-4 py-2 bg-white/70 backdrop-blur-sm rounded-full border border-yellow-200">
          <div class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
          <span class="text-sm font-medium text-gray-700">현재 시간 기준: {{ mealTypeLabel }} 식사</span>
        </div>
      </div>

      <!-- 메인 업로드 카드 -->
      <div class="bg-white/80 backdrop-blur-sm rounded-3xl shadow-xl border border-white/50 overflow-hidden">

        <!-- 이미지 미리보기 섹션 -->
        <div v-if="imagePreviewUrl" class="relative">
          <div class="aspect-w-16 aspect-h-12 bg-gray-100">
            <img
              :src="imagePreviewUrl"
              alt="미리보기"
              class="w-full h-64 object-cover"
            />
          </div>
          <button
            @click="clearImage"
            class="absolute top-4 right-4 w-8 h-8 bg-red-500 hover:bg-red-600 text-white rounded-full flex items-center justify-center transition-colors"
          >
            ✕
          </button>
        </div>

        <!-- 업로드 폼 -->
        <div class="p-8">
          <form v-if="!uploading && !uploadComplete" @submit.prevent="handleUpload" class="space-y-6">

            <!-- 파일 업로드 영역 -->
            <div class="space-y-3">
              <label class="block text-lg font-semibold text-gray-800">
                📸 식단 사진 선택
              </label>

              <!-- 드래그 앤 드롭 영역 -->
              <div
                class="relative border-2 border-dashed border-yellow-300 rounded-2xl p-8 text-center hover:border-yellow-400 transition-colors group cursor-pointer"
                :class="{ 'bg-yellow-50': !imagePreviewUrl }"
                @click="$refs.fileInput.click()"
                @drop.prevent="handleDrop"
                @dragover.prevent
                @dragenter.prevent
              >
                <input
                  ref="fileInput"
                  type="file"
                  id="image"
                  @change="onFileChange"
                  accept="image/*"
                  required
                  class="hidden"
                />

                <div v-if="!imagePreviewUrl" class="space-y-3">
                  <div class="text-6xl">📷</div>
                  <div>
                    <p class="text-lg font-medium text-gray-700 mb-1">사진을 선택하거나 여기로 드래그하세요</p>
                    <p class="text-sm text-gray-500">JPG, PNG 파일을 지원합니다</p>
                  </div>
                </div>

                <div v-else class="space-y-2">
                  <div class="text-4xl">✅</div>
                  <p class="text-lg font-medium text-green-700">사진이 선택되었습니다!</p>
                  <p class="text-sm text-gray-600">다른 사진을 선택하려면 클릭하세요</p>
                </div>
              </div>
            </div>

            <!-- 업로드 버튼 -->
            <button
              type="submit"
              :disabled="!imageFile"
              class="w-full bg-gradient-to-r from-yellow-400 to-orange-400 hover:from-yellow-500 hover:to-orange-500 disabled:from-gray-300 disabled:to-gray-400 text-white font-bold py-4 px-6 rounded-2xl transition-all duration-300 transform hover:scale-[1.02] disabled:scale-100 shadow-lg hover:shadow-xl disabled:cursor-not-allowed text-lg"
            >
              <span class="flex items-center justify-center gap-2">
                <span>🚀</span> AI 분석 시작하기
              </span>
            </button>
          </form>

          <!-- 분석 결과 -->
          <div v-if="result" class="space-y-6">
            <div class="bg-gradient-to-br from-green-50 to-emerald-50 rounded-2xl p-6 border border-green-200">
              <div class="flex items-center gap-3 mb-4">
                <div class="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center">
                  <span class="text-white text-xl">🔍</span>
                </div>
                <h3 class="text-xl font-bold text-gray-800">분석 완료!</h3>
              </div>

              <div class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div class="bg-white/60 rounded-xl p-4">
                    <div class="text-sm text-gray-600 font-medium mb-1">감지된 음식</div>
                    <div class="text-lg font-semibold text-gray-800">{{ result.detectedFoods }}</div>
                  </div>
                  <div class="bg-white/60 rounded-xl p-4">
                    <div class="text-sm text-gray-600 font-medium mb-1">총 칼로리</div>
                    <div class="text-lg font-semibold text-gray-800">{{ result.totalCalories }} kcal</div>
                  </div>
                  <div class="bg-white/60 rounded-xl p-4">
                    <div class="text-sm text-gray-600 font-medium mb-1">AI 점수</div>
                    <div class="text-lg font-semibold text-gray-800">{{ result.autoScore }} / 10</div>
                  </div>
                </div>

                <div class="bg-white/60 rounded-xl p-4">
                  <div class="text-sm text-gray-600 font-medium mb-2">AI 피드백</div>
                  <div class="text-gray-700 leading-relaxed">{{ result.aiFeedback }}</div>
                </div>
              </div>

              <button
                @click="goToMealDetail(result.mealId)"
                class="w-full mt-4 bg-gradient-to-r from-blue-500 to-indigo-500 hover:from-blue-600 hover:to-indigo-600 text-white font-semibold py-3 px-6 rounded-xl transition-all duration-300 transform hover:scale-[1.02] shadow-lg hover:shadow-xl"
              >
                📊 상세 분석 결과 보기
              </button>
            </div>

            <!-- AI 점수 설명 -->
            <div class="bg-blue-50 rounded-2xl p-4 border border-blue-200">
              <div class="flex items-start gap-3">
                <div class="text-2xl">💡</div>
                <div class="text-sm text-blue-800 leading-relaxed">
                  <strong class="font-semibold">EatNote AI 건강 점수란?</strong><br>
                  개인정보(성별, 연령, 신체지수)를 바탕으로 권장 섭취량 대비 평가됩니다.
                  열량 적정성, 영양소 균형, 나트륨·당류 등을 종합 분석하여 10점 만점으로 산정합니다.
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 하단 네비게이션 -->
      <div class="flex gap-4 mt-8">
        <button
          @click="goToDashboard"
          class="flex-1 bg-white/60 hover:bg-white/80 backdrop-blur-sm text-gray-700 font-semibold py-3 px-6 rounded-2xl transition-all duration-300 border border-gray-200 hover:border-gray-300 cursor-pointer"
        >
          📋 식단 목록으로
        </button>

        <button
          v-if="uploadComplete"
          @click="resetForm"
          class="flex-1 bg-gradient-to-r from-green-400 to-emerald-400 hover:from-green-500 hover:to-emerald-500 text-white font-semibold py-3 px-6 rounded-2xl transition-all duration-300 transform hover:scale-[1.02] shadow-lg hover:shadow-xl cursor-pointer"
        >
          ➕ 새 식단 등록
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import NomiBasic from '@/components/NomiBasic.vue'
import NomiLoading from '@/components/NomiLoading.vue'
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'

const toast = useToast()

// 상태 변수
const mealType = ref('breakfast')
const imageFile = ref(null)
const uploading = ref(false)
const result = ref(null)
const router = useRouter()
const uploadComplete = ref(false)
const imagePreviewUrl = ref(null)

const onFileChange = (e) => {
  const file = e.target.files[0]
  imageFile.value = file
  if (file) {
    imagePreviewUrl.value = URL.createObjectURL(file)
  }
}

const handleDrop = (e) => {
  const files = e.dataTransfer.files
  if (files.length > 0) {
    const file = files[0]
    if (file.type.startsWith('image/')) {
      imageFile.value = file
      imagePreviewUrl.value = URL.createObjectURL(file)
    }
  }
}

const clearImage = () => {
  imageFile.value = null
  imagePreviewUrl.value = null
  if (URL.revokeObjectURL && imagePreviewUrl.value) {
    URL.revokeObjectURL(imagePreviewUrl.value)
  }
}

const resetForm = () => {
  imageFile.value = null
  imagePreviewUrl.value = null
  result.value = null
  uploadComplete.value = false
}

const goToDashboard = () => {
  router.push('/meals')
}

const goToMealDetail = (mealId) => {
  if (!mealId) return
  router.replace(`/meal/${mealId}`)
}

onMounted(() => {
  const now = new Date()
  const hour = now.getHours()
  if (hour >= 4 && hour < 11) mealType.value = 'breakfast'
  else if (hour < 16) mealType.value = 'lunch'
  else if (hour < 23) mealType.value = 'dinner'
  else mealType.value = 'extra'
})

const mealTypeLabel = computed(() => {
  switch (mealType.value) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    default: return '간식'
  }
})

const suggestedMessage = computed(() => {
  const now = new Date()
  const month = now.getMonth() + 1
  const date = now.getDate()

  let type = ''
  let emoji = ''

  switch (mealType.value) {
    case 'breakfast': type = '아침'; emoji = '☀️'; break
    case 'lunch': type = '점심'; emoji = '🍽️'; break
    case 'dinner': type = '저녁'; emoji = '🌙'; break
    default: type = '간식'; emoji = '🍩'
  }

  return `${month}월 ${date}일의 ${type} 식사를 올려주세요 ${emoji}`
})

const handleUpload = async () => {
  if (!imageFile.value) return;

  const formData = new FormData();
  formData.append('file', imageFile.value);
  uploading.value = true;

  try {
    const token = localStorage.getItem('accessToken');
    const response = await axios.post('/api/meal/upload', formData, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    result.value = response.data.data;
    uploadComplete.value = true;
    toast.success('식단이 성공적으로 업로드되었습니다.')
  } catch (error) {
    console.error('[❌ 업로드 에러 발생]', error);

    const msg = error?.response?.data?.message || '';
    if (msg.includes('감지된 음식이 없습니다')) {
      toast.warning('😥 음식이 감지되지 않았어요. \n 더 선명한 사진으로 다시 시도해보세요! \n 음식의 전체가 보이도록 찍어주세요!!')
    } else {
      toast.error('🚨 식단 업로드 중 문제가 발생했습니다.')
    }
  } finally {
    uploading.value = false;
  }
}
</script>

<style scoped>
/* 추가 커스텀 스타일 */
.container {
  animation: fadeInUp 0.6s ease-out;
}

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

/* 파일 업로드 영역 호버 효과 */
.group:hover .text-6xl {
  transform: scale(1.1);
  transition: transform 0.2s ease;
}

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #fbbf24;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #f59e0b;
}
</style>