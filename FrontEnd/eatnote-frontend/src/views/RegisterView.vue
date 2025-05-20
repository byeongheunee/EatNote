<template>
  <div class="register-wrapper">
    <h2>EatNote 회원가입 📘</h2>

    <!-- 1단계: 회원 유형 선택 -->
    <div v-if="step === 1">
      <label>회원 유형을 선택하세요</label>
      <div class="user-type-buttons">
        <button @click="selectUserType(1)">트레이너</button>
        <button @click="selectUserType(2)">일반 회원</button>
      </div>
    </div>

    <!-- 2단계: 공통 정보 입력 -->
    <div v-else-if="step === 2">
      <label>이메일</label>
      <input v-model="form.email" type="email" />
      <p v-if="errors.email" style="color: red; font-size: 0.9rem;">이메일은 필수 입력 항목입니다.</p>

      <label>비밀번호</label>
      <input v-model="form.password" type="password" />
      <p v-if="errors.password" style="color: red; font-size: 0.9rem;">비밀번호는 필수 입력 항목입니다.</p>

      <label>비밀번호 확인</label>
      <input v-model="form.passwordConfirm" type="password" />
      <p v-if="errors.passwordConfirm" style="color: red; font-size: 0.9rem;">비밀번호 확인은 필수입니다.</p>
      <p v-if="passwordMismatch" style="color: red; font-size: 0.9rem;">비밀번호가 일치하지 않습니다.</p>

      <label>이름</label>
      <input v-model="form.name" type="text" />
      <p v-if="errors.name" style="color: red; font-size: 0.9rem;">이름은 필수 입력 항목입니다.</p>

      <label>닉네임</label>
      <div class="nickname-check">
        <input v-model="form.nickname" type="text" />
        <button type="button" @click="checkNickname">중복 확인</button>
      </div>
      <p v-if="errors.nickname" style="color: red; font-size: 0.9rem;">닉네임은 필수 입력 항목입니다.</p>
      <p v-if="nicknameMessage" :style="{ color: nicknameAvailable ? 'green' : 'red' }">
        {{ nicknameMessage }}
      </p>

      <label>성별</label>
      <select v-model="form.gender">
        <option value="M">남성</option>
        <option value="F">여성</option>
      </select>

      <label>나이</label>
      <input v-model="form.age" type="number" />
      <p v-if="errors.age" style="color: red; font-size: 0.9rem;">나이는 필수 입력 항목입니다.</p>

      <label>프로필 사진</label>
      <input type="file" @change="handleFileChange" />

      <button @click="goToNextStep">다음</button>
    </div>

    <!-- 3단계: 상세 정보 입력 -->
    <div v-else-if="step === 3">
      <component :is="detailComponent" v-model="formDetail" />
      <button @click="submitForm">회원가입 완료</button>
    </div>
  </div>

</template>






