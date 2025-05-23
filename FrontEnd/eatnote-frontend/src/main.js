import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import { setupAxios } from './plugins/axios'

import Toast from 'vue-toastification'
import VCalendar from 'v-calendar'
import 'v-calendar/dist/style.css'
import 'vue-toastification/dist/index.css'

import { useAuthStore } from '@/stores/auth'

const app = createApp(App)
const pinia = createPinia()

// 1. Pinia 플러그인 설정
pinia.use(piniaPluginPersistedstate)
app.use(pinia) // 2. pinia 먼저 등록

// 3. 다른 것들 부착
app.use(router)
app.use(Toast)
app.use(VCalendar, {})

// 4. authStore 접근 (Pinia mount 이후여야 함)
const auth = useAuthStore()

// 5. Axios 인터셉터 설정
setupAxios(auth)

// 6. 로그인 상태이면 WebSocket 연결
if (auth.isLoggedIn && auth.user?.userId) {
  auth.connectWebSocket(auth.user.userId)
}

// 7. 앱 mount
app.mount('#app')
