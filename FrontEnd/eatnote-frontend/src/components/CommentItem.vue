<template>
  <div class="comment-item" :class="{ 'comment-reply': isReply }">
    <!-- 댓글 카드 -->
    <div class="comment-card">
      <!-- 댓글 헤더 -->
      <div class="comment-header">
        <div class="author-info">
          <div class="author-avatar">
            <span class="avatar-emoji">{{ getAvatarEmoji(comment.userNickname) }}</span>
          </div>
          <div class="author-details">
            <span class="author-name">{{ comment.userNickname }}</span>
            <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
          </div>
        </div>
        
        <!-- 작성자 표시 -->
        <div class="author-badge" v-if="isAuthor">
          <span class="badge-text">작성자</span>
        </div>
      </div>

      <!-- 댓글 내용 -->
      <div class="comment-content">
        <p class="content-text">{{ comment.content }}</p>
      </div>

      <!-- 댓글 액션 -->
      <div class="comment-actions">
        <!-- 좋아요/싫어요 -->
        <div class="reaction-buttons">
          <button 
            @click="handleLike('LIKE')" 
            class="reaction-btn like-btn"
            :class="{ 'active': comment.myReaction === 'LIKE' }"
          >
            <svg class="reaction-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L9 7v13m-3-4H4a2 2 0 01-2-2v-6a2 2 0 012-2h2.5"></path>
            </svg>
            <span class="reaction-count">{{ comment.likeCount }}</span>
          </button>
          
          <button 
            @click="handleLike('DISLIKE')" 
            class="reaction-btn dislike-btn"
            :class="{ 'active': comment.myReaction === 'DISLIKE' }"
          >
            <svg class="reaction-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14H5.236a2 2 0 01-1.789-2.894l3.5-7A2 2 0 018.736 3h4.018c.163 0 .326.02.485.06L17 4m-7 10v5a2 2 0 002 2h.095c.5 0 .905-.405.905-.905 0-.714.211-1.412.608-2.006L15 17V4m-3 4H9a2 2 0 00-2 2v6a2 2 0 002 2h2.5"></path>
            </svg>
            <span class="reaction-count">{{ comment.dislikeCount }}</span>
          </button>
        </div>

        <!-- 액션 버튼들 -->
        <div class="action-buttons">
          <button @click="showReply = !showReply" class="action-btn reply-btn">
            <svg class="action-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"></path>
            </svg>
            {{ showReply ? '답글 취소' : '답글 달기' }}
          </button>
          
          <button
            v-if="isAuthor"
            @click="handleDelete"
            class="action-btn delete-btn"
          >
            <svg class="action-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
            </svg>
            삭제
          </button>
        </div>
      </div>
    </div>

    <!-- 답글 입력 -->
    <div v-if="showReply" class="reply-input-container">
      <CommentInput
        :parentCommentId="comment.commentId"
        :onSubmit="handleReplySubmit"
        :targetType="targetType"
        :targetId="targetId"
      />
    </div>

    <!-- 답글 목록 -->
    <div v-if="comment.replies && comment.replies.length" class="replies-container">
      <div class="replies-header">
        <div class="replies-line"></div>
        <span class="replies-count">{{ comment.replies.length }}개의 답글</span>
        <button @click="showReplies = !showReplies" class="toggle-replies-btn">
          <svg class="toggle-icon" :class="{ 'rotated': showReplies }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
          </svg>
          {{ showReplies ? '접기' : '펼쳐보기' }}
        </button>
      </div>
      
      <div v-if="showReplies" class="replies-list">
        <CommentItem 
          v-for="reply in comment.replies" 
          :key="reply.commentId" 
          :comment="reply" 
          :onReload="onReload"
          :targetType="targetType" 
          :targetId="targetId"
          :isReply="true"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, computed } from 'vue'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import CommentInput from './CommentInput.vue'
import CommentItem from './CommentItem.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()

const props = defineProps({
  comment: Object,
  onReload: Function,
  targetType: String,
  targetId: [String, Number],
  isReply: {
    type: Boolean,
    default: false
  }
})

const auth = useAuthStore()
const showReply = ref(false)
const showReplies = ref(false) // 답글 펼치기/접기 상태

// 현재 로그인한 유저와 댓글 작성자가 동일한지 확인
const isAuthor = computed(() => {
  return auth.user?.userId === props.comment.userId
})

// 아바타 이모지 생성
const getAvatarEmoji = (nickname) => {
  const emojis = ['😊', '😎', '🤗', '😄', '🙂', '😋', '🤔', '😌', '🥰', '😇']
  const index = nickname ? nickname.charCodeAt(0) % emojis.length : 0
  return emojis[index]
}

// 날짜 포맷 함수
const formatDate = (datetime) => {
  const date = new Date(datetime)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 1) {
    return '오늘'
  } else if (diffDays === 2) {
    return '어제'
  } else if (diffDays <= 7) {
    return `${diffDays - 1}일 전`
  } else {
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`
  }
}

// 답글 제출 처리
const handleReplySubmit = () => {
  showReply.value = false
  // 답글 작성 후 자동으로 답글 목록 펼치기
  showReplies.value = true
  props.onReload()
}

// 좋아요/싫어요 처리
const handleLike = async (type) => {
  if (!auth.isLoggedIn) {
    toast.warning('로그인이 필요합니다.')
    return
  }

  try {
    const token = localStorage.getItem('accessToken')
    await axios.post('/api/likes', {
      contentType: 'COMMENT',
      contentId: props.comment.commentId,
      likeType: type
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    props.onReload()
  } catch (e) {
    console.error('댓글 좋아요 실패:', e)
    toast.error('좋아요 처리에 실패했습니다. 다시 시도해주세요.')
  }
}

// 댓글 삭제 처리
const handleDelete = async () => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return

  try {
    const token = localStorage.getItem('accessToken')
    await axios.delete(`/api/comments/${props.comment.commentId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    props.onReload()
    toast.success('댓글이 삭제되었습니다. 🗑️')
  } catch (e) {
    console.error('댓글 삭제 실패:', e)
    toast.error('댓글 삭제에 실패했습니다.')
  }
}
</script>

