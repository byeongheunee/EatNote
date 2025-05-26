<template>
  <div v-if="visible" class="profile-modal">
    <div class="modal-card">
      <!-- 닫기 버튼 -->
      <button @click="$emit('close')" class="close-button">
        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
      </button>

      <!-- 프로필 헤더 -->
      <div class="profile-header">
        <div class="profile-avatar">
          <img :src="getProfileImage(profile.profileImage)" class="avatar-image" />
          <div class="avatar-ring"></div>
        </div>
        <div class="profile-basic">
          <h2 class="profile-name">{{ profile.nickname }}</h2>
          <div class="profile-badges">
            <span class="gender-badge" :class="profile.gender === 'M' ? 'male' : 'female'">
              {{ profile.gender === 'M' ? '👨 남성' : '👩 여성' }}
            </span>
            <span class="user-type-badge" :class="getUserTypeBadgeClass()">
              {{ getUserTypeLabel() }}
            </span>
          </div>
        </div>
      </div>

      <!-- 프로필 정보 -->
      <div class="profile-content">
        <!-- 트레이너 정보 -->
        <div v-if="profile.userType === 1" class="info-section">
          <h3 class="section-title">
            <span class="title-icon">💪</span>
            트레이너 정보
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">소개</span>
              <span class="info-value">{{ profile.introduction || '소개 없음' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">헬스장</span>
              <span class="info-value">{{ profile.gymName || '정보 없음' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">홈페이지</span>
              <a v-if="profile.gymWebsite" :href="profile.gymWebsite" target="_blank" class="info-link">
                <svg class="link-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"></path>
                </svg>
                {{ profile.gymWebsite }}
              </a>
              <span v-else class="info-value">없음</span>
            </div>
            <div class="info-item">
              <span class="info-label">인스타그램</span>
              <a v-if="profile.instagramUrl" :href="profile.instagramUrl" target="_blank" class="info-link instagram">
                <svg class="link-icon" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                </svg>
                {{ profile.instagramUrl }}
              </a>
              <span v-else class="info-value">없음</span>
            </div>
          </div>
          
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-number">{{ profile.totalFeedbacks }}</span>
              <span class="stat-label">총 피드백</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ profile.followingCount }}</span>
              <span class="stat-label">팔로잉</span>
            </div>
          </div>
        </div>

        <!-- 일반 회원 정보 -->
        <div v-else-if="profile.userType === 2" class="info-section">
          <h3 class="section-title">
            <span class="title-icon">🏃</span>
            회원 정보
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">목표</span>
              <span class="info-value">{{ profile.goal || '미설정' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">알레르기</span>
              <span class="info-value">{{ profile.allergyIds?.length ? profile.allergyIds.join(', ') : '없음' }}</span>
            </div>
          </div>
          
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-number">{{ profile.totalMeals }}</span>
              <span class="stat-label">총 식단 기록</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ profile.averageScore ?? '-' }}</span>
              <span class="stat-label">평균 점수</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ profile.consecutiveDays }}</span>
              <span class="stat-label">연속 기록일</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ profile.followingCount }}</span>
              <span class="stat-label">팔로잉</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 팔로우 액션 -->
      <div class="follow-section">
        <!-- 로그인 유저가 트레이너라면 -->
        <template v-if="isTrainer">
          <div class="follow-actions">
            <template v-if="profile.followedByOtherStatus === 'ACCEPTED'">
              <div class="status-card accepted">
                <div class="status-icon">👥</div>
                <p class="status-text">{{ profile.nickname }}님이 나를 팔로우 중입니다</p>
                <button @click="cancelFollow" class="action-button danger">
                  <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                  팔로우 끊기
                </button>
              </div>
            </template>

            <template v-else-if="profile.followedByOtherStatus === 'PENDING'">
              <div class="status-card pending">
                <div class="status-icon">📩</div>
                <p class="status-text">{{ profile.nickname }}님의 팔로우 요청</p>
                <div class="button-group">
                  <button @click="acceptFollowRequest" class="action-button success">
                    <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                    수락
                  </button>
                  <button @click="rejectFollowRequest" class="action-button danger">
                    <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                    </svg>
                    거절
                  </button>
                </div>
              </div>
            </template>

            <template v-else-if="profile.followedByOtherStatus === 'REJECTED'">
              <div class="status-card rejected">
                <div class="status-icon">🙅</div>
                <p class="status-text">{{ profile.nickname }}님의 요청을 거절하였습니다</p>
              </div>
            </template>

            <template v-else>
              <div class="status-card neutral">
                <div class="status-icon">🙈</div>
                <p class="status-text">{{ profile.nickname }}님의 팔로우 요청이 없습니다</p>
              </div>
            </template>
          </div>
        </template>

        <!-- 일반 사용자일 경우 -->
        <template v-else>
          <!-- 내가 보낸 팔로우 상태 -->
          <div class="follow-actions">
            <template v-if="profile.followStatus === 'ACCEPTED'">
              <div class="status-card accepted">
                <div class="status-icon">✅</div>
                <p class="status-text">팔로우 중</p>
                <button @click="cancelFollow" class="action-button danger">
                  <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                  팔로우 취소
                </button>
              </div>
            </template>
            <template v-else-if="profile.followStatus === 'PENDING'">
              <div class="status-card pending">
                <div class="status-icon">⏳</div>
                <p class="status-text">팔로우 요청 중</p>
              </div>
            </template>
            <template v-else-if="profile.followStatus === 'REJECTED'">
              <div class="status-card rejected">
                <div class="status-icon">❌</div>
                <p class="status-text">팔로우 요청이 거절되었습니다</p>
              </div>
            </template>
            <template v-else>
              <button @click="sendFollowRequest" class="action-button primary large">
                <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                팔로우 요청
              </button>
            </template>

            <template v-if="profile.userType === 2">
              <!-- 상대방이 나를 팔로우하는 상태 -->
              <template v-if="profile.followedByOtherStatus === 'ACCEPTED'">
                <div class="status-card accepted secondary">
                  <div class="status-icon">👀</div>
                  <p class="status-text">상대방이 나를 팔로우하고 있습니다</p>
                </div>
              </template>
              <template v-else-if="profile.followedByOtherStatus === 'PENDING'">
                <div class="status-card pending">
                  <div class="status-icon">📩</div>
                  <p class="status-text">팔로우 요청이 도착했습니다</p>
                  <div class="button-group">
                    <button @click="acceptFollowRequest" class="action-button success">
                      <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                      </svg>
                      수락
                    </button>
                    <button @click="rejectFollowRequest" class="action-button danger">
                      <svg class="button-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                      </svg>
                      거절
                    </button>
                  </div>
                </div>
              </template>
              <template v-else-if="profile.followedByOtherStatus === 'REJECTED'">
                <div class="status-card rejected secondary">
                  <div class="status-icon">🙅</div>
                  <p class="status-text">상대방의 요청을 거절하였습니다</p>
                </div>
              </template>
              <template v-else-if="profile.followStatus !== 'NONE' && profile.followStatus">
                <div class="status-card neutral secondary">
                  <div class="status-icon">🙈</div>
                  <p class="status-text">상대방의 팔로우 요청이 없습니다</p>
                </div>
              </template>
            </template>
            
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'

