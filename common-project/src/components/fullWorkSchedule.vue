<template>
  <div class="calendar-wrapper">
    <div class="calendar-header">
      <div class="header-row">
        <div class="year-month">
          {{ store.currentYear || defaultYear }}년 {{ store.currentMonth || defaultMonth }}월
        </div>
        <button ref="addButton" class="add-button" @click="openGallery">+</button>
      </div>
      <div class="weekdays">
        <span v-for="(day, index) in [''].concat(weekDays)" :key="index" :class="{ sunday: index === 1 }">
          {{ day }}
        </span>
      </div>
    </div>

    <!-- 튜토리얼 컴포넌트 -->
    <TutorialComponent
      v-if="isFirstVisit"
      :addButton="addButton.value"
      @tutorialFinished="isFirstVisit = false"
    />

    <!-- 데이터가 없을 경우 -->
    <div v-else-if="store.people.length === 0" class="empty-state">
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

    <!-- 모달 -->
    <GalleryModal
      v-if="isModalVisible"
      :is-visible="isModalVisible"
      :images="selectedImages"
      @close="isModalVisible = false"
    />

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
import { ref, onMounted, nextTick } from 'vue'
import { useApiStore } from '@/stores/apiRequest'
import GalleryModal from '@/components/GalleryModal.vue'
import TutorialComponent from '@/components/TutorialComponent.vue'

const store = useApiStore()

const defaultYear = new Date().getFullYear() // 한국 기준 현재 연도
const defaultMonth = new Date().getMonth() + 1 // 한국 기준 현재 월
const weekDays = ['일', '월', '화', '수', '목', '금', '토']

const addButton = ref(null)
const isModalVisible = ref(false)
const selectedImages = ref([])
const isFirstVisit = ref(!localStorage.getItem('visitedFullWorkSchedule')) // 첫 방문 여부
const galleryInput = ref(null)

  const openGallery = () => {
    galleryInput.value.click() // 파일 선택 대화 상자 열기
    
    // 이벤트 리스너가 중복 등록되지 않도록 기존 리스너 제거
    galleryInput.value.removeEventListener('change', handleFileSelection)
    
    // 새로운 이벤트 리스너 등록
    galleryInput.value.addEventListener('change', handleFileSelection)
  }

// 파일 선택 처리 함수
const handleFileSelection = (event) => {
  const files = event.target.files
  if (files.length > 0) {
    const images = Array.from(files).map(file => URL.createObjectURL(file))
    selectedImages.value = images
    isModalVisible.value = true
  }
}

const resetTutorial = () => {
  localStorage.setItem('visitedFullWorkSchedule', 'false')
  isFirstVisit.value = true
}

onMounted(async () => {
  await store.fetchData() // 데이터 로드
  await nextTick() // DOM 업데이트
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
  margin-top: 20px;
}
</style>
