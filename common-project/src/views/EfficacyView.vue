<template>
  <div class="efficacy-container">
    <!-- 주요 효능 -->
    <div class="efficacy-section">
      <h3 class="efficacy-title">주요 효능</h3>
      <ul v-if="medicineDetail.efficacy && medicineDetail.efficacy.length > 0" class="efficacy-list">
        <li v-for="(efficacy, index) in medicineDetail.efficacy" :key="index" class="efficacy-item">
          {{ efficacy }}
        </li>
      </ul>
      <p v-else class="no-info">dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd</p>
    </div>

    <!-- 가용 가능 질환 -->
    <div class="disease-section">
      <h3 class="disease-title">가용 가능 질환</h3>
      <ul class="disease-list">
        <li class="disease-item">감기로 인한 발열 및 통증</li>
        <li class="disease-item">염증성 질환에 의한 통증</li>
        <li class="disease-item">비염 및 축농증으로 인한 두통</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { useApiStore } from "@/stores/apiRequest";

const apiStore = useApiStore();
const medicineDetail = apiStore.medicineDetail;
</script>

<style scoped>
/* ✅ 효능 및 질환 정보 컨테이너 */
.efficacy-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: flex-start;
  justify-content: flex-start;
  width: 100%;
  max-width: 350px;
  margin: 0 auto;
  position: relative;
  word-wrap: break-word; /* ✅ 긴 단어 자동 줄바꿈 */
  overflow-wrap: break-word; /* ✅ 추가적인 줄바꿈 지원 */
}

/* ✅ 주요 효능 섹션 */
.efficacy-section,
.disease-section {
  width: 100%;
}

.efficacy-title,
.disease-title {
  font-family: "Inter-SemiBold", sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #000;
  margin: 0; /* ✅ 기본 마진 제거 */
  margin-bottom: 8px; /* ✅ 하단 여백만 명확하게 지정 */
}

.efficacy-list,
.disease-list {
  list-style-type: none;
  padding: 0;
}

.efficacy-item,
.disease-item {
  font-family: "Inter-Medium", sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: #333;
  padding: 5px 0;
  border-bottom: 1px solid #ddd;
  word-wrap: break-word; /* ✅ 리스트 항목 줄바꿈 */
  overflow-wrap: break-word; /* ✅ 긴 텍스트 대응 */
  white-space: normal; /* ✅ 한 줄이 너무 길어지지 않도록 */
}

.efficacy-item:last-child,
.disease-item:last-child {
  border-bottom: none;
}

.no-info {
  font-size: 14px;
  color: #888;
  font-style: italic;
  margin: 0;
}

/* ✅ 반응형 적용 */
@media (max-width: 600px) {
  .medicine-info-container {
    width: 95%;
  }
  .info-title {
    font-size: 14px;
  }
  .info-content, .dosage-list, .storage-list {
    font-size: 12px;
  }
}
</style>
