<!-- FullWorkSchedule.vue -->
<template>
  <div class="full-work-schedule">
    <div v-if="store.people.length === 0" class="empty-state">
      <p>현재 등록된 일정이 없습니다.</p>
    </div>
    <!-- 자동 스크롤 대상이 되는 캘린더 영역 -->
    <div v-else class="calendar-body" ref="calendarBodyRef">
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
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useApiStore } from '@/stores/apiRequest'

const store = useApiStore()

// 스크롤 대상 요소
const calendarBodyRef = ref(null)

onMounted(async () => {
  // 1) 캘린더 데이터 생성
  store.generateCalendar()

  // 2) DOM 업데이트 후 처리
  await nextTick()
  const today = new Date().getDate()
  let targetWeekIndex = 0
  store.calendar.forEach((week, index) => {
    if (week.includes(today)) {
      targetWeekIndex = index
    }
  })

  // 3) 헤더 높이 동적 보정 후 스크롤 이동
  if (calendarBodyRef.value) {
    const weekElements = calendarBodyRef.value.querySelectorAll('.week')
    if (weekElements.length > targetWeekIndex) {
      const targetElement = weekElements[targetWeekIndex]
      // 헤더 요소의 실제 높이 읽기 (예: .header)
      const headerEl = document.querySelector('.header')
      const headerHeight = headerEl ? headerEl.offsetHeight : 0
      // 타겟 요소의 offsetTop에서 헤더 높이만큼 빼줌
      const scrollPosition = targetElement.offsetTop - headerHeight
      calendarBodyRef.value.scrollTo({
        top: scrollPosition,
        behavior: 'smooth'
      })
    }
  }
})
</script>

<style scoped>
.full-work-schedule {
  /* 자식 컴포넌트도 flex 컨테이너로 */
  display: flex;
  flex-direction: column;
  flex: 1; /* 부모에서 넘겨준 공간 전부 사용 */
  overflow: hidden; /* 내부에서 스크롤 처리 */
}


.calendar-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.week {
  margin-bottom: 16px;
}

.dates {
  display: grid;
  /* 55px은 person-name 열과 동일하게 설정 */
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

/* 일정별 색상 유지 */
.schedule-box.nig {
  background-color: #DDD4cD;
}
.schedule-box.day {
  background-color: #fff8bf;
}
.schedule-box.eve {
  background-color: #e4c7f1;
}
.schedule-box.off {
  background-color: #fcd6c8;
}

.empty-state {
  text-align: center;
  font-size: 16px;
  color: gray;
  margin-top: 190px;
}
</style>
