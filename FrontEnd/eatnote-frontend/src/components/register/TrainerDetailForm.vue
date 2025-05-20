<template>
  <div class="detail-form">
    <label>📞 전화번호</label>
    <input v-model="model.phone" type="text" />

    <label>🏠 주소</label>
    <input v-model="model.address" type="text" />

    <label>🏋️‍♂️ 헬스장 이름</label>
    <input v-model="model.gymName" type="text" />

    <label>🌐 헬스장 홈페이지</label>
    <input v-model="model.gymWebsite" type="url" />

    <label>📜 자격증 번호</label>
    <input v-model="model.certificationNumber" type="text" />

    <label>📑 사업자 등록번호</label>
    <input v-model="model.businessNumber" type="text" />

    <label>👨‍🏫 소개</label>
    <textarea v-model="model.introduction" rows="2"></textarea>

    <label>📚 경력</label>
    <textarea v-model="model.career" rows="2"></textarea>

    <label>📸 인스타그램</label>
    <input v-model="model.instagramUrl" type="url" />
  </div>
</template>

<script setup>
const model = defineModel()
</script>

<style scoped>
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
</style>
