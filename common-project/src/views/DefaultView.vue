<template>
  <div class="medicine-info-container">
    <div class="info-section">
      <div class="info-title">성분</div>
      <div class="info-content">{{ medicineDetail.basicInfo?.ingredient || "정보 없음" }}</div>
    </div>
    
    <div class="info-section">
      <div class="info-title">제형</div>
      <div class="info-content">{{ medicineDetail.basicInfo?.form || "정보 없음" }}</div>
    </div>
    
    <div class="info-section">
      <div class="info-title">복용법 및 용량</div>
      <div class="info-content">
        <ul class="dosage-list">
          <li>성인: {{ medicineDetail.basicInfo?.dosage?.adult || "정보 없음" }}</li>
          <li>어린이: {{ medicineDetail.basicInfo?.dosage?.child || "정보 없음" }}</li>
        </ul>
      </div>
    </div>
    
    <div class="info-section">
      <div class="info-title">보관 방법</div>
      <div class="info-content">
        <ul class="storage-list">
          <li>조건: {{ medicineDetail.basicInfo?.storage?.condition || "정보 없음" }}</li>
          <li>기간: {{ medicineDetail.basicInfo?.storage?.duration || "정보 없음" }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useApiStore } from "@/stores/apiRequest";

const apiStore = useApiStore();
const medicineDetail = apiStore.medicineDetail;
</script>

<style scoped>
.medicine-info-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: flex-start;
  justify-content: flex-start;
  width: 90%;
  max-width: 400px;
  margin: 0 auto;
  position: relative;
  word-wrap: break-word; /* ✅ 긴 단어 자동 줄바꿈 */
  overflow-wrap: break-word; /* ✅ 추가적인 줄바꿈 지원 */
}
.info-section {
  flex-shrink: 0;
  width: 100%;
}
.info-title {
  color: #000000;
  text-align: left;
  font-family: "Inter-SemiBold", sans-serif;
  font-size: 16px;
  line-height: 22px;
  font-weight: 600;
  margin-bottom: 5px;
}
.info-content {
  color: #000000;
  text-align: left;
  font-family: "Inter-Medium", sans-serif;
  font-size: 14px;
  line-height: 22px;
  font-weight: 500;
}
.dosage-list, .storage-list {
  list-style-type: disc; /* ✅ 목록 스타일 추가 */
  padding-left: 20px; /* ✅ 들여쓰기 추가 */
  margin: 0;
}
.storage-list li {
  font-family: "Inter-Medium", sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: #333;
  overflow-wrap: break-word;
}
.dosage-list li {
  font-family: "Inter-Medium", sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: #333;
  overflow-wrap: break-word;
}
@media (max-width: 600px) {
  .medicine-info-container {
    width: 95%;
  }
}
</style>
