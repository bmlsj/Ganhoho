<!--FullWorkScheduleView.vue -->
<template>
  <div class="work-schedule-layout">
    <div class="header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <!-- 튜토리얼 시 배경 블러 -->
        <div :class="{'overlay': tutorialStep === 1 && isFirstVisit}"></div>
      </div>
      <!-- 요일 헤더 -->
      <div class="weekdays">
        <span v-for="(day, index) in [''].concat(weekDays)" :key="index" :class="{ sunday: index === 1 }">
          {{ day }}
        </span>
      </div>
    </div>

    <!-- 플로팅 메뉴: 메인 + 버튼 및 서브 버튼들 -->
    <div class="fab-container">
      <!-- 메인 버튼 -->
      <button
        class="fab-main"
        :class="{ 'fab-open': isOpen }"
        @click="toggleMenu"
      >
        +
      </button>

      <!-- 서브 버튼들 (가로로 나타남) -->
      <transition-group name="fab" tag="div" class="fab-sub-container">
        <button
          v-if="isOpen"
          v-for="(btn, index) in subButtons"
          :key="btn.id"
          class="fab-sub"
          @click="onSubButtonClick(btn)"
        >
          {{ btn.label }}
        </button>
      </transition-group>
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

/* 스토어 & 기본값 */
const store = useApiStore()
const defaultYear = new Date().getFullYear()
const defaultMonth = new Date().getMonth() + 1
const weekDays = ['일', '월', '화', '수', '목', '금', '토']

/* 튜토리얼 관련 */
const tutorialStep = ref(1)
const isFirstVisit = ref(localStorage.getItem('visitedFullWorkSchedule') !== 'true')

const nextTutorialStep = async () => {
  if (tutorialStep.value === 1) {
    tutorialStep.value = 2
    localStorage.setItem('visitedFullWorkSchedule', 'true')
    isFirstVisit.value = false
    await nextTick()
    document.removeEventListener('click', nextTutorialStep)
  }
}

/* 라우터 관련 */
const router = useRouter()
const route = useRoute()

/* 파일 업로드용 */
const galleryInput = ref(null)
const openGallery = () => {
  galleryInput.value.click()
}
const handleFileSelection = async (event) => {
  const files = event.target.files
  if (files.length > 0) {
    await store.sendImageToAPI(files[0])
  }
}

/* 기존 toggleView 로직: 라우트 전환 */
const toggleView = () => {
  if (route.name === 'FullWorkSchedule') {
    router.push({ name: 'WeeklySchedule' })
  } else {
    router.push({ name: 'FullWorkSchedule' })
  }
}
/* isWeekly 계산 */
const isWeekly = computed(() => route.name === 'WeeklySchedule')

/* 플로팅 버튼 (메뉴) 관련 */
const isOpen = ref(false)
const subButtons = [
  // 서브 버튼 1: 이미지 등록
  { id: 'gallery', label: '이미지등록' },
  // 서브 버튼 2: 주 단위 보기
  { id: 'toggle', label: '주 단위 보기' },
]

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

/* 서브 버튼 클릭 분기 */
const onSubButtonClick = (btn) => {
  if (btn.id === 'gallery') {
    // 이미지 등록
    openGallery()
  } else if (btn.id === 'toggle') {
    // 주 단위 보기
    toggleView()
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
})

onUnmounted(() => {
  document.removeEventListener('click', nextTutorialStep)
})
</script>


<style scoped>
.work-schedule-layout {
  position: relative;
  font-family: Arial, sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: 100vh; /* 전체 화면 사용 */
}

/* 헤더 */
.header {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 10;
  border-bottom: 1px solid #ddd;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.year-month {
  font-size: 18px;
  font-weight: bold;
  margin-left: 18px;
}
.weekdays {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  align-items: center;
  justify-items: center;
  column-gap: 2px;
  text-align: center;
  padding: 4px 0;
}
.sunday {
  color: red;
}

/* 메인 컨텐츠 */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-top: 8px;
}

/* 플로팅 버튼 컨테이너 */
.fab-container {
  position: fixed;
  top: 20px; /* 원하는 위치 */
  right: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 9999;
}

/* 메인 + 버튼: 기본 상태 */
.fab-main {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #DCEAF7;
  color: #000000;
  font-size: 24px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

/* .fab-open 시 커지고 색상 변경 */
.fab-main.fab-open {
  width: 40px;
  height: 40px;
  background-color: #0056b3;
  font-size: 28px;
  transform: rotate(45deg);
}

/* 서브 버튼 컨테이너 */
.fab-sub-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px;
}

/* 서브 버튼 */
.fab-sub {
  width: 30px;
  height:30px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background-color: #dceaf7;
  color: #333;
  font-size: 14px;
  box-shadow: 0px 2px 5px rgba(0,0,0,0.15);
  margin-top: 10px;
  transition: transform 0.3s;
}
.fab-sub:hover {
  transform: scale(1.1);
}

/* transition-group 애니메이션 */
.fab-enter-from,
.fab-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.8);
}
.fab-enter-active,
.fab-leave-active {
  transition: all 0.3s ease;
}

/* 튜토리얼 배경 블러 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(2px);
  z-index: 100;
}
</style>

