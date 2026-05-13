<template>
  <div class="max-w-5xl mx-auto px-6 py-16">
    <div class="text-center mb-16">
      <h1 class="text-4xl md:text-5xl font-bold text-charcoal tracking-tight mb-4">
        Simple, honest pricing
      </h1>
      <p class="text-lg text-muted">
        Start free. Upgrade when you're ready to unlock everything.
      </p>
    </div>

    <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-5 max-w-5xl mx-auto">
      <!-- Free -->
      <div class="bg-white rounded-2xl p-7 border border-surface shadow-sm">
        <h3 class="font-semibold text-charcoal text-lg mb-1">Free</h3>
        <p class="text-muted text-xs mb-5">Get started, no card needed</p>
        <div class="mb-6">
          <span class="text-3xl font-bold text-charcoal">$0</span>
          <span class="text-muted text-xs ml-1">forever</span>
        </div>
        <ul class="space-y-2.5 mb-6 text-sm text-charcoal/80">
          <li class="flex items-center gap-2"><Check /> First 3 lessons</li>
          <li class="flex items-center gap-2"><Check /> AI tutor on free lessons</li>
          <li class="flex items-center gap-2"><Check /> Progress tracking</li>
        </ul>
        <router-link to="/register" class="block text-center border-2 border-coral text-coral font-semibold px-5 py-2.5 rounded-lg hover:bg-coral/5 active:scale-[0.98] transition-all text-sm">
          Get Started Free
        </router-link>
      </div>

      <!-- Monthly -->
      <div class="bg-white rounded-2xl p-7 border border-surface shadow-sm">
        <h3 class="font-semibold text-charcoal text-lg mb-1">Pro Monthly</h3>
        <p class="text-muted text-xs mb-5">Cancel anytime</p>
        <div class="mb-6">
          <span class="text-3xl font-bold text-charcoal">$7</span>
          <span class="text-muted text-xs ml-1">/month</span>
        </div>
        <ul class="space-y-2.5 mb-6 text-sm text-charcoal/80">
          <li class="flex items-center gap-2"><Check /> All 17 lessons</li>
          <li class="flex items-center gap-2"><Check /> AI tutor on every lesson</li>
          <li class="flex items-center gap-2"><Check /> New lessons included</li>
        </ul>
        <button @click="handleCheckout('monthly')"
                class="w-full bg-coral hover:bg-coral-hover active:scale-[0.98] text-white font-semibold px-5 py-2.5 rounded-lg transition-all text-sm">
          Get Pro Monthly
        </button>
      </div>

      <!-- Yearly -->
      <div class="bg-white rounded-2xl p-7 border-2 border-coral relative shadow-lg">
        <div class="absolute -top-2.5 left-1/2 -translate-x-1/2 bg-coral text-white text-[10px] font-bold px-3 py-0.5 rounded-full uppercase tracking-wider">
          Save 42%
        </div>
        <h3 class="font-semibold text-charcoal text-lg mb-1">Pro Yearly</h3>
        <p class="text-muted text-xs mb-5">Best value</p>
        <div class="mb-1">
          <span class="text-3xl font-bold text-charcoal">$49</span>
          <span class="text-muted text-xs ml-1">/year</span>
        </div>
        <p class="text-xs text-muted mb-5">Just $4.08/month</p>
        <ul class="space-y-2.5 mb-6 text-sm text-charcoal/80">
          <li class="flex items-center gap-2"><Check /> All 17 lessons</li>
          <li class="flex items-center gap-2"><Check /> AI tutor on every lesson</li>
          <li class="flex items-center gap-2"><Check /> New lessons included</li>
        </ul>
        <button @click="handleCheckout('yearly')"
                class="w-full bg-coral hover:bg-coral-hover active:scale-[0.98] text-white font-semibold px-5 py-2.5 rounded-lg transition-all text-sm">
          Get Pro Yearly
        </button>
      </div>

      <!-- Lifetime -->
      <div v-if="lifetimeAvailable" class="bg-charcoal rounded-2xl p-7 text-white shadow-lg">
        <h3 class="font-semibold text-lg mb-1">Lifetime</h3>
        <p class="text-white/60 text-xs mb-5">{{ lifetimeRemaining }} of {{ lifetimeMax }} left</p>
        <div class="mb-6">
          <span class="text-3xl font-bold">$54</span>
          <span class="text-white/60 text-xs ml-1">one time</span>
        </div>
        <ul class="space-y-2.5 mb-6 text-sm text-white/80">
          <li class="flex items-center gap-2"><Check color="white" /> All 17 lessons forever</li>
          <li class="flex items-center gap-2"><Check color="white" /> AI tutor forever</li>
          <li class="flex items-center gap-2"><Check color="white" /> All future lessons</li>
        </ul>
        <button @click="handleCheckout('lifetime')"
                class="w-full bg-coral hover:bg-coral-hover active:scale-[0.98] text-white font-semibold px-5 py-2.5 rounded-lg transition-all text-sm">
          Get Lifetime Access
        </button>
      </div>
    </div>

    <p class="text-center text-xs text-muted mt-8">
      All plans include access to new lessons as Claude evolves. Payments processed securely by Stripe.
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../api'
import { API_BASE } from '../api'

const Check = (props) => h('svg', {
  class: `w-4 h-4 ${props.color === 'white' ? 'text-white/60' : 'text-coral'} shrink-0`,
  fill: 'none', viewBox: '0 0 24 24', stroke: 'currentColor', 'stroke-width': '2.5'
}, [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: 'M4.5 12.75l6 6 9-13.5' })])

const router = useRouter()
const auth = useAuthStore()
const lifetimeAvailable = ref(true)
const lifetimeRemaining = ref(100)
const lifetimeMax = ref(100)

onMounted(async () => {
  try {
    const res = await fetch(`${API_BASE}/api/billing/lifetime-remaining`)
    if (res.ok) {
      const data = await res.json()
      lifetimeAvailable.value = data.available
      lifetimeRemaining.value = data.remaining
      lifetimeMax.value = data.sold + data.remaining
    }
  } catch {}
})

async function handleCheckout(priceId) {
  if (!auth.isLoggedIn) {
    router.push(`/register?plan=${priceId}`)
    return
  }

  if (auth.user?.plan === 'PRO' || auth.user?.plan === 'LIFETIME') {
    try {
      const { data } = await api.get('/api/billing/portal')
      window.location.href = data.portalUrl
    } catch {
      router.push('/settings')
    }
    return
  }

  try {
    const { data } = await api.post('/api/billing/create-checkout-session', { priceId })
    window.location.href = data.sessionUrl
  } catch (e) {
    alert(e.response?.data?.error || 'Failed to start checkout')
  }
}
</script>
