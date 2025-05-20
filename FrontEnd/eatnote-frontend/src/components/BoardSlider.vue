<template>
  <div class="overflow-x-auto whitespace-nowrap flex items-center gap-4 py-4 px-2 bg-white shadow rounded mb-8">
    <button
      v-for="board in boards"
      :key="board.boardId"
      @click="$emit('selectBoard', board.boardId)"
      class="px-4 py-2 rounded-full transition font-semibold"
      :class="board.boardId === selectedBoardId ? 'bg-black text-white' : 'bg-gray-200 text-black'"
    >
      {{ board.name }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  boards: Array,
  selectedBoardId: Number
})
defineEmits(['selectBoard'])
</script>
