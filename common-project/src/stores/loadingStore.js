import { defineStore } from 'pinia';

export const useLoadingStore = defineStore('loading', {
  state: () => ({
    // 여러 요청이 동시에 진행될 경우를 위해 카운터 사용
    loadingCount: 0,
  }),
  getters: {
    isLoading: (state) => state.loadingCount > 0,
  },
  actions: {
    startLoading() {
      this.loadingCount++;
      console.log("로딩 시작, 현재 카운트:", this.loadingCount);
    },
    stopLoading() {
      if (this.loadingCount > 0) {
        this.loadingCount--;
        console.log("로딩 종료, 현재 카운트:", this.loadingCount);
      }
    },
    resetLoading() {
      this.loadingCount = 0;
    },
  },
});
