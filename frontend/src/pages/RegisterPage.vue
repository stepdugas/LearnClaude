<template>
  <div class="min-h-screen flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <router-link to="/" class="text-2xl font-semibold text-charcoal tracking-tight">
          <span class="text-coral">Learn</span>Claude.ai
        </router-link>
        <h1 class="text-3xl font-bold text-charcoal mt-6 mb-2">Create your account</h1>
        <p class="text-muted">Start learning Claude AI for free</p>
      </div>

      <form @submit.prevent="handleRegister" class="bg-white border border-surface rounded-2xl p-8 space-y-5 shadow-md hover:shadow-lg transition-shadow duration-300">
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
            minlength="8"
            class="w-full px-4 py-3 rounded-lg border border-surface bg-cream text-charcoal placeholder-muted focus:outline-none focus:ring-2 focus:ring-coral/40 focus:border-coral transition-colors"
            placeholder="At least 8 characters"
          />
        </div>

        <div>
          <label for="confirmPassword" class="block text-sm font-medium text-charcoal mb-1.5">Confirm Password</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            required
            class="w-full px-4 py-3 rounded-lg border border-surface bg-cream text-charcoal placeholder-muted focus:outline-none focus:ring-2 focus:ring-coral/40 focus:border-coral transition-colors"
            placeholder="Confirm your password"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-coral hover:bg-coral-hover active:scale-[0.98] disabled:opacity-50 text-white font-semibold py-3 rounded-lg transition-all"
        >
          {{ loading ? 'Creating account...' : 'Create Account' }}
        </button>
      </form>

      <p class="text-center text-sm text-muted mt-6">
        Already have an account?
        <router-link to="/login" class="text-coral hover:text-coral-hover font-medium">Sign in</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const showToast = inject('showToast')

const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }

  loading.value = true
  try {
    await auth.register(email.value, password.value)
    showToast('Account created! Welcome to LearnClaude.ai', 'success')
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.error || 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
