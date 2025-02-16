<!-- FullWorkSchedule.vue -->
<template>
  <div class="full-work-schedule">
    <div class="weekdays">
      <span
        v-for="(day, index) in [''].concat(weekDays)"
        :key="index"
        :class="{ sunday: index === 1 }"
      >
        {{ day }}
      </span>
    </div>
    <div v-if="store.people.length === 0" class="empty-state">
      <p>현재 등록된 일정이 없습니다.</p>
    </div>
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
const weekDays = ['일', '월', '화', '수', '목', '금', '토']
const calendarBodyRef = ref(null)

onMounted(async () => {
  await nextTick()
  store.generateCalendar()
  await nextTick()
  // 오늘이 포함된 주로 부드럽게 스크롤
  const today = new Date().getDate()
  let targetWeekIndex = 0
  store.calendar.forEach((week, index) => {
    if (week.includes(today)) {
      targetWeekIndex = index
    }
  })
  if (calendarBodyRef.value) {
    const weekElements = calendarBodyRef.value.querySelectorAll('.week')
    if (weekElements.length > targetWeekIndex) {
      weekElements[targetWeekIndex].scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }
})
</script>

<style scoped>
.full-work-schedule .weekdays {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  align-items: center;
  justify-items: center;
  column-gap: 2px;
  text-align: center;
}
.full-work-schedule .sunday {
  color: red;
}
.full-work-schedule .calendar-body {
  display: flex;
  flex-direction: column;
  max-height: 70vh;
  overflow-y: auto;
}
.full-work-schedule .week {
  margin-bottom: 16px;
}
.full-work-schedule .dates {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  column-gap: 2px;
}
.full-work-schedule .date {
  text-align: center;
  font-weight: bold;
  font-size: 13px;
}
.full-work-schedule .person-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.full-work-schedule .person-name {
  width: 55px;
  height: 23px;
  flex-shrink: 0;
  text-align: center;
  line-height: 23px;
  font-weight: bold;
  font-size: 13px;
}
.full-work-schedule .person-schedule {
  flex: 1;
  display: flex;
}
.full-work-schedule .schedule-box {
  flex: 1;
  text-align: center;
  padding: 4px 6px;
  border: 1px solid #ccc;
  margin: 0 2px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1;
}
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
.full-work-schedule .empty-state {
  text-align: center;
  font-size: 16px;
  color: gray;
  margin-top: 190px;
}
</style>
