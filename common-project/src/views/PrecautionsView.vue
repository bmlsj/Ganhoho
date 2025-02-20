<template>
  <div class="precautions-container">
    <!-- 사용 금지 환자 -->
    <div class="prohibited-section">
      <h3 class="section-title">사용 금지 환자</h3>
      <ul v-if="medicineDetail.precautions?.prohibitedUsers?.length > 0" class="prohibited-list">
        <li v-for="(user, index) in medicineDetail.precautions?.prohibitedUsers" :key="index" class="prohibited-item">
          {{ user }}
        </li>
      </ul>
      <p v-else class="no-info">정보 없음</p>
    </div>

    <!-- 과다 복용 경고 -->
    <div class="overdose-section">
      <h3 class="section-title">과다 복용 경고</h3>
      <ul v-if="medicineDetail.precautions?.overdoseWarnings?.length > 0" class="overdose-list">
        <li v-for="(warning, index) in medicineDetail.precautions?.overdoseWarnings" :key="index" class="overdose-item">
          {{ warning }}
        </li>
      </ul>
      <p v-else class="no-info">정보 없음</p>
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
.precautions-container {
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


/* ✅ 공통 섹션 */
.prohibited-section,
.overdose-section {
  width: 100%;
}

/* ✅ 제목 스타일 */
.section-title {
  font-family: "Inter-SemiBold", sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #000;
  margin:0;
  margin-bottom: 8px;
}

/* ✅ 리스트 스타일 */
.prohibited-list,
.overdose-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.prohibited-item,
.overdose-item {
  font-family: "Inter-Medium", sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: #333;
  padding: 5px 0;
  border-bottom: 1px solid #ddd;
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
}

.prohibited-item:last-child,
.overdose-item:last-child {
  border-bottom: none;
}

/* ✅ 정보 없음 표시 */
.no-info {
  font-size: 14px;
  color: #888;
  font-style: italic;
  margin: 0;
}

/* ✅ 반응형 적용 (모바일 최적화) */
@media (max-width: 600px) {
  .precautions-container {
    width: 95%;
  }
}

</style>