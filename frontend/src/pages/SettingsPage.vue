<template>
  <div class="max-w-2xl mx-auto px-6 py-12">
    <h1 class="text-3xl font-bold text-charcoal mb-8">Account Settings</h1>

    <!-- Plan -->
    <div class="bg-white border border-surface rounded-2xl p-8 mb-6 shadow-sm">
      <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-4">Your Plan</h2>

      <div class="flex items-center gap-3 mb-4">
        <span class="text-xl font-bold text-charcoal">{{ planLabel }}</span>
        <span class="text-xs font-bold px-3 py-1 rounded-full"
              :class="{
                'bg-surface text-muted': plan === 'FREE',
                'bg-coral/10 text-coral': plan === 'PRO',
                'bg-charcoal text-white': plan === 'LIFETIME'
              }">
          {{ plan }}
        </span>
      </div>

      <template v-if="plan === 'FREE'">
        <p class="text-muted text-sm mb-4">You have access to the first 3 lessons. Upgrade to unlock all 17.</p>
        <router-link to="/pricing" class="inline-block bg-coral hover:bg-coral-hover active:scale-[0.97] text-white font-semibold px-6 py-2.5 rounded-lg transition-all text-sm">
          Upgrade
        </router-link>
      </template>

      <template v-else-if="plan === 'PRO'">
        <p class="text-muted text-sm mb-1">
          Status: <span class="font-medium text-charcoal">{{ status?.subscriptionStatus || 'active' }}</span>
        </p>
        <p v-if="status?.planExpiresAt" class="text-muted text-sm mb-4">
          Next billing: <span class="font-medium text-charcoal">{{ formatDate(status.planExpiresAt) }}</span>
        </p>
        <button @click="openPortal" class="inline-block bg-surface hover:bg-muted/10 text-charcoal font-medium px-6 py-2.5 rounded-lg transition-all text-sm">
          Manage Subscription
        </button>
      </template>

      <template v-else-if="plan === 'LIFETIME'">
        <div class="flex items-center gap-2 text-green-600">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
          </svg>
          <span class="font-medium">Lifetime Access — no billing</span>
        </div>
      </template>
    </div>

    <!-- Notifications -->
    <div class="bg-white border border-surface rounded-2xl p-8 mb-6 shadow-sm">
      <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-4">Notifications</h2>
      <label class="flex items-center gap-3 cursor-pointer">
        <input type="checkbox" v-model="notifyNewLessons" @change="toggleNotifications"
               class="w-4 h-4 accent-coral rounded" />
        <span class="text-sm text-charcoal">Email me when new lessons are added</span>
      </label>
    </div>

    <!-- Account info -->
    <div class="bg-white border border-surface rounded-2xl p-8 shadow-sm">
      <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-4">Account</h2>
      <div class="flex items-center gap-3">
        <div class="w-9 h-9 rounded-full bg-coral text-white text-sm font-bold flex items-center justify-center shrink-0">
          {{ auth.user?.email?.charAt(0)?.toUpperCase() || '?' }}
        </div>
        <p class="text-sm text-charcoal">{{ auth.user?.email }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../api'

const auth = useAuthStore()
const status = ref(null)
const notifyNewLessons = ref(auth.user?.notifyNewLessons ?? true)

const plan = computed(() => auth.user?.plan || 'FREE')
const planLabel = computed(() => {
  if (plan.value === 'LIFETIME') return 'Lifetime Access'
  if (plan.value === 'PRO') return 'Pro'
  return 'Free'
})

function formatDate(isoStr) {
  if (!isoStr) return ''
  return new Date(isoStr).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })
}

onMounted(async () => {
  try {
    const { data } = await api.get('/api/billing/status')
    status.value = data
  } catch {}
})

async function openPortal() {
  try {
    const { data } = await api.get('/api/billing/portal')
    window.location.href = data.portalUrl
  } catch {}
}

async function toggleNotifications() {
  try {
    await api.put('/api/user/notifications', { notifyNewLessons: notifyNewLessons.value })
  } catch {
    notifyNewLessons.value = !notifyNewLessons.value
  }
}
</script>
