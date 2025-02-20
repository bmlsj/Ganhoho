<template>
  <div class="loading-overlay" v-if="isLoading">
    <div class="spinner"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useLoadingStore } from '@/stores/loadingStore';

const loadingStore = useLoadingStore();
const isLoading = computed(() => loadingStore.isLoading);
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255,255,255,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #ccc;
  border-top-color: #1d3f72;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
