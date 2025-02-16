<!--FullWorkScheduleView.vue -->
<template>
  <div class="container">
    <div class="header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <!-- 플로팅 메뉴를 헤더 내부에 배치 -->
      <div class="fab-container">
        <div class="fab-menu">
          <button
            class="fab-main"
            :class="{ 'fab-open': isOpen }"
            @click="toggleMenu"
          >
            +
          </button>
          <transition-group name="fab" tag="div" class="fab-sub-container">
            <button
              v-if="isOpen"
              v-for="(btn, index) in subButtons"
              :key="btn.id"
              class="fab-sub"
              @click="handleSubButton(btn)"
            >
              {{ btn.label }}
            </button>
          </transition-group>
        </div>
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
import change from '@/assets/change.png'
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
  { id: 'gallery', label: gallery },
  // 서브 버튼 2: 주 단위 보기
  { id: 'toggle', label: change },
]

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

// 서브 버튼 클릭 핸들러: 기능 수행 후 메뉴 닫기
const handleSubButton = (btn) => {
  if (btn.id === 'gallery') {
    openGallery()
  } else if (btn.id === 'toggle') {
    toggleView()
  }
  // 서브 버튼 클릭 후 메뉴 닫기
  isOpen.value = false
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
/* 부모 컨테이너: 전체 화면을 사용 */
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 헤더 영역: 헤더는 고정되지 않고, 콘텐츠 영역과 별도로 분리 */
.header {
  flex: 0 0 auto;  /* 자연스럽게 콘텐츠 앞에 위치 */
  background-color: white;
  border-bottom: 1px solid #ddd;
  /* 헤더의 높이는 내용에 따라 결정됨 */
}

/* 헤더 내부 */
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
}
.year-month {
  font-size: 18px;
  font-weight: bold;
}

/* 요일 헤더 */
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

/* 콘텐츠 영역: 헤더 아래에서 스크롤 */
.content {
  flex: 1;
  overflow-y: auto;
}

/* 플로팅 메뉴 컨테이너: 헤더 내부에서 오른쪽에 위치 */
.fab-container {
  position: absolute;
  top: 5%;  /* 헤더 높이의 중간 정도 */
  right: 10px; /* 헤더 우측에서 16px 떨어짐 */
  transform: translateY(-50%); /* 중앙 정렬 */
  z-index: 20;
}

/* fab-menu: 플로팅 메뉴 내부, 버튼들을 가로로 배치 */
.fab-menu {
  display: flex;
  flex-direction: row;
  align-items: center;
}

/* 메인 버튼 (기본 크기 30px) */
.fab-main {
  width: 25px;
  height: 25px;
  border-radius: 50%;
  background-color: #DCEAF7;
  color: #000;
  font-size: 24px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 메인 버튼이 열렸을 때: 왼쪽으로 슬라이드, 크기 커짐 */
.fab-main.fab-open {
  transform: translateX(-5px) rotate(45deg);
  width: 30px;
  height: 30px;
  background-color: #0056b3;
  font-size: 28px;
}

/* 서브 버튼 컨테이너: 가로 정렬 */
.fab-sub-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  margin-left: 10px;
}

/* 서브 버튼 */
.fab-sub {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background-color: #dceaf7;
  color: #333;
  font-size: 14px;
  transition: transform 0.3s;
}
.fab-sub:hover {
  transform: scale(1.1);
}

/* Transition-group 애니메이션: 수평 슬라이드 효과 */
.fab-enter-from,
.fab-leave-to {
  opacity: 0;
  transform: translateX(10px) scale(0.8);
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