const emit = defineEmits(['follow-requested', 'close'])

const toast = useToast?.()
const auth = useAuthStore()

const isTrainer = computed(() => auth.user.userType === 1)

const defaultProfile = '/images/default-profile.png'
const getProfileImage = (path) => path ? `http://localhost:8080${path}` : defaultProfile

const props = defineProps({
  visible: Boolean,
  profile: Object,
})

const getUserTypeLabel = () => {
  switch (props.profile.userType) {
    case 0: return '👑 관리자'
    case 1: return '💪 트레이너'
    case 2: return '🏃 회원'
    default: return '❓ 알 수 없음'
  }
}

const getUserTypeBadgeClass = () => {
  switch (props.profile.userType) {
    case 0: return 'admin'
    case 1: return 'trainer'
    case 2: return 'member'
    default: return 'unknown'
  }
}

const sendFollowRequest = async () => {
  try {
    const res = await axios.post('/api/follow', {
      nickname: props.profile.nickname,
    }, {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    })

    if (res.data?.success) {
      const msg = `${props.profile.nickname}님에게 팔로우를 요청하였습니다.`
      if (toast) toast.success(msg)
      else alert(msg)

      emit('follow-requested')
    } else {
      const fallback = res.data?.message || '팔로우 요청에 실패했습니다.'
      toast?.error(fallback) ?? alert(fallback)
    }
  } catch (e) {
    console.error('팔로우 요청 실패:', e)
    toast?.error('서버 오류로 요청을 처리할 수 없습니다.') ?? alert('서버 오류 발생')
  }
}

