<template>
  <div class="article-detail-container">
    <!-- 게시글 메인 컨텐츠 -->
    <div v-if="article" class="article-content">
      <!-- 게시글 헤더 -->
      <header class="article-header">
        <div class="header-main">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <div class="meta-left">
              <div class="author-info" @click="openAuthorProfile">
                <div class="author-avatar">
                  <span class="avatar-icon">👤</span>
                </div>
                <div class="author-details">
                  <span class="author-name">{{ article.userNickname }}</span>
                  <span v-if="article && !isMyArticle && auth.user?.userType === 2" class="follow-status">
                    {{ followStatusLabel }}
                  </span>
                </div>
              </div>
            </div>
            <div class="meta-right">
              <span class="publish-date">{{ formatDate(article.createdAt) }}</span>
            </div>
          </div>
        </div>
      </header>

      <!-- 게시글 본문 -->
      <main class="article-body">
        <!-- 메인 이미지 -->
        <div v-if="article.imageUrl" class="main-image-container">
          <img
            :src="getImageUrl(article.imageUrl)"
            :alt="article.title"
            class="main-image"
          />
        </div>

        <!-- 본문 내용 -->
        <div class="content-text">
          {{ article.content }}
        </div>

        <!-- 추가 첨부 이미지 -->
        <div v-if="additionalImages.length" class="additional-images">
          <h4 class="images-title">첨부 이미지</h4>
          <div class="images-grid">
            <div
              v-for="(img, index) in additionalImages"
              :key="index"
              class="image-item"
            >
              <img
                :src="getImageUrl(img.filePath)"
                :alt="img.originalName"
                class="attachment-image"
              />
            </div>
          </div>
        </div>

        <!-- 수정/삭제 버튼 -->
        <div v-if="isMyArticle" class="action-buttons-bottom">
          <button @click="goToEdit" class="action-btn edit-btn">
            <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
            </svg>
            수정
          </button>
          <button @click="deleteArticle" class="action-btn delete-btn">
            <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
            </svg>
            삭제
          </button>
        </div>
      </main>

      <!-- 게시글 푸터 (통계 및 반응) -->
      <footer class="article-footer">
        <div class="stats-container">
          <div class="view-count">
            <svg class="stat-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
            </svg>
            <span>조회 {{ article.viewCnt }}</span>
          </div>
          <div class="reaction-buttons">
            <LikeDislikeButtons
              contentType="ARTICLE"
              :contentId="articleId"
              :likeCount="article.likeCount"
              :dislikeCount="article.dislikeCount"
              :myReaction="article.myReaction"
              :onUpdated="fetchArticle"
            />
          </div>
        </div>
      </footer>
    </div>

    <!-- 댓글 섹션 -->
    <section class="comments-section">
      <div class="comments-header">
        <h3 class="comments-title">
          <svg class="title-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-3.582 8-8 8a9.863 9.863 0 01-4.906-1.294l-3.181.795.795-3.181A9.863 9.863 0 013 12c0-4.418 3.582-8 8-8s8 3.582 8 8z"></path>
          </svg>
          댓글 {{ comments.length }}개
        </h3>
      </div>

      <!-- 댓글 입력 -->
      <div class="comment-input-container">
        <CommentInput
          :parentCommentId="null"
          :onSubmit="loadComments"
          targetType="ARTICLE"
          :targetId="articleId"
        />
      </div>

      <!-- 댓글 목록 -->
      <div class="comments-list">
        <div v-if="comments.length" class="comments-items">
          <CommentItem
            v-for="comment in comments"
            :key="comment.commentId"
            :comment="comment"
            :onReload="loadComments"
            targetType="ARTICLE"
            :targetId="articleId"
          />
        </div>
        <div v-else class="no-comments">
          <div class="no-comments-icon">💬</div>
          <p class="no-comments-text">아직 댓글이 없습니다</p>
          <p class="no-comments-subtext">첫 번째 댓글을 작성해보세요!</p>
        </div>
      </div>
    </section>

    <!-- 프로필 모달 -->
    <UserProfileModal
      :visible="isUserProfileOpen"
      :profile="selectedProfile"
      @close="isUserProfileOpen = false"
      @follow-requested="handleFollowRequested"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import CommentItem from '@/components/CommentItem.vue'
import CommentInput from '@/components/CommentInput.vue'
import LikeDislikeButtons from '@/components/LikeDislikeButtons.vue'
import UserProfileModal from '@/components/UserProfileModal.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()

// 📌 라우터에서 articleId와 boardId 추출
const route = useRoute()
const router = useRouter()

