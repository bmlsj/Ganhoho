<template>
  <div class="container">
    <!-- 검색 헤더 -->
    <div class="search-header">
      <img src="mask-group0.svg" alt="검색 배경" class="mask-group" />
      <input 
        v-model="searchQuery" 
        type="text" 
        placeholder="검색어를 입력해 주세요." 
        class="search-input" 
        @keyup.enter="search"
      />
      <button @click="openCamera" class="search-button">
        <img src="frame0.svg" alt="검색 아이콘" class="search-icon" />
      </button>
    </div>
    
    <!-- 약 정보 이 부분을 컴포넌트로 빼서 라우터 for 돌려서 보면 편하지 않을까? assets에 png 넣어두고--> 
    <div class="medicine-info">
      <img class="medicine-image" src="image-26920.png" alt="약 이미지" />
      <div class="medicine-details">
        <div class="medicine-name">타이레놀</div>
        <div class="medicine-content">아세트아미노펜 500mg</div>
        <div class="medicine-expiry">제조일로부터 36개월</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const searchQuery = ref("");

const search = () => {
  // 검색 로직 추가 가능
};

const openCamera = () => {
  // 카메라 연동 로직 추가 가능
};
</script>

<style scoped>
.container {
  background: #ffffff;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.search-header {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 20px;
  width: 350px;
  height: 50px;
  box-shadow: inset 0px 4px 4px rgba(0, 0, 0, 0.05);
  padding: 0 10px;
  position: relative;
}

.mask-group {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: auto;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: #333;
  padding-left: 40px;
}

.search-button {
  background: none;
  border: none;
  cursor: pointer;
}

.search-icon {
  width: 24px;
  height: 24px;
}

.medicine-info {
  display: flex;
  align-items: center;
  margin-top: 20px;
}

.medicine-image {
  border-radius: 20px;
  width: 145px;
  height: 79px;
  object-fit: cover;
  margin-right: 15px;
}

.medicine-details {
  display: flex;
  flex-direction: column;
}

.medicine-name {
  font-weight: bold;
  font-size: 18px;
  color: #000;
}

.medicine-content,
.medicine-expiry {
  font-size: 14px;
  color: #666;
}
</style>
