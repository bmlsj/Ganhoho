<template>
  <div class="calendar-wrapper">
    <div class="calendar-header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <div :class="{'overlay': tutorialStep === 1 && isFirstVisit}"></div>

        <p v-if="tutorialStep === 1 && isFirstVisit" class="text-right target">
          버튼을 눌러 스케줄을<br>추가하세요.
        </p>

        <button ref="addButton" 
          :class="{
            'add-button': true,
            'tuto-button': tutorialStep === 1 && isFirstVisit,
            'target-circle': tutorialStep === 1}"
          @click="openGallery">+</button>
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
      <button class="reset-button" @click="resetTutorial">튜토리얼 다시 보기</button>
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
import { ref, onMounted, nextTick,onUnmounted } from 'vue'
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
    localStorage.setItem('visitedFullWorkSchedule', 'true') // ✅ 화면 터치하는 순간 저장됨
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
    await store.sendImageToAPI(files[0]) // ✅ 첫 번째 선택한 파일을 API로 전송
  }
}

const resetTutorial = () => {
  // ✅ 기존 이벤트 제거 (중복 방지)
  document.removeEventListener('click', nextTutorialStep)

  localStorage.setItem('visitedFullWorkSchedule', 'false') // ✅ 튜토리얼 다시 시작
  isFirstVisit.value = true
  tutorialStep.value = 1

  // ✅ 튜토리얼 시작 시 이벤트 다시 등록
  setTimeout(() => {
    document.addEventListener('click', nextTutorialStep)
  }, 100) // **잠시 대기 후 등록 → 버튼 클릭 이벤트 무효화 방지**
}

onMounted(async () => {
  await store.fetchData() // 데이터 로드
  await nextTick() // DOM 업데이트
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

.add-button {
  background-color: #dceaf7;
  font-family: 'PlusJakartaSans-SemiBold', sans-serif;
  font-size: 14px;
  border: none;
  cursor: pointer;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  line-height: 0;
  margin-right: 10px;
}

.empty-state {
  text-align: center;
  font-size: 16px;
  color: gray;
  margin-top: 190px;
}
.tuto-button {
  width: 30px;
  height: 30px;
  position: relative;
  z-index: 200;
  background-color: #dceaf7;
  padding: 10px;
  border-radius: 50%;
  animation: dungdung 1.0s linear alternate infinite; /* 여기가 핵심 저기 1.0s에 둥둥 거리는 효과 시간? 설정이 가능해 */
}
@keyframes dungdung {
  from {
    transform: translateY(-5px); /* 여기서 어느정도 둥둥 거릴지 조정 가능 */
  }
  
  to {
    transform: translateY(5px); /* 여기서 어느정도 둥둥 거릴지 조정 가능 */
  }
}
.text-right {
  text-align: right;
  background: white;
  color: #007bff;
  font-size: 10px;
  font-weight: bold;
  padding: 5px;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
  margin-left: 60px;
  position: relative; /* 말풍선 꼬리 위치 조정을 위해 필요 */
  display: inline-block; /* 내용 크기에 맞게 조절 */
}

.text-right::after {
  content: "";
  position: absolute;
  top: 50%;
  right: -10px; /* 오른쪽 꼬리 위치 */
  transform: translateY(-50%);
  border-width: 8px;
  border-style: solid;
  border-color: transparent transparent transparent white; /* 삼각형 만들기 */
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

.target {
  position: relative;
  z-index: 200;
  background: white;
  padding: 10px;
  border-radius: 10px;
}

</style>
