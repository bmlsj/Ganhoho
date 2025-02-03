import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useApiStore = defineStore('api', () => {
  const people = ref([])
  const calendar = ref([])
  const currentYear = ref(null) // API에서 받아온 년도
  const currentMonth = ref(null) // API에서 받아온 월
  const API_URL = "" // 실제 API URL 설정 필요
  const token = ref(null) // JWT 토큰 저장

  const fetchData = async () => {
    try {
      const response = await axios.get(`${API_URL}/api/schedules/ocr`, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })

      if (response.status === 200) {
        const responseData = response.data.data

        if (responseData.length > 0) {
          const firstPerson = responseData[0] // 첫 번째 사용자의 데이터 사용
          currentYear.value = firstPerson.year
          currentMonth.value = firstPerson.month
          people.value = responseData.map(person => ({
            name: person.name,
            schedule: person.schedule.reduce((acc, day) => {
              acc[day.day] = day.type
              return acc
            }, {})
          }))

          generateCalendar() // API에서 받아온 날짜 기준으로 캘린더 생성
        }
      }
    } catch (error) {
      console.error("데이터 가져오기 실패:", error)
    }
  }

  const generateCalendar = () => {
    if (!currentYear.value || !currentMonth.value) return

    let firstDay = new Date(currentYear.value, currentMonth.value - 1, 1).getDay()
    const lastDate = new Date(currentYear.value, currentMonth.value, 0).getDate()

    let calendarData = []
    let week = [null, ...new Array(7).fill(null)]

    for (let i = 1; i <= firstDay; i++) {
      week[i] = null
    }

    for (let day = 1; day <= lastDate; day++) {
      week[(firstDay % 7) + 1] = day
      firstDay++

      if (firstDay % 7 === 0 || day === lastDate) {
        calendarData.push([...week])
        week = [null, ...new Array(7).fill(null)]
      }
    }

    calendar.value = calendarData
  }

  return { people, calendar, currentYear, currentMonth, fetchData, generateCalendar, token }
})
