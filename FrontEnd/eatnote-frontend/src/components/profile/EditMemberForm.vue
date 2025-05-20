<template>
  <div class="space-y-4">
    <!-- 상단에 뒤로가기 버튼 -->
    <div class="flex justify-end mb-4">
      <button @click="goBack" class="text-sm text-gray-600 underline hover:text-black">← 마이페이지로 돌아가기</button>
    </div>

    <!-- 프로필 사진 -->
    <div>
      <label class="font-semibold block mb-2">프로필 사진</label>

      <!-- 기존 사진 보여주기 -->
      <div v-if="!profileDeleted">
        <img :src="previewImageUrl" alt="프로필 이미지" class="w-24 h-24 rounded-full mb-2" />
        <button @click="deleteProfileImage" class="text-sm text-red-600">🗑 삭제하기</button>
      </div>

      <!-- 새로 업로드 -->
      <div v-else>
        <input type="file" accept="image/*" @change="handleImageUpload" />
        <p class="text-xs text-gray-500">* 새 프로필 사진을 선택하세요</p>
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

    <!-- 신체 정보 -->
    <label class="font-semibold block mt-4">신체 정보</label>

    <div>
      <label class="block font-semibold">키(cm)</label>
      <input v-model.number="form.height" placeholder="키(cm)를 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">몸무게(kg)</label>
      <input v-model.number="form.weight" placeholder="몸무게(kg)를 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">체수분(%)</label>
      <input v-model.number="form.bodyWater" placeholder="체수분(%)을 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">단백질량(%)</label>
      <input v-model.number="form.protein" placeholder="단백질량(%)을 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">무기질(%)</label>
      <input v-model.number="form.mineral" placeholder="무기질(%)을 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">체지방(kg)</label>
      <input v-model.number="form.bodyFat" placeholder="체지방(kg)을 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">골격근량(kg)</label>
      <input v-model.number="form.skeletalMuscle" placeholder="골격근량(kg)을 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">체지방량(kg)</label>
      <input v-model.number="form.bodyFatMass" placeholder="체지방량(kg)을 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">BMI</label>
      <input v-model.number="form.bmi" placeholder="BMI를 입력해 주세요." class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">체지방률(%)</label>
      <input v-model.number="form.bodyFatPercentage" placeholder="체지방률(%)을 입력해 주세요." class="input-style" />
    </div>

    <!-- 목표 선택 -->
    <div>
      <label class="block font-semibold">목표</label>
      <select v-model="form.goal" class="input-style">
        <option disabled value="">목표를 선택하세요</option>
        <option v-for="g in goalList" :key="g.code" :value="g.code">
          {{ g.label }}
        </option>
      </select>
    </div>

    <!-- 알레르기 -->
    <label class="font-semibold block mt-4">알레르기 선택</label>

    <!-- 카테고리 선택 -->
    <select v-model="selectedCategory" class="input-style">
      <option disabled value="">카테고리를 선택하세요</option>
      <option v-for="(list, category) in allergyMap" :key="category" :value="category">
        {{ category }}
      </option>
    </select>

    <!-- 알레르기 멀티 선택 (체크박스) -->
    <div v-if="selectedCategory" class="pl-4 mt-2">
      <label class="block font-medium text-gray-600 mb-2">
        {{ selectedCategory }} 알레르기 목록
      </label>
      <div v-for="a in allergyMap[selectedCategory]" :key="a.allergyId" class="mb-1">
        <label>
          <input
            type="checkbox"
            :value="a.allergyId"
            v-model="form.allergyIds"
            class="mr-2"
          />
          {{ a.name }}
        </label>
      </div>
    </div>

    <!-- 제출 -->
    <button @click="submitForm" class="btn-primary mt-4">💾 저장</button>
  </div>
</template>

<script setup>
import { ref, reactive, watchEffect, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const props = defineProps(['userData'])

const form = reactive({
  password: '',
  nickname: '',
  height: 0,
  weight: 0,
  bodyWater: 0,
  protein: 0,
  mineral: 0,
  bodyFat: 0,
  skeletalMuscle: 0,
  bodyFatMass: 0,
  bmi: 0,
  bodyFatPercentage: 0,
  goal: '',
  allergyIds: []
})

// 🔸 알레르기 관련
const allergyMap = ref({})  // { 카테고리: AllergyResponse[] }
const selectedCategory = ref('')

// 🔸 목표 관련
const goalList = ref([])

// 🔸 프로필 관련
const profileDeleted = ref(false)
const file = ref(null)
const previewImageUrl = ref(getImageUrl(props.userData.user?.profileImage))

function getImageUrl(path) {
  return path ? `http://localhost:8080${path}` : '/images/default-profile.png'
}

// 🔸 사용자 데이터 초기 세팅
watchEffect(() => {
  if (props.userData.user && props.userData.memberDetails) {
    const u = props.userData.user
    const m = props.userData.memberDetails

    form.nickname = u.nickname
    form.height = m.height
    form.weight = m.weight
    form.bodyWater = m.bodyWater
    form.protein = m.protein
    form.mineral = m.mineral
    form.bodyFat = m.bodyFat
    form.skeletalMuscle = m.skeletalMuscle
    form.bodyFatMass = m.bodyFatMass
    form.bmi = m.bmi
    form.bodyFatPercentage = m.bodyFatPercentage
    form.goal = m.goal
    form.allergyIds = props.userData.allergies?.map(a => a.allergyId) || []
  }
})

// 🔸 알레르기 목록 불러오기
const loadAllergies = async () => {
  try {
    const res = await axios.get('/api/allergies/grouped')
    allergyMap.value = res.data.data
  } catch (err) {
    console.error('알레르기 목록 조회 실패:', err)
  }
}

// 🔸 목표 목록 불러오기
const loadGoalList = async () => {
  try {
    const res = await axios.get('/api/common/goal')
    goalList.value = res.data.data  // [{ code: '감량', label: '체중 감량' }, ...]
  } catch (err) {
    console.error('목표 목록 조회 실패:', err)
  }
}

// 🔸 프로필 사진 삭제
const deleteProfileImage = () => {
  profileDeleted.value = true
  file.value = null
}

// 🔸 프로필 사진 업로드
const handleImageUpload = (e) => {
  file.value = e.target.files[0]
}

const router = useRouter()

const goBack = () => {
  router.replace('/profile') // 또는 router.back()
}

// 🔸 폼 제출
const submitForm = async () => {
  const formData = new FormData()

  const commonPayload = {
    password: form.password,
    nickname: form.nickname
  }

  const memberPayload = {
    height: form.height,
    weight: form.weight,
    bodyWater: form.bodyWater,
    protein: form.protein,
    mineral: form.mineral,
    bodyFat: form.bodyFat,
    skeletalMuscle: form.skeletalMuscle,
    bodyFatMass: form.bodyFatMass,
    bmi: form.bmi,
    bodyFatPercentage: form.bodyFatPercentage,
    goal: form.goal,
    allergyIds: form.allergyIds
  }

  formData.append('common', JSON.stringify(commonPayload))
  formData.append('member', JSON.stringify(memberPayload))

  if (profileDeleted.value && file.value) {
    formData.append('file', file.value)
  }

  try {
    const res = await axios.put('/api/users/member', formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    alert('수정 성공!')
    router.replace('/profile') // 저장 후 마이페이지로 이동
  } catch (err) {
    console.error('수정 실패', err)
    alert('에러 발생: ' + (err?.response?.data?.message || '서버 오류'))
  }
}

// 🔸 컴포넌트 마운트 시 초기 데이터 불러오기
onMounted(() => {
  loadAllergies()
  loadGoalList()
})
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
