import { createRouter, createWebHistory } from 'vue-router'
import FullWorkScheduleView from '@/views/fullWorkScheduleView.vue'
import PillSearchView from '@/views/pillSearchView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/', //기본 경로에서 `/fullworkscheduleview`로 리디렉션
      redirect: { name: 'FullWorkScheduleView' }
    },
    {
      path: '/fullworkscheduleview',
      name: 'FullWorkScheduleView',
      component: FullWorkScheduleView
    },
    {
      path: '/pillsearch',
      name: 'PillSearchView',
      component: PillSearchView
    },
  ]
})

export default router
