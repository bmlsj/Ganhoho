<template>
  <div class="medicine-info">
    <img class="medicine-image" :src="imageSrc" alt="약 이미지" />
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
  grid-template-columns: 40% 60%; /* 이미지-정보 비율 조정 */
  gap: 1rem;
  margin: 1vh auto;
  width: 90%;
  max-width: 600px;
  padding: 2%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
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
.medicine-content,
.medicine-expiry {
  font-size: clamp(0.75rem, 1.5vw + 0.5rem, 1rem);
  line-height: 1.4;
  color: #666;
}
</style>
