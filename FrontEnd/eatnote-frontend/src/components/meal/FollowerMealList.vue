<!-- src/components/meal/FollowerMealList.vue -->
<template>
  <div class="follower-meal-list">
    <!-- 헤더 섹션 -->
    <div class="header-section">
      <h2 class="section-title">팔로워 식단</h2>
      <p class="section-subtitle">팔로우하는 사람들의 식단을 확인해보세요</p>
    </div>

    <!-- 팔로워 프로필 섹션 -->
    <div class="followers-section">
      <h3 class="followers-title">팔로우 중인 사람들</h3>
      <div class="followers-scroll">
        <div class="follower-item all-followers" :class="{ active: selectedUserId === null }" @click="selectUser(null)">
          <div class="follower-avatar all-avatar">
            <span class="all-icon">👥</span>
          </div>
          <p class="follower-name">전체</p>
        </div>

        <div v-for="user in filteredFollowings" :key="user.userId" class="follower-item"
          :class="{ active: selectedUserId === user.userId }" @click="selectUser(user.userId)">
          <div class="follower-avatar">
            <img :src="getImageUrl(user.profileImage)" alt="profile" class="follower-image" />
          </div>
          <p class="follower-name">{{ user.nickname }}</p>
        </div>
      </div>
    </div>

    <!-- 필터 섹션 -->
    <div class="filter-section">
      <div class="filter-buttons">
        <button @click="setFilter('all')" :class="['filter-button', { active: filter === 'all' }]">
          <span class="filter-icon">🍽️</span>
          <span>전체 식단</span>
        </button>
        <button @click="setFilter('today')" :class="['filter-button', { active: filter === 'today' }]">
          <span class="filter-icon">📅</span>
          <span>오늘의 식단</span>
        </button>
        <button @click="setFilter('week')" :class="['filter-button', { active: filter === 'week' }]">
          <span class="filter-icon">📊</span>
          <span>이번주 식단</span>
        </button>
      </div>
    </div>

    <!-- 선택된 사용자 정보 -->
    <div v-if="selectedUserId" class="selected-user-info">
      <div class="user-info-card">
        <img :src="getImageUrl(selectedUser?.profileImage)" alt="profile" class="user-info-avatar" />
        <div class="user-info-text">
          <h4 class="user-info-name">{{ selectedUser?.nickname }}님의 식단</h4>
          <p class="user-info-description">{{ filteredMeals.length }}개의 식단이 있습니다</p>
        </div>
      </div>
    </div>

    <!-- 식단 그리드 -->
    <div v-if="limitedMeals.length > 0" class="meals-grid">
      <div v-for="meal in limitedMeals" :key="meal.mealId" class="meal-card" @click="goToDetail(meal.mealId)">
        <div class="meal-image-container">
          <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="meal-image" />
          <div class="meal-type-badge">{{ getMealTypeEmoji(meal.mealType) }}</div>
          <!-- <div class="meal-author" v-if="!selectedUserId">
            <img :src="getImageUrl(meal.user?.profileImage)" alt="author" class="author-avatar" />
            <span class="author-name">{{ meal.user?.nickname }}</span>
          </div> -->
        </div>

        <div class="meal-content">
          <h3 class="meal-title">{{ meal.detectedFoods }}</h3>
          <p class="meal-date">{{ formatDate(meal.mealTime) }}</p>
          <div class="meal-info">
            <div class="calorie-info">
              <span class="calorie-icon">🔥</span>
              <span class="calorie-text">{{ meal.totalCalories }}kcal</span>
            </div>
          </div>

          <div class="meal-actions">
            <div class="feedback-count">
              <span class="feedback-icon">💬</span>
              <span>{{ meal.feedbackCount }}</span>
            </div>
            <LikeButton contentType="MEAL" :contentId="meal.mealId" :likeCount="meal.likeCount"
              :myReaction="meal.myReaction || null" @onUpdated="refreshMealList" />
          </div>
        </div>
      </div>
    </div>

    <!-- 빈 상태 -->
    <div v-else class="empty-state">
      <div class="empty-icon">🍽️</div>
      <h3 class="empty-title">
        {{ selectedUserId ? '해당 사용자는 아직 식단을 올리지 않았습니다' : '팔로우하는 사람들의 식단이 없어요' }}
      </h3>
      <p class="empty-description">
        {{ selectedUserId ? '다른 사용자를 선택해보세요' : '새로운 사람들을 팔로우해보세요!' }}
      </p>
    </div>

    <!-- 더보기 버튼 -->
    <div class="load-more-section" v-if="filteredMeals.length > mealLimit">
      <button @click="mealLimit += 9" class="load-more-button">
        <span>더 많은 식단 보기</span>
        <span class="load-more-icon">📋</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import { useRouter } from 'vue-router'
