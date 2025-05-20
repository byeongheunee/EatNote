<template>
  <div>
    <!-- 로그인 상태 전용 헤더 -->
    <Header
      @go-feature="scrollToFeature"
      @go-usage="scrollToUsage"
    />

    <!-- 본문 -->
    <div class="max-w-3xl mx-auto my-10 p-6 bg-white rounded shadow">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold mb-4">회원정보</h2>
        <button @click="showModal = true" class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">
          ✏️ 수정하기
        </button>
      </div>

      <div v-if="user" class="mb-6">
        <img :src="getProfileImage(user.profileImage)" alt="프로필" class="w-24 h-24 rounded-full mb-4" />
        <p><strong>이메일:</strong> {{ user.email }}</p>
        <p><strong>이름:</strong> {{ user.name }}</p>
        <p><strong>닉네임:</strong> {{ user.nickname }}</p>
        <p><strong>성별:</strong> {{ user.gender }}</p>
        <p><strong>나이:</strong> {{ user.age }}</p>
        <p><strong>가입일:</strong> {{ user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-' }}</p>
      </div>

      <!-- 일반 회원 정보 -->
      <div v-if="memberDetails">
        <h3 class="text-xl font-semibold mt-6 mb-2">신체 정보</h3>
        <p><strong>키:</strong> {{ memberDetails.height }} cm</p>
        <p><strong>몸무게:</strong> {{ memberDetails.weight }} kg</p>
        <p><strong>BMI:</strong> {{ memberDetails.bmi }}</p>
        <p><strong>체수분:</strong> {{ memberDetails.bodyWater }} %</p>
        <p><strong>단백질:</strong> {{ memberDetails.protein }} %</p>
        <p><strong>무기질:</strong> {{ memberDetails.mineral }} %</p>
        <p><strong>체지방:</strong> {{ memberDetails.bodyFat }} kg</p>
        <p><strong>골격근:</strong> {{ memberDetails.skeletalMuscle }} kg</p>
        <p><strong>체지방량:</strong> {{ memberDetails.bodyFatMass }} kg</p>
        <p><strong>체지방률:</strong> {{ memberDetails.bodyFatPercentage }} %</p>
        <p><strong>목표:</strong> {{ memberDetails.goal }}</p>
        <p><strong>담당 트레이너:</strong> {{ memberDetails.trainerNickname }}</p>

        <h3 class="text-xl font-semibold mt-6 mb-2">알레르기</h3>
        <ul>
          <li v-for="a in allergies" :key="a.allergyId">
            {{ a.name }} ({{ a.category }})
          </li>
        </ul>
      </div>

      <!-- 트레이너 정보 -->
      <div v-if="trainerDetails">
        <h3 class="text-xl font-semibold mt-6 mb-2">트레이너 상세 정보</h3>
        <p><strong>연락처:</strong> {{ trainerDetails.phone }}</p>
        <p><strong>주소:</strong> {{ trainerDetails.address }}</p>
        <p><strong>소속 헬스장:</strong> {{ trainerDetails.gymName }}</p>
        <p>
          <strong>헬스장 홈페이지:</strong>
          <a :href="trainerDetails.gymWebsite" class="text-blue-600 underline" target="_blank">{{ trainerDetails.gymWebsite }}</a>
        </p>
        <p><strong>자격증 번호:</strong> {{ trainerDetails.certificationNumber }}</p>
        <div v-if="trainerDetails.certificationImage">
          <strong>자격증 이미지:</strong><br />
          <img :src="getProfileImage(trainerDetails.certificationImage)" alt="자격증" class="w-48 rounded mt-1" />
        </div>
        <p><strong>사업자 등록번호:</strong> {{ trainerDetails.businessNumber }}</p>
        <p><strong>자기소개:</strong> {{ trainerDetails.introduction }}</p>
        <p><strong>경력 요약:</strong> {{ trainerDetails.career }}</p>
        <p>
          <strong>인스타그램:</strong>
          <a :href="trainerDetails.instagramUrl" class="text-pink-500 underline" target="_blank">{{ trainerDetails.instagramUrl }}</a>
        </p>
        <p><strong>인증 여부:</strong> {{ trainerDetails.isVerified ? '✅ 인증됨' : '❌ 미인증' }}</p>
      </div>
    </div>

    <!-- 비밀번호 확인 모달 -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white p-6 rounded shadow w-96">
        <h3 class="text-lg font-semibold mb-4">비밀번호 확인</h3>
        <input
          v-model="password"
          type="password"
          placeholder="비밀번호를 입력하세요"
          class="w-full p-2 border rounded mb-4"
        />
        <div class="flex justify-end space-x-2">
          <button @click="closeModal" class="px-3 py-1 border rounded">취소</button>
          <button @click="checkPassword" class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700">확인</button>
        </div>
        <p v-if="errorMessage" class="text-red-500 mt-2">{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Header from '@/components/common/Header.vue'

const auth = useAuthStore()
const router = useRouter()
const user = ref({})
const memberDetails = ref(null)
const trainerDetails = ref(null)
const allergies = ref([])

const showModal = ref(false)
const password = ref('')
const errorMessage = ref('')

const getProfileImage = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchMyInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me', {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
    const data = res.data.data

    user.value = data.user
    memberDetails.value = data.memberDetails || null
    trainerDetails.value = data.trainerDetails || null
    allergies.value = data.allergies || []
  } catch (err) {
    console.error('회원정보 조회 실패', err)
  }
}

const handleLogout = () => {
  localStorage.removeItem('accessToken')
  router.push('/login')
}

const closeModal = () => {
  showModal.value = false
  password.value = ''
  errorMessage.value = ''
}

const checkPassword = async () => {
  try {
    const res = await axios.post('/api/users/check-password', {
      password: password.value
    }, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    })

    if (res.data.success) {
      closeModal()
      router.push('/profile/edit')
    } else {
      errorMessage.value = '비밀번호가 일치하지 않습니다.'
    }
  } catch (err) {
    errorMessage.value = '서버 오류 또는 인증 실패입니다.'
  }
}

onMounted(fetchMyInfo)
</script>
