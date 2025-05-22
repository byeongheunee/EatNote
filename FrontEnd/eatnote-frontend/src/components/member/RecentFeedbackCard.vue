                 <template>
  <div>
    <h2 class="text-lg font-bold mb-2">나의 트레이너</h2>
    <div v-if="feedbacks.length === 0" class="text-sm text-gray-500">아직 받은 피드백이 없습니다.</div>
    <div v-else class="flex flex-col gap-4">
      <div
        v-for="feedback in feedbacks"
        :key="feedback.feedbackId"
        class="border rounded p-3 shadow-sm bg-white"
        @click="goToMealDetail(feedback.mealId)"
      >
        <div class="flex items-center gap-3 mb-2">
          <img
            :src="getProfileImage(feedback.profileImage)"
            alt="프로필"
            class="w-10 h-10 rounded-full object-cover border"
          />
          <div>
            <p class="text-sm font-semibold">{{ feedback.trainerNickname }}</p>
            <p class="text-xs text-gray-500">{{ feedback.gymName }}</p>
          </div>
        </div>
        <p class="text-sm text-gray-700"><strong>식사:</strong> {{ translateMealType(feedback.mealType) }} · {{ feedback.detectedFoods }}</p>
        <p class="text-sm mt-1"><strong>피드백:</strong> {{ feedback.content }}</p>
        <p class="text-sm text-green-700 mt-1 font-semibold">트레이너 점수: {{ feedback.trainerScore }}/10</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  feedbacks: Array
})

const router = useRouter()

const getProfileImage = (url) => {
  return url ? `http://localhost:8080${url}` : '/images/default-profile.png'
}

const translateMealType = (type) => {
  switch(type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}`)
}
</script>


<style scoped>
.text-green-700 {
  color: #15803d;
}
</style>