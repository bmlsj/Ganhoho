<template>
  <div class="tutorial">
    <h2>📌 앱 사용 가이드</h2>
    <p>여기에서 일정 등록 및 관리가 가능합니다.</p>
    <p>설정을 통해 근무 일정을 추가하세요.</p>
    <button @click="$emit('skip')">건너뛰기</button>
  </div>
</template>

<style scoped>
.tutorial {
  text-align: center;
  font-size: 16px;
  color: gray;
  margin-top: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
}
button {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 5px;
  margin-top: 10px;
}
</style>