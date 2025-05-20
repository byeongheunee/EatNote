<template>
  <div class="space-y-4">
    <div
      v-for="article in articles"
      :key="article.articleId"
      class="cursor-pointer p-4 border rounded mb-2 hover:bg-gray-50"
      @click="$emit('open-detail', article.articleId)"
    >
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">{{ article.title }}</h3>
        <span class="text-sm text-gray-400">{{ formatDate(article.createdAt) }}</span>
      </div>
      <p class="text-gray-600 mt-1 line-clamp-2">{{ article.content }}</p>
      <div class="text-sm text-gray-500 mt-2">
        {{ article.userNickname }} · 조회수 {{ article.viewCnt }} · 👍 {{ article.likeCount }} · 👎 {{ article.dislikeCount }}
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ articles: Array })
defineEmits(['open-detail'])

const formatDate = (date) => new Date(date).toLocaleString()
</script>
