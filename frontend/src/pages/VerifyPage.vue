<template>
  <div class="max-w-xl mx-auto px-6 py-16">
    <!-- Loading -->
    <div v-if="loading" class="text-center py-20">
      <div class="skeleton h-8 w-48 mx-auto mb-4"></div>
      <div class="skeleton h-40 w-full rounded-2xl"></div>
    </div>

    <!-- Valid certificate -->
    <template v-else-if="cert && cert.valid">
      <div class="text-center mb-8">
        <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold text-charcoal mb-1">This certificate is valid</h1>
        <p class="text-muted text-sm">Verified by LearnClaude.ai</p>
      </div>

      <div class="bg-white border border-surface rounded-2xl p-8 shadow-sm">
        <div class="space-y-4">
          <div>
            <p class="text-xs text-muted uppercase tracking-wider mb-1">Recipient</p>
            <p class="text-lg font-semibold text-charcoal">{{ cert.userName }}</p>
          </div>
          <div>
            <p class="text-xs text-muted uppercase tracking-wider mb-1">Course</p>
            <p class="text-lg font-semibold text-charcoal">{{ cert.courseTitle }}</p>
          </div>
          <div>
            <p class="text-xs text-muted uppercase tracking-wider mb-1">Completed</p>
            <p class="text-charcoal">{{ formatDate(cert.completedAt) }}</p>
          </div>
          <div>
            <p class="text-xs text-muted uppercase tracking-wider mb-1">Certificate ID</p>
            <p class="text-xs text-muted font-mono">{{ cert.certificateId }}</p>
          </div>
        </div>
      </div>

      <div class="text-center mt-8">
        <router-link to="/" class="text-sm text-muted hover:text-charcoal transition-colors">
          <span class="text-coral font-semibold">Learn</span>Claude.ai — Interactive Claude AI Courses
        </router-link>
      </div>
    </template>

    <!-- Invalid -->
    <template v-else>
      <div class="text-center py-20">
        <div class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold text-charcoal mb-2">Certificate not found</h1>
        <p class="text-muted mb-6">This certificate ID is invalid or does not exist.</p>
        <router-link to="/" class="text-coral hover:text-coral-hover font-medium">Go to LearnClaude.ai</router-link>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { API_BASE } from '../api'

const route = useRoute()
const loading = ref(true)
const cert = ref(null)

function formatDate(isoStr) {
  if (!isoStr) return ''
  return new Date(isoStr).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })
}

onMounted(async () => {
  try {
    const res = await fetch(`${API_BASE}/api/certificate/verify/${route.params.certificateId}`)
    cert.value = await res.json()
  } catch {
    cert.value = { valid: false }
  } finally {
    loading.value = false
  }
})
</script>
