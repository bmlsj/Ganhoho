<template>
  <div v-if="isFirstVisit">
    <!-- 1단계: 메시지 확대 -->
    <div v-if="tutorialStep === 1" class="tutorial-overlay" @click="nextTutorialStep">
      <div class="magnify">
        <p class="tutorial-text">현재 등록된 일정이 없습니다.</p>
      </div>
    </div>
    <!-- 2단계: "+" 버튼 강조 -->
    <div v-if="tutorialStep === 2" @click="nextTutorialStep">
      <div
        class="highlight-circle"
        :style="{ top: `${highlightPosition.top}px`, left: `${highlightPosition.left}px`, width: `${highlightPosition.size}px`, height: `${highlightPosition.size}px` }"
      ></div>
      <p class="tutorial-comment">이 버튼을 눌러 근무표를 등록해주세요!</p>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  addButton: Object, // 버튼 DOM 요소
})
const emit = defineEmits(['tutorialFinished']) // 튜토리얼 종료 이벤트

const isFirstVisit = ref(!localStorage.getItem('visitedFullWorkSchedule'))
const tutorialStep = ref(1)
const highlightPosition = ref({ top: 0, left: 0, size: 0 })

const nextTutorialStep = async () => {
  if (tutorialStep.value === 1) {
    tutorialStep.value = 2
    await nextTick()
    updateButtonPosition()
  } else {
    isFirstVisit.value = false
    localStorage.setItem('visitedFullWorkSchedule', 'true')
    emit('tutorialFinished')
  }
}

const updateButtonPosition = () => {
  if (props.addButton) {
    const rect = props.addButton.getBoundingClientRect()
    highlightPosition.value = {
      top: rect.top + window.scrollY + rect.height / 2,
      left: rect.left + window.scrollX + rect.width / 2,
      size: Math.max(rect.width, rect.height) + 20,
    }
  }
}
</script>

<style scoped>
/* ✅ 튜토리얼 오버레이 */
.tutorial-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6); /* 배경 어둡게 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* ✅ 돋보기 확대 효과 */
.magnify {
  background: white;
  border: 5px solid #007bff;
  border-radius: 16px;
  padding: 20px;
  font-size: 20px;
  animation: zoomIn 1s ease-in-out;
}

@keyframes zoomIn {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.highlight-circle {
  position: fixed; /* 부모 기준을 뷰포트로 설정 */
  top: calc(var(--highlight-top) - var(--highlight-size) / 2); /* 중심 위치 */
  left: calc(var(--highlight-left) - var(--highlight-size) / 2); /* 중심 위치 */
  width: var(--highlight-size);
  height: var(--highlight-size);
  border-radius: 50%;
  border: 4px solid #007bff; /* 강조 색상 */
  animation: pulse 1.2s infinite ease-in-out; /* 심장 뛰는 애니메이션 */
  background: transparent;
  pointer-events: none;
  z-index: 1001;
}
@keyframes pulse {
  0% { transform: scale(1); opacity: 0.7; }
  50% { transform: scale(1.2); opacity: 1; }
  100% { transform: scale(1); opacity: 0.7; }
}

.tutorial-comment {
  position: absolute;
  top: calc(var(--highlight-top));
  left: calc(var(--highlight-left) - 150px);
  width: 120px;
  text-align: center;
  background: white;
  color: #007bff;
  font-size: 14px;
  font-weight: bold;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
  z-index: 1002;
}
/* .button-highlight {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6); 
  clip-path: circle(var(--highlight-size) at var(--highlight-left) var(--highlight-top));
  transition: clip-path 0.3s ease-in-out;
  z-index: 999;
} */
</style>
