<template>
  <div class="identification-container">
    <!-- 약물 외형 정보 -->
    <div class="identification-section">
      <div class="section-title">약물 외형 정보</div>
      <div class="identification-details">
        <ul class="identification-details">
          <li>크기: {{ medicineDetail.identificationInfo?.size || "정보 없음" }}</li>
          <li>모양: {{ medicineDetail.identificationInfo?.shape || "정보 없음" }}</li>
        </ul>
      </div>
    </div>

    <!-- 포장 정보 -->
    <div class="packaging-section">
      <div class="section-title">포장 정보</div>
      <div class="packaging-details">
        <ul class="identification-details">
          <li>포장 형태: {{ medicineDetail.identificationInfo?.package || "정보 없음" }}</li>
          <li>포장 상 식별 정보: {{ medicineDetail.identificationInfo?.packageInfo || "정보 없음" }}</li>
        </ul>
      </div>
    </div>

    <!-- 안구 식별 가능 특징 -->
    <div class="marking-section">
      <div class="section-title">안구 식별 가능 특징</div>
      <div class="marking-details">
        {{ medicineDetail.identificationInfo?.marking || "정보 없음" }}
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
/* ✅ 전체 컨테이너 */
.identification-container {
  display: flex;
  flex-direction: column;
  gap: 13px;
  align-items: flex-start;
  justify-content: flex-start;
  width: 90%;
  max-width: 400px;
  margin: 0 auto;
  position: relative;
  word-wrap: break-word; /* ✅ 긴 단어 자동 줄바꿈 */
  overflow-wrap: break-word; /* ✅ 추가적인 줄바꿈 지원 */
}

/* ✅ 공통 섹션 스타일 */
.identification-section,
.packaging-section,
.marking-section {
  width: 100%;
  height: auto;
}

/* ✅ 섹션 타이틀 */
.section-title {
  color: #000000;
  text-align: left;
  font-family: "Inter-SemiBold", sans-serif;
  font-size: 16px;
  line-height: 24px;
  font-weight: 600;
  margin-bottom: 5px;
}

.identification-list { 
  list-style-type: disc; /* ✅ 목록 스타일 추가 */
  padding-left: 20px; /* ✅ 들여쓰기 추가 */
  margin: 0;
}

/* ✅ 리스트 스타일 */
.identification-details,
.packaging-details,
.marking-details {
  font-family: "Inter-Bold", sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  padding-left: 13px;
  color: #000000;
  overflow-wrap: break-word; /* ✅ 긴 텍스트 줄바꿈 */
}




/* ✅ 반응형 적용 */
@media (max-width: 600px) {
  .medicine-info-container {
    width: 95%;
  }
}
</style>
