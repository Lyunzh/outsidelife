
import AdminLoginView from '@/views/AdminLogin/AdminLoginView.vue';
import HomeView from '@/views/Home/HomeView.vue';
import SpotView from '@/views/Spot/SpotView.vue';
import RouteView from '@/views/Route/RouteView.vue';
import ReportManageView from '@/views/AdminCenter/ReportManageView.vue'

import UserLoginView from '@/views/UserLogin/UserLoginView.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { getUserIdentity } from '@/apis/identity';

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
      beforeEnter: async (to, from, next) => {
        const role = await getUserIdentity()
        if (role.data["identity"] !== 'admin') {
          console.log(role)
          console.log(role.identity)
          next('/adminlogin')
        } else {
          next()
        }
      }
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
