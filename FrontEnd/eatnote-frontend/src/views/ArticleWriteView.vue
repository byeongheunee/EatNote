<template>
  <div class="article-edit-page">
    <Header />

    <div class="container mx-auto px-6 py-12 max-w-5xl">
      
      <!-- 페이지 제목과 목록 이동 버튼 -->
      <div class="mb-10 flex justify-between items-end">
        <div>
          <h1 class="text-4xl font-bold text-gray-900 mb-3">
            {{ isEditMode ? '게시글 수정' : '게시글 작성' }}
          </h1>
          <p class="text-lg text-gray-600">
            {{ isEditMode ? '게시글을 수정해보세요' : '새로운 게시글을 작성해보세요' }}
          </p>
        </div>
        <button
          @click="goToBoard"
          class="nav-button secondary"
        >
          <span class="button-icon">←</span>
          <span>목록으로</span>
        </button>
      </div>

      <!-- 메인 에디터 영역 -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <!-- 왼쪽: 작성 폼 -->
        <div class="lg:col-span-2">
          <div class="editor-card">
            
            <!-- 제목 섹션 -->
            <div class="section-container mb-6">
              <h3 class="section-title">제목</h3>
              <div class="title-input-container">
                <input 
                  v-model="title" 
                  type="text" 
                  placeholder="게시글 제목을 입력하세요" 
                  class="title-input"
                  maxlength="200"
                />
                <div class="char-counter">
                  <span :class="{ 'text-red-500': title.length > 180 }">
                    {{ title.length }}
                  </span>
                  <span class="text-gray-400">/200자</span>
                </div>
              </div>
            </div>

            <!-- 내용 섹션 -->
            <div class="section-container mb-6">
              <h3 class="section-title">내용</h3>
              <textarea 
                v-model="content" 
                placeholder="게시글 내용을 입력하세요&#10;&#10;• 다른 회원들과 건설적인 소통을 위해 정중한 언어를 사용해주세요&#10;• 개인정보나 민감한 정보는 포함하지 마세요&#10;• 관련성 있는 내용으로 작성해주세요" 
                class="content-textarea"
              ></textarea>
            </div>

            <!-- 파일 업로드 섹션 -->
            <div class="section-container">
              <h3 class="section-title">
                <span>이미지 첨부</span>
                <span class="section-subtitle">(최대 5개)</span>
              </h3>
              
              <!-- 파일 업로드 영역 -->
              <div class="upload-area">
                <input 
                  type="file" 
                  multiple 
                  accept="image/*" 
                  @change="handleFileChange" 
                  class="file-input" 
                  id="file-upload"
                />
                <label for="file-upload" class="upload-label">
                  <div class="upload-icon">📷</div>
                  <div class="upload-text">
                    <p class="upload-title">이미지를 선택하거나 여기에 드롭하세요</p>
                    <p class="upload-subtitle">JPG, PNG 파일만 업로드 가능합니다</p>
                  </div>
                </label>
              </div>

              <!-- 선택된 파일 목록 -->
              <div v-if="fileNames.length" class="selected-files">
                <h4 class="file-list-title">선택된 파일</h4>
                <div class="file-list">
                  <div v-for="name in fileNames" :key="name" class="file-item">
                    <span class="file-icon">📄</span>
                    <span class="file-name">{{ name }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 미리보기 및 설정 -->
        <div class="lg:col-span-1">
          <div class="preview-card sticky-card">
            
            <!-- 게시글 정보 -->
            <div class="info-section">
              <h3 class="card-title">게시글 정보</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">상태</span>
                  <span class="status-badge">
                    {{ isEditMode ? '수정 중' : '작성 중' }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="info-label">제목 길이</span>
                  <span class="info-value">{{ title.length }}자</span>
                </div>
                <div class="info-item">
                  <span class="info-label">내용 길이</span>
                  <span class="info-value">{{ content.length }}자</span>
                </div>
                <div class="info-item">
                  <span class="info-label">첨부 이미지</span>
                  <span class="info-value">{{ totalImageCount }}개</span>
                </div>
              </div>
            </div>

            <!-- 기존 이미지 미리보기 -->
            <div v-if="existingImages.length" class="preview-section">
              <h4 class="preview-title">기존 이미지</h4>
              <div class="image-grid">
                <div v-for="(img, i) in existingImages" :key="img.fileId" class="image-item">
                  <img :src="getImageUrl(img.filePath)" class="preview-image" />
                  <button
                    class="remove-button"
                    @click="removeExistingImage(i)"
                    title="이미지 삭제"
                  >
                    ✕
                  </button>
                </div>
              </div>
            </div>

            <!-- 새 이미지 미리보기 -->
            <div v-if="previewUrls.length" class="preview-section">
              <h4 class="preview-title">새 이미지 미리보기</h4>
              <div class="image-grid">
                <div v-for="(url, i) in previewUrls" :key="i" class="image-item">
                  <img :src="url" class="preview-image" />
                  <div class="new-badge">NEW</div>
                </div>
              </div>
            </div>

            <!-- 액션 버튼 -->
            <div class="action-section">
              <button 
                @click="submit" 
                :disabled="!canSubmit"
                class="submit-button"
                :class="{ 'disabled': !canSubmit }"
              >
                <span class="button-icon">💾</span>
                <span>{{ isEditMode ? '수정완료' : '작성완료' }}</span>
              </button>
              
              <div v-if="!canSubmit" class="validation-message">
                {{ validationMessage }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Header from '@/components/common/Header.vue'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const toast = useToast()
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const articleId = Number(route.params.articleId)
const isEditMode = !isNaN(articleId)
const boardId = Number(route.query.boardId)

const title = ref('')
const content = ref('')
const files = ref([])
const fileNames = ref([])
const previewUrls = ref([])
const existingImages = ref([])
const deletedFileIds = ref([])

const getImageUrl = (path) => `http://localhost:8080${path}`

// 계산된 속성들
const totalImageCount = computed(() => {
  return existingImages.value.length + files.value.length
})

const canSubmit = computed(() => {
  return title.value.trim() && content.value.trim()
})

const validationMessage = computed(() => {
  if (!title.value.trim()) return '제목을 입력해주세요'
  if (!content.value.trim()) return '내용을 입력해주세요'
  return ''
})

const goToBoard = () => {
  router.push(`/community/${boardId}`)
}

const fetchArticle = async () => {
  try {
    const res = await axios.get(`/api/articles/${articleId}`, {
      headers: { Authorization: `Bearer ${auth.accessToken}` }
    })
    const data = res.data.data
    title.value = data.title
    content.value = data.content
    existingImages.value = data.attachments || []
  } catch (error) {
    console.error('게시글 조회 실패:', error)
    toast.error('게시글을 불러오는데 실패했습니다.')
  }
}

const removeExistingImage = (index) => {
  const removed = existingImages.value.splice(index, 1)[0]
  deletedFileIds.value.push(removed.fileId)
  toast.success('이미지가 삭제 예정 목록에 추가되었습니다.')
}

const handleFileChange = (event) => {
  const selectedFiles = Array.from(event.target.files)

  const totalImages = existingImages.value.length + selectedFiles.length
  if (totalImages > 5) {
    toast.warning('첨부 이미지는 최대 5개까지만 업로드할 수 있습니다.')
    return
  }

  // 이전 미리보기 URL 정리
  previewUrls.value.forEach(url => URL.revokeObjectURL(url))

  files.value = selectedFiles
  fileNames.value = files.value.map(file => file.name)
  previewUrls.value = files.value.map(file => URL.createObjectURL(file))
  
  if (selectedFiles.length > 0) {
    toast.success(`${selectedFiles.length}개의 이미지가 선택되었습니다.`)
  }
}

const submit = async () => {
  if (!canSubmit.value) {
    toast.warning(validationMessage.value)
    return
  }

  const articleData = isEditMode
    ? {
      title: title.value,
      content: content.value,
      deleteFileIds: deletedFileIds.value
    }
    : {
      title: title.value,
      content: content.value,
      boardId: boardId
    }

  const formData = new FormData()
  formData.append('article', JSON.stringify(articleData))
  files.value.forEach(file => formData.append('files', file))

  const method = isEditMode ? 'put' : 'post'
  const url = isEditMode
    ? `/api/articles/${articleId}`
    : `/api/articles?boardId=${boardId}`

  try {
    const res = await axios({
      method,
      url,
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${auth.accessToken}`
      }
    })

    const newArticleId = isEditMode ? articleId : res.data.data
    toast.success(isEditMode ? '게시글이 성공적으로 수정되었습니다!' : '게시글이 성공적으로 작성되었습니다!')
    router.replace(`/community/${boardId}/article/${newArticleId}`)
  } catch (e) {
    console.error('게시글 저장 실패:', e)
    toast.error(isEditMode ? '수정 중 오류가 발생했습니다.' : '작성 중 오류가 발생했습니다.')
  }
}

onMounted(() => {
  if (isEditMode) {
    fetchArticle()
  }
})

onUnmounted(() => {
  previewUrls.value.forEach(url => URL.revokeObjectURL(url))
})
</script>

<style scoped>
/* 페이지 전체 배경 */
.article-edit-page {
  padding-top: 80px; /* 헤더 높이만큼 여백 */
  min-height: 100vh;
  background: linear-gradient(135deg, #faf7f2 0%, #faf7f2 100%);
}

.container {
  max-width: 1400px;
}

/* 네비게이션 버튼 */
.nav-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
  min-width: 120px;
  justify-content: center;
}

.nav-button.secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
}

.nav-button.secondary:hover {
  background: #e5e7eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.button-icon {
  font-size: 18px;
}

/* 메인 카드 스타일 */
.editor-card,
.preview-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.editor-card {
  padding: 32px;
}

.preview-card {
  padding: 24px;
}

.sticky-card {
  position: sticky;
  top: 100px;
  align-self: flex-start;
}

/* 섹션 스타일 */
.section-container {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #f59e0b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-subtitle {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f59e0b;
}

/* 제목 입력 */
.title-input-container {
  position: relative;
}

.title-input {
  width: 100%;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  padding: 16px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
  transition: all 0.2s ease;
  padding-right: 80px;
}

.title-input:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.title-input::placeholder {
  color: #9ca3af;
}

.char-counter {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  font-weight: 500;
}

/* 내용 입력 */
.content-textarea {
  width: 100%;
  min-height: 300px;
  font-size: 16px;
  line-height: 1.6;
  color: #111827;
  padding: 20px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
  transition: all 0.2s ease;
  resize: vertical;
}

.content-textarea:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.content-textarea::placeholder {
  color: #9ca3af;
  line-height: 1.6;
}

/* 파일 업로드 */
.upload-area {
  position: relative;
  margin-bottom: 20px;
}

.file-input {
  position: absolute;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
  z-index: 2;
}

.upload-label {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-label:hover {
  border-color: #f59e0b;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.2);
}

.upload-icon {
  font-size: 48px;
  opacity: 0.7;
}

.upload-text {
  flex: 1;
}

.upload-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 4px;
}

.upload-subtitle {
  font-size: 14px;
  color: #6b7280;
}

/* 선택된 파일 목록 */
.selected-files {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
}

.file-list-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.file-icon {
  font-size: 16px;
}

.file-name {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

/* 정보 섹션 */
.info-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f3f4f6;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.status-badge {
  background: #10b981;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 미리보기 섹션 */
.preview-section {
  margin-bottom: 24px;
}

.preview-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e5e7eb;
  transition: all 0.2s ease;
}

.image-item:hover {
  border-color: #f59e0b;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-button {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-button:hover {
  background: #dc2626;
  transform: scale(1.1);
}

.new-badge {
  position: absolute;
  top: 4px;
  left: 4px;
  background: #10b981;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
}

/* 액션 섹션 */
.action-section {
  text-align: center;
}

.submit-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #f59e0b;
  color: white;
  padding: 16px 32px;
  border-radius: 12px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  width: 100%;
  justify-content: center;
}

.submit-button:hover:not(.disabled) {
  background: #d97706;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

.submit-button.disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.validation-message {
  margin-top: 12px;
  font-size: 14px;
  color: #ef4444;
  font-weight: 500;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .sticky-card {
    position: static;
  }
  
  .image-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .editor-card,
  .preview-card {
    padding: 20px;
  }
  
  .nav-button {
    width: auto;
    min-width: 100px;
    padding: 10px 16px;
    font-size: 14px;
  }
  
  .button-icon {
    font-size: 16px;
  }
  
  /* 모바일에서 제목과 버튼 세로 배치 */
  .mb-10.flex {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .upload-label {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .upload-icon {
    font-size: 36px;
  }
  
  .image-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 애니메이션 */
.container {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>