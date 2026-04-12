import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板', icon: 'DataAnalysis' }
      },
      {
        path: 'customers',
        name: 'Customers',
        component: () => import('@/views/customers/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'User' }
      },
      {
        path: 'customer-pool',
        name: 'CustomerPool',
        component: () => import('@/views/customers/PoolList.vue'),
        meta: { title: '公海池', icon: 'ShoppingCart' }
      },
      {
        path: 'customers/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/customers/CustomerDetail.vue'),
        meta: { title: '客户详情', hidden: true }
      },
      {
        path: 'follow-ups',
        name: 'FollowUps',
        component: () => import('@/views/follow/FollowUpList.vue'),
        meta: { title: '跟进记录', icon: 'Document' }
      },
      {
        path: 'opportunities',
        name: 'Opportunities',
        component: () => import('@/views/opportunity/OpportunityList.vue'),
        meta: { title: '商机管理', icon: 'Money' }
      },
      {
        path: 'contracts',
        name: 'Contracts',
        component: () => import('@/views/contract/ContractList.vue'),
        meta: { title: '合同管理', icon: 'DocumentCopy' }
      },
      {
        path: 'leads',
        name: 'Leads',
        redirect: '/leads/list',
        meta: { title: '销售线索', icon: 'Promotion', redirectPath: '/leads/list' },
        children: [
          {
            path: 'list',
            name: 'LeadList',
            component: () => import('@/views/lead/list.vue'),
            meta: { title: '线索列表', icon: 'ShoppingCart' }
          },
          {
            path: 'create',
            name: 'LeadCreate',
            component: () => import('@/views/lead/create.vue'),
            meta: { title: '创建线索', icon: 'Plus', hidden: true }
          },
          {
            path: 'detail/:id',
            name: 'LeadDetail',
            component: () => import('@/views/lead/detail.vue'),
            meta: { title: '线索详情', icon: 'View', hidden: true }
          },
          {
            path: 'edit/:id',
            name: 'LeadEdit',
            component: () => import('@/views/lead/edit.vue'),
            meta: { title: '编辑线索', icon: 'Edit', hidden: true }
          }
        ]
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/system/UserList.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('@/views/system/RoleList.vue'),
        meta: { title: '角色权限', icon: 'Setting' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/system/Profile.vue'),
        meta: { title: '个人中心', hidden: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - CRM 客户管理系统`
  }
})

export default router
