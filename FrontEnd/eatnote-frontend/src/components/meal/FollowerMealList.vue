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
        <div 
          class="follower-item all-followers"
          :class="{ active: selectedUserId === null }"
          @click="selectUser(null)"
        >
          <div class="follower-avatar all-avatar">
            <span class="all-icon">👥</span>
          </div>
          <p class="follower-name">전체</p>
        </div>
        
        <div 
          v-for="user in filteredFollowings" 
          :key="user.userId" 
          class="follower-item"
          :class="{ active: selectedUserId === user.userId }"
          @click="selectUser(user.userId)"
        >
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
        <button 
          @click="setFilter('all')" 
          :class="['filter-button', { active: filter === 'all' }]"
        >
          <span class="filter-icon">🍽️</span>
          <span>전체 식단</span>
        </button>
        <button 
          @click="setFilter('today')" 
          :class="['filter-button', { active: filter === 'today' }]"
        >
          <span class="filter-icon">📅</span>
          <span>오늘의 식단</span>
        </button>
        <button 
          @click="setFilter('week')" 
          :class="['filter-button', { active: filter === 'week' }]"
        >
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
      <div 
        v-for="meal in limitedMeals" 
        :key="meal.mealId" 
        class="meal-card"
        @click="goToDetail(meal.mealId)"
      >
        <div class="meal-image-container">
          <img :src="getImageUrl(meal.imageUrl)" alt="meal" class="meal-image" />
          <div class="meal-type-badge">{{ mealTypeKor(meal.mealType) }}</div>
          <div class="meal-author" v-if="!selectedUserId">
            <img :src="getImageUrl(meal.user?.profileImage)" alt="author" class="author-avatar" />
            <span class="author-name">{{ meal.user?.nickname }}</span>
          </div>
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
            <LikeButton
              contentType="MEAL"
              :contentId="meal.mealId"
              :likeCount="meal.likeCount"
              :myReaction="meal.myReaction || null"
              @onUpdated="refreshMealList"
            />
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
.follower-meal-list {
  width: 100%;
  animation: fadeInUp 0.6s ease-out;
}

/* 헤더 섹션 */
.header-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-subtitle {
  color: #6b7280;
  font-size: 0.95rem;
  margin: 0;
}

/* 팔로워 섹션 */
.followers-section {
  margin-bottom: 2rem;
}

.followers-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 1rem 0;
}

.followers-scroll {
  display: flex;
  gap: 1rem;
  overflow-x: auto;
  padding: 0.5rem 0;
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
  padding: 0.75rem;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.5);
  border: 2px solid transparent;
}

.follower-item:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(245, 158, 11, 0.3);
}

.follower-item.active {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(217, 119, 6, 0.15));
  border-color: #f59e0b;
  transform: translateY(-2px);
}

.follower-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 0.5rem;
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
  font-size: 1.5rem;
}

.follower-name {
  font-size: 0.8rem;
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
  margin-bottom: 2rem;
}

.filter-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  border: 2px solid rgba(229, 231, 235, 0.8);
  background: rgba(255, 255, 255, 0.9);
  color: #6b7280;
  font-weight: 600;
  font-size: 0.9rem;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(5px);
}

.filter-button:hover {
  border-color: rgba(245, 158, 11, 0.5);
  background: rgba(255, 255, 255, 1);
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.filter-button.active {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-color: #f59e0b;
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  transform: translateY(-1px);
}

.filter-icon {
  font-size: 1rem;
}

/* 선택된 사용자 정보 */
.selected-user-info {
  margin-bottom: 2rem;
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(5, 150, 105, 0.1));
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: 12px;
  padding: 1rem;
}

.user-info-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #10b981;
}

.user-info-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: #047857;
  margin: 0 0 0.25rem 0;
}

.user-info-description {
  font-size: 0.9rem;
  color: #059669;
  margin: 0;
}

/* 식단 그리드 */
.meals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.meal-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.meal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.3);
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
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.9), rgba(5, 150, 105, 0.9));
  color: white;
  padding: 0.375rem 0.75rem;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 600;
  backdrop-filter: blur(5px);
}

.meal-author {
  position: absolute;
  bottom: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(0, 0, 0, 0.7);
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  backdrop-filter: blur(5px);
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
  font-size: 0.8rem;
  font-weight: 600;
}

.meal-content {
  padding: 1.25rem;
}

.meal-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
  line-height: 1.4;
}

.meal-date {
  color: #6b7280;
  font-size: 0.85rem;
  margin: 0 0 0.75rem 0;
}

.meal-info {
  margin-bottom: 1rem;
}

.calorie-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.1));
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.calorie-icon {
  font-size: 1rem;
}

.calorie-text {
  font-weight: 600;
  color: #d97706;
  font-size: 0.9rem;
}

.meal-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(229, 231, 235, 0.5);
}

.feedback-count {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  color: #6b7280;
  font-size: 0.85rem;
  font-weight: 500;
}

.feedback-icon {
  font-size: 0.9rem;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 2px dashed rgba(245, 158, 11, 0.3);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.7;
}

.empty-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.empty-description {
  color: #6b7280;
  margin: 0;
}

/* 더보기 버튼 */
.load-more-section {
  text-align: center;
  padding-top: 1rem;
}

.load-more-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  backdrop-filter: blur(10px);
  color: #6b7280;
  border: 2px solid rgba(229, 231, 235, 0.8);
  border-radius: 12px;
  padding: 0.875rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-button:hover {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border-color: #f59e0b;
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.3);
}

.load-more-icon {
  font-size: 1rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .followers-scroll {
    gap: 0.75rem;
  }
  
  .follower-item {
    min-width: 70px;
    padding: 0.5rem;
  }
  
  .follower-avatar {
    width: 50px;
    height: 50px;
  }
  
  .follower-name {
    font-size: 0.75rem;
    max-width: 60px;
  }
  
  .meals-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
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
    padding: 0.625rem 1rem;
    font-size: 0.85rem;
  }
  
  .meal-content {
    padding: 1rem;
  }
  
  .empty-state {
    padding: 2rem 1rem;
  }
  
  .empty-icon {
    font-size: 3rem;
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