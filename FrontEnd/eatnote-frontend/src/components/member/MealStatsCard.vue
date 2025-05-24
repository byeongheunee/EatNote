<template>
  <div class="stats-container">
    <div class="stats-grid">
      <div 
        v-for="(stat, index) in statsList" 
        :key="stat.label"
        class="stat-card"
        :class="getStatClass(index)"
      >
        <div class="stat-header">
          <div class="stat-icon">
            {{ stat.icon }}
          </div>
          <div class="stat-indicator"></div>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'

const props = defineProps({
  stats: {
    type: Object,
    required: true
  }
})

const statsList = computed(() => [
  { 
    label: '연속 기록일', 
    value: `${props.stats.consecutiveDays}일`,
    icon: '🔥'
  },
  { 
    label: '총 식단 기록', 
    value: props.stats.totalMeals,
    icon: '📊'
  },
  { 
    label: '피드백 받은 식단', 
    value: props.stats.feedbackReceived,
    icon: '💡'
  },
  { 
    label: '평균 칼로리', 
    value: props.stats.averageCalories.toLocaleString(),
    icon: '⚡'
  },
])

const getStatClass = (index) => {
  const classes = ['stat-streak', 'stat-total', 'stat-feedback', 'stat-calorie']
  return classes[index] || ''
}
</script>

<style scoped>
.stats-container {
  width: 100%;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.stat-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 1.5rem;
  border: 1px solid #f1f5f9;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.stat-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.stat-body {
  text-align: left;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1;
  margin-bottom: 0.5rem;
  letter-spacing: -0.025em;
}

.stat-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
  line-height: 1.4;
}

/* 각 카드별 테마 색상 */
.stat-streak .stat-indicator {
  background: #ef4444;
}

.stat-streak:hover .stat-icon {
  background: #fef2f2;
  border-color: #fecaca;
}

.stat-total .stat-indicator {
  background: #3b82f6;
}

.stat-total:hover .stat-icon {
  background: #eff6ff;
  border-color: #bfdbfe;
}

.stat-feedback .stat-indicator {
  background: #10b981;
}

.stat-feedback:hover .stat-icon {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.stat-calorie .stat-indicator {
  background: #f59e0b;
}

.stat-calorie:hover .stat-icon {
  background: #fffbeb;
  border-color: #fed7aa;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
  
  .stat-card {
    padding: 1.25rem;
  }
  
  .stat-value {
    font-size: 1.75rem;
  }
  
  .stat-icon {
    width: 36px;
    height: 36px;
    font-size: 1.125rem;
  }
}

@media (max-width: 640px) {
  .stats-grid {
    gap: 0.75rem;
  }
  
  .stat-card {
    padding: 1rem;
  }
  
  .stat-value {
    font-size: 1.5rem;
  }
  
  .stat-label {
    font-size: 0.8rem;
  }
  
  .stat-icon {
    width: 32px;
    height: 32px;
    font-size: 1rem;
  }
  
  .stat-header {
    margin-bottom: 0.75rem;
  }
}

/* 미니멀한 애니메이션 */
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

.stat-card {
  animation: fadeInUp 0.4s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0s; }
.stat-card:nth-child(2) { animation-delay: 0.05s; }
.stat-card:nth-child(3) { animation-delay: 0.1s; }
.stat-card:nth-child(4) { animation-delay: 0.15s; }

/* 숫자 강조 효과 */
.stat-value {
  animation: slideIn 0.6s ease-out 0.2s both;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>