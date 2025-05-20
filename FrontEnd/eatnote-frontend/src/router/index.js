import { createRouter, createWebHistory } from 'vue-router'

// 기본 페이지들
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import CommunityView from '@/views/CommunityView.vue'
import ArticleWriteView from '@/views/ArticleWriteView.vue'
import VideoRecommendView from '@/views/VideoRecommendView.vue'
import MealUpload from '@/views/MealUploadPage.vue'
import MealDetail from '@/views/MealDetailPage.vue'
import MealList from '@/views/MealListPage.vue'
import MealStats from '@/views/MyMealStatsPage.vue'
import FollowerMeals from '@/views/FollowerMealView.vue'
import MyPage from '@/views/MyPageView.vue'
import EditProfileView from '@/views/EditProfileView.vue'
import MealUploadPage from '@/views/MealUploadPage.vue'
import MealDetailPage from '@/views/MealDetailPage.vue'
import MyPageView from '@/views/MyPageView.vue'

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
    { path: '/trainer', name: 'TrainerDashboard', component: TrainerDashboardView, meta: { requiresAuth: true }
    },

<<<<<<< HEAD
    {
      path: '/meal/:id',
      name: 'MealDetail',
      component: MealDetail,
    },
    {
      path: '/meals/followings',
      name: 'FollowerMeals',
      component: FollowerMeals,
    },

    {
      path: '/meals/:userId',
      name: 'MealList',
      component: MealList,
    },
    {
      path: '/meal/:Id/stats',
      name: 'MealStats',
      component: MealStats,
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
      component: EditProfileView
    },
=======
    // ✅ 탭 기반 식단 메인 뷰
    { path: '/meals', name: 'MealView', component: MealView },
>>>>>>> 7f93114 (commit)
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