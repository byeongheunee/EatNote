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

        <MyFollowList v-if="user.userType === 2" ref="memberFollowListRef" @open-profile="openProfileModal" />

        <!-- 최근 게시글 -->
        <h3 class="text-xl font-semibold mt-6 mb-2">📚 내가 작성한 최근 게시글</h3>
        <ArticleList :articles="myArticles.slice(0, 10)" @open-detail="goToDetail" />
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
        
        <MyFollowList v-if="user.userType === 1" ref="trainerFollowListRef" @open-profile="openProfileModal" />

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

    <!-- 프로필 모달 -->
    <UserProfileModal
      :visible="profileModalVisible"
      :profile="selectedProfile"
      @close="profileModalVisible = false"
      @follow-requested="handleFollowRequested"
    />

  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import ArticleList from '@/components/ArticleList.vue'
import MyFollowList from '@/components/common/MyFollowList.vue'
import UserProfileModal from '@/components/UserProfileModal.vue'


const myArticles = ref([])
const auth = useAuthStore()
const router = useRouter()
const user = ref({})
const memberDetails = ref(null)
const trainerDetails = ref(null)
const allergies = ref([])

const showModal = ref(false)
const password = ref('')
const errorMessage = ref('')

const memberFollowListRef = ref(null)
const trainerFollowListRef = ref(null)

// 프로필 모달 제어 관련
const profileModalVisible = ref(false)
const selectedProfile = ref(null)

const handleFollowRequested = async () => {
  if (selectedProfile.value?.followStatus === 'ACCEPTED') {
    selectedProfile.value.followStatus = 'NONE'
  } else {
    selectedProfile.value.followStatus = 'PENDING'
  }

  // 조건 분기해서 정확한 컴포넌트의 refresh() 호출
  if (user.value.userType === 1 && trainerFollowListRef.value?.refresh) {
    await trainerFollowListRef.value.refresh()
  } else if (user.value.userType === 2 && memberFollowListRef.value?.refresh) {
    await memberFollowListRef.value.refresh()
  }

  // 모달 닫기
  profileModalVisible.value = false
}

const openProfileModal = async (otherUser) => {
  try {
    const token = auth.accessToken

    const res = await axios.get(`/api/users/${otherUser.userId}/profile`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    if (!res.data.success) {
      const code = res.data.code

      if (code === 'FORBIDDEN_ADMIN_PROFILE') {
        alert('관리자는 프로필을 조회할 수 없습니다.')
      } else if (code === 'USER_NOT_FOUND') {
        alert('해당 사용자가 존재하지 않습니다.')
      } else if (code === 'VALIDATION_FAILED') {
        alert('알 수 없는 사용자 유형입니다.')
      } else {
        alert(res.data.message || '알 수 없는 오류가 발생했습니다.')
      }

      return
    }
    console.log(res.data.data)
    selectedProfile.value = res.data.data
    profileModalVisible.value = true

  } catch (e) {
    console.error('상대방 프로필 조회 실패:', e)
    alert('프로필 정보를 불러오지 못했습니다.')
  }
}

const fetchMyArticles = async () => {
  try {
    const res = await axios.get('/api/articles', {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      },
      params: {
        keyword: user.value.nickname,
        searchField: 'NICKNAME',
        sort: 'createdAt'
      }
    })
    myArticles.value = res.data.data
  } catch (err) {
    console.error('내 게시글 조회 실패', err)
  }
}

// 게시글 클릭 시 상세 페이지 이동
const goToDetail = (articleId) => {
  // 해당 게시글을 boardId까지 포함해서 찾아야 하므로,
  const article = myArticles.value.find(a => a.articleId === articleId)
  if (!article) return

  router.push({
    name: 'ArticleDetail',
    params: {
      boardId: article.boardId,
      articleId: article.articleId
    }
  })
}

const getProfileImage = (path) => path ? `http://localhost:8080${path}` : '/images/default-profile.png'

const fetchMyInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me', {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })
    const data = res.data.data
    console.log(data)
    user.value = data.user
    memberDetails.value = data.memberDetails || null
    trainerDetails.value = data.trainerDetails || null
    allergies.value = data.allergies || []

    // auth store의 사용자 정보도 같이 업데이트
    auth.setUser(data.user)
  } catch (err) {
    console.error('회원정보 조회 실패', err)
  }
}

// const handleLogout = () => {
//   localStorage.removeItem('accessToken')
//   router.push('/login')
// }

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

onMounted(async () => {
  await fetchMyInfo()
  await fetchMyArticles()
})
</script>
