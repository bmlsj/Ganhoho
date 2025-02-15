<template>
  <div class="container">
    <div class="ellipse-6"></div>
    <div class="ellipse-7"></div>
    <div class="ellipse-8"></div>
    <div class="ellipse-5"></div>
    <div class="wrapper">
      <div class="logo">GANHOHO</div>

      <div class="barcode">
        <div class="icons">
          <img :src="nurseImg" class="nurse" />
          <img :src="doctorImg" class="medical-doctor" />
        </div>
        <img :src="barcodeImg" class="barcode2" />
        <p class="invite-text">
          Join GANHOHO with your invitation code!
        </p>
      </div>

      <!-- a 태그의 href에 딥링크를 지정하여 클릭 시 앱 실행 -->
      <a :href="deepLink" class="invite-button">
        초대 수락하기
      </a>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import nurseImg from "@/assets/nurse0.png"
import doctorImg from "@/assets/medical-doctor0.png"
import barcodeImg from "@/assets/barcode1.svg"

// URL 파라미터에서 그룹 아이디를 받아옴
const route = useRoute()
const groupId = route.params.groupid

// 딥링크 URL (ssafyd209://ganhoho/group?groupCode={groupId})
const deepLink = `ssafyd209://ganhoho/group?groupCode=${groupId}`
</script>

<style scoped>
.container {
  background: #92a4aa;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
  text-align: center;
}

/* 배경 효과 */
.ellipse-6,
.ellipse-7,
.ellipse-8,
.ellipse-5 {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}
.ellipse-6 {
  background: rgba(26, 133, 171, 0.7);
  width: 320px;
  height: 320px;
  left: 60%;
  top: 70%;
}
.ellipse-7 {
  background: #941ca7;
  width: 200px;
  height: 200px;
  left: -10%;
  top: 50%;
}
.ellipse-8 {
  background: #c22eda;
  width: 200px;
  height: 200px;
  left: 60%;
  top: -10%;
}
.ellipse-5 {
  background: #115169;
  width: 320px;
  height: 320px;
  left: -20%;
  top: -20%;
}

/* 로고 */
.logo {
  font-size: 40px;
  font-weight: 800;
  color: white;
  margin-bottom: 30px;
  z-index: 100;
}

/* 바코드 영역 */
.barcode {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80%;
  max-width: 300px;
  box-shadow: 0px 24px 24px rgba(0, 0, 0, 0.1);
  z-index: 100;
}
.icons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 10px;
}
.nurse,
.medical-doctor {
  width: 40px;
  height: 40px;
}
.barcode2 {
  width: 80%;
  height: auto;
}
.invite-text {
  font-size: 12px;
  color: black;
  margin-top: 10px;
  font-weight: bold;
}

/* 초대 수락 버튼 */
.invite-button {
  background: #79c7e3;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 13px;
  width: 80%;
  max-width: 300px;
  padding: 15px;
  margin-top: 20px;
  box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: background 0.3s ease-in-out;
  text-decoration: none;
  text-align: center;
  display: inline-block;
  z-index: 100;
}
.invite-button:hover {
  background: #5bb8d4;
}

.wrapper {
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  padding: 30px;
  backdrop-filter: blur(15px);
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 85%;
  max-width: 350px;
  text-align: center;
}
</style>