const articleId = Number(route.params.articleId)
const boardId = Number(route.params.boardId)

const article = ref(null)
const additionalImages = ref([])
const comments = ref([])

const auth = useAuthStore()
const currentUser = computed(() => auth.user)

const isUserProfileOpen = ref(false)
const selectedProfile = ref(null)

const handleFollowRequested = () => {
  // 팔로우 요청 → 'PENDING' 으로
  // 팔로우 취소 → 'NONE' 으로
  if (selectedProfile.value?.followStatus === 'ACCEPTED') {
    selectedProfile.value.followStatus = 'NONE'
  } else {
    selectedProfile.value.followStatus = 'PENDING'
  }

  // 💡 article도 같은 사용자면 동기화
  if (article.value?.userId === selectedProfile.value?.userId) {
    article.value.followStatus = selectedProfile.value.followStatus
  }
}

// 작성자 프로필 열기
const openAuthorProfile = async () => {
  console.log('openAuthorProfile 호출됨')
  console.log('isMyArticle:', isMyArticle.value)
  console.log('article.userId:', article.value?.userId)
  console.log('currentUser.userId:', currentUser.value?.userId)

  // 내가 작성한 글이면 모달 열지 않음
  if (isMyArticle.value) {
    console.log('내가 작성한 글이므로 모달을 열지 않습니다')
    return
  }

  try {
    const token = auth.accessToken
    console.log('API 호출 시작, token:', token ? '있음' : '없음')

    const res = await axios.get(`/api/users/${article.value.userId}/profile`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    console.log('API 응답:', res.data)

    // 응답이 실패한 경우
    if (!res.data.success) {
      const code = res.data.code

      if (code === 'FORBIDDEN_ADMIN_PROFILE') {
        toast.warning('관리자는 프로필을 조회할 수 없습니다. ⚠️')
      } else if (code === 'USER_NOT_FOUND') {
        toast.warning('해당 사용자가 존재하지 않습니다.')
      } else if (code === 'VALIDATION_FAILED') {
        toast.error('알 수 없는 사용자 유형입니다. ⚠️')
      } else {
        toast.error(res.data.message || '알 수 없는 오류가 발생했습니다. 🚨')
      }

      return
    }

    // 정상 응답 처리
    const profile = res.data.data
    console.log('프로필 데이터:', profile)
    selectedProfile.value = profile
    isUserProfileOpen.value = true
    console.log('모달 열림 상태:', isUserProfileOpen.value)

  } catch (e) {
    console.error('작성자 프로필 조회 실패:', e)
    toast.error('작성자 정보를 불러오지 못했습니다.')
  }
}

const followStatusLabel = computed(() => {
  switch (article.value?.followStatus) {
    case 'ACCEPTED': return '팔로우 중';
    case 'PENDING': return '팔로우 요청 중';
    case 'REJECTED': return '팔로우 거절됨';
    default: return '팔로우 안 함';
  }
});

// 권한 확인
const isMyArticle = computed(() => {
  return article.value?.userId === currentUser.value?.userId
})

// 게시글 삭제
const deleteArticle = async () => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    const token = localStorage.getItem('accessToken')
    await axios.delete(`/api/articles/${articleId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    toast.success('게시글이 성공적으로 삭제되었습니다! 🗑️')
    router.push(`/community/${boardId}`)
  } catch (e) {
    console.error('게시글 삭제 실패:', e)
    toast.error('게시글 삭제 중 오류가 발생했습니다.')
  }
}

// 수정 페이지로 이동
const goToEdit = () => {
  router.push(`/articles/${articleId}/edit`)
}

// 게시글 조회
const fetchArticle = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get(`/api/articles/${articleId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    article.value = res.data.data
    console.log(article.value)
    additionalImages.value = article.value.attachments?.slice(1) || []
  } catch (err) {
    console.error('게시글 조회 실패:', err)
  }
}

// 댓글 조회
const loadComments = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    const res = await axios.get('/api/comments', {
      params: { targetType: 'ARTICLE', targetId: articleId },
      headers: { Authorization: `Bearer ${token}` }
    })
    comments.value = res.data.data
  } catch (e) {
    console.error('댓글 불러오기 실패:', e)
  }
}

// 조회수 증가
const increaseViewCount = async () => {
  try {
    const token = localStorage.getItem('accessToken')
    await axios.patch(`/api/articles/${articleId}/view-count`, null, {
      headers: { Authorization: `Bearer ${token}` }
    })
  } catch (e) {
    console.error('조회수 증가 실패:', e)
  }
}

// 날짜 포맷
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}.${month}.${day} ${hours}:${minutes}`
}

const getImageUrl = (path) => `http://localhost:8080${path}`

