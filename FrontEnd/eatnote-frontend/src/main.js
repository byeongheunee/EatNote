import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App)
const pinia = createPinia()

// persist 기능 활성화
pinia.use(piniaPluginPersistedstate)

app.use(createPinia())
app.use(router)

app.mount('#app')
