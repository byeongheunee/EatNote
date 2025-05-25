<template>
  <div class="quick-menu-container">
    <div class="menu-grid">
      <RouterLink 
        v-for="(menu, index) in menuItems" 
        :key="menu.name"
        :to="menu.path" 
        class="menu-item"
        :class="getMenuClass(index)"
      >
        <div class="menu-icon">
          {{ menu.icon }}
        </div>
        <div class="menu-content">
          <div class="menu-name">{{ menu.name }}</div>
          <div class="menu-desc">{{ menu.desc }}</div>
        </div>
        <div class="menu-arrow">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const menuItems = [
  {
    name: '식단 등록',
    desc: '새로운 식단 기록',
    path: '/meal/upload',
    icon: '📱'
  },
  {
    name: '피드백 확인',
    desc: '전문가 조언 보기',
    path: '/meals',
    icon: '💡'
  },
  {
    name: '운동 추천',
    desc: '맞춤 운동 영상',
    path: '/video-recommend',
    icon: '🏃‍♂️'
  },
  {
    name: '커뮤니티',
    desc: '다른 회원과 소통',
    path: '/community',
    icon: '👥'
  }
]

const getMenuClass = (index) => {
  const classes = ['menu-upload', 'menu-feedback', 'menu-exercise', 'menu-community']
  return classes[index] || ''
}
</script>

<style scoped>
.quick-menu-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.menu-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.75rem;
  flex: 1;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  padding: 1rem 1.25rem;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.menu-item:hover {
  transform: translateX(4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

.menu-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.menu-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.menu-name {
  font-size: 1rem;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.2;
}

.menu-desc {
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.3;
}

.menu-arrow {
  color: #94a3b8;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.menu-item:hover .menu-arrow {
  color: #64748b;
  transform: translateX(2px);
}

/* 각 메뉴별 테마 색상 */
.menu-upload:hover .menu-icon {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #1d4ed8;
}

.menu-feedback:hover .menu-icon {
  background: #f0fdf4;
  border-color: #bbf7d0;
  color: #047857;
}

.menu-exercise:hover .menu-icon {
  background: #fef2f2;
  border-color: #fecaca;
  color: #dc2626;
}

.menu-community:hover .menu-icon {
  background: #fffbeb;
  border-color: #fed7aa;
  color: #d97706;
}

/* 호버 시 왼쪽 테두리 강조 */
.menu-upload:hover {
  border-left-color: #3b82f6;
  border-left-width: 3px;
}

.menu-feedback:hover {
  border-left-color: #10b981;
  border-left-width: 3px;
}

.menu-exercise:hover {
  border-left-color: #ef4444;
  border-left-width: 3px;
}

.menu-community:hover {
  border-left-color: #f59e0b;
  border-left-width: 3px;
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .menu-item {
    padding: 0.875rem 1rem;
    gap: 0.875rem;
  }
  
  .menu-icon {
    width: 36px;
    height: 36px;
    font-size: 1.125rem;
  }
  
  .menu-name {
    font-size: 0.9rem;
  }
  
  .menu-desc {
    font-size: 0.8rem;
  }
}

/* 애니메이션 */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.menu-item {
  animation: slideInLeft 0.4s ease-out;
}

.menu-item:nth-child(1) { animation-delay: 0s; }
.menu-item:nth-child(2) { animation-delay: 0.1s; }
.menu-item:nth-child(3) { animation-delay: 0.2s; }
.menu-item:nth-child(4) { animation-delay: 0.3s; }

/* 액티브 상태 */
.menu-item:active {
  transform: scale(0.98);
}
</style>