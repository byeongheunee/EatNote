import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import VCalendar from 'v-calendar'
import 'v-calendar/dist/style.css' // ✅ 경로 수정

const app = createApp(App)
const pinia = createPinia()

pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)
app.use(VCalendar, {}) // ✅ Vue 3 등록 방식
app.mount('#app')
