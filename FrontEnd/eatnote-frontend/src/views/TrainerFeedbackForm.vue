<template>
  <div class="p-6 max-w-4xl mx-auto space-y-8">
    <Header />
    <h1 class="text-2xl font-bold">
      {{ feedbackId ? '✏️ 피드백 수정' : '🍱 식단 피드백 작성' }}
    </h1>

    <div v-if="loading">불러오는 중입니다...</div>
    <div v-else>
      <!-- 🥗 식단 정보 -->
      <div class="bg-white p-4 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">식단 정보</h2>
        <img :src="getImageUrl(meal.imageUrl)" class="w-full max-w-md rounded" />
        <p class="mt-2"><strong>식사 시간:</strong> {{ formatDate(meal.mealTime) }}</p>
        <p><strong>감지된 음식:</strong> {{ meal.detectedFoods }}</p>
        <p><strong>AI 점수:</strong> {{ meal.autoScore }}</p>
        <p><strong>식사 유형:</strong> {{
          { breakfast: '아침', lunch: '점심', dinner: '저녁', extra: '간식' }[meal.mealType]
        }}</p>
        <p><strong>AI 피드백:</strong> {{ meal.aiFeedback }}</p>

        <h3 class="mt-4 font-semibold text-base">🧪 성분 정보</h3>
        <ul class="grid grid-cols-2 gap-2 mt-1 text-sm">
          <li><strong>총 칼로리:</strong> {{ meal.totalCalories }} kcal</li>
          <li><strong>탄수화물:</strong> {{ meal.carbohydrates }} g</li>
          <li><strong>단백질:</strong> {{ meal.protein }} g</li>
          <li><strong>지방:</strong> {{ meal.fat }} g</li>
          <li><strong>당류:</strong> {{ meal.sugars }} g</li>
          <li><strong>나트륨:</strong> {{ meal.sodium }} mg</li>
        </ul>
      </div>

      <!-- 👤 회원 정보 -->
      <div class="bg-white p-4 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">회원 정보</h2>
        <p v-if="memberDetails.nickname"><strong>닉네임:</strong> {{ memberDetails.nickname }}</p>
        <p v-if="memberDetails.goal"><strong>목표:</strong> {{ memberDetails.goal }}</p>
        <p v-if="memberDetails.height"><strong>키:</strong> {{ memberDetails.height }} cm</p>
        <p v-if="memberDetails.weight"><strong>몸무게:</strong> {{ memberDetails.weight }} kg</p>
        <p v-if="memberDetails.bodyFatPercentage"><strong>체지방률:</strong> {{ memberDetails.bodyFatPercentage }}%</p>
        <p v-if="memberDetails.bmi"><strong>BMI:</strong> {{ memberDetails.bmi }}</p>
      </div>

      <!-- ⚠️ 알레르기 정보 -->
      <div v-if="allergies.length > 0" class="bg-white p-4 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">알레르기</h2>
        <ul class="list-disc list-inside">
          <li v-for="allergy in allergies" :key="allergy.name">{{ allergy.name }}</li>
        </ul>
      </div>

      <!-- 📝 피드백 입력 -->
      <div class="bg-white p-4 rounded shadow space-y-4">
        <h2 class="text-lg font-semibold">{{ feedbackId ? '피드백 수정' : '피드백 작성' }}</h2>
        <textarea v-model="feedbackContent" rows="5" class="w-full border rounded p-2"
          placeholder="회원에게 전달할 피드백 내용을 입력하세요."></textarea>
        <div>
          <label class="font-semibold">점수</label>
          <select v-model.number="trainerScore" class="border rounded p-1 ml-2 w-24">
            <option disabled value="0">점수 선택</option>
            <option v-for="n in 10" :key="n" :value="n">{{ n }}점</option>
          </select>
        </div>

        <button @click="submitFeedback" class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">
          {{ feedbackId ? '수정 완료' : '제출' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const mealId = route.params.mealId
const feedbackId = route.params.feedbackId || null

const loading = ref(true)
const meal = ref({})
const memberDetails = ref({})
const allergies = ref([])
const feedbackContent = ref('')
const trainerScore = ref(0)

const getImageUrl = (path) => path ? `http://localhost:8080${path}` : '/images/default-meal.jpg'
const formatDate = (datetime) => new Date(datetime).toLocaleString('ko-KR')

const submitFeedback = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const payload = {
      content: feedbackContent.value,
      trainerScore: trainerScore.value
    }

    if (feedbackId) {
      // 수정 요청
      await axios.put(`/api/trainer/feedback/${feedbackId}`, payload, {
        headers: { Authorization: `Bearer ${token}` }
      })
      alert('피드백이 수정되었습니다.')
    } else {
      // 새 피드백 등록
      await axios.post(`/api/trainer/feedback/meal/${mealId}`, payload, {
        headers: { Authorization: `Bearer ${token}` }
      })
      alert('피드백이 등록되었습니다.')
    }

    router.push('/trainer')
  } catch (e) {
    console.error('피드백 처리 실패', e)
    alert('요청에 실패했습니다.')
  }
}

onMounted(async () => {
  console.log('🚨 feedbackId:', feedbackId)
  try {
    const token = localStorage.getItem('accessToken')

    if (feedbackId) {
      // ✅ [1] 수정용 로직 먼저 처리
      const fbRes = await axios.get(`/api/trainer/feedback/${feedbackId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const fb = fbRes.data.data
      feedbackContent.value = fb.content
      trainerScore.value = fb.trainerScore

      // ✅ 피드백 응답에 포함된 mealId로 식단 정보 조회
      const res = await axios.get(`/api/trainer/meals/${fb.mealId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = res.data.data
      meal.value = data.meal
      memberDetails.value = data.memberDetails
      allergies.value = data.allergies

    } else if (mealId) {
      // ✅ [2] 작성용 로직 (mealId가 있어야 실행됨)
      const res = await axios.get(`/api/trainer/meals/${mealId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = res.data.data
      meal.value = data.meal
      memberDetails.value = data.memberDetails
      allergies.value = data.allergies
    } else {
      throw new Error('mealId 또는 feedbackId가 유효하지 않습니다.')
    }

  } catch (e) {
    console.error('데이터 로딩 실패', e)
    alert('잘못된 요청입니다.')
    router.push('/trainer')
  } finally {
    loading.value = false
  }
})

</script>
