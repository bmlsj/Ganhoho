<template>
  <LoadingSpinner />
  <MyAlert />
  <router-view /> 
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import MyAlert from '@/components/MyAlert.vue';
</script>
