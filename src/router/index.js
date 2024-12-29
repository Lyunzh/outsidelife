import AdminCenterView from '@/views/AdminCenter/AdminCenterView.vue';
import AdminLoginView from '@/views/AdminLogin/AdminLoginView.vue';
import HomeView from '@/views/Home/HomeView.vue';
import SpotView from '@/views/Spot/SpotView.vue';
import RouteView from '@/views/Route/RouteView.vue';
import UserCenterView from '@/views/UserCenter/UserCenterView.vue';
import UserHomepageView from '@/views/UserHomepage/UserHomepageView.vue';
import UserLoginView from '@/views/UserLogin/UserLoginView.vue';
import { createRouter, createWebHistory } from 'vue-router';
import SearchView from '@/views/SearchView/SearchView.vue';
import { identityGet } from '@/apis/identity';

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
      path: '/usercenter/:userid',
      name: 'usercenter',
      component: UserCenterView,
      beforeEnter: async (to, from, next) => {
        const role = await identityGet()
        if (role.data["identity"] !== 'user') {
          console.log(role)
          console.log(role.identity)
          next('/userlogin')
        } else {
          next()
        }
      }
    },
    {
      path: '/userhomepage/:userid',
      name: 'userhomepage',
      component: UserHomepageView,
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
      component: AdminCenterView,
      beforeEnter: async (to, from, next) => {
        const role = await identityGet()
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
      path: '/search/:keyword',
      name: 'Search',
      component: SearchView
    }
  ]
});


export default router;
