<template>
  <div class="container">
    <!-- 검색 헤더 -->
    <div class="search-header">
      <img :src="maskGroup" alt="검색 배경" class="mask-group" />
      <input 
        v-model="searchQuery"
        type="text"
        placeholder="검색어를 입력해 주세요."
        class="search-input"
        @input="filterMedicineList"
        @keyup.enter="search"
      />
      <button @click="triggerCamera" class="search-button">
        <img :src="frameIcon" alt="검색 아이콘" class="search-icon" />
      </button>
    </div>


    <!-- 약 정보 목록 -->
    <div v-if="filteredMedicineList.length > 0" class="pill-list">
      <div v-for="(pill, index) in filteredMedicineList" 
           :key="index" 
           class="pill-card"
           @click="goToDetailPage(pill.id)">
        <div class="pill-image-container">
          <img :src="!medicineDetail?.imageSrc ? defaultImage : medicineDetail.imageSrc" :alt="pill.name" class="pill-image" />
        </div>
        <div class="pill-info">
          <h3 class="pill-name">{{ pill.name }}</h3>
          <p class="pill-content">{{ pill.content }}</p>
          <p class="pill-expiry">제조일로부터 {{ pill.expiry }}</p>
        </div>
      </div>
    </div>

    <!-- 검색 결과 없음 -->
    <div v-else class="no-results">
      <p>검색 결과가 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue"
import { useRouter } from "vue-router"
import { useApiStore } from "@/stores/apiRequest"
import maskGroup from '@/assets/mask-group0.svg'
import frameIcon from '@/assets/frame0.svg'
import { storeToRefs } from 'pinia'
import defaultImage from '@/assets/default-image.png'

const apiStore = useApiStore()
const { medicineId } = storeToRefs(apiStore)
const router = useRouter()
const searchQuery = ref("")
const filteredMedicineList = ref([])

// 엔터 키 입력 시 검색 함수 실행
const search = async () => {
  const query = searchQuery.value.trim();
  if (!query) {
    filteredMedicineList.value = [];
    return;
  }
  console.log("검색 시작:", query);
  const success = await apiStore.fetchMedicineList(query);
  filteredMedicineList.value = success ? apiStore.medicineList : [];
};

onMounted(async () => {
  console.log("onMounted: 토큰 확인", apiStore.token)
  try {
    if (!apiStore.token) {
      console.error("토큰이 없습니다. 로그인이 필요합니다.");
      return;
    }
    // 초기 로딩 시에는 빈 목록으로 시작
    filteredMedicineList.value = [];
  } catch (error) {
    console.error("초기화 중 오류 발생:", error);
    filteredMedicineList.value = [];
  }

  // ✅ 앱에서 호출할 전역 함수 등록
  document.addEventListener('tokenReceived', (e) => {
    const { user_id, access_token } = e.detail
    console.log("Component - Token received via event:", access_token)
    apiStore.setToken(user_id, access_token)
  })

  // ① 네이티브 앱에게 카메라를 열도록 요청하는 함수 등록
  function openNativeCamera() {
    console.log("window.openNativeCamera 호출됨");
    // iOS: WKWebView의 messageHandler 호출 예시
    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openCamera) {
      console.log("iOS 네이티브 openCamera 호출");
      window.webkit.messageHandlers.openCamera.postMessage(null);
    } 
    // Android: 웹뷰에 주입된 인터페이스(예: AndroidInterface)를 통한 호출
    else if (window.AndroidCameraInterface && typeof window.AndroidCameraInterface.openCamera === 'function') {
      console.log("Android 네이티브 openCamera 호출");
      window.AndroidCameraInterface.openCamera();
    }
    else {
      console.error("네이티브 카메라 인터페이스가 제공되지 않았습니다.");
    }
  }

  // ② 네이티브 앱에서 사진 촬영 후 호출할 콜백 함수 등록  
  // 전달받은 imageData는 보통 base64 형식의 문자열(예: "iVBORw0KGgoAAAANSUhEUgAA...")이라고 가정합니다.
  window.onImageCaptured = function(imageData) {
    console.log("window.onImageCaptured 호출됨, imageData:", imageData);
    // imageData가 dataURL 전체라면 그대로 사용, 아니라면 dataURL 접두어를 붙입니다.
    let dataUrl = imageData.startsWith("data:image/")
      ? imageData
      : "data:image/png;base64," + imageData;
    console.log("변환된 dataUrl:", dataUrl);
    // dataURL을 File 객체로 변환
    const file = dataURLtoFile(dataUrl, "captured.png");
    if (!file) {
      console.error("이미지 파일 변환 실패");
      return;
    }
    console.log("파일 객체 생성됨:", file);
    // 네이티브 앱에서 전달받은 파일을 바로 업로드
    apiStore.uploadMedicineImage(file);
  }

  // dataURL을 File 객체로 변환하는 유틸 함수
  function dataURLtoFile(dataurl, filename) {
    const arr = dataurl.split(',');
    const match = arr[0].match(/:(.*?);/);
    if (!match) {
      console.error("올바르지 않은 dataURL:", dataurl);
      return null;
    }
    const mime = match[1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, { type: mime });
  }
})
// ✅ Watch store의 medicineId 변경 시 자동 페이지 이동
watch(medicineId, (newId) => {
  if (newId) {
    console.log("자동 이동: 약 ID", newId)
    router.push(`/pill-detail/${newId}`)
    apiStore.medicineId = null
  }
})
// ✅ 약 상세 페이지 이동
const goToDetailPage = (medicineId) => {
  console.log("📢 이동할 약 ID:", medicineId); // ✅ 콘솔에서 확인
  if (!medicineId) {
    console.error("🚨 오류! 전달된 medicineId 값이 없음!");
    return;
  }
  router.push(`/pill-detail/${medicineId}`);
};

