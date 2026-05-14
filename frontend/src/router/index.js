import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LandingPage from '../pages/LandingPage.vue'

const routes = [
  { path: '/', component: LandingPage, meta: { title: 'LearnClaude.ai — Master Claude AI Tools' } },
  {
    path: '/login',
    component: () => import('../pages/LoginPage.vue'),
    meta: { title: 'Sign In | LearnClaude.ai' }
  },
  {
    path: '/register',
    component: () => import('../pages/RegisterPage.vue'),
    meta: { title: 'Create Account | LearnClaude.ai' }
  },
  {
    path: '/pricing',
    component: () => import('../pages/PricingPage.vue'),
    meta: { title: 'Pricing | LearnClaude.ai' }
  },
  {
    path: '/settings',
    component: () => import('../pages/SettingsPage.vue'),
    meta: { requiresAuth: true, title: 'Settings | LearnClaude.ai' }
  },
  {
    path: '/dashboard',
    component: () => import('../pages/DashboardPage.vue'),
    meta: { requiresAuth: true, title: 'Dashboard | LearnClaude.ai' }
  },
  {
    path: '/lessons/:id',
    component: () => import('../pages/LessonPage.vue'),
    meta: { requiresAuth: true, title: 'Lesson | LearnClaude.ai' }
  },
  {
    path: '/learn',
    component: () => import('../pages/LearnIndexPage.vue'),
    meta: { title: 'Learn Claude AI — All Lessons | LearnClaude.ai' }
  },
  {
    path: '/learn/:slug',
    component: () => import('../pages/LearnLessonPage.vue'),
    meta: { title: 'Learn Claude AI | LearnClaude.ai' }
  },
  {
    path: '/terms',
    component: () => import('../pages/TermsPage.vue'),
    meta: { title: 'Terms of Service | LearnClaude.ai' }
  },
  {
    path: '/privacy',
    component: () => import('../pages/PrivacyPage.vue'),
    meta: { title: 'Privacy Policy | LearnClaude.ai' }
  },
  {
    path: '/admin',
    component: () => import('../pages/AdminPage.vue'),
    meta: { requiresAuth: true, requiresAdmin: true, title: 'Admin | LearnClaude.ai' }
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('../pages/NotFoundPage.vue'),
    meta: { title: 'Page Not Found | LearnClaude.ai' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash) return { el: to.hash, behavior: 'smooth' }
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { path: '/dashboard' }
  }
})

router.afterEach((to) => {
  document.title = to.meta.title || 'LearnClaude.ai'
})

export default router
