<!-- src/views/TrainerFeedbackForm.vue -->
<template>
  <div class="p-6 max-w-4xl mx-auto space-y-8">
    <h1 class="text-2xl font-bold">🍱 식단 피드백 작성</h1>

    <div v-if="loading">불러오는 중입니다...</div>
    <div v-else>
      <!-- 🥗 식단 정보 -->
      <div class="bg-white p-4 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">식단 정보</h2>
        <img :src="getImageUrl(meal.imageUrl)" class="w-full max-w-md rounded" />
        <p class="mt-2"><strong>식사 시간:</strong> {{ formatDate(meal.mealTime) }}</p>
        <p><strong>감지된 음식:</strong> {{ meal.detectedFoods }}</p>
        <p><strong>자동 점수:</strong> {{ meal.autoScore }}</p>
      </div>

      <!-- 👤 회원 정보 -->
      <div class="bg-white p-4 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">회원 정보</h2>
        <p><strong>닉네임:</strong> {{ memberDetails.nickname }}</p>
        <p><strong>목표:</strong> {{ memberDetails.goal }}</p>
        <p><strong>키:</strong> {{ memberDetails.height }} cm</p>
        <p><strong>몸무게:</strong> {{ memberDetails.weight }} kg</p>
        <p><strong>체지방률:</strong> {{ memberDetails.bodyFatPercentage }}%</p>
        <p><strong>BMI:</strong> {{ memberDetails.bmi }}</p>
        <p><strong>단백질:</strong> {{ memberDetails.protein }}%</p>
        <p><strong>무기질:</strong> {{ memberDetails.mineral }}%</p>
        <p><strong>체수분:</strong> {{ memberDetails.bodyWater }}%</p>
        <p><strong>골격근량:</strong> {{ memberDetails.skeletalMuscle }}kg</p>
        <p><strong>체지방량:</strong> {{ memberDetails.bodyFatMass }}kg</p>
      </div>

      <!-- ⚠️ 알레르기 정보 -->
      <div class="bg-white p-4 rounded shadow">
        <h2 class="text-lg font-semibold mb-2">알레르기</h2>
        <ul class="list-disc list-inside">
          <li v-for="allergy in allergies" :key="allergy.name">{{ allergy.name }}</li>
        </ul>
      </div>

      <!-- 📝 피드백 입력 -->
      <div class="bg-white p-4 rounded shadow space-y-4">
        <h2 class="text-lg font-semibold">피드백 작성</h2>
        <textarea v-model="feedbackContent" rows="5" class="w-full border rounded p-2"
          placeholder="회원에게 전달할 피드백 내용을 입력하세요."></textarea>
        <div>
          <label class="font-semibold">점수 (10점 만점)</label>
          <input type="number" v-model.number="trainerScore" min="0" max="10" step="0.1"
            class="w-24 border rounded p-1 ml-2" />
        </div>
        <button @click="submitFeedback" class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">제출</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const mealId = route.params.mealId

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
    await axios.post(`/api/trainer/feedback/meal/${mealId}`, {
      content: feedbackContent.value,
      trainerScore: trainerScore.value
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('피드백이 성공적으로 등록되었습니다.')
    router.push('/trainer')
  } catch (e) {
    console.error('피드백 등록 실패', e)
    alert('피드백 등록에 실패했습니다.')
  }
}

onMounted(async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get(`/api/trainer/meals/${mealId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = res.data.data
    meal.value = data.meal
    memberDetails.value = data.memberDetails
    allergies.value = data.allergies
  } catch (e) {
    console.error('식단 상세 정보 불러오기 실패', e)
    alert('잘못된 요청입니다.')
    router.push('/trainer')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 스타일은 필요에 따라 조정 가능 */
</style>