<style scoped>
/* 댓글 아이템 */
.comment-item {
  margin-bottom: 1rem;
}

.comment-reply {
  margin-left: 2rem;
  position: relative;
}

.comment-reply::before {
  content: '';
  position: absolute;
  left: -1rem;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, rgba(245, 158, 11, 0.3) 0%, rgba(245, 158, 11, 0.1) 100%);
  border-radius: 1px;
}

/* 댓글 카드 */
.comment-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
  backdrop-filter: blur(8px);
  border: 2.5px solid rgba(32, 21, 2, 0.1);
  border-radius: 16px;
  padding: 1.25rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.comment-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #f59e0b, #d97706);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.comment-card:hover {
  border-color: rgba(245, 158, 11, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.1);
}

.comment-card:hover::before {
  opacity: 1;
}

/* 댓글 헤더 */
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.author-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 10px rgba(245, 158, 11, 0.3);
  flex-shrink: 0;
}

.avatar-emoji {
  font-size: 1.1rem;
  color: white;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

.author-name {
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.comment-date {
  font-size: 0.75rem;
  color: #9ca3af;
}

.author-badge {
  background: linear-gradient(135deg, #93d48f, #93d48f);
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-size: 0.7rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.badge-text {
  display: block;
}

/* 댓글 내용 */
.comment-content {
  margin-bottom: 1rem;
}

.content-text {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #374151;
  margin: 0;
  white-space: pre-line;
}

/* 댓글 액션 */
.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(245, 158, 11, 0.1);
}

/* 반응 버튼 */
.reaction-buttons {
  display: flex;
  gap: 0.5rem;
}

.reaction-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0.75rem;
  border: 1px solid rgba(229, 231, 235, 0.5);
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.8rem;
  font-weight: 500;
}

.reaction-icon {
  width: 14px;
  height: 14px;
}

.reaction-count {
  font-weight: 600;
}

.like-btn {
  color: #6b7280;
}

.like-btn:hover {
  background: rgba(16, 185, 129, 0.1);
  border-color: rgba(16, 185, 129, 0.3);
  color: #70d66b;
}

.like-btn.active {
  background: linear-gradient(135deg, #93d48f, #93d48f);
  border-color: #88d684;
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.dislike-btn {
  color: #6b7280;
}

.dislike-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
  color: #ef4444;
}

.dislike-btn.active {
  background: linear-gradient(135deg, #f07c7c, #f07c7c);
  border-color: #ee6767;
  color: white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.4rem 0.75rem;
  border: 0.5px solid #bdc1c9;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.8rem;
  font-weight: 500;
  color: #6b7280;
}

.action-icon {
  width: 14px;
  height: 14px;
}

.reply-btn:hover {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.delete-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

/* 답글 입력 */
.reply-input-container {
  margin-top: 1rem;
  padding-left: 1rem;
  border-left: 3px solid rgba(245, 158, 11, 0.2);
}

/* 답글 목록 */
.replies-container {
  margin-top: 1rem;
}

.replies-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding-left: 1rem;
}

.replies-line {
  width: 20px;
  height: 2px;
  background: linear-gradient(90deg, #f59e0b, #d97706);
  border-radius: 1px;
}

.replies-count {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

/* 답글 펼치기/접기 버튼 */
.toggle-replies-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.3rem 0.6rem;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.2);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.75rem;
  font-weight: 500;
  color: #f59e0b;
  margin-left: auto;
  margin-right: 23px;
}

.toggle-replies-btn:hover {
  background: rgba(245, 158, 11, 0.15);
  border-color: rgba(245, 158, 11, 0.3);
}

.toggle-icon {
  width: 12px;
  height: 12px;
  transition: transform 0.3s ease;
}

.toggle-icon.rotated {
  transform: rotate(180deg);
}

.replies-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .comment-reply {
    margin-left: 1rem;
  }
  
  .comment-reply::before {
    left: -0.5rem;
  }

  .comment-card {
    padding: 1rem;
  }

  .comment-actions {
    flex-direction: column;
    gap: 0.75rem;
    align-items: stretch;
  }

  .reaction-buttons,
  .action-buttons {
    justify-content: center;
  }

  .author-avatar {
    width: 32px;
    height: 32px;
  }

  .avatar-emoji {
    font-size: 1rem;
  }

  .author-name {
    font-size: 0.85rem;
  }

  .comment-date {
    font-size: 0.7rem;
  }

  .content-text {
    font-size: 0.9rem;
  }

  .replies-header {
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .toggle-replies-btn {
    margin-left: 0;
    margin-top: 0.25rem;
  }
}

@media (max-width: 640px) {
  .comment-reply {
    margin-left: 0.5rem;
  }

  .reply-input-container {
    padding-left: 0.5rem;
  }

  .replies-header {
    padding-left: 0.5rem;
  }

  .reaction-buttons,
  .action-buttons {
    flex-wrap: wrap;
    gap: 0.25rem;
  }

  .reaction-btn,
  .action-btn {
    padding: 0.3rem 0.5rem;
    font-size: 0.75rem;
  }
}

/* 애니메이션 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.comment-item {
  animation: fadeInUp 0.4s ease-out;
}

.replies-list .comment-item {
  animation-delay: 0.1s;
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