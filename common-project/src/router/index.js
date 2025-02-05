import { createRouter, createWebHistory } from 'vue-router'
import FullWorkScheduleView from '@/views/fullWorkScheduleView.vue'

const routes = [
  {
    path: '/',
    name: 'FullWorkScheduleView',
    component: FullWorkScheduleView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
// import { createRouter, createWebHistory } from 'vue-router'
// import FullWorkScheduleView from '@/views/fullWorkScheduleView.vue'


// const router = createRouter({
//   history: createWebHistory(),
//   routes: [
//     {
//       path: '/fullworkscheduleview',
//       name: 'FullWorkScheduleView',
//       component: FullWorkScheduleView
//     },
    
//   ]
// })

// export default router
