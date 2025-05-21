import { createRouter, createWebHistory } from 'vue-router'

// 기본 페이지들
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import CommunityView from '@/views/CommunityView.vue'
import ArticleWriteView from '@/views/ArticleWriteView.vue'
import VideoRecommendView from '@/views/VideoRecommendView.vue'
import MyPage from '@/views/MyPageView.vue'
import EditProfileView from '@/views/EditProfileView.vue'
import MealUploadPage from '@/views/MealUploadPage.vue'
import MealDetailPage from '@/views/MealDetailPage.vue'
import MyPageView from '@/views/MyPageView.vue'
import TrainerFeedbackForm from '@/views/TrainerFeedbackForm.vue'
import TrainerFeedbackPage from '@/views/TrainerFeedbackPage.vue'

// 탭 통합 메인 뷰
import MealView from '@/views/MealView.vue'
import TrainerDashboardView from '@/views/TrainerDashboardView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: HomeView },
    { path: '/login', name: 'Login', component: LoginView },
    { path: '/register', name: 'Register', component: RegisterView },
    { path: '/community', name: 'Community', component: CommunityView },
    { path: '/articles/write', name: 'ArticleWrite', component: ArticleWriteView },
    { path: '/videos', name: 'Videos', component: VideoRecommendView },
    { path: '/meal/upload', name: 'MealUpload', component: MealUploadPage },
    { path: '/meal/:id', name: 'MealDetail', component: MealDetailPage },
    { path: '/profile', name: 'MyPage', component: MyPageView, meta: { requiresAuth: true } },
    {
      path: '/trainer',
      name: 'TrainerDashboard',
      component: TrainerDashboardView,
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'MyPage',
      component: MyPage,
      meta: { requiresAuth: true }, // 해당 라우트가 로그인한 사용자만 접근 가능하도록 제한하겠다는 의미
    },
    {
      path: '/profile/edit',
      name: 'EditProfile',
      component: EditProfileView,
    },
    { path: '/meals', name: 'MealView', component: MealView },

    {
      path: '/trainer/feedback/:mealId',
      name: 'TrainerFeedbackForm',
      component: TrainerFeedbackForm,
      meta: { requiresAuth: true },
    },
    {
      path: '/trainer/feedback',
      name: 'TrainerFeedbackPage',
      component: TrainerFeedbackPage,
      meta: { requiresAuth: true },
    },
  ],
})

// 🔐 전역 네비게이션 가드
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('accessToken')
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
0
