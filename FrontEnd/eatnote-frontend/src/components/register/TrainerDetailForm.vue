<template>
  <div class="space-y-5">
    <div>
      <label class="block text-sm font-medium text-gray-700">📞 전화번호</label>
      <input v-model="model.phone" type="text" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">🏠 주소</label>
      <input v-model="model.address" type="text" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">🏋️‍♂️ 헬스장 이름</label>
      <input v-model="model.gymName" type="text" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">🌐 헬스장 홈페이지</label>
      <input v-model="model.gymWebsite" type="url" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">📜 자격증 번호</label>
      <input v-model="model.certificationNumber" type="text" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">📎 자격증 사진 업로드</label>
      <input type="file" @change="handleCertFileChange" class="mt-1 w-full" />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">👨‍🏫 소개</label>
      <textarea v-model="model.introduction" rows="2" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"></textarea>
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">📚 경력</label>
      <textarea v-model="model.career" rows="2" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"></textarea>
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">📸 인스타그램</label>
      <input v-model="model.instagramUrl" type="url" class="mt-1 w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400" />
    </div>

    <div class="flex justify-between mt-6">
      <button type="button" @click="$emit('goBack')" class="bg-gray-300 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-400">뒤로가기</button>
    </div>
  </div>
</template>

<script setup>
const model = defineModel()

const handleCertFileChange = (e) => {
  const file = e.target.files[0]
  model.certificationFile = file
}
</script>

<!-- <style scoped>
.detail-form label {
  display: block;
  margin-top: 16px;
  font-weight: bold;
}
.detail-form input,
.detail-form textarea {
  width: 100%;
  padding: 8px;
  margin-top: 4px;
  border: 1px solid #ccc;
}
</style> -->
