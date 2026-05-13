<template>
  <div class="min-h-screen flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <router-link to="/" class="text-2xl font-semibold text-charcoal tracking-tight">
          <span class="text-coral">Learn</span>Claude.ai
        </router-link>
        <h1 class="text-3xl font-bold text-charcoal mt-6 mb-2">Welcome back</h1>
        <p class="text-muted">Sign in to continue learning</p>
      </div>

      <form @submit.prevent="handleLogin" class="bg-white border border-surface rounded-2xl p-8 space-y-5 shadow-md">
        <div v-if="error" class="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg">
          {{ error }}
        </div>

        <div>
          <label for="email" class="block text-sm font-medium text-charcoal mb-1.5">Email</label>
          <input
            id="email"
            v-model="email"
            type="email"
            required
            class="w-full px-4 py-3 rounded-lg border border-surface bg-cream text-charcoal placeholder-muted focus:outline-none focus:ring-2 focus:ring-coral/40 focus:border-coral transition-colors"
            placeholder="you@example.com"
          />
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-charcoal mb-1.5">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            required
            class="w-full px-4 py-3 rounded-lg border border-surface bg-cream text-charcoal placeholder-muted focus:outline-none focus:ring-2 focus:ring-coral/40 focus:border-coral transition-colors"
            placeholder="Your password"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-coral hover:bg-coral-hover active:scale-[0.98] disabled:opacity-50 text-white font-semibold py-3 rounded-lg transition-all"
        >
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>

      <p class="text-center text-sm text-muted mt-6">
        Don't have an account?
        <router-link to="/register" class="text-coral hover:text-coral-hover font-medium">Create one</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const showToast = inject('showToast')

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(email.value, password.value)
    showToast('Welcome back!', 'success')
    router.push(route.query.redirect || '/dashboard')
  } catch (e) {
    error.value = e.response?.data?.error || 'Login failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