import LikeButton from '@/components/LikeButton.vue'
import dayjs from 'dayjs'

const meals = ref([])
const followings = ref([])
const selectedUserId = ref(null)
const filter = ref('all')
const router = useRouter()
const auth = useAuthStore()
const mealLimit = ref(9)

const limitedMeals = computed(() => filteredMeals.value.slice(0, mealLimit.value))
const filteredFollowings = computed(() => followings.value.filter(user => user.userType === 2))
const selectedUser = computed(() => followings.value.find(user => user.userId === selectedUserId.value))

const filteredMeals = computed(() => {
  if (filter.value === 'today') {
    const today = dayjs().format('YYYY-MM-DD')
    return meals.value.filter(m => dayjs(m.mealTime).format('YYYY-MM-DD') === today)
  } else if (filter.value === 'week') {
    const startOfWeek = dayjs().startOf('week')
    const endOfWeek = dayjs().endOf('week')
    return meals.value.filter(m => dayjs(m.mealTime).isAfter(startOfWeek) && dayjs(m.mealTime).isBefore(endOfWeek))
  }
  return meals.value
})

const getImageUrl = (path) => {
  if (!path) return '/images/default-profile.png'
  return `http://localhost:8080${path.startsWith('/') ? path : '/' + path}`
}

const formatDate = (datetime) => new Date(datetime).toLocaleDateString('ko-KR')

// 식사 타입에 따른 이모티콘 (첫 번째 코드와 동일하게 수정)
const getMealTypeEmoji = (mealType) => {
  switch (mealType) {
    case 'breakfast': return '☀️' // 아침
    case 'lunch': return '🍽️' // 점심  
    case 'dinner': return '🌙' // 저녁
    case 'extra': return '🍩' // 간식
    default: return '🍽️'
  }
}

const mealTypeKor = (type) => {
  switch (type) {
    case 'breakfast': return '아침'
    case 'lunch': return '점심'
    case 'dinner': return '저녁'
    case 'extra': return '간식'
    default: return type
  }
}

const loadAllMeals = async () => {
  try {
    const res = await axios.get('/api/meal/followings')
    meals.value = res.data.data
  } catch (e) {
    console.error('팔로워 식단 로딩 실패:', e)
  }
}

const loadFollowings = async () => {
  try {
    const res = await axios.get('/api/follow/following')
    followings.value = res.data.data
  } catch (e) {
    console.error('팔로우 목록 로딩 실패:', e)
  }
}

const selectUser = async (userId) => {
  if (selectedUserId.value === userId) {
    selectedUserId.value = null
    await loadAllMeals()
    return
  }

  selectedUserId.value = userId
  if (userId === null) {
    await loadAllMeals()
  } else {
    try {
      const res = await axios.get(`/api/users/user/${userId}/meals`)
      meals.value = res.data.data
    } catch (e) {
      console.error('선택 유저 식단 조회 실패:', e)
    }
  }
  mealLimit.value = 9 // 사용자 변경 시 개수 초기화
}

const setFilter = (f) => {
  filter.value = f
  mealLimit.value = 9 // 필터 변경 시 개수 초기화
}

const goToDetail = (mealId) => router.push(`/meal/${mealId}`)

const refreshMealList = async () => {
  if (selectedUserId.value) {
    try {
      const res = await axios.get(`/api/users/user/${selectedUserId.value}/meals`)
      meals.value = res.data.data
    } catch (e) {
      console.error('선택 유저 식단 새로고침 실패:', e)
    }
  } else {
    await loadAllMeals()
  }
}

