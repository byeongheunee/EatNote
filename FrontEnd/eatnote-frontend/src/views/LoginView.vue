<template>
  <div class="login-wrapper">
    <h2>EatNote 로그인 🍽️</h2>

    <label>이메일</label>
    <input v-model="email" type="email" placeholder="이메일 입력" />

    <label>비밀번호</label>
    <input v-model="password" type="password" placeholder="비밀번호 입력" />

    <button @click="handleLogin">로그인</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const email = ref('')
const password = ref('')

const auth = useAuthStore()
const router = useRouter()

const handleLogin = async () => {
  const success = await auth.login(email.value, password.value)
  if (success) {
    alert('로그인 성공!')
    router.push('/')
  } else {
    alert('로그인 실패. 이메일 또는 비밀번호를 확인하세요!')
  }
}
</script>

<style scoped>
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
</style>
