<template>
  <LoadingSpinner />
  <router-view /> 
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router';
import LoadingSpinner from '@/components/LoadingSpinner.vue';
</script>
