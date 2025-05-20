// src/stores/auth.js
import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null,
    user: null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.accessToken,
    isTrainer: (state) => state.user?.userType === 1,
    isMember: (state) => state.user?.userType === 2,
  },

  actions: {
    setUser(user) {
      this.user = user
    },
    setToken(token) {
      this.accessToken = token
    },
    async login(email, password) {
      try {
        const response = await axios.post('/api/auth/login', { email, password })
        const { accessToken, user } = response.data.data

        this.accessToken = accessToken
        this.user = user

        // localStorage도 명시적으로 덮어쓰기
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('user', JSON.stringify(user))

        return true
      } catch (error) {
        console.error('로그인 실패:', error)
        return false
      }
    },
    async logout() {
      try {
        await axios.post('/api/auth/logout', {}, {
          headers: { Authorization: `Bearer ${this.accessToken}` }
        })
      } catch (e) {
        console.warn('서버 로그아웃 실패 (무시)', e)
      } finally {
        // 클라이언트 상태 정리
        this.accessToken = null
        this.user = null
        localStorage.removeItem('accessToken')
        localStorage.removeItem('user')
      }
    },
  },

  // ✅ 자동 저장 + 복원 설정
  persist: true
})
