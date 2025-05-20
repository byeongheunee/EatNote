<template>
  <div class="max-w-2xl mx-auto p-6">
    <h2 class="text-2xl font-bold mb-6">회원정보 수정</h2>
    <component :is="editComponent" :userData="userData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import EditMemberForm from '@/components/profile/EditMemberForm.vue'
import EditTrainerForm from '@/components/profile/EditTrainerForm.vue'
import axios from 'axios'

const userData = ref({})
const editComponent = ref('')

const fetchMyInfo = async () => {
  const res = await axios.get('/api/auth/me', {
    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
  })
  userData.value = res.data.data

  // ✅ userType은 localStorage에서 가져옴
  const localUser = JSON.parse(localStorage.getItem('user'))
  const userType = localUser?.userType
  editComponent.value = userType === 1 ? EditTrainerForm : EditMemberForm
}

onMounted(fetchMyInfo)
</script>