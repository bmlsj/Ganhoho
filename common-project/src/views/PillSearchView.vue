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
    </div>

    <!-- 약 정보 목록 -->
    <div v-if="filteredMedicineList.length > 0" class="pill-list">
      <div
        v-for="(pill, index) in filteredMedicineList" 
        :key="index" 
        class="pill-card"
        @click="goToDetailPage(pill.id)"
      >
        <div class="pill-image-container">
          <img :src="pill.imageSrc || defaultImage" :alt="pill.name" class="pill-image" />
        </div>
        <div class="pill-info">
          <h3 class="pill-name">{{ pill.name }}</h3>
          <p class="pill-content">{{ pill.content }}</p>
          <p class="pill-expiry">제조일로부터 {{ pill.expiry }}</p>
        </div>
      </div>
    </div>
    <div v-else class="no-results">
      <p>검색 결과가 없습니다.</p>
    </div>

    <!-- 플로팅 버튼: 카메라 모달 열기 -->
    <button class="floating-button" @click="openCameraModal">
      <img :src="PhotoIcon" alt="카메라 아이콘" class="search-icon" />
    </button>

    <!-- 카메라 모달 -->
    <div v-if="showCameraModal" class="camera-modal">
      <div class="camera-container">
        <video ref="videoRef" autoplay playsinline class="camera-video"></video>
        <div class="button-group">
          <button @click="capturePhoto">Capture Photo</button>
          <button @click="closeCameraModal">Cancel</button>
        </div>
        <!-- 캡쳐한 이미지가 필요할 경우 hidden canvas 사용 -->
        <canvas ref="canvasRef" style="display: none;"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useApiStore } from "@/stores/apiRequest";
import maskGroup from '@/assets/mask-group0.svg';
import PhotoIcon from '@/assets/PhotoIcon.png';

const apiStore = useApiStore();
const router = useRouter();

const searchQuery = ref("");
const filteredMedicineList = ref([]);
const showCameraModal = ref(false);
const videoRef = ref(null);
const canvasRef = ref(null);
let stream = null;

// 예시: 검색어 변경 및 목록 업데이트 (원래 코드에 맞게 수정)
watch(searchQuery, async (newQuery) => {
  if (newQuery.length >= 1) {
    const success = await apiStore.fetchMedicineList(newQuery);
    filteredMedicineList.value = success ? apiStore.medicineList : [];
  } else {
    filteredMedicineList.value = [];
  }
});

// 약 상세 페이지 이동
const goToDetailPage = (medicineId) => {
  if (!medicineId) {
    console.error("medicineId 값이 없습니다!");
    return;
  }
  router.push(`/pill-detail/${medicineId}`);
};

// 카메라 모달 열기: getUserMedia API 사용
const openCameraModal = async () => {
  try {
    // 후면 카메라 사용(facingMode: "environment") - 모바일에서 주로 사용
    stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: "environment" },
      audio: false,
    });
    if (videoRef.value) {
      videoRef.value.srcObject = stream;
    }
    showCameraModal.value = true;
  } catch (error) {
    console.error("카메라 접근 오류:", error);
  }
};

// 캡쳐 버튼: video의 현재 프레임을 canvas에 그린 후 업로드
const capturePhoto = () => {
  if (!videoRef.value || !canvasRef.value) return;

  const video = videoRef.value;
  const canvas = canvasRef.value;
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;

  const context = canvas.getContext("2d");
  context.drawImage(video, 0, 0, canvas.width, canvas.height);

  // 캔버스 이미지를 DataURL로 변환
  const dataUrl = canvas.toDataURL("image/png");
  // DataURL을 File 객체로 변환
  const file = dataURLtoFile(dataUrl, "captured.png");

  // 업로드 함수 호출
  apiStore.uploadMedicineImage(file);
  closeCameraModal();
};

// DataURL을 File 객체로 변환하는 함수
function dataURLtoFile(dataurl, filename) {
  const arr = dataurl.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new File([u8arr], filename, { type: mime });
}

// 모달 닫기: 스트림 정리 및 모달 숨김
const closeCameraModal = () => {
  showCameraModal.value = false;
  if (stream) {
    stream.getTracks().forEach((track) => track.stop());
    stream = null;
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
.floating-button {
  position: fixed;
  bottom: 15vh; /* ✅ 뷰포트 기준 상대적인 위치 */
  right: 7vw; /* ✅ 뷰포트 기준 상대적인 위치 */
  width: clamp(50px, 5vw, 60px); /* ✅ 화면 크기에 따라 버튼 크기 조정 */
  height: clamp(50px, 5vw, 60px);
  background-color: #FFFFFF;
  color: white;
  border: 2px solid #E9E9F1;
  border-radius: 50%;
  font-size: 24px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
