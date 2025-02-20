<template>
  <div class="container">
    <div class="header">
      <div class="header-row">
        <div class="year-month left-item">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <div class="button-group right-item">
          <!-- 갤러리 버튼 래퍼 -->
          <div class="button-wrapper">
            <button
              ref="galleryButton"
              :class="{
                'btn-gallery': true,
                'tuto-button': tutorialStep === 1 && isFirstVisit,
                'target-circle': tutorialStep === 1 && isFirstVisit
              }"
              @click="openGallery"
            >
              <img class="gallery-image" :src="gallery" alt="이미지 등록" />
            </button>

            <!-- 갤러리 말풍선 (tutorialStep 1) -->
            <div
              v-if="tutorialStep === 1 && isFirstVisit"
              class="explanation-text gallery-explanation"
            >
              이미지를 등록해보세요!
            </div>
          </div>

          <!-- 토글 버튼 래퍼 -->
          <div class="button-wrapper">
            <button
              ref="toggleButton"
              :class="{
                'btn-toggle': true,
                'tuto-button': tutorialStep === 2 && isFirstVisit,
                'target-circle': tutorialStep === 2 && isFirstVisit
              }"
              @click="toggleView"
            >
              <img class="toggle-image" :src="isWeekly ? toggleon : toggleoff" alt="주 단위 보기" />
            </button>

            <!-- 토글 말풍선 (tutorialStep 2) -->
            <div
              v-if="tutorialStep === 2 && isFirstVisit"
              class="explanation-text toggle-explanation"
            >
              주 단위 / 전체 보기를<br />전환할 수 있어요!
            </div>
          </div>
        </div>
        <!-- 튜토리얼 시 배경 블러 -->
        <div :class="{'overlay': (tutorialStep === 1 || tutorialStep === 2) && isFirstVisit}"></div>
      </div>
      <!-- 요일 헤더 -->
      <div class="weekdays">
        <span
          v-for="(day, index) in [''].concat(weekDays)"
          :key="index"
          :class="{ sunday: index === 1 }"
        >
          {{ day }}
        </span>
      </div>
    </div>

    <!-- 콘텐츠 영역 -->
    <div class="content">
      <router-view />
    </div>

    <!-- 파일 선택 input -->
    <input
      type="file"
      ref="galleryInput"
      accept="image/*"
      style="display: none"
      @change="handleFileSelection"
    />
  </div>
</template>


<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useApiStore } from '@/stores/apiRequest'
import gallery from '@/assets/gallery.png'
import toggleon from '@/assets/toggleon.png'
import toggleoff from '@/assets/toggleoff.png'

/* 스토어 & 기본값 */
const store = useApiStore()
const defaultYear = new Date().getFullYear()
const defaultMonth = new Date().getMonth() + 1
const weekDays = ['일', '월', '화', '수', '목', '금', '토']

/* 튜토리얼 관련 */
const tutorialStep = ref(1)
const isFirstVisit = ref(localStorage.getItem('visitedFullWorkSchedule') !== 'true')
const isWeekly = computed(() => route.name === 'WeeklySchedule')

/* 라우터 관련 */
const router = useRouter()
const route = useRoute()

/* 파일 업로드용 */
const galleryInput = ref(null)

const nextTutorialStep = async () => {
  if (tutorialStep.value === 1) {
    // 첫 단계: 갤러리 효과 종료, 토글 효과 활성화
    tutorialStep.value = 2;
    // 필요시 여기서 토글 효과와 관련한 추가 로직을 넣으세요.
  } else if (tutorialStep.value === 2) {
    // 두 번째 단계: 튜토리얼 종료
    tutorialStep.value = 3;
    localStorage.setItem('visitedFullWorkSchedule', 'true');
    isFirstVisit.value = false;
    document.removeEventListener('click', nextTutorialStep);
  }
}

const openGallery = () => {
  galleryInput.value.click()
}
const handleFileSelection = async (event) => {
  const files = event.target.files
  if (files.length > 0) {
    await store.sendImageToAPI(files[0])
  }
}
const toggleView = () => {
  if (route.name === 'FullWorkSchedule') {
    router.push({ name: 'WeeklySchedule' })
  } else {
    router.push({ name: 'FullWorkSchedule' })
  }
}

