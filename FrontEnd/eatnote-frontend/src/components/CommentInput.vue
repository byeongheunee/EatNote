<template>
  <div class="comment-input-wrapper">
    <!-- 로그인하지 않은 경우 -->
    <div v-if="!auth.isLoggedIn" class="login-prompt">
      <div class="login-prompt-content">
        <div class="prompt-icon">🔒</div>
        <div class="prompt-text">
          <h4 class="prompt-title">댓글을 작성하려면 로그인이 필요합니다</h4>
          <p class="prompt-description">로그인하고 다른 사용자들과 소통해보세요!</p>
        </div>
        <button @click="goToLogin" class="login-button">
          <svg class="login-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"></path>
          </svg>
          로그인하기
        </button>
      </div>
    </div>

    <!-- 로그인한 경우 -->
    <div v-else class="comment-input-container">
      <!-- 사용자 정보 -->
      <div class="user-info">
        <div class="user-avatar">
          <span class="avatar-emoji">💬</span>
        </div>
        <div class="user-details">
          <span class="user-name">{{ auth.user?.nickname || '사용자' }}</span>
          <span class="comment-label">{{ parentCommentId ? '답글' : '댓글' }} 작성</span>
        </div>
      </div>

      <!-- 댓글 입력 영역 -->
      <div class="input-section">
        <div class="input-row">
          <!-- 텍스트 입력 -->
          <div class="textarea-container">
            <textarea 
              v-model="content" 
              ref="textareaRef"
              rows="2"
              class="comment-textarea"
              :placeholder="parentCommentId ? '답글을 입력하세요...' : '댓글을 입력하세요...'"
              @input="handleInput"
              @focus="handleFocus"
              @blur="handleBlur"
              @keydown.enter.ctrl="submitComment"
              @keydown.enter.meta="submitComment"
            ></textarea>
            
            <!-- 글자 수 카운터 -->
            <div class="character-counter">
              <span :class="['counter-text', { 'counter-warning': content.length > 500 }]">
                {{ content.length }}/500
              </span>
            </div>
          </div>

          <!-- 액션 버튼들 -->
          <div class="action-buttons">
            <div class="button-group">
              <!-- 취소 버튼 (내용이 있을 때만) -->
              <button 
                v-if="content.trim()"
                @click="clearContent" 
                class="cancel-button"
                title="취소"
              >
                <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>

              <!-- 등록 버튼 -->
              <button 
                @click="submitComment" 
                :disabled="!content.trim() || isSubmitting || content.length > 500"
                class="submit-button"
                :class="{ 'button-loading': isSubmitting }"
                :title="parentCommentId ? '답글 등록 (Ctrl+Enter)' : '댓글 등록 (Ctrl+Enter)'"
              >
                <div v-if="isSubmitting" class="loading-spinner"></div>
                <svg v-else class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 도움말 텍스트 -->
        <div class="help-text">
          <span class="help-shortcut">Ctrl + Enter로 빠른 등록</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const toast = useToast()

const props = defineProps({
  parentCommentId: { type: Number, default: null },
  onSubmit: { type: Function, default: () => { } },
  targetType: { type: String, required: true },
  targetId: { type: [String, Number], required: true }
})

const content = ref('')
const isSubmitting = ref(false)
const isFocused = ref(false)
const textareaRef = ref(null)

const auth = useAuthStore()
const router = useRouter()

// 텍스트 영역 행 수 계산
const textareaRows = computed(() => {
  if (!content.value || content.value.length < 50) return 2
  if (content.value.length < 150) return 3
  return 4
})

// 입력 처리
const handleInput = () => {
  // 최대 글자 수 제한
  if (content.value.length > 500) {
    content.value = content.value.substring(0, 500)
  }
}

// 포커스 처리
const handleFocus = () => {
  isFocused.value = true
}

const handleBlur = () => {
  isFocused.value = false
}

// 내용 지우기
const clearContent = () => {
  content.value = ''
  textareaRef.value?.focus()
}

// 로그인 페이지로 이동
const goToLogin = () => {
  router.push({ 
    path: '/login', 
    query: { redirect: router.currentRoute.value.fullPath } 
  })
}

