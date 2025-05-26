<template>
  <div class="trainer-feedback-page">
    <Header />

    <div class="feedback-container">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <h1 class="page-title">
          🥗 트레이너 피드백 관리
        </h1>
      </section>

      <div class="content-layout">
        <!-- 유저 목록 섹션 -->
        <section class="user-list-section">
          <div class="section-header">
            <h2 class="section-title">팔로워 목록</h2>
            <div class="section-badge">
              <div class="w-2 h-2 bg-blue-400 rounded-full animate-pulse"></div>
              <span class="badge-text">{{ users.length }}명</span>
            </div>
          </div>
          <div class="card-container">
            <TrainerFolloweeList :users="users" :selected-user-id="selectedUserId" :total-pending="pendingMeals.length"
              :total-done="calendarStats.reduce((sum, s) => sum + s.written, 0)" @select="selectUser" />
          </div>
        </section>

        <!-- 전체 미작성 식단 섹션 -->
        <section v-if="!selectedUserId && pendingMeals.length > 0" class="content-section">
          <div class="section-header">
            <h2 class="section-title">🟡 전체 피드백 미작성 식단</h2>
            <div class="section-badge">
              <div class="w-2 h-2 bg-red-400 rounded-full animate-pulse"></div>
              <span class="badge-text">{{ pendingMeals.length }}개</span>
            </div>
          </div>
          <div class="card-container">
            <div class="meals-grid">
              <TrainerMealCard v-for="meal in visiblePendingMeals" :key="meal.mealId" :meal="meal"
                :highlightPending="true" @feedback="goToFeedbackForm" @edit="editFeedback" />
            </div>
            <div v-if="visiblePendingMeals.length < pendingMeals.length" class="show-more-container">
              <button @click="showMorePending" class="show-more-button">
                <span>더보기</span>
                <span class="button-icon">↓</span>
              </button>
            </div>
          </div>
        </section>

        <!-- 선택된 유저의 식단 목록 -->
        <section v-if="selectedUserId" class="content-section">
          <div class="section-header">
            <h2 class="section-title">👤 {{ selectedUserNickname }}님의 식단</h2>
            <div class="section-badge">
              <div class="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
              <span class="badge-text">{{ meals.length }}개</span>
            </div>
          </div>
          <div class="card-container">
            <div v-if="meals.length > 0">
              <div class="meals-grid">
                <TrainerMealCard v-for="meal in visibleMeals" :key="meal.mealId" :meal="meal"
                  :highlightPending="!meal.isFeedbackedByMe" @feedback="goToFeedbackForm" @view="goToMealDetail" />
              </div>
              <div v-if="visibleMeals.length < meals.length" class="show-more-container">
                <button @click="showMore" class="show-more-button">
                  <span>더보기</span>
                  <span class="button-icon">↓</span>
                </button>
              </div>
            </div>
            <div v-else class="empty-state">
              <div class="empty-icon">🍽️</div>
              <p class="empty-text">식단 데이터가 없습니다</p>
              <p class="empty-subtext">아직 등록된 식단이 없어요</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import TrainerFolloweeList from '@/components/Trainer/TrainerFolloweeList.vue'
import TrainerMealCard from '@/components/Trainer/TrainerMealCard.vue'
import Header from '@/components/common/Header.vue'
import { useToast } from 'vue-toastification'
const toast = useToast()

const visibleCount = ref(6)
const visibleCountPending = ref(6)

const visibleMeals = computed(() => {
  return meals.value.slice(0, visibleCount.value)
})

const visiblePendingMeals = computed(() => {
  return pendingMeals.value.slice(0, visibleCountPending.value)
})

const showMore = () => {
  visibleCount.value = Math.min(visibleCount.value + 6, meals.value.length)
}

const showMorePending = () => {
  visibleCountPending.value = Math.min(visibleCountPending.value + 6, pendingMeals.value.length)
}

const users = ref([])
const pendingMeals = ref([])
const selectedUserId = ref(null)
const selectedUserNickname = ref('')
const meals = ref([])
const calendarStats = ref([])
const selectedMonth = ref(new Date().toISOString().slice(0, 7))
const router = useRouter()

const editFeedback = (feedbackId) => {
  router.push(`/trainer/feedback/${feedbackId}/edit`)
}

const authHeader = {
  headers: {
    Authorization: `Bearer ${localStorage.getItem('accessToken')}`
  }
}

