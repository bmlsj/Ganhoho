<template>
  <div class="medicine-info" :class="{ 'no-image': !imageSrc }">
    <!-- imageSrc가 있을 때만 이미지 렌더링 -->
    <img v-if="imageSrc" class="medicine-image" :src="imageSrc" alt="약 이미지" />
    <div class="medicine-details">
      <div class="medicine-name">{{ name }}</div>
      <div class="medicine-content">{{ content }}</div>
      <div class="medicine-expiry">{{ expiry }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  name: String,      // 약 이름
  content: String,   // 성분 정보
  expiry: String,    // 유효기간
  imageSrc: String   // 약 이미지 경로
});
</script>

<style scoped>
.medicine-info {
  display: grid;
  grid-template-columns: 40% 60%; /* 기본적으로 이미지-정보 비율 */
  gap: 1rem;
  margin: 1vh auto;
  width: 90%;
  max-width: 600px;
  padding: 2%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

/* imageSrc가 없는 경우, 텍스트만 보이도록 전체 너비 사용 */
.medicine-info.no-image {
  grid-template-columns: 100%;
}

.medicine-image {
  border-radius: 15px;
  width: 100%;
  height: auto;
  aspect-ratio: 1.8; /* 이미지 비율 고정 */
  object-fit: cover;
}

.medicine-details {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.medicine-name {
  font-weight: bold;
  font-size: clamp(1rem, 2vw + 0.5rem, 1.5rem);
  color: #000;
}

.medicine-content {
  font-size: clamp(0.75rem, 1.5vw + 0.5rem, 1rem);
  line-height: 1.4;
  color: #000;
}

.medicine-expiry {
  font-size: clamp(0.75rem, 1.5vw + 0.5rem, 1rem);
  line-height: 1.4;
  color: #666;
}
</style>
