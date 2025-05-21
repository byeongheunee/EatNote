<template>
  <div class="article-detail-view px-6 pb-20">
    <Header />

    <h1 class="text-3xl font-bold text-center mt-10 mb-6">게시판</h1>

    <!-- 게시판 슬라이더 -->
    <BoardSlider
      :boards="boards"
      :selectedBoardId="selectedBoardId"
      @selectBoard="selectBoard"
    />

    <!-- 게시글 상세 보기 -->
    <ArticleDetail :articleId="articleId" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/common/Header.vue'
import BoardSlider from '@/components/BoardSlider.vue'
import ArticleDetail from '@/components/ArticleDetail.vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const articleId = Number(route.params.articleId)
const selectedBoardId = ref(Number(route.params.boardId))

const boards = ref([])

const fetchBoards = async () => {
  const res = await axios.get('/api/boards')
  boards.value = res.data.data
}

const selectBoard = (id) => {
  router.push(`/community/${id}`) // 게시판 선택 시 목록으로 이동
}

onMounted(fetchBoards)
</script>

<!-- <style scoped>
.article-detail-view {
  max-width: 720px;
  margin: 0 auto;
}
</style> -->