<script setup>
  import axios from 'axios'
  import { useRouter } from 'vue-router'
  import { ref, computed } from 'vue'
  import TrainerDetailForm from '@/components/register/TrainerDetailForm.vue'
  import MemberDetailForm from '@/components/register/MemberDetailForm.vue'

  const router = useRouter()
  const step = ref(1)
  const userType = ref(null)
  const profileImageFile = ref(null)

  const form = ref({
    email: '',
    password: '',
    passwordConfirm: '',
    name: '',
    nickname: '',
    gender: 'M',
    userType: null,
    age: 0,
  })

  const passwordMismatch = computed(() => form.value.password !== form.value.passwordConfirm)

  const formDetail = ref({})

  const handleFileChange = (event) => {
    profileImageFile.value = event.target.files[0]
  }

  const selectUserType = (type) => {
    userType.value = type
    form.value.userType = type
    step.value = 2
  }

  const errors = ref({
    email: false,
    password: false,
    passwordConfirm: false,
    name: false,
    nickname: false,
    age: false,
  })

  const goToNextStep = () => {
    if (passwordMismatch.value) {
      alert('비밀번호가 일치하지 않습니다.')
      return
    }

    // 필수 항목 체크
    errors.value.email = !form.value.email
    errors.value.password = !form.value.password
    errors.value.passwordConfirm = !form.value.passwordConfirm
    errors.value.name = !form.value.name
    errors.value.nickname = !form.value.nickname
    errors.value.age = !form.value.age

    if (Object.values(errors.value).some(e => e)) {
      return
    }

    if (nicknameAvailable.value !== true) {
      nicknameAvailable.value = false
      nicknameMessage.value = '닉네임 중복 확인을 해주세요.'
      return
    }

    if (userType.value === 1) {
      formDetail.value = {
        phone: '', address: '', gymName: '', gymWebsite: '', certificationNumber: '',
        businessNumber: '', introduction: '', career: '', instagramUrl: ''
      }
    } else {
      formDetail.value = {
        height: 0, weight: 0, bodyWater: 0, protein: 0, mineral: 0, bodyFat: 0,
        skeletalMuscle: 0, bodyFatMass: 0, bmi: 0, bodyFatPercentage: 0,
        goal: '', trainerNickname: '', allergyIds: []
      }
    }
    step.value = 3
  }

  const detailComponent = computed(() =>
    userType.value === 1 ? TrainerDetailForm : MemberDetailForm
  )

  const submitForm = async () => {
    const userJson = {
      ...form.value,
      ...(userType.value === 1
        ? { trainerDetails: formDetail.value }
        : { memberDetails: formDetail.value }),
    }
    delete userJson.passwordConfirm // 백엔드에 전송하지 않도록 제거

    const formData = new FormData()
    formData.append('user', JSON.stringify(userJson))
    if (profileImageFile.value) {
      formData.append('file', profileImageFile.value)
    }

    try {
      const response = await axios.post('/api/users/register', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      alert('회원가입 성공! 🎉')
      console.log('서버 응답:', response.data)

      // 로그인 페이지로 이동
      router.push('/login')

    } catch (error) {
      console.error('회원가입 실패:', error)
      alert('회원가입 중 오류가 발생했습니다.')
    }
  }

  const nicknameAvailable = ref(null)
  const nicknameMessage = ref('')

  const checkNickname = async () => {
    if (!form.value.nickname) {
      nicknameMessage.value = '닉네임을 입력해주세요.'
      nicknameAvailable.value = false
      return
    }

    try {
      const response = await axios.get(`/api/users/check-nickname?nickname=${form.value.nickname}`)
      console.log(response)
      nicknameAvailable.value = true
      nicknameMessage.value = '사용 가능한 닉네임입니다.'
    } catch (err) {
      nicknameAvailable.value = false
      nicknameMessage.value = '이미 사용 중인 닉네임입니다.'
    }
  }

</script>









<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nanum+Pen+Script&display=swap');

.register-wrapper {
  max-width: 700px;
  margin: 3rem auto;
  padding: 3rem;
  background: repeating-linear-gradient(to bottom,
      #fff,
      #fff 28px,
      #e0e0e0 29px);
  border: 1px solid #ccc;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(150, 130, 100, 0.15);
  font-family: 'Nanum Pen Script', cursive;
  line-height: 30px;
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
  color: #7a6855;
  font-size: 1.2rem;
}

input,
select {
  width: 100%;
  background-color: transparent;
  border: none;
  border-bottom: 1px dashed #a8a8a8;
  font-size: 1.2rem;
  padding: 4px 0;
  font-family: inherit;
}

input[type="file"] {
  border: none;
}

input:focus,
select:focus {
  outline: none;
  border-bottom: 1px solid #333;
  background-color: rgba(255, 255, 255, 0.5);
}

button {
  margin-top: 2rem;
  padding: 0.7rem 1.5rem;
  background-color: #d3bfa6;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #c2ae96;
}

.user-type-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
}

.user-type-buttons button {
  background-color: #f2e6dc;
  color: #5b4636;
  border: 1px solid #d2c4b2;
  border-radius: 6px;
  padding: 0.6rem 1.2rem;
  font-size: 1.1rem;
  font-family: 'Nanum Pen Script', cursive;
}

.nickname-check {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nickname-check input {
  flex: 1;
}

.nickname-check button {
  padding: 0.4rem 0.8rem;
  font-size: 1rem;
}

</style>
