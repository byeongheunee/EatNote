<template>
  <div class="detail-form">
    <label>📏 키 (cm)</label>
    <input v-model.number="model.height" type="number" />

    <label>⚖️ 몸무게 (kg)</label>
    <input v-model.number="model.weight" type="number" />

    <label>💧 체수분</label>
    <input v-model.number="model.bodyWater" type="number" />

    <label>🥩 단백질</label>
    <input v-model.number="model.protein" type="number" />

    <label>⚙️ 무기질</label>
    <input v-model.number="model.mineral" type="number" />

    <label>🔥 체지방</label>
    <input v-model.number="model.bodyFat" type="number" />

    <label>💪 골격근량</label>
    <input v-model.number="model.skeletalMuscle" type="number" />

    <label>🧈 체지방량</label>
    <input v-model.number="model.bodyFatMass" type="number" />

    <label>📊 BMI</label>
    <input v-model.number="model.bmi" type="number" />

    <label>📉 체지방률 (%)</label>
    <input v-model.number="model.bodyFatPercentage" type="number" />

    <label>🎯 목표</label>
    <select v-model="model.goal">
      <option value="">선택</option>
      <option value="감량">감량</option>
      <option value="증량">증량</option>
      <option value="유지">유지</option>
    </select>

    <label>📛 담당 트레이너 닉네임</label>
    <input v-model="model.trainerNickname" type="text" />

    <label>🌰 알레르기 ID 목록 (쉼표로 구분)</label>
    <input
      v-model="allergyInput"
      @change="updateAllergies"
      placeholder="예: 10,11,12"
    />
  </div>
</template>

<script setup>
import { watch, ref } from 'vue'

const model = defineModel()
const allergyInput = ref('')

const updateAllergies = () => {
  model.value.allergyIds = allergyInput.value
    .split(',')
    .map((id) => parseInt(id.trim()))
    .filter((id) => !isNaN(id))
}

watch(
  () => model.value.allergyIds,
  (newVal) => {
    allergyInput.value = newVal.join(',')
  },
  { immediate: true }
)
</script>

<style scoped>
.detail-form label {
  display: block;
  margin-top: 16px;
  font-weight: bold;
}
.detail-form input,
.detail-form select {
  width: 100%;
  padding: 8px;
  margin-top: 4px;
  border: 1px solid #ccc;
}
</style>
