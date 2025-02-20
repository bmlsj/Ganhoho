<template>
  <div class="container">
    <h3 class="title">{{ medicineDetail?.ITEM_NAME || '의약품 상세정보' }}</h3>

    <!-- 이미지 -->
    <div class="image-container">
      <img :src="medicineDetail?.ITEM_IMAGE || defaultImage" alt="알약 이미지" class="medicine-image" />
    </div>

    <!-- 기본 정보 -->
    <div class="info-box">
      <div class="info-item">
        <span class="info-label">제조사:</span>
        <span class="info-value">{{ medicineDetail?.ENTP_NAME }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">분류:</span>
        <span class="info-value">{{ medicineDetail?.ETC_OTC_CODE }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">유효기간:</span>
        <span class="info-value">{{ medicineDetail?.VALID_TERM }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">보관방법:</span>
        <span class="info-value">{{ medicineDetail?.STORAGE_METHOD }}</span>
      </div>
    </div>

    <!-- 네비게이션 바 -->
    <div class="tab-container">
      <div class="tab-buttons">
        <button 
          v-for="(tab, index) in tabs" 
          :key="tab.id"
          @click="navigateTo(tab.id, index)" 
          :class="{ 'active-tab': selectedTab === tab.id }"
        >
          {{ tab.name }}
        </button>
      </div>
    </div>

    <!-- 컨텐츠 영역 -->
    <div class="router-view">
      <div v-if="selectedTab === 'default'" class="section">
        <h2 class="section-title">성분</h2>
        <p class="material-text">{{ medicineDetail?.MATERIAL_NAME || '정보 없음' }}</p>
        
        <h2 class="section-title mt-4">용법용량</h2>
        <div class="content-text" v-html="parseXmlContent(medicineDetail?.UD_DOC_DATA)"></div>
      </div>
      <div v-else-if="selectedTab === 'identification'" class="section">
        <h2 class="section-title">성상</h2>
        <p class="material-text">{{ medicineDetail?.CHART || '정보 없음' }}</p>
      </div>
      <div v-else-if="selectedTab === 'efficacy'" class="section">
        <h2 class="section-title">효능 효과</h2>
        <div class="content-text" v-html="parseXmlContent(medicineDetail?.EE_DOC_DATA)"></div>
      </div>
      <div v-else-if="selectedTab === 'precautions'" class="section">
        <h2 class="section-title">사용상의 주의사항</h2>
        <div class="content-text" v-html="parseXmlContent(medicineDetail?.NB_DOC_DATA)"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useApiStore } from '@/stores/apiRequest'
import defaultImage from '@/assets/default-image.png'

const route = useRoute()
const apiStore = useApiStore()
const selectedTab = ref('default')
const selectedTabIndex = ref(0)

// medicineDetail을 computed로 변경하여 store의 변경사항을 실시간으로 반영
const medicineDetail = computed(() => apiStore.medicineDetail)

const tabs = [
  { id: 'default', name: '기본' },
  { id: 'identification', name: '식별' },
  { id: 'efficacy', name: '효능' },
  { id: 'precautions', name: '주의사항' }
]

const parseXmlContent = (xmlString) => {
  if (!xmlString) return '';
  return xmlString
    .replace(/<!\[CDATA\[(.*?)\]\]>/g, '$1')
    .replace(/<DOC.*?>/g, '')
    .replace(/<\/DOC>/g, '')
    .replace(/<SECTION.*?>/g, '')
    .replace(/<\/SECTION>/g, '')
    .replace(/<ARTICLE title="(.*?)">/g, '<h3>$1</h3>')
    .replace(/<\/ARTICLE>/g, '')
    .replace(/<PARAGRAPH.*?>/g, '<p>')
    .replace(/<\/PARAGRAPH>/g, '</p>');
}

const navigateTo = (tab, index) => {
  selectedTab.value = tab
  selectedTabIndex.value = index
}

onMounted(async () => {
  try {
    const result = await apiStore.fetchMedicineDetail(route.params.id)
    if (!result) {
      console.error('의약품 상세 정보를 가져오는데 실패했습니다.')
    }
  } catch (error) {
    console.error('의약품 상세 정보 요청 중 오류 발생:', error)
  }
})
</script>

<style scoped>
.container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.image-container {
  width: 100%;
  margin-bottom: 20px;
}

.medicine-image {
  width: 100%;
  max-width: 400px;
  height: auto;
  display: block;
  margin: 0 auto;
}

.info-box {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 10px;
}

.info-label {
  font-weight: bold;
  margin-right: 10px;
}

/* 네비게이션 바 */
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

/* 탭 인디케이터 제거 */
.tab-indicator {
  display: none; /* 또는 이 스타일 블록 자체를 삭제 */
}

.tab-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  width: 100%;
  max-width: 400px;
}

button {
  background: none;
  width: 92%;
  border: none;
  font-size: clamp(12px, 2vw, 16px);
  font-weight: 600;
  padding: 8px;
  cursor: pointer;
  outline: none;
  transition: background-color 0.3s ease-in-out, color 0.3s ease-in-out;
  border-radius: 20px;
}

.active-tab {
  background: #79C7E3;
  color: #ffffff;
  font-weight: bold;
}

button:not(.active-tab) {
  background: none;
  color: #151515;
}

.router-view {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  display: flex;
  flex-grow: 1;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  padding: 5px;
}

.section {
  margin-bottom: 30px;
  width: 100%;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.material-text {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.content-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.mt-4 {
  margin-top: 25px;
}

:deep(h3) {
  font-size: 16px;
  font-weight: bold;
  margin: 15px 0;
  color: #333;
}

:deep(p) {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}
</style>

