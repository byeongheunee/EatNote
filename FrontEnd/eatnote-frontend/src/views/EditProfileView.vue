<template>
  <div class="max-w-2xl mx-auto p-6">
    <h2 class="text-2xl font-bold mb-6">회원정보 수정</h2>
    <component :is="editComponent" :userData="userData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

import EditMemberForm from '@/components/profile/EditMemberForm.vue'
import EditTrainerForm from '@/components/profile/EditTrainerForm.vue'

// 스토어에서 사용자 정보 가져오기
const auth = useAuthStore()
const userType = auth.user.userType

const userData = ref({})
const editComponent = ref('')

// Axios interceptor는 글로벌 설정에 적용돼 있어야 함!
const fetchMyInfo = async () => {
  try {
    const res = await axios.get('/api/auth/me')
    userData.value = res.data.data

    // 스토어에 있는 userType으로 분기
    editComponent.value = userType === 1 ? EditTrainerForm : EditMemberForm
  } catch (err) {
    console.error('회원정보 조회 실패:', err)
  }
}

onMounted(fetchMyInfo)
</script>
