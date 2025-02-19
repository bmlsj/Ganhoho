import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia({strick:false});
pinia.use(piniaPluginPersistedstate);
app.use(pinia);
app.use(router);
if (!window.receiveToken) {
  window.receiveToken = function(user_id, access_token, refresh_token) { // 🔹 user_id 추가
    if (!user_id) {
      console.error("🚨 receiveToken: user_id가 없습니다.");
      return;
    }

    localStorage.setItem("userId", user_id); // 현재 로그인한 사용자 ID 저장  // 수정됨
    localStorage.setItem(`user_${user_id}_token`, access_token); // 계정별 저장  // 수정됨
    localStorage.setItem(`user_${user_id}_refresh_token`, refresh_token); // 계정별 저장  // 수정됨

    document.dispatchEvent(new CustomEvent('tokenReceived', {
      detail: { user_id, access_token, refresh_token }
    }));

    console.log(`🔐 계정별 토큰 저장 완료: user_${user_id}_token`);
  }
}

app.mount('#app');
