import { createRouter, createWebHistory } from 'vue-router'
import FullWorkScheduleView from '@/views/FullWorkScheduleView.vue'
import PillSearchView from '@/views/PillSearchView.vue'
import PillDetailView from '@/views/PillDetailView.vue'
import InvitationLinkView from '@/views/InvitationLinkView.vue'
import DefaultView from '@/views/DefaultView.vue'
import IdentificationView from '@/views/IdentificationView.vue'
import EfficacyView from '@/views/EfficacyView.vue'
import PrecautionsView from '@/views/PrecautionsView.vue'

import FullWorkSchedule from '@/components/FullWorkSchedule.vue'
import WeeklySchedule from '@/components/WeeklySchedule.vue'
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/fullworkscheduleview',
      name: 'FullWorkScheduleView',
      component: FullWorkScheduleView,
      children:[
        {
          path: '',
          name: 'FullWorkSchedule',
          component: FullWorkSchedule
        },
        {
          path: '/weekly',
          name: 'WeeklySchedule',
          component: WeeklySchedule
        },
      ]
    },
    {
      path: '/pillsearch',
      name: 'PillSearchView',
      component: PillSearchView
    },
    {
      path: '/invitationlinkview/:groupid',
      name: 'InvitationLinkView',
      component: InvitationLinkView
    },
    {
      path: '/pill-detail/:id', // ✅ ID를 동적으로 전달
      name: 'PillDetailView',
      component: PillDetailView,
      props: true,
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
