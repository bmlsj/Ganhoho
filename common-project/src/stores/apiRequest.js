import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useApiStore = defineStore('api', () => {
  const people = ref([])
  const calendar = ref([])
  const currentYear = ref(null) // API에서 받아온 년도
  const currentMonth = ref(null) // API에서 받아온 월
  const API_URL = "" // 실제 API URL 설정 필요 그런데 이거 .env에서 관리하는 거로 기억하는데 어떻게 하더라..
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

  const sendImageToAPI = async (file) => {
    const formData = new FormData()
    formData.append('ocrImg', file) // API에서 요구하는 `ocrImg` 필드로 추가

    try {
      const response = await axios.post(`${API_URL}/api/schedules/ocr`, formData, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'multipart/form-data'
        }
      })

      if (response.status === 200) {
        console.log("✅ 이미지 업로드 성공:", response.data)
        alert("이미지 업로드 성공!")
        await fetchData() //API 요청 후 최신 데이터 반영
      } else {
        console.error("업로드 실패:", response.data)
        alert("업로드 실패")
      }
    } catch (error) {
      console.error("API 요청 오류:", error)
      alert("서버 오류로 업로드 실패")
    }
  }
  return { people, calendar, currentYear, currentMonth, fetchData, generateCalendar,sendImageToAPI, token }
})
