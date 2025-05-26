<template>
  <div class="article-list-container">
    <!-- 게시글 목록 -->
    <div class="articles-grid">
      <article
        v-for="article in articles"
        :key="article.articleId"
        class="article-card"
        @click="$emit('open-detail', article.articleId)"
      >
        <!-- 이미지 영역 -->
        <div class="article-image-container">
          <div v-if="article.imageUrl" class="article-image-wrapper">
            <img
              :src="getImageUrl(article.imageUrl)"
              :alt="article.title"
              class="article-image"
            />
            <div class="image-overlay"></div>
          </div>
          <div v-else class="article-placeholder">
            <span class="placeholder-icon">📄</span>
          </div>
        </div>

        <!-- 콘텐츠 영역 -->
        <div class="article-content">
          <!-- 헤더 -->
          <div class="article-header">
            <div class="article-meta">
              <span class="article-date">{{ formatDate(article.createdAt) }}</span>
              <span class="meta-separator">•</span>
              <span class="article-author">{{ article.userNickname }}</span>
            </div>
            <div class="article-stats">
              <span class="stat-item">
                <svg class="stat-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                </svg>
                {{ article.viewCnt }}
              </span>
            </div>
          </div>

          <!-- 제목 -->
          <h3 class="article-title">{{ article.title }}</h3>

          <!-- 내용 요약 -->
          <p class="article-excerpt">{{ article.content }}</p>

          <!-- 푸터 -->
          <div class="article-footer">
            <div class="footer-left">
              <span class="read-more">자세히 보기</span>
            </div>
            <div class="footer-right">
              <LikeButton
                contentType="ARTICLE"
                :contentId="article.articleId"
                :likeCount="article.likeCount"
                :myReaction="article.myReaction"
                @onUpdated="$emit('refresh')"
                class="like-button-custom"
              />
            </div>
          </div>
        </div>

        <!-- 호버 효과 -->
        <div class="article-hover-effect"></div>
      </article>
    </div>
  </div>
</template>

<script setup>
import LikeButton from '@/components/LikeButton.vue'

const props = defineProps({
  articles: Array
})

defineEmits(['open-detail', 'refresh'])

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}.${month}.${day}`
}

const getImageUrl = (path) => `http://localhost:8080${path}`
</script>

<style scoped>
/* 컨테이너 */
.article-list-container {
  padding: 0;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #6b7280;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #374151;
}

.empty-description {
  font-size: 1rem;
  margin: 0;
}

/* 게시글 그리드 */
.articles-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

/* 게시글 카드 */
.article-card {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(245, 158, 11, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  min-height: 200px;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.2);
}

/* 이미지 영역 */
.article-image-container {
  flex-shrink: 0;
  width: 280px;
  position: relative;
  overflow: hidden;
}

.article-image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.article-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .article-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(245, 158, 11, 0.1) 0%, rgba(217, 119, 6, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.article-card:hover .image-overlay {
  opacity: 1;
}

.article-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-right: 1px solid rgba(229, 231, 235, 0.5);
}

.placeholder-icon {
  font-size: 3rem;
  color: #d1d5db;
  opacity: 0.7;
}

/* 콘텐츠 영역 */
.article-content {
  flex: 1;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 헤더 */
.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #6b7280;
  font-weight: 500;
}

.article-date {
  color: #f59e0b;
  font-weight: 600;
}

.meta-separator {
  color: #d1d5db;
}

.article-author {
  color: #374151;
}

.article-stats {
  display: flex;
  gap: 1rem;
  margin-right: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 1rem;
  color: #9ca3af;
  font-weight: 500;
}

.stat-icon {
  margin-top: 5px;
  width: 18px;
  height: 18px;
}

/* 제목 */
.article-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #111827;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.3s ease;
}

.article-card:hover .article-title {
  color: #f59e0b;
}

/* 내용 요약 */
.article-excerpt {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #6b7280;
  margin: 0;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 푸터 */
.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid rgba(229, 231, 235, 0.3);
}

.footer-left {
  flex: 1;
}

.read-more {
  font-size: 0.85rem;
  color: #f59e0b;
  font-weight: 600;
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
}

.article-card:hover .read-more {
  opacity: 1;
  transform: translateX(0);
}

.footer-right {
  display: flex;
  align-items: center;
}

/* 좋아요 버튼 커스터마이징 */
.like-button-custom {
  transform: scale(0.9);
  transition: transform 0.3s ease;
}

.article-card:hover .like-button-custom {
  transform: scale(1);
}

/* 호버 효과 */
.article-hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.02) 0%, rgba(217, 119, 6, 0.02) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.article-card:hover .article-hover-effect {
  opacity: 1;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .articles-grid {
    gap: 1.25rem;
  }

  .article-image-container {
    width: 240px;
  }

  .article-content {
    padding: 1.25rem;
  }
}

@media (max-width: 768px) {
  .article-card {
    flex-direction: column;
    min-height: auto;
  }

  .article-image-container {
    width: 100%;
    height: 200px;
  }

  .article-content {
    padding: 1rem;
  }

  .article-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .article-stats {
    align-self: flex-end;
  }

  .article-title {
    font-size: 1.1rem;
  }

  .article-excerpt {
    font-size: 0.9rem;
    -webkit-line-clamp: 2;
  }
}

@media (max-width: 640px) {
  .articles-grid {
    gap: 1rem;
  }

  .article-content {
    padding: 0.75rem;
    gap: 0.75rem;
  }

  .article-title {
    font-size: 1rem;
  }

  .article-meta {
    font-size: 0.8rem;
  }

  .article-excerpt {
    font-size: 0.85rem;
  }

  .empty-state {
    padding: 3rem 1rem;
  }

  .empty-icon {
    font-size: 3rem;
  }

  .empty-title {
    font-size: 1.25rem;
  }
}

/* 애니메이션 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.article-card {
  animation: fadeInUp 0.5s ease-out;
}

.article-card:nth-child(even) {
  animation-delay: 0.1s;
}

/* 접근성 */
.article-card:focus {
  outline: 2px solid #f59e0b;
  outline-offset: 2px;
}

/* 로딩 상태 (선택사항) */
.loading-skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>