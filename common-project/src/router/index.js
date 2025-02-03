import { createRouter, createWebHistory } from 'vue-router'
import FullWorkScheduleView from '@/views/fullWorkScheduleView.vue'

const routes = [
  {
    path: '/',
    name: 'FullWorkSchedule',
    component: FullWorkScheduleView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
