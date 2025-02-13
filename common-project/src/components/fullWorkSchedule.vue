<template>
  <div class="calendar-wrapper">
    <div class="calendar-header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <div :class="{'overlay': tutorialStep === 1 && isFirstVisit}"></div> <!-- 블러처리 -->

        <div class="button-wrapper ">
          <p v-if="tutorialStep === 1 && isFirstVisit" class="add-schedule-text target tuto-text">
            버튼을 눌러 스케줄을<br> 추가하세요.
          </p>
          <button ref="addButton" 
                  :class="{
                    'add-button': true,
                    'tuto-button': tutorialStep === 1 && isFirstVisit,
                    'target-circle': tutorialStep === 1
                  }"
                  @click="openGallery">+</button>
        </div>
      </div>

      <div class="weekdays">
        <span v-for="(day, index) in [''].concat(weekDays)" :key="index" :class="{ sunday: index === 1 }">
          {{ day }}
        </span>
      </div>
    </div>

    <!-- 데이터가 없을 경우 -->
    <div v-if="store.people.length === 0" class="empty-state">
      <p>현재 등록된 일정이 없습니다.</p>
      <!-- <button class="reset-button" @click="resetTutorial">튜토리얼 다시 보기</button> -->
    </div>

    <!-- 캘린더 UI -->
    <div v-else class="calendar-body">
      <div v-for="(week, weekIndex) in store.calendar" :key="weekIndex" class="week">
        <div class="dates">
          <div v-for="(day, dayIndex) in week" :key="dayIndex" class="date">
            {{ day || '' }}
          </div>
        </div>

        <div v-for="person in store.people" :key="person.name" class="person-row">
          <div class="person-name">{{ person.name }}</div>
          <div class="person-schedule">
            <div
              v-for="(day, dayIndex) in week.slice(1, 8)"
              :key="dayIndex"
              class="schedule-box"
              :class="person.schedule[day]?.toLowerCase()"
              :style="{ visibility: day === null ? 'hidden' : 'visible' }"
            >
              {{ person.schedule[day] || '-' }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <input
      type="file"
      ref="galleryInput"
      accept="image/*"
      style="display: none"
      multiple
      @change="handleFileSelection"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, watchEffect } from 'vue'
import { useApiStore } from '@/stores/apiRequest'

const store = useApiStore()

const defaultYear = new Date().getFullYear() // 한국 기준 현재 연도
const defaultMonth = new Date().getMonth() + 1 // 한국 기준 현재 월
const weekDays = ['일', '월', '화', '수', '목', '금', '토']

const addButton = ref(null)
const galleryInput = ref(null)
const tutorialStep = ref(1)
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

const openGallery = () => {
  galleryInput.value.click() // 파일 선택 대화 상자 열기
}

// 파일 선택 처리 함수
const handleFileSelection = async (event) => {
  const files = event.target.files
  if (files.length > 0) {
    await store.sendImageToAPI(files[0]) // 첫 번째 선택한 파일을 API로 전송
  }
}

onMounted(async () => {
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

// Pinia store가 변경될 때마다 `isDataLoaded` 체크
watchEffect(() => {
  console.log("📢 데이터 상태 변경 감지:", store.isDataLoaded)
})

onUnmounted(() => {
  document.removeEventListener('click', nextTutorialStep)
})
</script>

<style scoped>
/* ------------------------------------------- */
/* 기본 스타일 */
/* ------------------------------------------- */
.calendar-wrapper {
  font-family: Arial, sans-serif;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
}

.calendar-header {
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
  margin-bottom: 8px;
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

.weekdays {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  align-items: center;
  justify-items: center;
  column-gap: 2px;
  text-align: center;
}

.sunday {
  color: red;
}

.calendar-body {
  display: flex;
  flex-direction: column;
}

.week {
  margin-bottom: 16px;
}

.dates {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  column-gap: 2px;
}

.date {
  text-align: center;
  font-weight: bold;
  font-size: 13px;
}

.person-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.person-name {
  width: 55px;
  height: 23px;
  flex-shrink: 0;
  text-align: center;
  line-height: 23px;
  font-weight: bold;
  font-size: 13px;
}

.person-schedule {
  flex: 1;
  display: flex;
}

.schedule-box {
  flex: 1;
  text-align: center;
  padding: 4px 6px;
  border: 1px solid #ccc;
  margin: 0 2px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1;
}

/* Nig 일정 스타일 */
.schedule-box.nig {
  background-color: #DDD4cD;
}
/* Day 일정 스타일 */
.schedule-box.day {
  background-color: #fff8bf;
}
/* Eve 일정 스타일 */
.schedule-box.eve {
  background-color: #e4c7f1;
}
/* Off 일정 스타일 */
.schedule-box.off {
  background-color: #fcd6c8;
}

.add-button { 
  background-color: #dceaf7;
  font-family: 'PlusJakartaSans-SemiBold', sans-serif;
  font-size: 18px;
  border: none;
  cursor: pointer;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.empty-state {
  text-align: center;
  font-size: 16px;
  color: gray;
  margin-top: 190px;
}

/* 튜토리얼 버튼 애니메이션 */
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

/* 말풍선 텍스트 */
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

/* 튜토리얼 시 배경 블러 */
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

/* target 클래스 */
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

/* ------------------------------------------- */
/* 1) 600px 이하: 글자/버튼 크기, 간격 등 축소 */
/* ------------------------------------------- */
@media (max-width: 600px) {
  .calendar-wrapper {
    padding: 8px;
  }

  .year-month {
    font-size: 16px;
    margin-left: 8px;
  }

  .weekdays {
    grid-template-columns: 45px repeat(7, 1fr); /* 첫 열(이름) 폭 축소 */
  }

  .date {
    font-size: 12px;
  }

  .person-name {
    width: 45px;
    font-size: 12px;
  }

  .schedule-box {
    font-size: 11px;
    padding: 3px 5px;
    margin: 0 1px;
  }

  .add-button {
    width: 24px;
    height: 24px;
    font-size: 14px;
    margin-right: 6px;
  }

  .add-schedule-text {
    font-size: 11px;
    padding: 4px 6px;
    margin-right: 8px;
  }

  .add-schedule-text::after {
    right: -12px;
    border-width: 6px;
  }
}

/* ------------------------------------------- */
/* 2) 400px 이하: 더 작은 해상도용 세부 조정 */
/* ------------------------------------------- */
@media (max-width: 400px) {
  .calendar-wrapper {
    padding: 6px;
  }

  .year-month {
    font-size: 14px;
    margin-left: 4px;
  }

  .weekdays {
    grid-template-columns: 40px repeat(7, 1fr);
  }

  .date {
    font-size: 11px;
  }

  .person-name {
    width: 40px;
    font-size: 11px;
  }

  .schedule-box {
    font-size: 10px;
    padding: 2px 3px;
    margin: 0 1px;
  }

  .add-button {
    width: 20px;
    height: 20px;
    font-size: 12px;
    margin-right: 4px;
  }

  .add-schedule-text {
    font-size: 10px;
    padding: 3px 5px;
    margin-right: 6px;
  }

  .add-schedule-text::after {
    right: -10px;
    border-width: 5px;
  }
}
</style>
