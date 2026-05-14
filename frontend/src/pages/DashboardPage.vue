<template>
  <div class="max-w-4xl mx-auto px-6 py-12">
    <!-- Upgrade success banner -->
    <div v-if="showUpgradeBanner" class="bg-green-50 border border-green-200 rounded-2xl p-5 mb-8 flex items-center justify-between animate-fade-in-up">
      <div class="flex items-center gap-3">
        <svg class="w-6 h-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
        </svg>
        <span class="text-green-800 font-medium">You're all set! All 17 lessons are now unlocked.</span>
      </div>
      <button @click="showUpgradeBanner = false" class="text-green-600 hover:text-green-800">
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Header -->
    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-3xl font-bold text-charcoal">Dashboard</h1>
        <p class="text-muted mt-1">Welcome back, {{ auth.user?.email }}</p>
      </div>
      <span class="text-xs font-semibold px-3 py-1.5 rounded-full"
            :class="{
              'bg-surface text-muted': auth.user?.plan === 'FREE',
              'bg-coral/10 text-coral': auth.user?.plan === 'PRO',
              'bg-charcoal text-white': auth.user?.plan === 'LIFETIME'
            }">
        {{ auth.user?.plan === 'LIFETIME' ? 'LIFETIME' : auth.user?.plan }} plan
      </span>
    </div>

    <!-- Progress Bar -->
    <div class="bg-white border border-surface rounded-2xl p-6 mb-8 shadow-sm">
      <div class="flex items-center justify-between mb-3">
        <span class="text-sm font-semibold text-charcoal">Your Progress</span>
        <span class="text-sm text-muted">{{ displayedProgress }}/17 lessons</span>
      </div>
      <div class="w-full bg-surface rounded-full h-3 overflow-hidden">
        <div class="bg-coral h-3 rounded-full transition-all duration-700 ease-out"
             :style="{ width: animatedPercent + '%' }"></div>
      </div>
      <p v-if="displayedProgress === 17" class="text-sm text-coral font-medium mt-3 animate-celebrate">
        You've completed all lessons!
      </p>
    </div>

    <!-- Skeleton loader -->
    <template v-if="loading">
      <div v-for="i in 3" :key="i" class="mb-8">
        <div class="skeleton h-6 w-48 mb-4"></div>
        <div class="space-y-3">
          <div v-for="j in 4" :key="j" class="skeleton h-16 w-full"></div>
        </div>
      </div>
    </template>

    <!-- Tier Sections -->
    <template v-else>
      <div v-for="tier in tiers" :key="tier.number" class="mb-8">
        <div class="flex items-center gap-3 mb-4">
          <span class="text-xl">{{ tier.icon }}</span>
          <div>
            <h2 class="text-lg font-semibold text-charcoal">{{ tier.label }}</h2>
            <span class="text-xs text-muted">{{ tier.sublabel }}</span>
          </div>
          <span v-if="!tier.free" class="text-xs font-medium bg-coral/10 text-coral px-2.5 py-0.5 rounded-full">PRO</span>
        </div>

        <div class="grid gap-3">
          <component
            v-for="lesson in lessonsByTier(tier.number)" :key="lesson.id"
            :is="canAccess(lesson) ? 'router-link' : 'div'"
            :to="canAccess(lesson) ? `/lessons/${lesson.id}` : undefined"
            @click="!canAccess(lesson) && (showUpgradeModal = true)"
            class="group flex items-center gap-4 p-4 rounded-xl border bg-white transition-all duration-200"
            :class="canAccess(lesson)
              ? 'border-surface hover:border-coral/30 hover:shadow-sm cursor-pointer'
              : 'border-surface/60 opacity-60 cursor-pointer'"
          >
            <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-semibold shrink-0 transition-colors"
                 :class="isCompleted(lesson.id)
                   ? 'bg-green-100 text-green-600'
                   : canAccess(lesson)
                     ? 'bg-coral/10 text-coral'
                     : 'bg-surface text-muted'">
              <svg v-if="isCompleted(lesson.id)" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              <span v-else>{{ lesson.number }}</span>
            </div>

            <span class="text-sm font-medium flex-1" :class="canAccess(lesson) ? 'text-charcoal' : 'text-muted'">
              {{ lesson.title }}
            </span>
            <span v-if="isNew(lesson)" class="text-[10px] font-bold bg-coral text-white px-2 py-0.5 rounded-full uppercase tracking-wider">
              NEW
            </span>

            <div v-if="!canAccess(lesson)">
              <svg class="w-4 h-4 text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
              </svg>
            </div>
            <svg v-else-if="!isCompleted(lesson.id)" class="w-4 h-4 text-muted group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </component>
        </div>
      </div>
    </template>

    <!-- Footer links -->
    <div class="mt-8 flex items-center gap-4 flex-wrap">
      <router-link to="/settings" class="text-sm text-muted hover:text-charcoal transition-colors">Settings</router-link>
      <router-link v-if="auth.isAdmin" to="/admin" class="text-sm text-coral hover:text-coral-hover font-medium transition-colors">Admin Panel</router-link>
      <button @click="handleLogout" class="text-sm text-muted hover:text-charcoal transition-colors">Sign out</button>
    </div>

    <!-- Upgrade modal -->
    <UpgradeModal :show="showUpgradeModal" @close="showUpgradeModal = false" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import UpgradeModal from '../components/UpgradeModal.vue'
import api from '../api'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const lessons = ref([])
const completedIds = ref([])
const loading = ref(true)
const animatedPercent = ref(0)
const showUpgradeModal = ref(false)
const showUpgradeBanner = ref(route.query.upgraded === 'true')

// After Stripe checkout redirect, refresh user data from API so plan is current
if (route.query.upgraded === 'true') {
  api.get('/api/user/me').then(({ data }) => {
    auth.setAuth({
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
      user: data
    })
  }).catch(() => {})
}

const tiers = [
  { number: 1, label: 'Tier 1 — The Basics', sublabel: 'Free Claude features', icon: '🌱', free: true },
  { number: 2, label: 'Tier 2 — Claude Pro', sublabel: 'Requires Claude Pro', icon: '🔧', free: false },
  { number: 3, label: 'Tier 3 — Power Tools', sublabel: 'Developer & advanced', icon: '⚡', free: false },
]

const displayedProgress = computed(() => completedIds.value.length)

function lessonsByTier(tier) {
  return lessons.value.filter(l => l.tier === tier)
}

function isCompleted(lessonId) {
  return completedIds.value.includes(lessonId)
}

function canAccess(lesson) {
  return lesson.plan === 'FREE' || auth.hasPaidPlan
}

function isNew(lesson) {
  if (!lesson.createdAt) return false
  const thirtyDaysAgo = Date.now() - 30 * 24 * 60 * 60 * 1000
  return new Date(lesson.createdAt).getTime() > thirtyDaysAgo && !isCompleted(lesson.id)
}

onMounted(async () => {
  try {
    const [lessonsRes, progressRes] = await Promise.all([
      api.get('/api/lessons'),
      api.get('/api/progress')
    ])
    lessons.value = lessonsRes.data
    completedIds.value = progressRes.data
  } catch (e) {
    console.error('Failed to load dashboard data', e)
  } finally {
    loading.value = false
    nextTick(() => {
      setTimeout(() => {
        animatedPercent.value = Math.round((completedIds.value.length / 17) * 100)
      }, 100)
    })
  }
})

async function handleLogout() {
  await auth.logout()
  router.push('/')
}
</script>