onMounted(async () => {
  await loadFollowings()
  await loadAllMeals()
})
</script>

<style scoped>
/* 페이지 전체 */
.follower-meal-list {
  width: 100%;
  animation: fadeInUp 0.6s ease-out;
}

/* 헤더 섹션 */
.header-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2D1810;
  margin: 0 0 8px 0;
}

.section-subtitle {
  color: #5D4037;
  font-size: 15px;
  margin: 0;
  line-height: 1.5;
}

/* 팔로워 섹션 */
.followers-section {
  margin-bottom: 32px;
}

.followers-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
}

.followers-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding: 8px 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.followers-scroll::-webkit-scrollbar {
  display: none;
}

.follower-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid transparent;
}

.follower-item:hover {
  transform: translateY(-2px);
  background: white;
  border-color: rgba(245, 158, 11, 0.3);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.follower-item.active {
  background: rgba(245, 158, 11, 0.1);
  border-color: #f59e0b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.2);
}

.follower-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 8px;
  border: 3px solid rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.follower-item.active .follower-avatar {
  border-color: #f59e0b;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.follower-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.all-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.all-icon {
  font-size: 24px;
}

.follower-name {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
  text-align: center;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70px;
}

/* 필터 섹션 */
.filter-section {
  margin-bottom: 32px;
}

.filter-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  font-weight: 600;
  font-size: 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-button:hover {
  border-color: #f59e0b;
  background: white;
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.filter-button.active {
  background: linear-gradient(135deg, #f3ae36, #ee9024);
  border-color: #f59e0b;
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  transform: translateY(-1px);
}

.filter-icon {
  font-size: 16px;
}

/* 선택된 사용자 정보 */
.selected-user-info {
  margin-bottom: 32px;
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: 12px;
  padding: 16px;
}

.user-info-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #10b981;
}

.user-info-name {
  font-size: 18px;
  font-weight: 700;
  color: #047857;
  margin: 0 0 4px 0;
}

.user-info-description {
  font-size: 14px;
  color: #059669;
  margin: 0;
}

/* 식단 그리드 */
.meals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.meal-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.meal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.2);
  border-color: #f59e0b;
}

.meal-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.meal-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.meal-card:hover .meal-image {
  transform: scale(1.05);
}

.meal-type-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  font-size: 1rem;
}

.meal-card:hover .meal-type-badge {
  transform: scale(1.1);
}

.meal-author {
  position: absolute;
  bottom: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0, 0, 0, 0.7);
  padding: 6px 12px;
  border-radius: 20px;
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.author-name {
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.meal-content {
  padding: 20px;
}

.meal-title {
  font-size: 18px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.meal-date {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 12px 0;
}

.meal-info {
  margin-bottom: 16px;
}

.calorie-info {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(245, 158, 11, 0.1);
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.calorie-icon {
  font-size: 16px;
}

.calorie-text {
  font-weight: 600;
  color: #d97706;
  font-size: 14px;
}

.meal-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.feedback-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
}

.feedback-icon {
  font-size: 14px;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 48px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  border: 2px dashed rgba(245, 158, 11, 0.3);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.7;
}

.empty-title {
  font-size: 20px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-description {
  color: #6b7280;
  margin: 0;
}

/* 더보기 버튼 */
.load-more-section {
  text-align: center;
  padding-top: 16px;
}

.load-more-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 24px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-button:hover {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border-color: #f59e0b;
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.3);
}

.load-more-icon {
  font-size: 16px;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .followers-scroll {
    gap: 12px;
  }

  .follower-item {
    min-width: 70px;
    padding: 8px;
  }

  .follower-avatar {
    width: 50px;
    height: 50px;
  }

  .follower-name {
    font-size: 12px;
    max-width: 60px;
  }

  .meals-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .filter-buttons {
    justify-content: center;
  }

  .filter-button {
    flex: 1;
    justify-content: center;
  }

  .user-info-card {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 640px) {
  .filter-button {
    padding: 10px 16px;
    font-size: 14px;
  }

  .meal-content {
    padding: 16px;
  }

  .empty-state {
    padding: 32px 16px;
  }

  .empty-icon {
    font-size: 48px;
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
</style>