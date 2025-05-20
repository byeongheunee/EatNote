<template>
  <div class="bg-white p-4 rounded-xl shadow hover:shadow-md transition duration-200">
    <div class="text-green-600 font-semibold text-lg mb-2">
      {{ feature.title }}
    </div>
    <div class="text-gray-600 text-sm leading-snug">
      {{ feature.description }}
    </div>
  </div>
</template>

<script setup>
defineProps({
  feature: {
    type: Object,
    required: true,
    // 예: { title: '간편한 식단 등록', description: '사진만 업로드하면 AI가 자동 분석하여 등록됩니다.' }
  }
})
</script>
