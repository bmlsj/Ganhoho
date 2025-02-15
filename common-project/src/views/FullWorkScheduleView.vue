<template>
  <div>
    <WeeklySchedule v-if="showWeeklyView" />
    <FullWorkSchedule v-else />
    <button @click="toggleView">{{ showWeeklyView ? '전체 보기' : '주 단위 보기' }}</button>
  </div>
</template>

<script setup>
import FullWorkSchedule from '@/components/FullWorkSchedule.vue'
import { useApiStore } from "@/stores/apiRequest"
import { onMounted,ref } from "vue";
import WeeklySchedule from '@/components/WeeklySchedule.vue'

const showWeeklyView = ref(false)
const apiStore = useApiStore();

const toggleView = () => {
  showWeeklyView.value = !showWeeklyView.value
}

onMounted(() => {
  // ✅ 앱에서 호출할 전역 함수 등록
  window.receiveToken = (access_token, refresh_token) => {
    console.log("📢 Received access token:", access_token)
    console.log("📢 Received refresh token:", refresh_token)
    // ✅ Pinia Store에 저장
    apiStore.setToken(access_token, refresh_token)
  }
})
</script>
