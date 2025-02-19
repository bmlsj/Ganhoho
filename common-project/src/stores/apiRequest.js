import { defineStore } from 'pinia';
import { ref } from 'vue';
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

  const userId = ref(localStorage.getItem("userId") || null); // 현재 로그인한 사용자 ID 추가  // 수정됨
  const token = ref(null); // 초기값 null로 변경  // 수정됨
  const refreshToken = ref(null); // 초기값 null로 변경  // 수정됨

  //token.value ="eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OCwiaWF0IjoxNzM5NjgzMjYzLCJleHAiOjE3Mzk3Njk2NjN9.5KmPHuxwU_GMkUXFENU3EU_FfHRHU6FeGM04kse40Mc"

  const setToken = (user_id, access_token, refresh_token) => { // 수정됨
    userId.value = user_id; // 현재 로그인한 사용자 ID 저장  // 수정됨
    token.value = access_token;
    refreshToken.value = refresh_token;

    localStorage.setItem("userId", user_id); // 현재 로그인한 사용자 ID 저장  // 수정됨
    localStorage.setItem(`user_${user_id}_token`, access_token); // 계정별 저장  // 수정됨
    localStorage.setItem(`user_${user_id}_refresh_token`, refresh_token); // 계정별 저장  // 수정됨
  };
 /**
   * 🔹 로그아웃 (현재 사용자 데이터만 삭제)
   */
 const logout = () => { // 수정됨
  if (userId.value) {
    localStorage.removeItem(`user_${userId.value}_token`); // 현재 계정의 데이터만 삭제  // 수정됨
    localStorage.removeItem(`user_${userId.value}_refresh_token`); // 현재 계정의 데이터만 삭제  // 수정됨
    localStorage.removeItem("userId"); // 사용자 ID 삭제  // 수정됨
  }
  userId.value = null;
  token.value = null;
  refreshToken.value = null;
};
  /**
   * 🔹 로그인한 계정의 토큰 불러오기
   */
  const loadUserData = () => { // 수정됨
    if (!userId.value) return;
    const storedToken = localStorage.getItem(`user_${userId.value}_token`);
    const storedRefreshToken = localStorage.getItem(`user_${userId.value}_refresh_token`);
    if (storedToken) token.value = storedToken;
    if (storedRefreshToken) refreshToken.value = storedRefreshToken;
  };

  loadUserData(); // 수정됨
  // (예시) 토큰 디버그 로그 -> 마스킹 처리
  // console.log("현재 토큰:", maskToken(token.value));

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
    loadUserData, 
    logout, 
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
