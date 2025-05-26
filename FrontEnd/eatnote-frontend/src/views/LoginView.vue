<template>
  <div
    style="min-height: 100vh; background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);"
    class="flex items-center justify-center px-4">
    <div
      class="max-w-md w-full bg-white/80 backdrop-blur-sm shadow-2xl rounded-3xl p-8 space-y-6 border border-orange-100">
      <!-- 헤더 -->
      <div class="text-center space-y-2">
        <div class="flex justify-center">
          <img
            @click="goHome"
            src="@/assets/icons/EatNoteLogo.png"
            alt="EatNote 로고"
            class="h-18 mt-8 mb-5 cursor-pointer hover:opacity-80 transition-all duration-300"
          />
        </div>
        <p class="text-gray-600 text-sm">건강한 식습관 관리의 시작</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <!-- 이메일 입력 -->
        <div class="space-y-2">
          <label class="block text-sm font-semibold text-gray-700 flex items-center gap-2">
            <span class="text-orange-500">📧</span>
            이메일
          </label>
          <input v-model="email" type="email" placeholder="example@eatnote.com" :class="[
            'w-full px-4 py-3 border-2 rounded-xl transition-all duration-300 bg-white/70',
            emailError ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 focus:border-orange-400 focus:ring-orange-200',
            'focus:outline-none focus:ring-4 focus:ring-opacity-20'
          ]" @blur="validateEmail" @input="clearEmailError" />
          <p v-if="emailError" class="text-red-500 text-sm flex items-center gap-1">
            <span>⚠️</span>
            {{ emailError }}
          </p>
        </div>

        <!-- 비밀번호 입력 -->
        <div class="space-y-2">
          <label class="block text-sm font-semibold text-gray-700 flex items-center gap-2">
            <span class="text-orange-500">🔒</span>
            비밀번호
          </label>
          <div class="relative">
            <input v-model="password" :type="showPassword ? 'text' : 'password'" placeholder="비밀번호를 입력하세요" :class="[
              'w-full px-4 py-3 pr-12 border-2 rounded-xl transition-all duration-300 bg-white/70',
              passwordError ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 focus:border-orange-400 focus:ring-orange-200',
              'focus:outline-none focus:ring-4 focus:ring-opacity-20'
            ]" @blur="validatePassword" @input="clearPasswordError" />
            <button type="button" @click="togglePassword"
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors">
              {{ showPassword ? '🙈' : '👁️' }}
            </button>
          </div>
          <p v-if="passwordError" class="text-red-500 text-sm flex items-center gap-1">
            <span>⚠️</span>
            {{ passwordError }}
          </p>
        </div>

        <!-- 로그인 버튼 -->
        <button @click="handleLogin" :disabled="isLoading" :class="[
          'w-full py-3 rounded-xl font-semibold text-white transition-all duration-300 transform',
          isLoading
            ? 'bg-gray-400 cursor-not-allowed'
            : 'bg-gradient-to-r from-orange-500 to-yellow-500 hover:from-orange-600 hover:to-yellow-600 hover:scale-[1.02] hover:shadow-lg active:scale-[0.98]'
        ]">
          <span v-if="isLoading" class="flex items-center justify-center gap-2">
            <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
            로그인 중...
          </span>
          <span v-else class="flex items-center justify-center gap-2">
            🚀 로그인
          </span>
        </button>
      </form>
      <!-- 하단 버튼들 -->
      <div class="flex gap-3 pt-4">
        <button @click="goHome"
          class="flex-1 py-2.5 border-2 border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 hover:border-gray-400 transition-all duration-300 font-medium flex items-center justify-center gap-2">
          🏠 홈으로
        </button>
        <button @click="goRegister"
          class="flex-1 py-2.5 border-2 border-orange-300 text-orange-600 rounded-xl hover:bg-orange-50 hover:border-orange-400 transition-all duration-300 font-medium flex items-center justify-center gap-2">
          ✨ 회원가입
        </button>
      </div>

      <!-- 추가 링크 -->
      <div class="text-center pt-4 border-t border-gray-200">
        <p class="text-sm text-gray-500">
          비밀번호를 잊으셨나요?
          <a href="#" class="text-orange-500 hover:text-orange-600 font-medium underline">
            비밀번호 찾기
          </a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

// 반응형 데이터
const email = ref('')
const password = ref('')
const emailError = ref('')
const passwordError = ref('')
const showPassword = ref(false)
const isLoading = ref(false)

// 스토어 및 라우터
const auth = useAuthStore()
const router = useRouter()
const toast = useToast()

// 이메일 유효성 검사
const validateEmail = () => {
  if (!email.value.trim()) {
    emailError.value = '이메일을 입력해주세요'
    return false
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    emailError.value = '올바른 이메일 형식을 입력해주세요'
    return false
  }

  emailError.value = ''
  return true
}

// 비밀번호 유효성 검사
const validatePassword = () => {
  if (!password.value.trim()) {
    passwordError.value = '비밀번호를 입력해주세요'
    return false
  }

  // if (password.value.length < 6) {
  //   passwordError.value = '비밀번호는 6자 이상 입력해주세요'
  //   return false
  // }

  passwordError.value = ''
  return true
}

// 에러 클리어 함수들
const clearEmailError = () => {
  if (emailError.value) emailError.value = ''
}

const clearPasswordError = () => {
  if (passwordError.value) passwordError.value = ''
}

// 비밀번호 표시/숨김 토글
const togglePassword = () => {
  showPassword.value = !showPassword.value
}

// 로그인 처리
const handleLogin = async () => {
  // 유효성 검사
  const isEmailValid = validateEmail()
  const isPasswordValid = validatePassword()

  if (!isEmailValid || !isPasswordValid) {
    toast.error('입력 정보를 확인해주세요 😅')
    return
  }

  try {
    isLoading.value = true
    console.log('[디버깅] 로그인 시도 이메일:', email.value)
    console.log('[디버깅] 로그인 시도 비밀번호:', password.value)

    await auth.logout() // 이전 세션 정리
    const success = await auth.login(email.value, password.value)

    if (success) {
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
      toast.error('로그인 실패 😢\n이메일 또는 비밀번호를 확인하세요.')
    }
  } catch (error) {
    console.error('로그인 오류:', error)
    toast.error('로그인 중 오류가 발생했습니다 😥')
  } finally {
    isLoading.value = false
  }
}

// 네비게이션 함수들
const goHome = () => {
  router.push('/')
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
/* 커스텀 애니메이션 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-container {
  animation: fadeInUp 0.6s ease-out;
}

/* 입력 필드 포커스 효과 개선 */
input:focus {
  transform: translateY(-1px);
}

/* 버튼 호버 효과 */
button:not(:disabled):hover {
  transform: translateY(-1px);
}

button:not(:disabled):active {
  transform: translateY(0);
}

/* 로딩 스피너 */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>