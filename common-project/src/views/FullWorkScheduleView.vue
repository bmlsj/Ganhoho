<!-- WorkScheduleLayout.vue -->
<template>
  <div class="work-schedule-layout">
    <div class="header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <div class="view-toggle">
          <button @click="toggleView">
            {{ isWeekly ? '전체 보기' : '주 단위 보기' }}
          </button>
        </div>
      </div>
      <!-- 요일 헤더 추가 -->
      <div class="weekdays">
        <span v-for="(day, index) in [''].concat(weekDays)" :key="index" :class="{ sunday: index === 1 }">
          {{ day }}
        </span>
      </div>
    </div>
    <!-- 콘텐츠 영역을 감싸는 컨테이너 -->
    <div class="content">
      <!-- 콘텐츠 영역: FullWorkSchedule 또는 WeeklySchedule -->
      <router-view />
    </div>
    <!-- 플로팅 + 버튼 -->
    <div class="button-wrapper">
      <p v-if="tutorialStep === 1 && isFirstVisit" class="add-schedule-text target tuto-text">
        버튼을 눌러 스케줄을<br> 추가하세요.
      </p>
      <button ref="addButton" 
              :class="{
                'floating-add-button': true,
                'tuto-button': tutorialStep === 1 && isFirstVisit,
                'target-circle': tutorialStep === 1
              }"
              @click="openGallery">+</button>
    </div>
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
import { ref, computed, onMounted,onUnmounted,nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useApiStore } from '@/stores/apiRequest'

const store = useApiStore()
const defaultYear = new Date().getFullYear()
const defaultMonth = new Date().getMonth() + 1
const weekDays = ['일', '월', '화', '수', '목', '금', '토']

const tutorialStep = ref(1)
const router = useRouter()  
const route = useRoute()
const galleryInput = ref(null)
const isFirstVisit = ref(localStorage.getItem('visitedFullWorkSchedule') !== 'true') // 첫 방문 여부

const nextTutorialStep = async () => {
  if (tutorialStep.value === 1) {
    tutorialStep.value = 2
    localStorage.setItem('visitedFullWorkSchedule', 'true') // ✅ 화면 터치하는 순간 저장
    isFirstVisit.value = false
    await nextTick()
    document.removeEventListener('click', nextTutorialStep)
  }
}

onMounted(async ()=> {
  console.log("📢 캘린더 업데이트 실행!")
  // ✅ 처음 로드 시 GET 요청을 실행하지 않음
  if (store.isDataLoaded) {
    console.log("📢 기존 데이터 유지됨 → GET 요청 생략")
  } else {
    console.log("📢 POST 요청이 먼저 실행되어야 합니다. (GET 요청 대기 중)")
  }

  await nextTick() // DOM 업데이트 후 캘린더 생성
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


// 토글 버튼 클릭 시 라우트 전환 (라우트 이름은 라우터 설정에 맞게 지정)
const toggleView = () => {
  if (route.name === 'FullWorkSchedule') {
    router.push({ name: 'WeeklySchedule' })
  } else {
    router.push({ name: 'FullWorkSchedule' })
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

// 현재 라우트 이름이 'WeeklySchedule'이면 주 단위 보기
const isWeekly = computed(() => route.name === 'WeeklySchedule')
</script>

<style scoped>
.work-schedule-layout {
  position: relative;
  font-family: Arial, sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
}

.header {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
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
  margin-bottom: 15px;
}

.year-month {
  font-size: 18px;
  font-weight: bold;
  margin-left: 18px;
}
.button-wrapper {
  position: relative; 
  display: flex; 
}
.floating-add-button {
  position: fixed;
  bottom: 3vh; /* ✅ 뷰포트 기준 상대적인 위치 */
  right: 8vw; /* ✅ 뷰포트 기준 상대적인 위치 */
  width: clamp(50px, 5vw, 60px); /* ✅ 화면 크기에 따라 버튼 크기 조정 */
  height: clamp(50px, 5vw, 60px);
  background-color: #007bff;
  color: white;
  font-size: 24px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.2);
}
.tuto-button {
  width: 30px;
  height: 30px;
  position: relative;
  z-index: 200;
  background-color: #dceaf7;
  padding: 10px;
  border-radius: 50%;
  animation: dungdung 1.0s linear alternate infinite;
}
.tuto-text {
  z-index: 200;
  animation: dungdung 1.0s linear alternate infinite;
}
@keyframes dungdung {
  from {
    transform: translateY(-5px);
  }
  to {
    transform: translateY(5px);
  }
}
.target {
  position: relative;
  z-index: 200;
  background: white;
  padding: 10px;
  border-radius: 10px;
  margin-right: 12px;
}
.target-circle {
  position: relative;
  z-index: 200;
  background: #dceaf7;
  padding: 10px;
  border-radius: 50%;
}
.view-toggle button {
  background-color: #dceaf7;
  flex-shrink: 0;
  font-size: 14px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  border-radius: 10px;
  justify-content: center;
  margin-right: 10px;
}

.view-toggle button:hover {
  background-color: #bbb;
}

.add-schedule-text {
  font-size: 12px;
  font-weight: bold;
  color: #007bff;
  background: white;
  
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
  white-space: nowrap;
  position: relative;
  margin-right: 20px;
}

.add-schedule-text::after {
  content: "";
  position: absolute;
  top: 30%;              /* 말풍선 높이의 40% 위치 (원하는 위치로 조정) */
  right: -20px;          /* 말풍선 바깥쪽에 위치 */
  width: 40px;
  height: 40px;
  background: white;     /* 말풍선 배경색과 동일 */
  /* 아래 clip-path 경로는 예시입니다. 디자인에 따라 경로 값을 조정하세요. */
  clip-path: path('M0,20 Q30,0 40,0 Q20,20 0,20 Z');
}
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
