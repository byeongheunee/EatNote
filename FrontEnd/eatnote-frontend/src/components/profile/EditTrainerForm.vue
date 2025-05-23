<template>
  <div class="space-y-6">
    <!-- 🔙 뒤로가기 -->
    <div class="flex justify-end mb-4">
      <button @click="goBack" class="text-sm text-gray-600 underline hover:text-black">
        ← 마이페이지로 돌아가기
      </button>
    </div>

    <!-- 프로필 사진 -->
    <div>
      <label class="block font-semibold mb-2">프로필 사진</label>
      <div v-if="!profileDeleted">
        <img :src="getImageUrl(user.profileImage)" alt="프로필 이미지" class="w-24 h-24 rounded-full mb-2" />
        <button @click="deleteProfileImage" class="text-sm text-red-600">🗑 삭제하기</button>
      </div>
      <div v-else>
        <input type="file" accept="image/*" @change="handleProfileImageUpload" />
        <p class="text-xs text-gray-500">* 새 프로필 사진을 선택하세요</p>
      </div>
    </div>

    <!-- 자격증 이미지 -->
    <div>
      <label class="block font-semibold mb-2">자격증 이미지</label>
      <div v-if="!certDeleted">
        <img :src="getImageUrl(trainer.certificationImage)" alt="자격증 이미지" class="w-48 rounded mb-2" />
        <button @click="deleteCertImage" class="text-sm text-red-600">🗑 삭제하기</button>
      </div>
      <div v-else>
        <input type="file" accept="image/*" @change="handleCertUpload" />
        <p class="text-xs text-gray-500">* 새 자격증 이미지를 선택하세요</p>
      </div>
    </div>

    <!-- 공통 정보 -->
    <div>
      <label class="block font-semibold">닉네임</label>
      <input v-model="form.nickname" placeholder="닉네임" class="input-style" />
    </div>
    <div>
      <label class="block font-semibold">비밀번호</label>
      <input v-model="form.password" type="password" placeholder="새 비밀번호 (선택)" class="input-style" />
    </div>

    <!-- 트레이너 상세 정보 -->
    <div v-for="(label, key) in trainerLabels" :key="key">
      <label class="block font-semibold">{{ label }}</label>
      <input v-model="form[key]" :placeholder="label + '를 입력해주세요.'" class="input-style" />
    </div>

    <!-- 저장 버튼 -->
    <button @click="submitForm" class="btn-primary">💾 저장</button>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'

const toast = useToast()
const props = defineProps(['userData'])
const router = useRouter()

const user = props.userData.user
const trainer = props.userData.trainerDetails

const goBack = () => {
  router.replace('/profile')
}

const form = reactive({
  password: '',
  nickname: user.nickname,
  phone: trainer.phone,
  address: trainer.address,
  gymName: trainer.gymName,
  gymWebsite: trainer.gymWebsite,
  certificationNumber: trainer.certificationNumber,
  businessNumber: trainer.businessNumber,
  introduction: trainer.introduction,
  career: trainer.career,
  instagramUrl: trainer.instagramUrl
})

const profileDeleted = ref(false)
const certDeleted = ref(false)
const profileFile = ref(null)
const certFile = ref(null)

const getImageUrl = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const deleteProfileImage = () => {
  profileDeleted.value = true
  profileFile.value = null
}
const handleProfileImageUpload = (e) => {
  profileFile.value = e.target.files[0]
}

const deleteCertImage = () => {
  certDeleted.value = true
  certFile.value = null
}
const handleCertUpload = (e) => {
  certFile.value = e.target.files[0]
}

const submitForm = async () => {
  const formData = new FormData()

  const commonPayload = {
    password: form.password,
    nickname: form.nickname
  }
  const trainerPayload = {
    phone: form.phone,
    address: form.address,
    gymName: form.gymName,
    gymWebsite: form.gymWebsite,
    certificationNumber: form.certificationNumber,
    businessNumber: form.businessNumber,
    introduction: form.introduction,
    career: form.career,
    instagramUrl: form.instagramUrl
  }

  formData.append('common', JSON.stringify(commonPayload))
  formData.append('trainer', JSON.stringify(trainerPayload))
  if (profileDeleted.value && profileFile.value) formData.append('file', profileFile.value)
  if (certDeleted.value && certFile.value) formData.append('certFile', certFile.value)

  try {
    await axios.put('/api/users/trainer', formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    // alert('수정 성공!')
    toast.success('트레이너 정보가 성공적으로 수정되었습니다!')
    router.replace('/profile')
  } catch (err) {
    console.error('수정 실패', err)
    // alert('에러 발생: ' + (err?.response?.data?.message || '서버 오류'))
    toast.error('수정 중 오류가 발생했습니다: ' + (err?.response?.data?.message || '서버 오류'))
  }
}

const trainerLabels = {
  phone: '연락처',
  address: '주소',
  gymName: '소속 헬스장',
  gymWebsite: '헬스장 홈페이지',
  certificationNumber: '자격증 번호',
  businessNumber: '사업자 등록번호',
  introduction: '자기소개',
  career: '경력 요약',
  instagramUrl: '인스타그램 URL'
}
</script>

<style scoped>
.input-style {
  display: block;
  margin-bottom: 12px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 100%;
}
.btn-primary {
  padding: 10px 16px;
  background-color: #22c55e;
  color: white;
  border-radius: 8px;
}
</style>
