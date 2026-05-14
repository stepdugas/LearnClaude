<template>
  <div class="max-w-3xl mx-auto px-6 py-16">
    <!-- Loading -->
    <div v-if="loading" class="text-center py-20">
      <div class="skeleton h-8 w-64 mx-auto mb-4"></div>
      <div class="skeleton h-64 w-full rounded-2xl"></div>
    </div>

    <!-- Not eligible yet -->
    <template v-else-if="!status.eligible">
      <div class="text-center mb-10">
        <h1 class="text-3xl font-bold text-charcoal mb-3">Your Certificate</h1>
        <p class="text-muted text-lg">Complete all lessons to unlock your certificate of completion.</p>
      </div>

      <div class="bg-white border border-surface rounded-2xl p-8 shadow-sm text-center">
        <div class="w-20 h-20 rounded-full bg-surface flex items-center justify-center mx-auto mb-6">
          <svg class="w-10 h-10 text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
          </svg>
        </div>
        <div class="mb-6">
          <span class="text-4xl font-bold text-charcoal">{{ status.completed }}</span>
          <span class="text-lg text-muted"> / {{ status.total }} lessons complete</span>
        </div>
        <div class="w-full bg-surface rounded-full h-3 max-w-sm mx-auto mb-6">
          <div class="bg-coral h-3 rounded-full transition-all duration-700"
               :style="{ width: Math.round((status.completed / status.total) * 100) + '%' }"></div>
        </div>
        <p class="text-muted mb-6">{{ status.total - status.completed }} lesson{{ status.total - status.completed !== 1 ? 's' : '' }} remaining</p>
        <router-link to="/dashboard" class="inline-block bg-coral hover:bg-coral-hover active:scale-[0.97] text-white font-semibold px-8 py-3 rounded-lg transition-all">
          Continue Learning
        </router-link>
      </div>
    </template>

    <!-- Certificate ready -->
    <template v-else>
      <div class="text-center mb-10">
        <div class="text-5xl mb-4 animate-celebrate">🎓</div>
        <h1 class="text-3xl font-bold text-charcoal mb-3">Congratulations!</h1>
        <p class="text-muted text-lg">You've completed The Complete Claude AI Course.</p>
      </div>

      <!-- Certificate preview card -->
      <div class="bg-white border-2 border-coral/20 rounded-2xl p-10 shadow-lg mb-8 relative overflow-hidden">
        <!-- Top accent -->
        <div class="absolute top-0 left-0 right-0 h-2 bg-coral"></div>

        <div class="text-center">
          <p class="text-sm text-muted mb-6">LearnClaude.ai</p>
          <h2 class="text-2xl font-bold text-charcoal mb-2">Certificate of Completion</h2>
          <div class="w-16 h-0.5 bg-coral mx-auto my-4"></div>
          <p class="text-sm text-muted mb-1">This certifies that</p>
          <p class="text-2xl font-bold text-coral my-3">{{ auth.user?.email }}</p>
          <p class="text-sm text-muted mb-1">has successfully completed</p>
          <p class="text-xl font-bold text-charcoal mt-2">The Complete Claude AI Course</p>
          <p class="text-xs text-muted mt-1 mb-6">17 lessons covering the full Claude ecosystem</p>

          <p v-if="status.issuedAt" class="text-xs text-muted">
            Completed on {{ formatDate(status.issuedAt) }}
          </p>
          <p v-if="status.certificateId" class="text-[10px] text-muted/60 mt-1">
            ID: {{ status.certificateId }}
          </p>
        </div>

        <!-- Bottom accent -->
        <div class="absolute bottom-0 left-0 right-0 h-2 bg-coral"></div>
      </div>

      <!-- Actions -->
      <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
        <button @click="downloadCertificate" :disabled="downloading"
                class="bg-coral hover:bg-coral-hover active:scale-[0.97] disabled:opacity-50 text-white font-semibold px-8 py-3.5 rounded-lg transition-all flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5M16.5 12L12 16.5m0 0L7.5 12m4.5 4.5V3" />
          </svg>
          {{ downloading ? 'Downloading...' : 'Download PDF' }}
        </button>

        <a v-if="status.certificateId"
           :href="linkedInShareUrl"
           target="_blank" rel="noopener noreferrer"
           class="bg-[#0A66C2] hover:bg-[#004182] active:scale-[0.97] text-white font-semibold px-8 py-3.5 rounded-lg transition-all flex items-center gap-2">
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 01-2.063-2.065 2.064 2.064 0 112.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
          </svg>
          Share on LinkedIn
        </a>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../api'

const auth = useAuthStore()
const loading = ref(true)
const downloading = ref(false)
const status = ref({ completed: 0, total: 17, eligible: false, certificateId: '', issuedAt: '' })

const linkedInShareUrl = computed(() => {
  if (!status.value.certificateId) return '#'
  const verifyUrl = `https://learnclaudeai.com/verify/${status.value.certificateId}`
  return `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(verifyUrl)}`
})

function formatDate(isoStr) {
  if (!isoStr) return ''
  return new Date(isoStr).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })
}

onMounted(async () => {
  try {
    const { data } = await api.get('/api/certificate/status')
    status.value = data
  } catch (e) {
    console.error('Failed to load certificate status', e)
  } finally {
    loading.value = false
  }
})

async function downloadCertificate() {
  downloading.value = true
  try {
    const response = await api.get('/api/certificate', { responseType: 'blob' })
    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }))
    const link = document.createElement('a')
    link.href = url
    link.download = 'learnclaudeai-certificate.pdf'
    link.click()
    window.URL.revokeObjectURL(url)

    // Refresh status to get certificate ID if it was just created
    const { data } = await api.get('/api/certificate/status')
    status.value = data
  } catch (e) {
    console.error('Failed to download certificate', e)
  } finally {
    downloading.value = false
  }
}
</script>