/* onMounted: 튜토리얼/캘린더 로직 */
onMounted(async () => {
  console.log("📢 캘린더 업데이트 실행!")
  if (store.isDataLoaded) {
    console.log("📢 기존 데이터 유지됨 → GET 요청 생략")
  } else {
    console.log("📢 POST 요청이 먼저 실행되어야 합니다. (GET 요청 대기 중)")
  }

  await nextTick()
  store.generateCalendar()
  console.log("📢 불러온 일정 데이터:", store.people)

  isFirstVisit.value = localStorage.getItem('visitedFullWorkSchedule') !== 'true'
  console.log("onMounted 후 isFirstVisit:", isFirstVisit.value)

  if (tutorialStep.value === 1 && isFirstVisit.value) {
    document.addEventListener('click', nextTutorialStep)
  }

  document.addEventListener('tokenReceived', (e) => {
    const { user_id, access_token } = e.detail
    console.log("Component - Token received via event:", access_token)
    store.setToken(user_id, access_token)
  })
})

onUnmounted(() => {
  document.removeEventListener('click', nextTutorialStep)
})
</script>

<style scoped>
/* 버튼 래퍼: 상대 위치로 두어 내부의 절대 요소 기준이 됨 */
.button-wrapper {
  position: relative;
  display: inline-block;
  /* flex container 내에서도 공간 차지를 최소화 */
}

/* 타겟 효과 및 애니메이션 (해당 버튼에 원형 효과) */
.tuto-button {
  width: 30px;
  height: 30px;
  position: relative;
  z-index: 200;
  background-color: #dceaf7;
  border-radius: 50%;
  animation: dungdung 1.0s linear alternate infinite;
}
.target-circle {
  position: relative;
  z-index: 200;
  background: #dceaf7;
  border-radius: 50%;
}

/* 공통 말풍선 스타일 */
.explanation-text {
  position: absolute;
  background: #fff;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 300;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  pointer-events: none;
}

/* 갤러리 말풍선 위치: 갤러리 버튼 위에 덮어씀 */
.gallery-explanation {
  top: 10%;
  right:35px;
  /* 중앙 정렬하려면 필요에 따라 transform 사용 가능 */
  animation: dungdung 1.0s linear alternate infinite;
}

/* 오른쪽 꼬리 (갤러리 말풍선) */
.gallery-explanation::after {
  content: "";
  position: absolute;
  top: 50%;
  right: -12px;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border: 6px solid transparent;
  border-left-color: #fff;
}

/* 토글 말풍선 위치: 토글 버튼 위에 덮어씀 */
.toggle-explanation {
  top: 1%;
  right: 35px;
  transform: translateY(-50%);
  animation: dungdung 1.0s linear alternate infinite;
}

/* 오른쪽 꼬리 (토글 말풍선) */
.toggle-explanation::after {
  content: "";
  position: absolute;
  top: 50%;
  right: -12px;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border: 6px solid transparent;
  border-left-color: #fff;
}

/* 기타 기존 스타일들은 그대로 유지 */
.container {
  position: relative;
  font-family: Arial, sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
  flex-direction: column;
  height: 100vh;
}
.header {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 10;
  border-bottom: 1px solid #ddd;
}
.header-row {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  align-items: center;
  padding: 8px;
  white-space: nowrap;
}
.left-item {
  grid-column: 1 / span 2;
}
.right-item {
  grid-column: 11 / span 2;
}
.year-month {
  padding-left: 8px;
  font-size: 18px;
  font-weight: bold;
}
.button-group {
  display: flex;
}
.button-group button {
  padding-left: 1px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: transparent;
}
.gallery-image {
  width: 28px;
  height: auto;
  display: block;
}
.toggle-image {
  width: 28px;
  height: auto;
  display: block;
}
.btn-gallery,
.btn-toggle {
  padding-left: 6px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1px;
}
.weekdays {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  align-items: center;
  justify-items: center;
  gap: 2px;
  padding: 4px 0;
}
.sunday {
  color: red;
}
.content {
  overflow-y: auto;
}
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  z-index: 100;
}

@keyframes dungdung {
  from {
    transform: translateY(-5px);
  }
  to {
    transform: translateY(5px);
  }
}

</style>

