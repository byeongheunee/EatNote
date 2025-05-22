<template>
  <div class="space-y-4">
    <!-- 신체 정보 -->
    <div>
      <label class="block font-semibold">📏 키 (cm) <span class="text-red-500">*필수</span></label>
      <input v-model.number="model.height" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">⚖️ 몸무게 (kg) <span class="text-red-500">*필수</span></label>
      <input v-model.number="model.weight" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">💧 체수분 (%)</label>
      <input v-model.number="model.bodyWater" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">🥩 단백질량 (%)</label>
      <input v-model.number="model.protein" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">⚙️ 무기질 (%)</label>
      <input v-model.number="model.mineral" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">🔥 체지방 (kg)</label>
      <input v-model.number="model.bodyFat" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">💪 골격근량 (kg)</label>
      <input v-model.number="model.skeletalMuscle" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">🧈 체지방량 (kg)</label>
      <input v-model.number="model.bodyFatMass" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">📊 BMI</label>
      <input v-model.number="model.bmi" type="number" class="input-style" />
    </div>

    <div>
      <label class="block font-semibold">📉 체지방률 (%)</label>
      <input v-model.number="model.bodyFatPercentage" type="number" class="input-style" />
    </div>

    <!-- 목표 -->
    <div>
      <label class="block font-semibold">🎯 목표 <span class="text-red-500">*필수</span></label>
      <select v-model="model.goal" class="input-style">
        <option disabled value="">목표를 선택하세요</option>
        <option v-for="g in goalList" :key="g.code" :value="g.code">
          {{ g.label }}
        </option>
      </select>
    </div>

    <!-- 담당 트레이너 -->
    <div>
      <label class="block font-semibold">📛 담당 트레이너 닉네임</label>
      <div class="flex gap-2 items-center">
        <input
          v-model="model.trainerNickname"
          type="text"
          class="input-style flex-1"
          placeholder="트레이너 닉네임 입력"
        />
        <button @click="checkTrainer" class="px-3 py-2 bg-blue-500 text-white rounded-md text-sm">
          확인
        </button>
      </div>

      <div v-if="model.trainerNickname" class="text-sm mt-1">
        <span v-if="trainerExists === true" class="text-green-600">✅ 트레이너가 존재합니다.</span>
        <span v-else-if="trainerExists === false" class="text-red-600">❌ 존재하지 않는 트레이너입니다.</span>
      </div>
    </div>

    <!-- 알레르기 선택 -->
    <div>
      <label class="block font-semibold">🌰 알레르기 선택</label>

      <!-- 카테고리 드롭다운 -->
      <select v-model="selectedCategory" class="input-style">
        <option disabled value="">카테고리를 선택하세요</option>
        <option v-for="(list, category) in allergyMap" :key="category" :value="category">
          {{ category }}
        </option>
      </select>

      <!-- 체크박스 목록 -->
      <div v-if="selectedCategory" class="pl-2 mt-2">
        <div v-for="a in allergyMap[selectedCategory]" :key="a.allergyId" class="mb-1">
          <label>
            <input
              type="checkbox"
              :value="a.allergyId"
              v-model="model.allergyIds"
              class="mr-2"
            />
            {{ a.name }}
          </label>
        </div>
      </div>

      <!-- 선택된 알레르기 출력 -->
      <div class="mt-4">
        <label class="block font-semibold">✅ 선택된 알레르기</label>
        <div v-if="selectedAllergyNames.length" class="flex flex-wrap gap-2 mt-2">
          <span
            v-for="(name, index) in selectedAllergyNames"
            :key="index"
            class="px-3 py-1 bg-pink-100 text-pink-800 rounded-full text-sm"
          >
            {{ name }}
          </span>
        </div>
        <div v-else class="text-sm text-gray-500 mt-1">선택된 알레르기가 없습니다.</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
const model = defineModel()

const trainerExists = ref(null)

const checkTrainer = async () => {
  if (!model.value.trainerNickname) {
    trainerExists.value = null
    return
  }

  try {
    const res = await fetch(`/api/users/check-trainer-nickname?nickname=${encodeURIComponent(model.value.trainerNickname)}`)
    const data = await res.json()
    trainerExists.value = data.data
  } catch (e) {
    console.error('트레이너 닉네임 확인 실패:', e)
    trainerExists.value = false
  }
}

// 알레르기 관련
const allergyMap = ref({})
const selectedCategory = ref('')

// 목표 관련
const goalList = ref([])

// 선택된 알레르기 이름 리스트
const selectedAllergyNames = computed(() => {
  const result = []
  for (const category in allergyMap.value) {
    for (const a of allergyMap.value[category]) {
      if (model.value.allergyIds?.includes(a.allergyId)) {
        result.push(a.name)
      }
    }
  }
  return result
})

// API 호출
const loadAllergies = async () => {
  try {
    const res = await fetch('/api/allergies/grouped')
    const data = await res.json()
    allergyMap.value = data.data
  } catch (e) {
    console.error('알레르기 목록 로딩 실패:', e)
  }
}

const loadGoals = async () => {
  try {
    const res = await fetch('/api/common/goal')
    const data = await res.json()
    goalList.value = data.data
  } catch (e) {
    console.error('목표 목록 로딩 실패:', e)
  }
}

onMounted(() => {
  model.value.allergyIds = model.value.allergyIds || []
  loadAllergies()
  loadGoals()
})
</script>

<style scoped>
.input-style {
  display: block;
  width: 100%;
  padding: 8px;
  margin-top: 4px;
  border: 1px solid #ccc;
  border-radius: 6px;
}
</style>
