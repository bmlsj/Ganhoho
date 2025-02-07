<template>
  <div class="container">
    <!-- 타이틀 -->
    <div class="title">{{ medicineDetail.medicineName || "타이레놀" }}</div>

    <!-- 이미지 -->
    <div class="image-container">
      <img :src="medicineDetail.imageUrl || defaultImage" alt="알약 이미지" class="medicine-image" />
    </div>

    <!-- 네비게이션 바 -->
    <div class="tab-container">
      <div class="tab-indicator" :style="{ transform: `translateX(${selectedTabIndex * 100}%)` }"></div>
      <div class="tab-buttons">
        <button @click="navigateTo('default', 0)" :class="{ 'active-tab': selectedTab === 'default' }">기본</button>
        <button @click="navigateTo('identification', 1)" :class="{ 'active-tab': selectedTab === 'identification' }">식별</button>
        <button @click="navigateTo('efficacy', 2)" :class="{ 'active-tab': selectedTab === 'efficacy' }">효능</button>
        <button @click="navigateTo('precautions', 3)" :class="{ 'active-tab': selectedTab === 'precautions' }">주의사항</button>
      </div>
    </div>

    <!-- 라우터 뷰 -->
    <div class="router-view">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import { useApiStore } from "@/stores/apiRequest"
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import defaultImage from "@/assets/image-26920.png" // 기본 이미지

const apiStore = useApiStore()
const route = useRoute()
const router = useRouter()

const selectedTab = ref("default")
const selectedTabIndex = ref(0) // ✅ 클릭된 버튼의 인덱스를 저장

console.log("📢 받은 약 ID:", route.params.id);

onMounted(() => {
  apiStore.fetchMedicineDetail(route.params.id)
  selectedTab.value = route.path.split("/").pop() // 현재 경로에서 마지막 부분 추출하여 탭 선택
  
  // 초기 탭 위치 설정 (URL을 보고 자동 감지)
  switch (selectedTab.value) {
    case "identification":
      selectedTabIndex.value = 1;
      break;
    case "efficacy":
      selectedTabIndex.value = 2;
      break;
    case "precautions":
      selectedTabIndex.value = 3;
      break;
    default:
      selectedTabIndex.value = 0;
  }
});

// ✅ 버튼 클릭 시 라우팅 및 스타일 변경
const navigateTo = (path, index) => {
  selectedTab.value = path
  selectedTabIndex.value = index
  router.push(`/pill-detail/${route.params.id}/${path}`)
};

const medicineDetail = apiStore.medicineDetail
</script>

<style scoped>
/* ✅ 반응형 크기 조정을 위한 CSS 변수 */
:root {
  --tab-width: 100px; /* 탭의 기본 너비 */
}

.container {
  font-family: Arial, sans-serif;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.title {
  margin-left:14px;
  text-align: left;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.image-container {
  text-align: center;
  margin-bottom: 20px;
}

.medicine-image {
  width: 100%;
  max-width: 300px;
  border-radius: 10px;
}

/* ✅ 네비게이션 바 */
.tab-container {
  width: 100%;
  max-width: 400px;
  height: 40px;
  position: relative;
  margin: 0 auto 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* ✅ 클릭된 버튼 아래 파란색 강조 바 */
.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  width: var(--tab-width);
  height: 4px;
  background: #007bff;
  transition: transform 0.3s ease-in-out;
}

/* ✅ 탭 버튼 스타일 */
.tab-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  width: 100%;
  max-width: 400px;
}

button {
  background: none;
  width:92%;
  border: none;
  font-size: clamp(12px, 2vw, 16px);
  font-weight: 600;
  padding: 8px;
  cursor: pointer;
  outline: none;
  transition: background-color 0.3s ease-in-out, color 0.3s ease-in-out;
  border-radius: 20px; /* ✅ 선택 버튼에 둥근 모서리 적용 */
}

/* ✅ 클릭된 버튼 스타일 */
.active-tab {
  background: #79C7E3; /* ✅ 배경을 파란색으로 변경 */
  color: #ffffff; /* ✅ 글자를 흰색으로 변경 */
  font-weight: bold;
}


/* ✅ 클릭되지 않은 버튼 스타일 */
button:not(.active-tab) {
  background: none;
  color: #151515;
}

/* ✅ RouterView 크기를 tab-container와 동일하게 설정 */
.router-view {
  width: 100%;
  max-width: 400px; /* ✅ tab-container와 동일한 너비 */

  margin: 0 auto;
  display: flex;
  flex-grow: 1;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  padding: 5px;
}
</style>

