import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/Home.vue') },
      { path: 'tutorials/:id', name: 'tutorial-detail', component: () => import('@/views/TutorialDetail.vue') },
      { path: 'favorites', name: 'favorites', meta: { requiresAuth: true }, component: () => import('@/views/Favorites.vue') },
      { path: 'my-tutorials', name: 'my-tutorials', meta: { requiresAuth: true }, component: () => import('@/views/MyTutorials.vue') },
      { path: 'tutorials/new', name: 'tutorial-new', meta: { requiresAuth: true }, component: () => import('@/views/TutorialEdit.vue') },
      { path: 'tutorials/:id/edit', name: 'tutorial-edit', meta: { requiresAuth: true }, component: () => import('@/views/TutorialEdit.vue') }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/categories' },
      { path: 'categories', name: 'admin-categories', component: () => import('@/views/admin/Categories.vue') },
      { path: 'users', name: 'admin-users', component: () => import('@/views/admin/Users.vue') },
      { path: 'tutorials', name: 'admin-tutorials', component: () => import('@/views/admin/Tutorials.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { name: 'home' }
  }
  return true
})

export default router
