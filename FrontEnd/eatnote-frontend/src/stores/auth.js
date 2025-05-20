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
    async login(email, password) {
      try {
        const response = await axios.post('/api/auth/login', { email, password })
        const { accessToken, user } = response.data.data

        this.accessToken = accessToken
        this.user = user

        // 로컬스토리지 저장
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('user', JSON.stringify(user))

        return true
      } catch (error) {
        console.error('로그인 실패:', error)
        return false
      }
    },

    logout() {
      this.accessToken = null
      this.user = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
    },

    restore() {
      const token = localStorage.getItem('accessToken')
      const user = localStorage.getItem('user')
      if (token && user) {
        this.accessToken = token
        this.user = JSON.parse(user)
      }
    }
  }
})
