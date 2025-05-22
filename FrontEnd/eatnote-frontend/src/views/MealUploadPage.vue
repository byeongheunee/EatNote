<template>
  <div>
    <Header />
    <h2 class="text-2xl font-bold text-gray-800">🍽️ 식단 업로드</h2>

    <p class="text-gray-600">{{ suggestedMessage }}</p>
    <p class="text-sm text-gray-500">
      <strong class="text-gray-700">현재 시간 기준 식사 유형:</strong> {{ mealTypeLabel }}
    </p>

    <div v-if="imagePreviewUrl" class="mb-4">
      <img :src="imagePreviewUrl" alt="미리보기" class="rounded w-full object-contain max-h-96" />
    </div>
    <!-- 업로드 폼 -->
    <form v-if="!uploading && !uploadComplete" @submit.prevent="handleUpload" class="space-y-4">
      <div>
        <label for="image" class="block text-gray-700 font-semibold mb-1">식단 이미지</label>
        <input type="file" id="image" @change="onFileChange" accept="image/*" required class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4
             file:rounded-full file:border-0 file:text-sm file:font-semibold
             file:bg-green-50 file:text-green-700
             hover:file:bg-green-100" />
      </div>
      <button type="submit" class="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
        업로드
      </button>
    </form>



    <!-- 업로드 중 표시 -->
    <div v-if="uploading" class="text-center space-y-2">
      <p class="text-purple-700 font-medium animate-pulse">업로드 중입니다...</p>
      <p class="text-gray-500">AI의 피드백 생성 중입니다...</p>
    </div>

    <!-- 분석 결과 -->
    <div v-if="result" @click="goToMealDetail(result.mealId)"
      class="cursor-pointer bg-gray-50 p-4 rounded shadow hover:bg-gray-100 transition">
      <!-- ✅ AI 점수 설명 -->
      <p class="text-sm text-gray-600 italic">
        EatNote의 AI 건강 점수는 사용자 개별 정보(성별, 연령, 신체지수 등)를 기반으로 산출된 권장 섭취량을 바탕으로 평가됩니다.
        식사 유형별 열량 적정성, 탄수화물·단백질·지방 비율의 균형, 나트륨 및 당류 섭취량을 종합적으로 분석하여 10점 만점으로 산정됩니다.
      </p>
      <h3 class="text-lg font-semibold text-gray-800 mb-2">🔍 분석 결과</h3>
      <p class="text-gray-700"><strong>감지된 음식:</strong> {{ result.detectedFoods }}</p>
      <p class="text-gray-700"><strong>총 칼로리:</strong> {{ result.totalCalories }} kcal</p>
      <p class="text-gray-700"><strong>AI 피드백:</strong> {{ result.aiFeedback }}</p>

      <button @click="goToMealDetail(result.mealId)"
        class="mt-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
        상세 조회하기
      </button>
    </div>
    <!-- 목록으로 이동 버튼 -->
    <div class="text-right mt-4">
      <button @click="goToDashboard" class="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400">
        식단 목록으로 이동
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import { useRouter } from 'vue-router'

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

const goToDashboard = () => {
  router.push('/meals') // MemberView.vue가 연결된 라우트 경로
}

const goToMealDetail = (mealId) => {
  if (!mealId) return
  router.replace(`/meal/${mealId}`)
}

onMounted(() => {
  const now = new Date()
  const hour = now.getHours()
  if (hour >= 5 && hour < 11) mealType.value = 'breakfast'
  else if (hour < 16) mealType.value = 'lunch'
  else if (hour < 21) mealType.value = 'dinner'
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
    uploadComplete.value = true; // ✅ 업로드 완료 상태 변경
  } catch (error) {
    console.error('[❌ 업로드 에러 발생]', error);

    const msg = error?.response?.data?.message || '';
    if (msg.includes('감지된 음식이 없습니다')) {
      alert('사진이 인식되지 않았습니다. 다시 확인해주세요.');
    } else {
      alert('식단 업로드 중 문제가 발생했습니다.');
    }
  } finally {
    uploading.value = false;
  }


}
</script>
