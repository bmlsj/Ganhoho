<!--WeeklySchedule.vue-->
<template>
  <div class="weekly-schedule">
    <div class="header-row">
    </div>
    <div v-if="store.people.length === 0" class="empty-state">
      <p>현재 등록된 일정이 없습니다.</p>
    </div>
    <div v-else class="calendar-body">
      <div class="week">
        <div class="dates">
          <div v-for="(day, dayIndex) in currentWeek" :key="dayIndex" class="date">
            {{ day || '' }}
          </div>
        </div>
        <div v-for="person in store.people" :key="person.name" class="person-row">
          <div class="person-name">{{ person.name }}</div>
          <div class="person-schedule">
            <div
              v-for="(day, dayIndex) in currentWeek.slice(1, 8)"
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
import { ref, computed, onMounted } from 'vue'
import { useApiStore } from '@/stores/apiRequest'

const store = useApiStore()
const defaultYear = new Date().getFullYear()
const defaultMonth = new Date().getMonth() + 1
const weekDays = ['일', '월', '화', '수', '목', '금', '토'] // (더 이상 표시하지 않더라도 내부 계산에 사용 가능)

// 현재 주 인덱스 (자동 설정)
const currentWeekIndex = ref(0)

// 현재 월의 모든 주를 계산하여 배열로 반환
const weeks = computed(() => {
  const month = store.currentMonth || defaultMonth
  const year = store.currentYear || defaultYear
  const firstDayOfMonth = new Date(year, month - 1, 1).getDay()
  const lastDateOfMonth = new Date(year, month, 0).getDate()
  let weeksArr = []
  let currentWeek = [null, ...new Array(7).fill(null)]
  let dayCounter = 1

  // 첫 주 채우기 (빈 칸 포함)
  for (let i = firstDayOfMonth; i < 7; i++) {
    currentWeek[i + 1] = dayCounter++
  }
  weeksArr.push([...currentWeek])

  // 나머지 주 계산
  while (dayCounter <= lastDateOfMonth) {
    currentWeek = [null, ...new Array(7).fill(null)]
    for (let i = 1; i <= 7 && dayCounter <= lastDateOfMonth; i++) {
      currentWeek[i] = dayCounter++
    }
    weeksArr.push([...currentWeek])
  }
  return weeksArr
})

// 현재 선택된 주
const currentWeek = computed(() => weeks.value[currentWeekIndex.value] || [])

// 현재 주의 날짜 범위 (예: "15일 - 21일")
const getCurrentWeekRange = computed(() => {
  const firstDay = currentWeek.value.find(day => day !== null) || 1
  const lastDay = [...currentWeek.value].reverse().find(day => day !== null) || firstDay
  return `${firstDay}일 - ${lastDay}일`
})


// 마운트 시, 오늘 날짜가 포함된 주를 자동 선택
onMounted(() => {
  const today = new Date().getDate()
  let targetIndex = 0
  weeks.value.forEach((week, index) => {
    if (week.includes(today)) {
      targetIndex = index
    }
  })
  currentWeekIndex.value = targetIndex
})
</script>

<style scoped>
.weekly-schedule {
  display: flex;
  flex-direction: column;
  height:auto; /* 부모에서 넘겨준 공간 전부 사용 */
  overflow-y:visible;
}

.weekly-schedule .header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.weekly-schedule .nav-button {
  background-color: #f1f1f1;
  border: none;
  padding: 6px 10px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 16px;
}

.weekly-schedule .year-month {
  font-size: 18px;
  font-weight: bold;
}


/* 캘린더 영역: 남은 공간을 모두 사용하고 스크롤 가능하게 */
.weekly-schedule .calendar-body {
  height: auto;
  overflow-y: visible;
}

.weekly-schedule .dates {
  display: grid;
  grid-template-columns: 55px repeat(7, 1fr);
  column-gap: 2px;
}

.weekly-schedule .date {
  text-align: center;
  font-weight: bold;
  font-size: 13px;
}

.weekly-schedule .person-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.weekly-schedule .person-name {
  width: 55px;
  height: 23px;
  flex-shrink: 0;
  text-align: center;
  line-height: 23px;
  font-weight: bold;
  font-size: 13px;
}

.weekly-schedule .person-schedule {
  flex: 1;
  display: flex;
}

.weekly-schedule .schedule-box {
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

.schedule-box.day {
  background-color: #fff8bf;
}

.schedule-box.eve {
  background-color: #e4c7f1;
}

.schedule-box.off {
  background-color: #fcd6c8;
}

.weekly-schedule .empty-state {
  text-align: center;
  font-size: 16px;
  color: gray;
  margin-top: 190px;
}
</style>