const goToFeedbackForm = (mealId) => {
  router.push(`/trainer/feedback/${mealId}`)
}

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}`)
}

const selectUser = async (userId) => {
  if (userId === null) {
    selectedUserId.value = null
    selectedUserNickname.value = ''
    return
  }

  selectedUserId.value = userId
  const selected = users.value.find(u => u.userId === userId)
  selectedUserNickname.value = selected?.nickname || ''

  try {
    const res = await axios.get(`/api/users/user/${userId}/meals`, authHeader)

    const sortedMeals = (res.data.data || [])
      .filter(m => m !== null)
      .sort((a, b) => {
        const aPending = !(a.isFeedbackedByMe === true || a.isFeedbackedByMe === 'true')
        const bPending = !(b.isFeedbackedByMe === true || b.isFeedbackedByMe === 'true')

        if (aPending && !bPending) return -1
        if (!aPending && bPending) return 1

        return new Date(b.mealTime) - new Date(a.mealTime)
      })

    meals.value = sortedMeals
    visibleCount.value = 6
  } catch (e) {
    console.error('식단 조회 실패', e)
    toast.error('해당 회원의 식단을 불러오는 데 실패했습니다.')
  }
}

const fetchCalendarStats = async () => {
  try {
    const res = await axios.get('/api/trainer/feedback/statistics', {
      ...authHeader,
      params: { month: selectedMonth.value }
    })
    calendarStats.value = res.data.data || []
    console.log('📅 calendarStats:', calendarStats.value)
  } catch (e) {
    toast.warning('달력 통계를 불러오지 못했어요. \n 화면을 새로고침 해보세요. 🔁')
  }
}

onMounted(async () => {
  try {
    const pendingRes = await axios.get('/api/trainer/pending', authHeader)
    pendingMeals.value = pendingRes.data.data || []

    const userRes = await axios.get('/api/trainer/feedback/followings', authHeader)
    users.value = (userRes.data.data || [])
      .filter(u => u !== null)
      .sort((a, b) => (b.pendingCount || 0) - (a.pendingCount || 0))

    await fetchCalendarStats()
  } catch (e) {
    console.error('초기 데이터 로딩 실패', e)
  }
})
</script>

<style scoped>
/* 페이지 전체 배경 */
.trainer-feedback-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ed 0%, #fef3c7 50%, #fef3c7 100%);
}

.feedback-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

/* 페이지 헤더 */
.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.page-title {
  font-size: 2.25rem;
  font-weight: 700;
  color: #374151;
  margin: 0;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 콘텐츠 레이아웃 */
.content-layout {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 유저 목록 섹션 */
.user-list-section {
  animation: fadeInUp 0.6s ease-out;
}

/* 콘텐츠 섹션 */
.content-section {
  animation: fadeInUp 0.6s ease-out;
  animation-delay: 0.2s;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 50px;
  padding: 0.5rem 1rem;
}

.badge-text {
  font-size: 0.85rem;
  font-weight: 600;
  color: #f59e0b;
}

/* 카드 컨테이너 */
.card-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
}

.card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.12);
  border-color: rgba(245, 158, 11, 0.3);
}

/* 식단 그리드 */
.meals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

/* 더보기 버튼 */
.show-more-container {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}

.show-more-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.75rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.show-more-button:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

.button-icon {
  font-size: 0.8rem;
  transition: transform 0.3s ease;
}

.show-more-button:hover .button-icon {
  transform: translateY(2px);
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 3rem 2rem;
  min-height: 200px;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
}

.empty-subtext {
  font-size: 0.9rem;
  color: #6b7280;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .feedback-container {
    padding: 1rem;
  }

  .page-title {
    font-size: 1.75rem;
  }

  .meals-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .card-container {
    padding: 1.25rem;
  }
}

@media (max-width: 640px) {
  .section-title {
    font-size: 1.25rem;
  }

  .show-more-button {
    padding: 0.625rem 1.25rem;
    font-size: 0.85rem;
  }

  .empty-state {
    padding: 2rem 1rem;
  }
}

/* 애니메이션 */
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

/* Tailwind 클래스 대체 */
.w-2 {
  width: 0.5rem;
}

.h-2 {
  height: 0.5rem;
}

.bg-blue-400 {
  background-color: #60a5fa;
}

.bg-red-400 {
  background-color: #f87171;
}

.bg-green-400 {
  background-color: #4ade80;
}

.rounded-full {
  border-radius: 9999px;
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.5;
  }
}
</style>