import { createRouter, createWebHistory } from 'vue-router'
import FullWorkScheduleView from '@/views/FullWorkScheduleView.vue'
import PillSearchView from '@/views/PillSearchView.vue'
import PillDetailView from '@/views/PillDetailView.vue'

import DefaultView from '@/views/DefaultView.vue'
import IdentificationView from '@/views/IdentificationView.vue'
import EfficacyView from '@/views/EfficacyView.vue'
import PrecautionsView from '@/views/PrecautionsView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
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
    {
      path: '/pill-detail/:id', // ✅ ID를 동적으로 전달
      name: 'PillDetailView',
      component: PillDetailView,
      children: [
        {
          path: 'default',
          name: 'DefaultView',
          component: DefaultView
        },
        {
          path: 'identification',
          name: 'IdentificationView',
          component: IdentificationView
        },
        {
          path: 'efficacy',
          name: 'EfficacyView',
          component: EfficacyView
        },
        {
          path: 'precautions',
          name: 'PrecautionsView',
          component: PrecautionsView
        }
      ],
      redirect: { name: 'DefaultView' } // ✅ 기본 탭으로 이동
    }
  ]
})

export default router
