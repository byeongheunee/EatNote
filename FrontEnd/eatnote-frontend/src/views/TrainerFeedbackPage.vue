<template>
  <div class="trainer-feedback-page">
    <Header />

    <div class="feedback-container">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <div class="header-content">
          <h1 class="page-title">트레이너 피드백 관리</h1>
          <p class="page-subtitle">팔로워들의 식단을 확인하고 전문적인 피드백을 제공하세요</p>
        </div>
      </section>

      <div class="content-layout">
        <!-- 유저 목록 섹션 -->
        <section class="user-list-section">
          <div class="section-header">
            <h2 class="section-title">관리중인 회원 목록</h2>
            <div class="section-badge">
              <div class="pulse-dot blue"></div>
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
              <div class="pulse-dot red"></div>
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
              <div class="pulse-dot green"></div>
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
/* 페이지 전체 배경 - 베이지 톤으로 통일 */
.trainer-feedback-page {
  padding-top: 80px; /* 헤더 높이만큼 여백 */
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

/* 메인 컨테이너 - 1400px로 통일 */
.feedback-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  animation: fadeInUp 0.6s ease-out;
}

/* 페이지 헤더 - 통일된 스타일 */
.page-header {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px solid #f59e0b;
}

.header-content {
  text-align: center;
}

.page-title {
  font-size: 42px;
  font-weight: 600;
  color: #2D1810;
  margin-bottom: 12px;
}

.page-subtitle {
  font-size: 18px;
  color: #5D4037;
  line-height: 1.6;
}

/* 콘텐츠 레이아웃 */
.content-layout {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 섹션 공통 스타일 */
.user-list-section,
.content-section {
  animation: fadeInUp 0.6s ease-out;
}

.user-list-section {
  animation-delay: 0.1s;
}

.content-section {
  animation-delay: 0.2s;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2D1810;
}

/* 섹션 뱃지 - 통일된 스타일 */
.section-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 20px;
  padding: 8px 16px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.pulse-dot.blue {
  background: #3b82f6;
}

.pulse-dot.red {
  background: #ef4444;
}

.pulse-dot.green {
  background: #10b981;
}

.badge-text {
  font-size: 14px;
  font-weight: 600;
  color: #f59e0b;
}

/* 카드 컨테이너 - 글래스모피즘 통일 */
.card-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  border-radius: 16px;
  padding: 17px;
  box-shadow: 0 10px 15px -3px rgba(100, 116, 139, 0.1), 0 4px 6px -2px rgba(100, 116, 139, 0.05);
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.card-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(100, 116, 139, 0.1), 0 10px 10px -5px rgba(100, 116, 139, 0.04);
}

/* 식단 그리드 - 고정 배치 */
.meals-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 3열 고정 */
  gap: 24px;
  margin-bottom: 24px;
}

/* 더보기 버튼 - 통일된 스타일 */
.show-more-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.show-more-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #f9fafa, #dee4e4);
  color: rgb(51, 50, 50);
  border: 0.1px solid rgb(116, 114, 114, 0.5);
  border-radius: 12px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px -1px rgba(100, 116, 139, 0.1);
}

.show-more-button:hover {
  background: linear-gradient(135deg, #c9caca, #878888);
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(100, 116, 139, 0.15);
}

.button-icon {
  font-size: 12px;
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
  padding: 48px 32px;
  min-height: 200px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-text {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.empty-subtext {
  font-size: 14px;
  color: #6b7280;
}

/* 반응형 디자인 */
@media (max-width: 1200px) {
  .meals-grid {
    grid-template-columns: repeat(2, 1fr); /* 1200px 이하에서 2열 */
  }
}

@media (max-width: 1024px) {
  .feedback-container {
    padding: 24px 16px;
  }
  
  .page-title {
    font-size: 32px;
  }
}

@media (max-width: 768px) {
  .feedback-container {
    padding: 16px 12px;
  }

  .page-header {
    margin-bottom: 32px;
  }

  .page-title {
    font-size: 28px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .content-layout {
    gap: 24px;
  }

  .meals-grid {
    grid-template-columns: 1fr; /* 768px 이하에서 1열 */
    gap: 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .card-container {
    padding: 20px;
  }
}

@media (max-width: 640px) {
  .page-title {
    font-size: 24px;
  }

  .section-title {
    font-size: 20px;
  }

  .show-more-button {
    padding: 10px 20px;
    font-size: 13px;
  }

  .empty-state {
    padding: 32px 20px;
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

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
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
  background: #f59e0b;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #d97706;
}
</style>