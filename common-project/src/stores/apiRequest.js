import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';

export const useApiStore = defineStore('api', () => {
  const people = ref([]);
  const calendar = ref([]);
  const currentYear = ref(null); // API에서 받아온 년도
  const currentMonth = ref(null); // API에서 받아온 월
  const API_URL = ""; // .env에서 관리
  const medicineList = ref([]);
  const medicineDetail = ref({}); // 의약품 상세 정보 저장
  const isDataLoaded = ref(false);
  const token=ref(null);
  if (window.AndroidInterface) {
    token.value = window.AndroidInterface.getToken()
    localStorage.setItem("token",token)
  }
  
  const fetchData = async () => { //전체 근무 페이지 ocr 데이터 받아오기.
    try {
      if(isDataLoaded.value){
        console.log("📢 기존 데이터 있음 → GET 요청 생략");
        console.log("다운됨?",isDataLoaded.value)
        return;
      }
      const response = await axios.get(`${API_URL}/api/schedules/ocr`, {
        //const response = await axios.get(`http://localhost:5000/schedules`, {
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      });

      if (response.status === 200) {
        console.log("📢 API 응답 데이터:", response.data);
        const responseData = response.data;
        console.log("📢 API 응답 데이터 개수:", responseData.length);

        if (responseData.length > 0) {
          const firstPerson = responseData[0]; // 첫 번째 사용자의 데이터 사용
          currentYear.value = firstPerson.year;
          currentMonth.value = firstPerson.month;

          const typeMapping = { O: "Off", E: "Eve", D: "Day", N: "Nig" };
          people.value = responseData.map((person) => ({
            name: person.name,
            schedule: person.schedule.reduce((acc, day) => {
              acc[day.day] = typeMapping[day.type] || day.type
              return acc;
            }, {}),
          }));
          
          isDataLoaded.value = true; // ✅ 데이터가 로드되었음을 표시
          generateCalendar();
          console.log("📢 변환된 일정 데이터:", people.value);
          generateCalendar(); // API에서 받아온 날짜 기준으로 캘린더 생성
        }
      }
    } catch (error) {
      console.error('데이터 가져오기 실패:', error);
    }
  };

  const generateCalendar = () => { //전체 근무 페이지 캘린더 만들기기
    if (!currentYear.value || !currentMonth.value) return;

    let firstDay = new Date(currentYear.value, currentMonth.value - 1, 1).getDay();
    const lastDate = new Date(currentYear.value, currentMonth.value, 0).getDate();

    let calendarData = [];
    let week = [null, ...new Array(7).fill(null)];

    for (let i = 1; i <= firstDay; i++) {
      week[i] = null;
    }

    for (let day = 1; day <= lastDate; day++) {
      week[(firstDay % 7) + 1] = day;
      firstDay++;

      if (firstDay % 7 === 0 || day === lastDate) {
        calendarData.push([...week]);
        week = [null, ...new Array(7).fill(null)];
      }
    }

    calendar.value = calendarData;
  };

  const sendImageToAPI = async (file) => {
    const formData = new FormData();
    formData.append('ocrImg', file);
  
    try {
      const response = await axios.post(`${API_URL}/api/schedules/ocr`, formData, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'multipart/form-data',
        },
      });
  
      if (response.status === 200) {
        console.log('✅ 이미지 업로드 성공:', response.data);
        alert('이미지 업로드 성공!');
  
        // ✅ POST 요청 성공 후 GET 요청 실행
        await fetchData();
  
        // ✅ 데이터 로드 완료 상태 저장 (GET 요청 이후)
        isDataLoaded.value = true;
      } else {
        console.error('업로드 실패:', response.data);
        alert('업로드 실패');
      }
    } catch (error) {
      console.error('API 요청 오류:', error);
      alert('서버 오류로 업로드 실패');
    }
  };

  const fetchMedicineList = async (keyword) => { //약 검색 받아오기기
    try {
      const response = await axios.get(`${API_URL}/api/medicines/search`, {
        //const response = await axios.get(`http://localhost:5000/medicines`, {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
      params: { keyword },
      });

      if (response.status === 200) {
        console.log("📢 API 응답 데이터:", response.data);
        medicineList.value = response.data.map((item) => ({
          id: item.medicineId,
          name: item.medicineName,
          content: item.basicInfo.ingredient, // 분류 코드 제거
          expiry: item.basicInfo.storage.duration, // API 응답에 유효기간 필드가 없음
          imageSrc: item.imageUrl, // 기본 이미지
        }));
        return true;
      }
      return false;
    } catch (error) {
      console.error('의약품 검색 오류:', error);
      return false;
    }
  };

  // ✅ 의약품 상세 정보 API 호출 추가
  const fetchMedicineDetail = async (medicineId) => { //약 상세정보 받아오기.
    try {
      console.log("📢 요청할 약 ID:", medicineId); // ✅ 콘솔에서 확인
      const formattedId = String(medicineId); // 혹시 숫자가 아니라 문자열이면 변환
      console.log("📢 변환된 약 ID:", formattedId);
      const response = await axios.get(`${API_URL}/api/medicines/${medicineId}`, {
        // const response = await axios.get(`http://localhost:5000/medicines?medicineId=${medicineId}`, {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
      });

      if (response.status === 200) {
        medicineDetail.value = response.data[0];
        console.log('의약품 상세 정보:', medicineDetail.value);
        return true;
      } else {
        console.error('의약품 상세 정보 요청 실패:', response.data);
        return false;
      }
    } catch (error) {
      console.error('의약품 상세 정보 요청 오류:', error);
      return false; 
    }
  };

  return {
    people,
    calendar,
    currentYear,
    currentMonth,
    medicineList,
    medicineDetail,
    isDataLoaded,
    fetchData,
    generateCalendar,
    sendImageToAPI,
    fetchMedicineList,
    fetchMedicineDetail,
    token,
  }
},{ strict: false });// ✅ Pinia Persist 추가 (새로고침해도 데이터 유지)
