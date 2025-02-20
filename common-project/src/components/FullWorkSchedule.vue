<!-- FullWorkSchedule.vue -->
<template>
  <div class="full-work-schedule">
    <div v-if="store.people.length === 0" class="empty-state">
      <p>현재 등록된 일정이 없습니다.</p>
      <p>{{ debuggedValue }}</p>
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
import { ref, onMounted, nextTick,computed } from 'vue'
import { useApiStore } from '@/stores/apiRequest'

const store = useApiStore()

onMounted(async () => {
  // 1) 캘린더 데이터 생성
  store.generateCalendar();
  console.log("📢 generateCalendar 호출 후:", store.calendar);
  console.log("달력인데 달력이야 달력이야:",store.calendar)
  // 2) DOM 업데이트 후 처리
  await nextTick();
  // setTimeout으로 100ms 지연 후 실행
})
</script>

<style scoped>
.full-work-schedule {
  /* 자식 컴포넌트도 flex 컨테이너로 */
  display: flex;
  flex-direction: column;
  flex: 1; 
  overflow-y:auto;
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
