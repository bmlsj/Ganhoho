import { defineStore } from 'pinia';
import { ref,watch } from 'vue';
import axios from 'axios';
// 1) 방금 만든 마스킹 함수 가져오기
import { maskURL, maskToken } from '@/utils/mask.js';
import { useLoadingStore } from '@/stores/loadingStore';

// axios 인터셉터 등록
axios.interceptors.request.use(
  (config) => {
    const loadingStore = useLoadingStore();
    loadingStore.startLoading();
    return config;
  },
  (error) => {
    const loadingStore = useLoadingStore();
    loadingStore.stopLoading();
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  (response) => {
    const loadingStore = useLoadingStore();
    loadingStore.stopLoading();
    return response;
  },
  (error) => {
    const loadingStore = useLoadingStore();
    loadingStore.stopLoading();
    return Promise.reject(error);
  }
)

export const useApiStore = defineStore('api', () => {
  const people = ref([]);
  const calendar = ref([]);
  const currentYear = ref(null);
  const currentMonth = ref(null);

  // .env 파일에 정의된 VITE_API_URL 사용
  const API_URL = import.meta.env.VITE_API_URL || 'https://i12d209.p.ssafy.io';
  const medicineList = ref([]);
  const medicineDetail = ref({});
  const isDataLoaded = ref(false);

  const medicineId = ref(null);

  const userId = ref(localStorage.getItem("user_id") || null);
  const token = ref(localStorage.getItem("token") || null);

  //token.value ="eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OCwiaWF0IjoxNzM5NjgzMjYzLCJleHAiOjE3Mzk3Njk2NjN9.5KmPHuxwU_GMkUXFENU3EU_FfHRHU6FeGM04kse40Mc"
  // 토큰 변경 감지를 위한 watch 추가
  watch(token, async (newToken, oldToken) => {
    if (newToken !== oldToken) {
      console.log("토큰 변경 감지: 스케줄 데이터 초기화");
      resetScheduleData();
    }
  });
  
  // 스케줄 데이터만 초기화하는 함수
  const resetScheduleData = () => {
    people.value = [];
    calendar.value = [];
    currentYear.value = null;
    currentMonth.value = null;
    isDataLoaded.value = false;
    
    // localStorage의 스케줄 관련 캐시 데이터만 삭제
    localStorage.removeItem('schedule-store');
  };

  const setToken = (user_id, access_token) => {
    userId.value = user_id;
    token.value = access_token;
    localStorage.setItem("user_id", user_id);
    localStorage.setItem("token", access_token);
  }

  // (예시) 토큰 디버그 로그 -> 마스킹 처리
  // console.log("현재 토큰:", maskToken(token.value));
  // --- 추가: updateToken 함수 (토큰 비교 후 fetchData 호출) ---
  const updateToken = (newUserId, newAccessToken) => {
    const storedToken = localStorage.getItem("token");
    if (newAccessToken !== storedToken) {
      console.log("새로운 토큰이 감지되었습니다. 상태 초기화 후 데이터를 새로 불러옵니다.");
      // 기존 persist 데이터 삭제 (localStorage와 sessionStorage 모두)
      localStorage.removeItem('schedule-store-default');
      localStorage.removeItem(`schedule-store-${userId.value}`);
      sessionStorage.removeItem('schedule-store-default');
      sessionStorage.removeItem(`schedule-store-${userId.value}`);
      
      // 상태 초기화
      resetScheduleData();
  
      token.value = newAccessToken;
      userId.value = newUserId;
      localStorage.setItem("user_id", newUserId);
      localStorage.setItem("token", newAccessToken);
  
      // 필요하다면 store의 $reset() 호출
      useApiStore().$reset();
  
      fetchData();
    } else {
      console.log("토큰이 변경되지 않았습니다.");
      if (!isDataLoaded.value) {
        fetchData();
      }
    }
  };
  // --- 끝 ---
  const fetchData = async () => {
    try {
      if (isDataLoaded.value) {
        console.log("📢 기존 데이터 있음 → GET 요청 생략");
        return;
      }
      // 마스킹된 URL만 로그에 찍기
      console.log("🔍 API 요청 URL:", maskURL(`${API_URL}/api/schedules/ocr`));
      console.log("내 토큰을 보자:",token.value)
      const response = await axios.get(`${API_URL}/api/schedules/ocr`, {
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      });

      if (response.status === 200) {
        console.log("📢 API 응답 데이터:", response.data);
        const responseData = response.data;

        if (responseData.length > 0) {
          const firstPerson = responseData[0];
          currentYear.value = firstPerson.year;
          currentMonth.value = firstPerson.month;

          const typeMapping = { OF: "Off", E: "Eve", D: "Day", N: "Nig" }
          people.value = responseData.map((person) => ({
            name: person.name,
            schedule: person.scheduleData.reduce((acc, day) => {
              acc[day.day] = typeMapping[day.type] || day.type
              return acc
            }, {}),
          }))
          console.log("피!!!플!!!!:",people.value)
          isDataLoaded.value = true;
          generateCalendar();
        }
      }
    } catch (error) {
      console.error('데이터 가져오기 실패:', error);
    }
  };

  const generateCalendar = () => {
    if (!currentYear.value || !currentMonth.value) {
      console.log("currentYear나 currentMonth가 설정되어 있지 않습니다:", currentYear.value, currentMonth.value);
      return;
    }
  
    console.log("달력 생성 시작 - 연도:", currentYear.value, "월:", currentMonth.value);
  
    // 1일의 요일(0: 일요일 ~ 6: 토요일)과 마지막 날짜 계산
    let firstDay = new Date(currentYear.value, currentMonth.value - 1, 1).getDay();
    const lastDate = new Date(currentYear.value, currentMonth.value, 0).getDate();
    console.log("첫번째 날의 요일 인덱스:", firstDay);
    console.log("해당 월의 마지막 날짜:", lastDate);
  
    let calendarData = [];
    // 인덱스를 1부터 사용하기 위해 첫 번째 요소를 null로 시작
    let week = [null, ...new Array(7).fill(null)];
    console.log("초기 week 배열:", week);
  
    // 첫 주의 시작 전 빈 칸 설정 (이미 null로 채워져 있지만, 디버깅용으로 반복문 기록)
    for (let i = 1; i <= firstDay; i++) {
      week[i] = null;
    }
    console.log("빈 칸 설정 후 week 배열:", week);
  
    // 날짜를 week 배열에 채워 넣기
    for (let day = 1; day <= lastDate; day++) {
      // 현재 요일 위치: (firstDay % 7) + 1 인덱스에 할당
      const index = (firstDay % 7) + 1;
      week[index] = day;
      console.log(`날짜 ${day}는 인덱스 ${index}에 할당됨 -> week:`, week);
      firstDay++;
  
      // 한 주가 끝났거나 마지막 날짜인 경우 week 배열을 calendarData에 저장
      if (firstDay % 7 === 0 || day === lastDate) {
        console.log(
          `한 주가 완료되었거나 마지막 날짜에 도달 (firstDay: ${firstDay}, day: ${day}). week 배열 저장:`,
          week
        );
        calendarData.push([...week]); // 현재 week 배열 복사해서 추가
        week = [null, ...new Array(7).fill(null)];
        console.log("다음 주를 위해 week 배열 초기화:", week);
      }
    }
  
    calendar.value = calendarData;
    console.log("최종 생성된 달력 데이터:", calendar.value);
  };
  

  const sendImageToAPI = async (file) => {
    const formData = new FormData();
    formData.append('ocrImg', file);

    // 마스킹된 URL 로그
    console.log("🔍 API 요청 URL:", maskURL(`${API_URL}/api/schedules/ocr`));
    console.log("내 토큰을 보자:",token.value)
    try {
      const response = await axios.post(`${API_URL}/api/schedules/ocr`, formData, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.status === 200) {
        console.log('✅ 이미지 업로드 성공:', response.data);
        //alert('이미지 업로드 성공!');

        // store 리셋
        people.value = [];
        calendar.value = [];
        currentYear.value = null;
        currentMonth.value = null;
        isDataLoaded.value = false;

        // 새로 fetch
        await fetchData();
      } else {
        console.error('업로드 실패:', response.data);
        //alert('업로드 실패');
      }
    } catch (error) {
      console.error('API 요청 오류:', error);
      //alert('서버 오류로 업로드 실패');
    }
  };

  const fetchMedicineList = async (itemName) => {
    try {
      // 검색 로그
      console.log("검색 요청:", itemName);
      // 마스킹된 토큰 로그
      console.log("토큰:", maskToken(token.value));
      // 마스킹된 URL 로그
      console.log("API URL:", maskURL(API_URL));

      const response = await axios.get(`${API_URL}/api/medicines/search`, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        params: {
          itemName: itemName.trim()
        },
        timeout: 15000
      });

      if (response.status === 200 && response.data) {
        console.log("📢 API 응답 원본 데이터:", response.data);

        const medicineData = response.data.items || [];
        medicineList.value = medicineData.map(item => ({
          id: item.ITEM_SEQ || '',
          name: item.ITEM_NAME || '',
          content: item.STORAGE_METHOD || '',
          expiry: item.VALID_TERM || '',
          imageSrc: item.ITEM_IMAGE || '',
          company: item.ENTP_NAME || '',
          type: item.ETC_OTC_CODE || '',
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
          // URL도 마스킹 가능
          config: {
            ...error.response.config,
            url: maskURL(error.response.config.url)
          }
        });
      }
      medicineList.value = [];
      return false;
    }
  };

  const fetchMedicineDetail = async (medicineId) => {
    try {
      console.log("📢 요청할 약 ID:", medicineId);
      console.log("테스트 토큰:", maskToken(token.value));
      console.log("API URL:", maskURL(API_URL));

      const response = await axios.get(`${API_URL}/api/medicines/${medicineId}`, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });

      if (response.status === 200) {
        console.log('의약품 상세 정보 원본:', response.data);
        if (!response.data) {
          console.error('의약품 상세 정보가 없습니다');
          return false;
        }
        medicineDetail.value = response.data;
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
          config: {
            ...error.response.config,
            url: maskURL(error.response.config.url)
          }
        });
      }
      return false;
    }
  };

  const uploadMedicineImage = async (file) => {
    if (!file) {
      console.error("🚨 업로드할 파일이 없습니다.");
      //alert("이미지를 선택해 주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("imageFile", file);

    try {
      const response = await axios.post(`${API_URL}/api/medicines/upload-image`, formData, {
        headers: {
          Authorization: `Bearer ${token.value}`,
          // "Content-Type": "multipart/form-data",  // 브라우저가 자동으로 처리
        },
      });

      if (response.status === 200) {
        console.log("✅ 이미지 업로드 성공:", response.data);
        //alert("이미지 업로드 성공!");

        // store 리셋
        people.value = [];
        calendar.value = [];
        currentYear.value = null;
        currentMonth.value = null;
        isDataLoaded.value = false;

        // 새로 fetch
        await fetchData();

        // medicineId를 store에 저장 (서버가 반환한 값)
        const id = response.data.medicineInfo?.[0]?.ITEM_SEQ;
        console.log("약!!!!!!!!!!!!!!!!!!!", id);
        if (!id) {
          //alert("인식에 실패했습니다. 다시 시도해 주세요.");
          console.log("인식에 실패했습니다. 다시 시도해 주세요.");
          return;
        }
        // medicineId 업데이트
        medicineId.value = id;
      }
    } catch (error) {
      console.error("🚨 이미지 업로드 오류:", error.response ? error.response.data : error.message);
      if (error.response) {
        const { status, message } = error.response.data;
        if (status === 400) {
          //alert("🚨 잘못된 이미지 형식입니다. (INVALID_IMAGE_FORMAT)");
          console.log("🚨 잘못된 이미지 형식입니다. (INVALID_IMAGE_FORMAT)");
        } else if (status === 401) {
         // alert("🚨 인증되지 않은 사용자입니다. 로그인 후 이용해 주세요. (UNAUTHORIZED)");
         console.log("🚨 인증되지 않은 사용자입니다. 로그인 후 이용해 주세요. (UNAUTHORIZED)");
        } else if (status === 404) {
          //alert("🚨 약 정보를 찾을 수 없습니다. (NOT_FOUND)");
          console.log("🚨 약 정보를 찾을 수 없습니다. (NOT_FOUND)");
        } else {
         // alert(`🚨 오류 발생: ${message}`);
         console.log(`🚨 오류 발생: ${message}`);
        }
      } else {
       // alert("🚨 네트워크 오류가 발생했습니다.")
       console.log("🚨 네트워크 오류가 발생했습니다.")
      }
    }
  };
  // --- 추가: 외부(안드로이드)에서 토큰을 전달받을 때 호출되는 event listener ---
  if (typeof window !== 'undefined') {
    document.addEventListener('tokenReceived', (event) => {
      const { user_id, access_token } = event.detail;
      updateToken(user_id, access_token);
    });
  }
  // --- 끝 ---
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
    uploadMedicineImage,
    setToken,
    resetScheduleData,
    updateToken,
    token,
    userId,
    medicineId,
  };
}, {
  persist: {
    enabled: true,
    strategies: [
      {
        key: `schedule-store-${localStorage.getItem("user_id") || "default"}`,
        storage: localStorage,
        paths: ['people', 'currentYear', 'currentMonth', 'isDataLoaded', 'token']
      }
    ]
  }
});
