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
  window.receiveToken = function(user_id, access_token) {
    localStorage.setItem("user_id", user_id);
    localStorage.setItem("token", access_token);
    document.dispatchEvent(new CustomEvent('tokenReceived', {
      detail: { user_id, access_token }
    }));
    console.log("Token dispatched via custom event.");
  }
}

app.mount('#app');
