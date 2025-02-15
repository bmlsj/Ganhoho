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
  window.receiveToken = function(access_token, refresh_token) {
    localStorage.setItem("token", access_token);
    localStorage.setItem("refresh_token", refresh_token);
    document.dispatchEvent(new CustomEvent('tokenReceived', {
      detail: { access_token, refresh_token }
    }));
    console.log("Token dispatched via custom event.");
  }
}

app.mount('#app');
