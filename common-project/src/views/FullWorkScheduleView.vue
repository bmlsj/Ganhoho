<!--FullWorkScheduleView.vue -->
<template>
  <div class="container">
    <div class="header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <div class="button-group">
          <button class="btn-gallery" @click="openGallery">
            <img class=gallery-image :src="gallery" alt="이미지 등록" />
          </button>
          <button class="btn-toggle" @click="toggleView">
            <img class=toggle-image :src="isWeekly ? toggleon : toggleoff" alt="주 단위 보기" />
          </button>
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
    tutorialStep.value = 2
    localStorage.setItem('visitedFullWorkSchedule', 'true')
    isFirstVisit.value = false
    await nextTick()
    document.removeEventListener('click', nextTutorialStep)
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
})

onUnmounted(() => {
  document.removeEventListener('click', nextTutorialStep)
})
</script>


<style scoped>
/* 부모 컨테이너: 전체 화면을 사용 */
.container {
  position: relative;
  font-family: Arial, sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
  flex-direction: column;
  height: 100vh; /* 전체 화면 사용 */
}

/* 헤더 영역: 헤더는 고정되지 않고, 콘텐츠 영역과 별도로 분리 */
.header {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 10;
  border-bottom: 1px solid #ddd;
}

/* 헤더 내부 */
.header-row {
  display: grid;
  grid-template-columns: auto auto; /* 첫 번째 컬럼: 년월, 두 번째 컬럼: 버튼 그룹 */
  column-gap: 175px;  /* 원하는 간격 조절 */
  align-items: center;
  padding: 8px 8px;
}
.year-month {
  padding-left:8px;
  font-size: 18px;
  font-weight: bold;
}
.button-group button {
  padding-left: 3px;
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
  width:28px;
  height:auto;
  display:block;
}
.btn-gallery,
.btn-toggle {
  padding-left: 6px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1px;
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
  overflow-y: auto;
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

