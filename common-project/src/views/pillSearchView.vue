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
      <input 
        ref="fileInput" 
        type="file" 
        accept="image/*" 
        capture="camera" 
        class="hidden-input" 
        @change="openCamera"
      />
    </div>


    <!-- 약 정보 목록 -->
    <div v-if="filteredMedicineList.length > 0">
      <div v-for="(pill, index) in filteredMedicineList" :key="index">
        <PillInformation
          :name="pill.name"
          :content="pill.content"
          :expiry="pill.expiry"
          :image-src="pill.imageSrc"
          @click="goToDetailPage(pill.id)"
        />
      </div>
    </div>

    <!-- 검색 결과 없음 -->
    <div v-else class="no-results">
      <p>검색 결과가 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import { useApiStore } from "@/stores/apiRequest"
import PillInformation from "@/components/PillInformation.vue"
import maskGroup from '@/assets/mask-group0.svg'
import frameIcon from '@/assets/frame0.svg'

const apiStore = useApiStore()
const router = useRouter()
const searchQuery = ref("")
const fileInput = ref(null)
const filteredMedicineList = ref([])

// API에서 의약품 목록 가져오기
onMounted(async () => {
  await apiStore.fetchMedicineList("");
  filteredMedicineList.value = apiStore.medicineList
})



// ✅ 검색 시 목록 필터링
const filterMedicineList = () => {
  if (!searchQuery.value) {
    filteredMedicineList.value = apiStore.medicineList;
  } else {
    filteredMedicineList.value = apiStore.medicineList.filter(pill => 
      pill.name.includes(searchQuery.value)
    );
  }
};


// ✅ 약 상세 페이지 이동
const goToDetailPage = (medicineId) => {
  console.log("📢 이동할 약 ID:", medicineId); // ✅ 콘솔에서 확인
  if (!medicineId) {
    console.error("🚨 오류! 전달된 medicineId 값이 없음!");
    return;
  }
  router.push(`/pill-detail/${medicineId}`);
};
// ✅ 카메라 버튼 클릭 시 숨겨진 input 실행
const triggerCamera = () => {
  fileInput.value.click();
};

// ✅ 파일 선택 시 실행될 함수
const openCamera = async (event) => {
  const file = event.target.files[0];

  if (!file) return;

  console.log("선택된 파일:", file);

  await apiStore.sendImageToAPI(file); //이건 ai한테 보내는 걸로 바꿔야함!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
</style>
