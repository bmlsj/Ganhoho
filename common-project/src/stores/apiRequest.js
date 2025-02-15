import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';

export const useApiStore = defineStore('api', () => {
  const people = ref([]);
  const calendar = ref([]);
  const currentYear = ref(null); // API에서 받아온 년도
  const currentMonth = ref(null); // API에서 받아온 월
  const API_URL = import.meta.env.VITE_API_URL || 'https://i12d209.p.ssafy.io'; // 기본값 추가
  const medicineList = ref([]);
  const medicineDetail = ref({}); // 의약품 상세 정보 저장
  const isDataLoaded = ref(false);
  const token = ref(localStorage.getItem("token") || null);
  const refreshToken = ref(localStorage.getItem("refresh_token") || null);

  const setToken = (access_token, refresh_token) => {
    token.value = access_token
    refreshToken.value = refresh_token
    localStorage.setItem("token", access_token)
    localStorage.setItem("refresh_token", refresh_token)
  }
  
  token.value = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OSwiaWF0IjoxNzM5NTM4OTMyLCJleHAiOjE3Mzk2MjUzMzJ9.pFiUHZWVb3zKlhMgc0vsDTZcS4aVDFg9BPEtD39MHqc";
const fetchData = async () => { //전체 근무 페이지 ocr 데이터 받아오기.
  try {

    if(isDataLoaded.value){
      console.log("📢 기존 데이터 있음 → GET 요청 생략");
      console.log("다운됨?",isDataLoaded.value)
      return;
    }
     const response = await axios.get(`${API_URL}/api/schedules/ocr`, {
      //const response = await axios.get(`http://localhost:5000/schedules`, { //목데이터 api
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

        const typeMapping = { OF: "Off", E: "Eve", D: "Day", N: "Nig" };
        people.value = responseData.map((person) => ({
          name: person.name,
          schedule: person.scheduleData.reduce((acc, day) => {
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
      
      // store 리셋 및 데이터 새로고침(ocr 새로고침)
      people.value = [];
      calendar.value = [];
      currentYear.value = null;
      currentMonth.value = null;
      isDataLoaded.value = false;
      
        
      // // ✅ 데이터 로드 완료 상태 저장 (GET 요청 이후)
      // isDataLoaded.value = true;
      // 새 데이터 불러오기
      // ✅ POST 요청 성공 후 GET 요청 실행
      await fetchData();
    } else {
      console.error('업로드 실패:', response.data);
      alert('업로드 실패');
    }
  } catch (error) {
    console.error('API 요청 오류:', error);
    alert('서버 오류로 업로드 실패');
  }
};


  const fetchMedicineList = async (itemName) => {
    try {


      console.log("검색 요청:", itemName);
      console.log("토큰:", token.value);
      console.log("API URL:", API_URL);

      const response = await axios.get(`${API_URL}/api/medicines/search`, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        params: {
          itemName: itemName.trim()
        },
        timeout: 5000
      });

      if (response.status === 200 && response.data) {
        console.log("📢 API 응답 원본 데이터:", response.data);
        
        // items 배열에서 데이터 추출
        const medicineData = response.data.items || [];
        
        medicineList.value = medicineData.map(item => ({
          id: item.ITEM_SEQ || '',
          name: item.ITEM_NAME || '',
          content: item.STORAGE_METHOD || '',
          expiry: item.VALID_TERM || '',
          imageSrc: item.CHART || '',
          company: item.ENTP_NAME || '', // 제약회사 정보 추가
          type: item.ETC_OTC_CODE || '', // 의약품 분류 추가
        }));
        
        console.log("최종 의약품 목록:", medicineList.value);
        return medicineList.value.length > 0;
      }
      return false;
    } catch (error) {
      console.error('의약품 검색 오류:', error);
      if (error.response) {
        console.error('서버 에러:', {
          status: error.response.status,
          data: error.response.data,
          headers: error.response.headers,
          config: error.response.config
        });
      }
      medicineList.value = [];
      return false;
    }
  };

  // ✅ 의약품 상세 정보 API 호출 추가
  const fetchMedicineDetail = async (medicineId) => {
    try {

      console.log("📢 요청할 약 ID:", medicineId);
      console.log("테스트 토큰:", token.value);
      console.log("API URL:", API_URL);

      const response = await axios.get(`${API_URL}/api/medicines/${medicineId}`, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });

      if (response.status === 200) {
        console.log('의약품 상세 정보 원본:', response.data);
        
        // 응답 데이터가 있는지 확인
        if (!response.data) {
          console.error('의약품 상세 정보가 없습니다');
          return false;
        }

        // 원본 데이터를 그대로 저장
        medicineDetail.value = response.data;
        
        console.log('저장된 의약품 상세 정보:', medicineDetail.value);
        return true;
      }
      return false;
    } catch (error) {
      console.error('의약품 상세 정보 요청 오류:', error);
      if (error.response) {
        console.error('서버 에러:', {
          status: error.response.status,
          data: error.response.data,
          headers: error.response.headers,
          config: error.response.config
        });
      }
      return false;
    }
  };
  
  const acceptInvitation = async () => { //수정 필요.. 
    try {

      const response = await axios.post(
        `${API_URL}/api/schedules/ocr`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token.value}`,
          },
        }
      );

      if (response.status === 200) {
        console.log("✅ 초대 수락 성공:", response.data);
        return true;
      } else {
        console.error("초대 수락 실패:", response.data);
        return false;
      }
    } catch (error) {
      console.error("API 요청 오류:", error);
      return false;
    }
  };
  const uploadMedicineImage = async (file) => {
    if (!file) {
      console.error("🚨 업로드할 파일이 없습니다.");
      alert("이미지를 선택해 주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("imageFile", file); // ✅ MultipartFile로 전송

    try {
      const response = await axios.post(`${API_URL}/api/medicines/upload-image`, formData, {
        headers: {
          Authorization: `Bearer ${token.value}`, // ✅ JWT 인증 토큰 추가
          "Content-Type": "multipart/form-data", // ✅ 멀티파트 설정
        },
      });

      if (response.status === 200) {
        console.log("✅ 이미지 업로드 성공:", response.data);
        alert("이미지 업로드 성공!");

        // 📌 상세 페이지로 이동
        const medicineId = response.data.medicineId;
        window.location.href = `/pill-detail/${medicineId}`;
      }
    } catch (error) {
      console.error("🚨 이미지 업로드 오류:", error);

      // 📌 상태 코드별 처리
      if (error.response) {
        const { status, message } = error.response.data;

        if (status === 400) {
          alert("🚨 잘못된 이미지 형식입니다. (INVALID_IMAGE_FORMAT)");
        } else if (status === 401) {
          alert("🚨 인증되지 않은 사용자입니다. 로그인 후 이용해 주세요. (UNAUTHORIZED)");
        } else if (status === 404) {
          alert("🚨 약 정보를 찾을 수 없습니다. (NOT_FOUND)");
        } else {
          alert(`🚨 오류 발생: ${message}`);
        }
      } else {
        alert("🚨 네트워크 오류가 발생했습니다.")
      }
    }
  }
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
    acceptInvitation,
    uploadMedicineImage,
    setToken,
    token,
    refreshToken
  }
}, { // ✅ Pinia Persist 추가 (새로고침해도 데이터 유지)
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'schedule-store',
        storage: localStorage,
        paths: ['people', 'currentYear', 'currentMonth', 'isDataLoaded','token'] // 유지할꺼 있으믄 여기에 넣으십셔 
      }
    ]
  }
});
