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

    <!-- 자동완성 목록 -->
    <div v-if="searchQuery" class="autocomplete-box">
      <div 
        v-for="(suggestion, index) in autocompleteResults" 
        :key="index" 
        class="autocomplete-item" 
        @click="selectSuggestion(suggestion)"
      >
        {{ suggestion.name }}
      </div>
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
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useApiStore } from "@/stores/apiRequest";
import PillInformation from "@/components/PillInformation.vue";
import maskGroup from '@/assets/mask-group0.svg';
import frameIcon from '@/assets/frame0.svg';

const apiStore = useApiStore();
const router = useRouter();
const searchQuery = ref("");
const fileInput = ref(null);
const filteredMedicineList = ref([]);

// API에서 의약품 목록 가져오기
onMounted(async () => {
  await apiStore.fetchMedicineList("");
  filteredMedicineList.value = apiStore.medicineList;
});

// ✅ 자동완성 목록 (최대 3개 추천)
const autocompleteResults = computed(() => {
  if (!searchQuery.value) return [];
  return apiStore.medicineList.filter(pill => 
    pill.name.includes(searchQuery.value)
  ).slice(0, 3);
});

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

// ✅ 자동완성 항목 선택
const selectSuggestion = (pill) => {
  searchQuery.value = pill.name;
  filterMedicineList();
};

// ✅ 약 상세 페이지 이동
const goToDetailPage = (medicineId) => {
  router.push({ name: 'PillDetailView', params: { id: medicineId } });
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
  width: min(90%, 800px);
  height: 50px;
  box-shadow: inset 0px 4px 4px rgba(0, 0, 0, 0.05);
  padding: 0 10px;
  margin: 0 auto 3vh;
  position: relative;
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

/* ✅ 자동완성 목록 스타일 */
.autocomplete-box {
  background: #fff;
  border: 1px solid #ccc;
  position: absolute;
  top: 100%;
  left: 5%;
  width: 90%;
  max-width: 800px;
  z-index: 10;
  border-radius: 5px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
}

.autocomplete-item {
  padding: 10px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.autocomplete-item:hover {
  background: #f5f5f5;
}

/* ✅ 검색 결과 없음 */
.no-results {
  text-align: center;
  color: gray;
  font-size: 16px;
  margin-top: 20px;
}
</style>
