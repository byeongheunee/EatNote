<!-- FollowerMeals.vue -->
<template>
    <div class="p-6">
        <h2 class="text-2xl font-bold mb-4">팔로우한 사용자의 식단</h2>

        <div v-if="loading" class="text-gray-500">불러오는 중...</div>
        <div v-else-if="meals.length === 0" class="text-gray-400">팔로우한 사용자의 식단이 없습니다.</div>

        <div v-else class="grid grid-cols-1 gap-4">
            <div v-for="meal in meals" :key="meal.mealId" @click="goToDetail(meal.mealId)"
                class="border p-4 rounded shadow hover:bg-gray-50 cursor-pointer">
                <p><strong>{{ meal.nickname }}</strong> 님의 {{ getMealTypeLabel(meal.mealType) }} 식사</p>
                <img :src="meal.imageUrl" alt="식단 이미지" class="w-full h-48 object-cover rounded my-2" />
                <p><strong>AI 점수:</strong> {{ meal.autoScore ?? '-' }}점</p>
                <p><strong>감지된 음식:</strong> {{ meal.detectedFoods }}</p>
                <div class="text-sm text-gray-500">{{ formatDate(meal.mealTime) }}</div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const meals = ref([])
const loading = ref(true)
const router = useRouter()

const getMealTypeLabel = (type) => {
    switch (type) {
        case 'breakfast': return '아침'
        case 'lunch': return '점심'
        case 'dinner': return '저녁'
        default: return '간식'
    }
}

const formatDate = (dateStr) => new Date(dateStr).toLocaleString()

const goToDetail = (mealId) => {
    router.push(`/meal/${mealId}`)
}

onMounted(async () => {
    const token = localStorage.getItem('accessToken')
    console.log('accessToken:', token)
    try {
        const res = await axios.get('/api/meal/followings', {
            headers: { Authorization: `Bearer ${token}` }
        })
        meals.value = res.data.data
    } catch (err) {
        console.error('팔로워 식단 조회 실패', err)
    } finally {
        loading.value = false
    }
})
</script>

<style scoped>
/* 필요한 스타일 추가 가능 */
</style>
