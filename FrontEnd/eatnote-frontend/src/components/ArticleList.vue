<template>
  <div class="space-y-4">
    <div
      v-for="article in articles"
      :key="article.articleId"
      class="p-4 border rounded mb-2 hover:bg-gray-50"
    >
      <div class="flex gap-4">
        <!-- 대표 이미지 -->
        <div v-if="article.imageUrl" class="flex-shrink-0">
          <img
            :src="getImageUrl(article.imageUrl)"
            alt="대표 이미지"
            class="w-48 h-48 object-cover rounded"
          />
        </div>

        <!-- 본문 영역 -->
        <div class="flex flex-col justify-between flex-grow">
          <div @click.stop="$emit('open-detail', article.articleId)" class="cursor-pointer">
            <!-- 제목 및 날짜 -->
            <div class="flex justify-between items-center">
              <h3 class="text-lg font-bold">{{ article.title }}</h3>
              <span class="text-sm text-gray-400">{{ formatDate(article.createdAt) }}</span>
            </div>

            <!-- 내용 요약 -->
            <p class="text-gray-600 mt-1 line-clamp-2">{{ article.content }}</p>
          </div>

          <!-- 작성자 및 좋아요 -->
          <div class="flex justify-between items-center mt-4 text-sm text-gray-500">
            <span>{{ article.userNickname }}</span>
            <LikeButton
              contentType="ARTICLE"
              :contentId="article.articleId"
              :likeCount="article.likeCount"
              :myReaction="article.myReaction"
              @onUpdated="$emit('refresh')"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import LikeButton from '@/components/LikeButton.vue'

defineProps({ articles: Array })
defineEmits(['open-detail', 'refresh'])

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`
}

const getImageUrl = (path) => `http://localhost:8080${path}`
</script>
