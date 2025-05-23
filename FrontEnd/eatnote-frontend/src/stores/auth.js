// src/stores/auth.js
import { defineStore } from 'pinia'
import axios from 'axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let stompClient = null // 모듈 스코프에서 관리

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
        // localStorage.setItem('accessToken', accessToken)
        // localStorage.setItem('user', JSON.stringify(user))

        // 로그인 후 바로 웹소켓 연결
        this.connectWebSocket(user.userId)

        return true
      } catch (error) {
        console.error('로그인 실패:', error)
        return false
      }
    },
    logout() {
      this.accessToken = null
      this.user = null
      // localStorage.removeItem('accessToken')
      // localStorage.removeItem('user')

      // 로그아웃 시 웹소켓 연결 해제
      this.disconnectWebSocket()
    },

    connectWebSocket(userId) {
      if (stompClient && stompClient.connected) {
        console.log('이미 연결된 WebSocket이 있습니다.')
        return
      }

      const socket = new SockJS('http://localhost:8080/ws')
      stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        onConnect: () => {
          console.log('✅ WebSocket 연결 성공!')
          stompClient.subscribe(`/topic/notifications/${userId}`, (message) => {
            const body = JSON.parse(message.body)
            
            alert(`🔔 알림: ${body.content}`)

            window.dispatchEvent(new CustomEvent('newNotification', { 
              detail: body 
            }))
          })
        },
        onStompError: (frame) => {
          console.error('WebSocket STOMP 에러:', frame)
        },
      })

      stompClient.activate()
    },

    disconnectWebSocket() {
      if (stompClient && stompClient.connected) {
        stompClient.deactivate()
        console.log('🛑 WebSocket 연결 해제됨')
      }
    },

  },
  // 자동 저장 + 복원 설정
  persist: true,
})
