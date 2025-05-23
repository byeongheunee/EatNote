<template>
  <div class="max-w-md mx-auto mt-20 p-8 bg-white shadow-lg rounded-2xl space-y-6">
    <h2 class="text-2xl font-bold text-center text-gray-800">EatNote 로그인 🍽️</h2>

    <div>
      <label class="block text-sm font-medium text-gray-700">이메일</label>
      <input v-model="email" type="email" placeholder="이메일 입력"
        class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">비밀번호</label>
      <input v-model="password" type="password" placeholder="비밀번호 입력"
        class="mt-1 w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <button @click="handleLogin"
      class="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition duration-300">
      로그인
    </button>

    <div class="flex justify-between space-x-4">
      <button @click="goHome"
        class="w-1/2 border border-gray-300 text-gray-700 py-2 rounded-md hover:bg-gray-100 transition duration-300">
        홈으로
      </button>
      <button @click="goResister"
        class="w-1/2 border border-gray-300 text-gray-700 py-2 rounded-md hover:bg-gray-100 transition duration-300">
        회원가입
      </button>
    </div>
  </div>
</template>


<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const email = ref('')
const password = ref('')
const auth = useAuthStore()
const router = useRouter()
const toast = useToast()

const handleLogin = async () => {
  console.log('[디버깅] 로그인 시도 이메일:', email.value)
  console.log('[디버깅] 로그인 시도 비밀번호:', password.value)
  await auth.logout() // 이전 세션 정리
  const success = await auth.login(email.value, password.value)
  if (success) {
    // alert('로그인 성공!')
    toast.success('로그인 성공! 🎉')
    auth.connectWebSocket(auth.user.userId) // 로그인된 사용자 ID로 WebSocket 연결

    await nextTick()

    console.log(auth)
    console.log(auth.user.userType)
    if (auth.user.userType === 1) {
      console.log("트레이너")
      router.push('/trainer')
    } else if (auth.user.userType === 2) {
      console.log("회원")
      router.push('/member')
    } else {
      router.push('/') // 예외 상황 대비
    }
  } else {
    // alert('로그인 실패. 이메일 또는 비밀번호를 확인하세요!')
    toast.error('로그인 실패 😢\n이메일 또는 비밀번호를 확인하세요.')
  }
}

const goHome = () => {
  router.push('/')
}

const goResister = () => {
  router.push('/register')
}
</script>


<!-- <style scoped>
.login-wrapper {
  max-width: 400px;
  margin: 3rem auto;
  padding: 2rem;
  background-color: #fff8f1;
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(100, 80, 60, 0.1);
  font-family: 'Nanum Pen Script', cursive;
}

h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #5b4636;
}

label {
  display: block;
  margin-top: 1rem;
  font-size: 1.2rem;
  color: #6a5844;
}

input {
  width: 100%;
  font-size: 1.1rem;
  padding: 6px;
  margin-top: 0.3rem;
  border: none;
  border-bottom: 1px dashed #aaa;
  background: transparent;
}

input:focus {
  outline: none;
  border-bottom: 1px solid #444;
}

button {
  margin-top: 2rem;
  padding: 0.7rem 1.5rem;
  background-color: #d3bfa6;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

button:hover {
  background-color: #c2ae96;
}
</style> -->
