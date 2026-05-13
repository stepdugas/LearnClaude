<template>
  <Teleport to="body">
    <Transition name="page">
      <div v-if="show" class="fixed inset-0 z-[90] flex items-center justify-center p-6" @click.self="$emit('close')">
        <div class="absolute inset-0 bg-charcoal/40 backdrop-blur-sm"></div>
        <div class="relative bg-white rounded-2xl p-8 max-w-md w-full shadow-2xl">
          <button @click="$emit('close')" class="absolute top-4 right-4 text-muted hover:text-charcoal transition-colors" aria-label="Close">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>

          <h2 class="text-2xl font-bold text-charcoal mb-2">Unlock All 17 Lessons</h2>
          <p class="text-muted text-sm mb-8">
            Get full access to every lesson and a personal AI tutor for every topic.
          </p>

          <div class="space-y-3">
            <button @click="checkout('monthly')"
                    class="w-full flex items-center justify-between bg-surface hover:bg-surface/80 text-charcoal font-medium px-5 py-3.5 rounded-xl transition-colors">
              <span>Monthly</span>
              <span class="font-bold">$7/mo</span>
            </button>
            <button @click="checkout('yearly')"
                    class="w-full flex items-center justify-between bg-coral hover:bg-coral-hover text-white font-medium px-5 py-3.5 rounded-xl transition-colors">
              <span>Yearly <span class="text-white/70 text-xs ml-1">save 42%</span></span>
              <span class="font-bold">$49/yr</span>
            </button>
            <button @click="checkout('lifetime')"
                    class="w-full flex items-center justify-between bg-charcoal hover:bg-charcoal/90 text-white font-medium px-5 py-3.5 rounded-xl transition-colors">
              <span>Lifetime</span>
              <span class="font-bold">$54</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../api'

defineProps({ show: Boolean })
const emit = defineEmits(['close'])

const router = useRouter()
const auth = useAuthStore()

async function checkout(priceId) {
  if (!auth.isLoggedIn) {
    router.push(`/register?plan=${priceId}`)
    emit('close')
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