// 댓글 등록
const submitComment = async () => {
  if (!content.value.trim()) {
    toast.warning('댓글 내용을 입력해주세요.')
    return
  }

  if (content.value.length > 500) {
    toast.warning('댓글은 500자 이내로 작성해주세요.')
    return
  }

  isSubmitting.value = true

  try {
    const token = localStorage.getItem('accessToken')

    await axios.post('/api/comments', {
      content: content.value.trim(),
      parentCommentId: props.parentCommentId,
      targetType: props.targetType,
      targetId: props.targetId
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    content.value = ''
    toast.success(props.parentCommentId ? '답글이 등록되었습니다! 💬' : '댓글이 등록되었습니다! 💬')
    props.onSubmit()
  } catch (err) {
    console.error('댓글 등록 실패:', err)
    if (err.response?.status === 401) {
      toast.error('로그인이 필요합니다.')
      goToLogin()
    } else if (err.response?.status === 403) {
      toast.error('댓글 작성 권한이 없습니다.')
    } else {
      toast.error('댓글 등록에 실패했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* 컨테이너 */
.comment-input-wrapper {
  width: 100%;
}

/* 로그인 프롬프트 */
.login-prompt {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1) 0%, rgba(217, 119, 6, 0.1) 100%);
  border: 2px dashed rgba(245, 158, 11, 0.3);
  border-radius: 16px;
  padding: 2rem;
}

.login-prompt-content {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  text-align: left;
}

.prompt-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.prompt-text {
  flex: 1;
}

.prompt-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.prompt-description {
  font-size: 0.9rem;
  color: #6b7280;
  margin: 0;
}

.login-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.login-button:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

.login-icon {
  width: 18px;
  height: 18px;
}

/* 댓글 입력 컨테이너 */
.comment-input-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
  backdrop-filter: blur(8px);
  border: 2px solid rgba(245, 158, 11, 0.1);
  border-radius: 16px;
  padding: 1.5rem;
  transition: all 0.3s ease;
}

.comment-input-container:focus-within {
  border-color: rgba(245, 158, 11, 0.3);
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.1);
}

/* 사용자 정보 */
.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 10px rgba(245, 158, 11, 0.3);
}

.avatar-emoji {
  font-size: 1.1rem;
  color: white;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 0.1rem;
}

.user-name {
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.comment-label {
  font-size: 0.8rem;
  color: #6b7280;
}

/* 입력 섹션 */
.input-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-row {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.textarea-container {
  position: relative;
  flex: 1;
}

.comment-textarea {
  width: 100%;
  height: 96px;
  padding: 1rem;
  padding-bottom: 2rem;
  border: 2px solid rgba(229, 231, 235, 0.5);
  border-radius: 12px;
  font-size: 0.95rem;
  line-height: 1.5;
  resize: none;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  color: #374151;
}

.comment-textarea:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

.comment-textarea::placeholder {
  color: #9ca3af;
}

/* 글자 수 카운터 */
.character-counter {
  position: absolute;
  bottom: 18px;
  right: 12px;
  pointer-events: none;
}

.counter-text {
  font-size: 0.75rem;
  color: #9ca3af;
  background: rgba(255, 255, 255, 0.95);
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  transition: color 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.counter-warning {
  color: #ef4444;
  font-weight: 600;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  flex-shrink: 0;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.cancel-button,
.submit-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
  position: relative;
}

.button-icon {
  width: 16px;
  height: 16px;
}

.cancel-button {
  background: rgba(255, 255, 255, 0.8);
  color: #6b7280;
  border: 1px solid rgba(229, 231, 235, 0.5);
}

.cancel-button:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #374151;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.submit-button {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.submit-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.4);
}

.submit-button:disabled {
  background: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.button-loading {
  cursor: not-allowed;
}

/* 도움말 텍스트 */
.help-text {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.25rem;
}

.help-shortcut {
  font-size: 0.75rem;
  color: #9ca3af;
  font-style: italic;
}

/* 로딩 스피너 */
.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .comment-input-container {
    padding: 1.25rem;
  }

  .login-prompt {
    padding: 1.5rem;
  }

  .login-prompt-content {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }

  .user-info {
    margin-bottom: 0.75rem;
  }

  .input-row {
    gap: 0.75rem;
  }

  .comment-textarea {
    padding: 0.875rem;
    padding-bottom: 1.875rem;
    font-size: 0.9rem;
    height: 88px;
  }

  .cancel-button,
  .submit-button {
    width: 40px;
    height: 40px;
  }

  .button-icon {
    width: 14px;
    height: 14px;
  }
}

@media (max-width: 640px) {
  .input-row {
    flex-direction: column;
    gap: 1rem;
  }

  .action-buttons {
    align-self: flex-end;
  }

  .button-group {
    flex-direction: row;
    gap: 0.75rem;
  }

  .help-text {
    justify-content: center;
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

.comment-input-wrapper {
  animation: fadeInUp 0.4s ease-out;
}
</style>