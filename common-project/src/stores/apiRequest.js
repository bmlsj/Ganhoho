import { defineStore } from 'pinia';
import { ref, watch } from 'vue';
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

  const token = ref(localStorage.getItem("token") || null);
  const refreshToken = ref(localStorage.getItem("refresh_token") || null);

  //token.value ="eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OCwiaWF0IjoxNzM5NjgzMjYzLCJleHAiOjE3Mzk3Njk2NjN9.5KmPHuxwU_GMkUXFENU3EU_FfHRHU6FeGM04kse40Mc"
// 토큰 변경 감지를 위한 watch 추가

watch(token, async (newToken, oldToken) => {
  if (newToken !== oldToken) {
    console.log("토큰 변경 감지: 스케줄 데이터 초기화");
    console.log(newToken)
    console.log(oldToken)
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
}

const setToken = (access_token, refresh_token) => {
  token.value = access_token;
  refreshToken.value = refresh_token;
  localStorage.setItem("token", access_token);
  localStorage.setItem("refresh_token", refresh_token);
}

  const fetchData = async () => {
    try {
      if (isDataLoaded.value) {
        console.log("📢 기존 데이터 있음 → GET 요청 생략");
        return;
      }
      console.log("🔍 API 요청 URL:", maskURL(`${API_URL}/api/schedules/ocr`));

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

          const typeMapping = { OF: "Off", E: "Eve", D: "Day", N: "Nig" };
          people.value = responseData.map((person) => ({
            name: person.name,
            schedule: person.scheduleData.reduce((acc, day) => {
              acc[day.day] = typeMapping[day.type] || day.type;
              return acc;
            }, {}),
          }));

          isDataLoaded.value = true;
          generateCalendar();
        }
      }
    } catch (error) {
      console.error('데이터 가져오기 실패:', error);
    }
  }
  
  const generateCalendar = () => {
    if (!currentYear.value || !currentMonth.value) return;
    let firstDay = new Date(currentYear.value, currentMonth.value - 1, 1).getDay();
    const lastDate = new Date(currentYear.value, currentMonth.value, 0).getDate();

    let calendarData = [];
    let week = [null, ...new Array(7).fill(null)];

    for (let i = 1; i <= firstDay; i++) {
      week[i] = null;
    }

    for (let day = 1; day <= lastDate; day++) {
      const index = (firstDay % 7) + 1;
      week[index] = day;
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

    // 마스킹된 URL 로그
    console.log("🔍 API 요청 URL:", maskURL(`${API_URL}/api/schedules/ocr`));

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
    token,
    refreshToken,
    medicineId,
  };
}, {
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'schedule-store',
        storage: localStorage,
        paths: ['people', 'currentYear', 'currentMonth', 'isDataLoaded',]
      }
    ]
  }
});
