// src/stores/alertStore.js
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAlertStore = defineStore('alert', () => {
  /** 모달 표시 여부 */
  const isVisible = ref(false);
  /** 모달에 표시할 메시지 */
  const message = ref('');

  /** 모달 열기 */
  function showAlert(msg) {
    message.value = msg;
    isVisible.value = true;
  }

  /** 모달 닫기 */
  function hideAlert() {
    isVisible.value = false;
    message.value = '';
  }

  return {
    isVisible,
    message,
    showAlert,
    hideAlert,
  };
});
