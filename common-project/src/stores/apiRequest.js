// stores/apiRequest.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';
// 1) 마스킹 함수 가져오기
import { maskURL, maskToken } from '@/utils/mask.js';
import { useLoadingStore } from '@/stores/loadingStore';
// ** AlertStore 가져오기 **
import { useAlertStore } from '@/stores/alertStore.js';

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
  // AlertStore 인스턴스 생성 (store 내에서 전역 알림 호출)
  const alertStore = useAlertStore();

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

          const typeMapping = { OF: "Off", E: "Eve", D: "Day", N: "Nig" }
          people.value = responseData.map((person) => ({
            name: person.name,
            schedule: person.scheduleData.reduce((acc, day) => {
              acc[day.day] = typeMapping[day.type] || day.type;
              return acc;
            }, {}),
          }));
          console.log("피!!!플!!!!:", people.value);
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
  
    let firstDay = new Date(currentYear.value, currentMonth.value - 1, 1).getDay();
    const lastDate = new Date(currentYear.value, currentMonth.value, 0).getDate();
    console.log("첫번째 날의 요일 인덱스:", firstDay);
    console.log("해당 월의 마지막 날짜:", lastDate);
  
    let calendarData = [];
    let week = [null, ...new Array(7).fill(null)];
    console.log("초기 week 배열:", week);
  
    for (let i = 1; i <= firstDay; i++) {
      week[i] = null;
    }
    console.log("빈 칸 설정 후 week 배열:", week);
  
    for (let day = 1; day <= lastDate; day++) {
      const index = (firstDay % 7) + 1;
      week[index] = day;
      console.log(`날짜 ${day}는 인덱스 ${index}에 할당됨 -> week:`, week);
      firstDay++;
  
      if (firstDay % 7 === 0 || day === lastDate) {
        console.log(
          `한 주가 완료되었거나 마지막 날짜에 도달 (firstDay: ${firstDay}, day: ${day}). week 배열 저장:`,
          week
        );
        calendarData.push([...week]);
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
        // 기본 alert 대신 커스텀 alert 호출
        alertStore.showAlert('이미지 업로드 성공!');

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
        alertStore.showAlert('업로드 실패');
      }
    } catch (error) {
      console.error('API 요청 오류:', error);
      alertStore.showAlert('서버 오류로 업로드 실패');
    }
  };

  const fetchMedicineList = async (itemName) => {
    try {
      console.log("검색 요청:", itemName);
      console.log("토큰:", maskToken(token.value));
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
        timeout: 5000
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
      alertStore.showAlert("이미지를 선택해 주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("imageFile", file);

    try {
      const response = await axios.post(`${API_URL}/api/medicines/upload-image`, formData, {
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      });

      if (response.status === 200) {
        console.log("✅ 이미지 업로드 성공:", response.data);
        alertStore.showAlert("이미지 업로드 성공!");

        // store 리셋
        people.value = [];
        calendar.value = [];
        currentYear.value = null;
        currentMonth.value = null;
        isDataLoaded.value = false;

        // 새로 fetch
        await fetchData();

        const id = response.data.medicineInfo?.[0]?.ITEM_SEQ;
        console.log("약!!!!!!!!!!!!!!!!!!!", id);
        if (!id) {
          alertStore.showAlert("인식에 실패했습니다. 다시 시도해 주세요.");
          return;
        }
        medicineId.value = id;
      }
    } catch (error) {
      console.error("🚨 이미지 업로드 오류:", error.response ? error.response.data : error.message);
      if (error.response) {
        const { status, message } = error.response.data;
        if (status === 400) {
          alertStore.showAlert("🚨 잘못된 이미지 형식입니다. (INVALID_IMAGE_FORMAT)");
        } else if (status === 401) {
          alertStore.showAlert("🚨 인증되지 않은 사용자입니다. 로그인 후 이용해 주세요. (UNAUTHORIZED)");
        } else if (status === 404) {
          alertStore.showAlert("🚨 약 정보를 찾을 수 없습니다. (NOT_FOUND)");
        } else {
          alertStore.showAlert(`🚨 오류 발생: ${message}`);
        }
      } else {
        alertStore.showAlert("🚨 네트워크 오류가 발생했습니다.");
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
        paths: ['people', 'currentYear', 'currentMonth', 'isDataLoaded', 'token']
      }
    ]
  }
});
