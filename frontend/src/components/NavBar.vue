<template>
  <nav class="sticky top-0 z-50 bg-cream/80 backdrop-blur-md border-b border-surface">
    <div class="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
      <router-link to="/" class="flex items-center gap-2 text-xl font-semibold text-charcoal tracking-tight">
        <svg class="w-7 h-7" viewBox="0 0 32 32" fill="none">
          <rect width="32" height="32" rx="7" fill="#FAF9F5"/>
          <g transform="translate(16,16)">
            <rect x="-2.2" y="-8" width="4.4" height="16" rx="2.2" fill="#DA7756"/>
            <rect x="-2.2" y="-7" width="4.4" height="14" rx="2.2" fill="#DA7756" transform="rotate(90)"/>
            <rect x="-1.8" y="-6.2" width="3.6" height="12.4" rx="1.8" fill="#DA7756" transform="rotate(45)"/>
            <rect x="-1.8" y="-6.2" width="3.6" height="12.4" rx="1.8" fill="#DA7756" transform="rotate(135)"/>
            <circle r="3.2" fill="#DA7756"/>
            <circle r="1.4" fill="#FAF9F5"/>
          </g>
        </svg>
        <span><span class="text-coral">Learn</span>Claude.ai</span>
      </router-link>

      <!-- Desktop nav -->
      <div class="hidden md:flex items-center gap-8">
        <router-link to="/#curriculum" class="text-muted hover:text-charcoal transition-colors text-sm font-medium">Curriculum</router-link>
        <router-link to="/#pricing" class="text-muted hover:text-charcoal transition-colors text-sm font-medium">Pricing</router-link>

        <template v-if="auth.isLoggedIn">
          <router-link to="/dashboard" class="text-muted hover:text-charcoal transition-colors text-sm font-medium">Dashboard</router-link>
          <button @click="handleLogout" class="text-muted hover:text-charcoal transition-colors text-sm font-medium">Sign Out</button>
        </template>
        <template v-else>
          <router-link to="/login" class="text-muted hover:text-charcoal transition-colors text-sm font-medium">Sign In</router-link>
          <router-link to="/register" class="bg-coral hover:bg-coral-hover active:scale-[0.97] text-white text-sm font-semibold px-5 py-2.5 rounded-lg transition-all">
            Get Started Free
          </router-link>
        </template>
      </div>

      <!-- Mobile hamburger -->
      <button @click="mobileOpen = !mobileOpen" class="md:hidden p-2 -mr-2" aria-label="Toggle menu">
        <svg v-if="!mobileOpen" class="w-6 h-6 text-charcoal" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
        </svg>
        <svg v-else class="w-6 h-6 text-charcoal" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Mobile menu -->
    <Transition name="page">
      <div v-if="mobileOpen" class="md:hidden border-t border-surface bg-cream px-6 py-4 space-y-3">
        <router-link @click="mobileOpen = false" to="/#curriculum" class="block text-muted hover:text-charcoal text-sm font-medium py-2">Curriculum</router-link>
        <router-link @click="mobileOpen = false" to="/#pricing" class="block text-muted hover:text-charcoal text-sm font-medium py-2">Pricing</router-link>
        <template v-if="auth.isLoggedIn">
          <router-link @click="mobileOpen = false" to="/dashboard" class="block text-muted hover:text-charcoal text-sm font-medium py-2">Dashboard</router-link>
          <button @click="handleLogout; mobileOpen = false" class="block text-muted hover:text-charcoal text-sm font-medium py-2">Sign Out</button>
        </template>
        <template v-else>
          <router-link @click="mobileOpen = false" to="/login" class="block text-muted hover:text-charcoal text-sm font-medium py-2">Sign In</router-link>
          <router-link @click="mobileOpen = false" to="/register" class="block text-center bg-coral hover:bg-coral-hover text-white text-sm font-semibold px-5 py-2.5 rounded-lg transition-colors">
            Get Started Free
          </router-link>
        </template>
      </div>
    </Transition>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const mobileOpen = ref(false)

async function handleLogout() {
  await auth.logout()
  mobileOpen.value = false
  router.push('/')
}
</script>