watch(() => articleId, async () => {
  await fetchArticle()
  await loadComments()
})

onMounted(async () => {
  await increaseViewCount()
  await fetchArticle()
  await loadComments()
})
</script>

<style scoped>
/* 컨테이너 */
.article-detail-container {
  background: transparent;
  border-radius: 0;
  overflow: visible;
}

/* 게시글 컨텐츠 */
.article-content {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 2.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(245, 158, 11, 0.1);
}

/* 게시글 헤더 */
.article-header {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid rgba(245, 158, 11, 0.1);
}

.header-main {
  flex: 1;
}

.article-title {
  font-size: 2rem;
  font-weight: 800;
  color: #111827;
  line-height: 1.3;
  margin-bottom: 1.5rem;
  background: linear-gradient(135deg, #374151, #111827);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 12px;
  transition: all 0.3s ease;
  user-select: none;
  pointer-events: auto;
}

.author-info:hover {
  background: rgba(245, 158, 11, 0.1);
  transform: translateY(-1px);
}

.author-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.avatar-icon {
  font-size: 1.2rem;
  color: white;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.author-name {
  font-weight: 600;
  color: #374151;
  font-size: 0.95rem;
}

.follow-status {
  font-size: 0.8rem;
  color: #f59e0b;
  font-weight: 500;
}

.publish-date {
  color: #6b7280;
  font-size: 0.9rem;
  font-weight: 500;
}

/* 하단 액션 버튼 (첨부 이미지 밑에 위치) */
.action-buttons-bottom {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(245, 158, 11, 0.1);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
}

.btn-icon {
  width: 16px;
  height: 16px;
}

.edit-btn {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.edit-btn:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.4);
}

.delete-btn {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.delete-btn:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(239, 68, 68, 0.4);
}

/* 게시글 본문 */
.article-body {
  margin-bottom: 2rem;
}

.main-image-container {
  margin-bottom: 2rem;
}

.main-image {
  width: 100%;
  max-height: 500px;
  object-fit: contain; /* ✅ 변경 */
  border-radius: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  background-color: #f9fafb; /* 빈 여백이 생길 수 있으므로 배경색도 추가 추천 */
}

.content-text {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #374151;
  white-space: pre-line;
  margin-bottom: 2rem;
}

/* 추가 이미지 */
.additional-images {
  width: 100%;
  height: 200px;
  object-fit: contain; /* ✅ cover → contain 으로 변경 */
  background-color: #f9fafb; /* ✅ 비는 공간 대비 배경색 추가 */
}

.images-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.images-title::before {
  content: '📎';
  font-size: 1.1rem;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 최대 5개 기준으로 간격 유지 */
  gap: 1rem;
}

.image-item {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.image-item:hover {
  transform: translateY(-4px);
}

.attachment-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

/* 게시글 푸터 */
.article-footer {
  padding-top: 2rem;
  border-top: 2px solid rgba(245, 158, 11, 0.1);
}

.stats-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-icon {
  width: 18px;
  height: 18px;
}

/* 댓글 섹션 */
.comments-section {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(245, 158, 11, 0.1);
}

.comments-header {
  margin-bottom: 1.5rem;
}

.comments-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.title-icon {
  width: 20px;
  height: 20px;
  color: #f59e0b;
}

.comment-input-container {
  margin-bottom: 2rem;
}

.comments-list {
  margin-top: 1rem;
}

.comments-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 빈 댓글 상태 */
.no-comments {
  text-align: center;
  padding: 3rem 2rem;
  color: #6b7280;
}

.no-comments-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.no-comments-text {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #374151;
}

.no-comments-subtext {
  font-size: 0.9rem;
  margin: 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .article-content,
  .comments-section {
    padding: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .article-header {
    margin-bottom: 1.5rem;
    padding-bottom: 1.5rem;
  }

  .action-buttons-bottom {
    justify-content: center;
    margin-top: 1.5rem;
    padding-top: 1rem;
  }

  .article-title {
    font-size: 1.6rem;
  }

  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .content-text {
    font-size: 1rem;
  }

  .images-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }

  .stats-container {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
}

@media (max-width: 640px) {
  .article-content,
  .comments-section {
    padding: 1rem;
  }

  .article-title {
    font-size: 1.4rem;
  }

  .author-avatar {
    width: 36px;
    height: 36px;
  }

  .action-btn {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .images-grid {
    grid-template-columns: 1fr;
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

.article-content,
.comments-section {
  animation: fadeInUp 0.6s ease-out;
}

.comments-section {
  animation-delay: 0.1s;
}
</style>