const cancelFollow = async () => {
  try {
    const isTrainer = auth.user.userType === 1
    const url = isTrainer ? '/api/follow/follower' : '/api/follow'

    const res = await axios.delete(url, {
      data: {
        nickname: props.profile.nickname,
      },
      headers: {
        Authorization: `Bearer ${auth.accessToken}`
      }
    });

    if (res.data?.success) {
      const msg = isTrainer
        ? `${props.profile.nickname}님을 팔로우 목록에서 제거했습니다.`
        : `${props.profile.nickname}님과의 팔로우가 취소되었습니다.`
      toast?.success(msg) ?? alert(msg)

      emit('follow-requested')
      emit('close')
    } else {
      const fallback = res.data?.message || '팔로우 취소에 실패했습니다.'
      toast?.error(fallback) ?? alert(fallback)
    }
  } catch (e) {
    console.error('팔로우 취소 실패:', e)
    toast?.error('서버 오류로 요청을 처리할 수 없습니다.') ?? alert('서버 오류 발생')
  }
}

const respondToFollowRequest = async (action) => {
  try {
    const res = await axios.post('/api/follow/respond', {
      followId: props.profile.followId,
      action: action
    })

    if (res.data?.success) {
      const msg = action === 'ACCEPT'
        ? `${props.profile.nickname}님의 팔로우 요청을 수락했습니다.`
        : `${props.profile.nickname}님의 팔로우 요청을 거절했습니다.`
      toast?.success(msg) ?? alert(msg)
      emit('follow-requested')
      emit('close')
    } else {
      const fallback = res.data?.message || '요청 처리에 실패했습니다.'
      toast?.error(fallback) ?? alert(fallback)
    }
  } catch (e) {
    console.error(`팔로우 ${action} 실패:`, e)
    toast?.error('서버 오류로 요청을 처리할 수 없습니다.') ?? alert('서버 오류 발생')
  }
}

const acceptFollowRequest = () => respondToFollowRequest('ACCEPT')
const rejectFollowRequest = () => respondToFollowRequest('REJECT')
</script>

<style scoped>
.profile-modal {
  background: transparent;
  border-radius: 0;
  overflow: visible;
}

.modal-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  padding: 2.5rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.1);
  position: relative;
  min-width: 600px;
  max-width: 800px;
  width: 90vw;
}

.close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 40px;
  height: 40px;
  background: rgba(107, 114, 128, 0.1);
  border: none;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #6b7280;
}

.close-button:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  transform: scale(1.1);
}

.close-button svg {
  width: 20px;
  height: 20px;
}

/* 프로필 헤더 */
.profile-header {
  text-align: center;
  margin-bottom: 2rem;
}

.profile-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: 1rem;
}

.avatar-image {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  object-fit: cover;
  box-shadow: 0 8px 25px rgba(245, 158, 11, 0.3);
}

.avatar-ring {
  position: absolute;
  inset: -4px;
  border: 2px solid #f59e0b;
  border-radius: 24px;
  background: linear-gradient(45deg, #f59e0b, #d97706);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask-composite: xor;
  -webkit-mask-composite: xor;
}

.profile-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #374151, #111827);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.profile-badges {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  flex-wrap: wrap;
}