// ✅ 카메라 버튼 클릭 시 네이티브 함수 호출
const triggerCamera = () => {
  console.log("triggerCamera 호출됨");
  if (typeof openNativeCamera === 'function') {
    openNativeCamera()
    console.log("openNativeCamera 함수 호출됨됨")
  } else {
    console.error("openNativeCamera 함수가 정의되지 않았습니다.");
  }
};
</script>

<style scoped>
.container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2vw;
  padding: 2vw;
  justify-content: center;
}

.search-header {
  display: flex;
  grid-column: 1 / -1; /* 전체 너비 사용 */
  align-items: center;
  background: #ffffff;
  border-radius: 20px;
  width: min(92%, 800px);
  height: 50px;
  box-shadow: inset 0px 4px 4px rgba(0, 0, 0, 0.05);
  padding: 0 10px;
  margin: 0 auto 3vh;
  position: sticky; /* ✅ 스크롤 시 고정 */
  top: 15px; /* ✅ 상단에 고정 */
  z-index: 100; /* ✅ 다른 요소 위에 표시 */
}
.search-header::before {
  content: "";
  position: absolute; 
  top: -15px; /* ✅ 기존의 틈을 메우기 */
  left: 0;
  width: 100%;
  height: 17px; /* ✅ 틈만큼 높이 설정 */
  background: #ffffff; /* ✅ 헤더 배경색과 동일하게 */
}
.mask-group {
  position: absolute;
  left: 2%;
  top: 50%;
  transform: translateY(-50%);
  width: clamp(16px, 4vw, 28px);
  height: auto;
  aspect-ratio: 1 / 1;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: clamp(1rem, 2vw, 1.2rem);
  color: #333;
  padding-left: 8%;
}

.search-button {
  background: none;
  border: none;
  cursor: pointer;
}

.search-icon {
  width: clamp(20px, 4vw, 32px);
  height: auto;
  aspect-ratio: 1 / 1;
}

/* ✅ 숨겨진 파일 선택 input */
.hidden-input {
  display: none;
}



/* ✅ 검색 결과 없음 */
.no-results {
  text-align: center;
  color: gray;
  font-size: 16px;
  margin-top: 20px;
}

.pill-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 14px;
  max-width: 500px;
  margin: 0 auto;
}

.pill-card {
  display: flex;
  background: #F5F7FF;
  border-radius: 10px;
  padding: 14px;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.pill-card:hover {
  transform: translateY(-2px);
}

.pill-image-container {
  width: 100px;
  height: 70px;
  margin-right: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
}

.pill-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.pill-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 2px; /* 글자 요소들 사이 간격 축소 */
}

.pill-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px; /* 간격 축소 */
  line-height: 1.2; /* 줄 간격 축소 */
}

.pill-content {
  font-size: 13px;
  color: #666;
  margin-bottom: 2px; /* 간격 축소 */
  line-height: 1.2; /* 줄 간격 축소 */
}

.pill-expiry {
  font-size: 12px;
  color: #888;
  line-height: 1.2; /* 줄 간격 축소 */
}
</style>
