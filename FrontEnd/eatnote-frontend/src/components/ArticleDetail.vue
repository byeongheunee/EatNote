<template>
  <div>
    <!-- 닫기 버튼 -->
    <div class="text-right mb-4">
      <button
        class="px-4 py-1 text-sm text-gray-600 hover:underline"
        @click="goBackToBoard"
      >
        ← 목록으로 돌아가기
      </button>
    </div>

    <div v-if="article" class="bg-white p-6 rounded shadow mb-10">
      <!-- 제목 + 작성자 -->
      <div class="flex justify-between items-center mb-2">
        <h2 class="text-2xl font-bold">{{ article.title }}</h2>
        <!-- 작성자 클릭 시 모달 열기 -->
        <span class="text-gray-500 text-sm hover:underline cursor-pointer" @click="openAuthorProfile">
          작성자 : {{ article.userNickname }}
          <span v-if="article && !isMyArticle && auth.user?.userType === 2" class="ml-2 text-xs text-blue-600">
            · {{ followStatusLabel }}
          </span>
        </span>
      </div>

      <!-- ✨ 수정/삭제 버튼 -->
      <div v-if="isMyArticle" class="flex justify-end gap-2 mb-2">
        <button
          @click="goToEdit"
          class="px-4 py-1 text-sm text-white bg-blue-600 rounded hover:bg-blue-700"
        >
          수정
        </button>
        <button
          @click="deleteArticle"
          class="px-4 py-1 text-sm text-white bg-red-600 rounded hover:bg-red-700"
        >
          삭제
        </button>
      </div>

      <!-- 내용 + 대표 이미지 -->
      <div class="flex flex-col md:flex-row gap-4 mt-4">
        <div class="flex-1 whitespace-pre-line text-gray-800">
          내용 : {{ article.content }}
        </div>
        <img v-if="article.imageUrl" :src="getImageUrl(article.imageUrl)" alt="대표 이미지"
          class="w-full md:w-80 h-auto rounded object-cover shadow" />
      </div>

      <!-- 추가 첨부 이미지 -->
      <div v-if="additionalImages.length" class="mt-6">
        <h4 class="font-semibold mb-2">첨부 이미지</h4>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
          <img v-for="(img, index) in additionalImages" :key="index" :src="getImageUrl(img.filePath)"
            :alt="img.originalName" class="w-full h-40 object-cover rounded shadow" />
        </div>
      </div>

      <!-- 좋아요/싫어요/조회수 -->
      <div class="flex items-center gap-3 text-gray-500 text-sm mt-4">
        <LikeDislikeButtons
          contentType="ARTICLE"
          :contentId="articleId"
          :likeCount="article.likeCount"
          :dislikeCount="article.dislikeCount"
          :myReaction="article.myReaction"
          :onUpdated="fetchArticle"
        />
        <span class="flex items-center gap-1"> 👁️ 조회수 {{ article.viewCnt }} </span>
      </div>

      <!-- 댓글 입력 -->
      <h3 class="mt-8 text-lg font-semibold">댓글</h3>

      <CommentInput
        :parentCommentId="null"
        :onSubmit="loadComments"
        targetType="ARTICLE"
        :targetId="articleId"
      />

      <!-- 댓글 목록 -->
      <div v-if="comments.length">
        <CommentItem
          v-for="comment in comments"
          :key="comment.commentId"
          :comment="comment"
          :onReload="loadComments"
          targetType="ARTICLE"
          :targetId="articleId"
        />
      </div>
      <div v-else>
        <p class="text-sm text-gray-500 mt-2">아직 댓글이 없습니다.</p>
      </div>
    </div>

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
  // 내가 작성한 글이면 모달 열지 않음
  if (isMyArticle.value) return

  try {
    const token = auth.accessToken

    const res = await axios.get(`/api/users/${article.value.userId}/profile`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    // 응답이 실패한 경우
    if (!res.data.success) {
      const code = res.data.code

      if (code === 'FORBIDDEN_ADMIN_PROFILE') {
        // alert('관리자는 프로필을 조회할 수 없습니다.')
        toast.warning('관리자는 프로필을 조회할 수 없습니다. ⚠️')
      } else if (code === 'USER_NOT_FOUND') {
        // alert('해당 사용자가 존재하지 않습니다.')
        toast.warning('해당 사용자가 존재하지 않습니다.')
      } else if (code === 'VALIDATION_FAILED') {
        // alert('알 수 없는 사용자 유형입니다.')
        toast.error('알 수 없는 사용자 유형입니다. ⚠️')
      } else {
        // alert(res.data.message || '알 수 없는 오류가 발생했습니다.')
        toast.error(res.data.message || '알 수 없는 오류가 발생했습니다. 🚨')
      }

      return
    }

    // 정상 응답 처리
    const profile = res.data.data
    console.log(profile)
    selectedProfile.value = profile
    isUserProfileOpen.value = true

  } catch (e) {
    console.error('작성자 프로필 조회 실패:', e)
    // alert('작성자 정보를 불러오지 못했습니다.')
    toast.error('작성자 정보를 불러오지 못했습니다.')
  }
}

const followStatusLabel = computed(() => {
  switch (article.value.followStatus) {
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
const hasMyComment = computed(() => {
  return comments.value.some(c => c.userId === currentUser.value?.userId)
})

// 게시글 삭제
const deleteArticle = async () => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    const token = localStorage.getItem('accessToken')
    await axios.delete(`/api/articles/${articleId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    // alert('게시글이 삭제되었습니다.')
    toast.success('게시글이 성공적으로 삭제되었습니다! 🗑️')
    router.push(`/community/${boardId}`)
  } catch (e) {
    console.error('게시글 삭제 실패:', e)
    // alert('삭제 중 오류가 발생했습니다.')
    toast.error('게시글 삭제 중 오류가 발생했습니다.')
  }
}

// 수정 페이지로 이동 <- 이 부분 이따가 수정.....
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

const getImageUrl = (path) => `http://localhost:8080${path}`

const goBackToBoard = () => {
  router.push(`/community/${boardId}`)
}

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
/* 필요 시 스타일 추가 */
</style>