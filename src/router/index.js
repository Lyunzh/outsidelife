
import AdminLoginView from '@/views/AdminLogin/AdminLoginView.vue';
import HomeView from '@/views/Home/HomeView.vue';
import SpotView from '@/views/Spot/SpotView.vue';
import RouteView from '@/views/Route/RouteView.vue';
import ReportManageView from '@/views/AdminCenter/ReportManageView.vue'

import UserLoginView from '@/views/UserLogin/UserLoginView.vue';
import { createRouter, createWebHistory } from 'vue-router';


//页面路由配置
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    
    {
      path: '/route/:id',
      name: 'route',
      component: RouteView,
    },
    {
      path: '/userlogin',
      name: 'userlogin',
      component: UserLoginView,
    },
    {
      path: '/adminlogin',
      name: 'adminlogin',
      component: AdminLoginView,
    },
    {
      path: '/admincenter',
      name: 'admincenter',
      component: ReportManageView,
    },
    {
      path: '/spot/:id',
      name: 'spot',
      component: SpotView
    },
    {
      path: '/admincenter',
      name: 'reportManage',
      component: ReportManageView,
      meta: { requiresAdmin: true }
    }
    
   
  ]
});


export default router;