.gender-badge,
.user-type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.gender-badge.male {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.gender-badge.female {
  background: rgba(236, 72, 153, 0.1);
  color: #ec4899;
}

.user-type-badge.admin {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.user-type-badge.trainer {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.user-type-badge.member {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

/* 정보 섹션 */
.info-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid rgba(245, 158, 11, 0.2);
}

.title-icon {
  font-size: 1.3rem;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 0.75rem;
  background: rgba(249, 250, 251, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(229, 231, 235, 0.5);
}

.info-label {
  font-weight: 600;
  color: #6b7280;
  font-size: 0.9rem;
  min-width: 80px;
}

.info-value {
  color: #374151;
  font-size: 0.9rem;
  text-align: right;
  flex: 1;
}

.info-link {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #3b82f6;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s ease;
}

.info-link:hover {
  color: #2563eb;
}

.info-link.instagram {
  color: #ec4899;
}

.info-link.instagram:hover {
  color: #db2777;
}

.link-icon {
  width: 14px;
  height: 14px;
}

/* 통계 그리드 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 1.25rem;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.05));
  border-radius: 16px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.stat-number {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #f59e0b;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

/* 팔로우 섹션 */
.follow-section {
  border-top: 2px solid rgba(245, 158, 11, 0.1);
  padding-top: 2rem;
}

.follow-actions {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.status-card {
  padding: 1.25rem;
  border-radius: 16px;
  text-align: center;
  border: 2px solid;
  transition: all 0.3s ease;
}

.status-card.accepted {
  background: rgba(34, 197, 94, 0.1);
  border-color: rgba(34, 197, 94, 0.2);
}

.status-card.pending {
  background: rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.2);
}

.status-card.rejected {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.2);
}

.status-card.neutral {
  background: rgba(107, 114, 128, 0.1);
  border-color: rgba(107, 114, 128, 0.2);
}

.status-card.secondary {
  opacity: 0.8;
  transform: scale(0.95);
}

.status-icon {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.status-text {
  font-weight: 600;
  color: #374151;
  margin-bottom: 1rem;
  font-size: 0.95rem;
}

.action-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
}

.action-button.large {
  padding: 1rem 2rem;
  font-size: 1rem;
}

.action-button.primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.action-button.primary:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.4);
}

.action-button.success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.action-button.success:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(34, 197, 94, 0.4);
}

.action-button.danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.action-button.danger:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(239, 68, 68, 0.4);
}

.button-icon {
  width: 16px;
  height: 16px;
}

.button-group {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  flex-wrap: wrap;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .modal-card {
    min-width: 0;
    max-width: 95vw;
    width: 95vw;
    padding: 2rem;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .modal-card {
    padding: 1.5rem;
    min-width: 0;
    max-width: 95vw;
  }
  
  .profile-name {
    font-size: 1.3rem;
  }
  
  .avatar-image {
    width: 70px;
    height: 70px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-button {
    padding: 0.6rem 1.2rem;
    font-size: 0.85rem;
  }
  
  .action-button.large {
    padding: 0.8rem 1.5rem;
    font-size: 0.9rem;
  }
  
  .button-group {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .modal-card {
    padding: 1rem;
  }
  
  .profile-badges {
    flex-direction: column;
    align-items: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .info-value {
    text-align: left;
  }
}

/* 애니메이션 */
@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-card {
  animation: fadeInScale 0.3s ease-out;
}

.status-card {
  animation: fadeInScale 0.4s ease-out;
}

.stat-item {
  animation: fadeInScale 0.5s ease-out;
}

.stat-item:nth-child(1) { animation-delay: 0.1s; }
.stat-item:nth-child(2) { animation-delay: 0.2s; }
.stat-item:nth-child(3) { animation-delay: 0.3s; }
.stat-item:nth-child(4) { animation-delay: 0.4s; }

/* 접근성 */
.action-button:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

.close-button:focus {
  outline: 2px solid #ef4444;
  outline-offset: 2px;
}
</style>