import { createRouter, createWebHistory } from 'vue-router'
import RegisterView from '../views/RegisterView.vue'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
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

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterView,
    },
    {
      path: '/community',
      name: 'Community',
      component: CommunityView
    },
    {
      path: '/articles/write',
      name: 'ArticleWrite',
      component: ArticleWriteView
    },
    {
      path: '/videos',
      name: 'Videos',
      component: VideoRecommendView
    },
    {
      path: '/meal/upload',
      name: 'MealUpload',
      component: MealUpload
    },

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
  ],
})

// 전역 가드 (router/index.js)
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('accessToken') // 로그인 여부 체크

  if (to.meta.requiresAuth && !isLoggedIn) {
    // 로그인 안 한 사람은 로그인 페이지로 튕김
    next('/login')
  } else {
    next()
  }
})

export default router