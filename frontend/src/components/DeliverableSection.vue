<template>
  <div class="bg-white border border-surface rounded-2xl p-8 shadow-sm">
    <!-- Header -->
    <div class="flex items-center gap-2.5 mb-4">
      <svg class="w-5 h-5 text-coral" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 18.75h-9m9 0a3 3 0 013 3h-15a3 3 0 013-3m9 0v-4.5A3.375 3.375 0 0012.75 10.5h-.75a.75.75 0 01-.75-.75V6.75a3 3 0 116 0v3a.75.75 0 01-.75.75h-.75A3.375 3.375 0 0012 14.25v4.5m-3-9V6.75a3 3 0 016 0" />
      </svg>
      <h2 class="text-sm font-semibold text-charcoal uppercase tracking-wider">Your Deliverable</h2>
    </div>

    <!-- Deliverable description -->
    <p class="text-coral font-medium text-sm mb-5">{{ deliverableDescription }}</p>

    <!-- Submission textarea -->
    <textarea
      v-model="submissionText"
      :disabled="submitting"
      placeholder="Describe what you built..."
      rows="4"
      class="w-full bg-cream border border-surface rounded-xl px-4 py-3 text-sm text-charcoal placeholder-muted focus:outline-none focus:ring-2 focus:ring-coral/30 focus:border-coral disabled:opacity-50 transition-colors resize-none"
    ></textarea>

    <!-- Submit button -->
    <button
      @click="submitDeliverable"
      :disabled="submitting || !submissionText.trim()"
      class="mt-3 bg-coral hover:bg-coral-hover active:scale-[0.97] disabled:opacity-50 text-white font-semibold px-6 py-2.5 rounded-lg transition-all text-sm"
    >
      <span v-if="submitting" class="flex items-center gap-2">
        <span class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
        Reviewing your work...
      </span>
      <span v-else>Submit for Review</span>
    </button>

    <!-- Feedback card -->
    <div v-if="feedback" class="mt-5 rounded-xl p-5 border-l-4"
         :class="passed ? 'border-green-500 bg-green-50' : 'border-amber-500 bg-amber-50'">
      <div v-html="renderedFeedback" class="text-sm text-charcoal/85 leading-relaxed"></div>

      <!-- Passed state -->
      <div v-if="passed" class="flex items-center gap-2 mt-3 text-green-600 font-medium text-sm">
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
        </svg>
        Deliverable complete!
      </div>

      <!-- Not yet passed -->
      <p v-else class="text-xs text-amber-700 mt-3">
        Your tutor thinks there's more to do -- but you can mark complete if you're ready.
      </p>
    </div>

    <!-- Error -->
    <div v-if="errorMsg" class="mt-3 text-xs text-red-500 bg-red-50 rounded-lg px-3 py-2">
      {{ errorMsg }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import api from '../api'

marked.setOptions({ breaks: true, gfm: true })

const props = defineProps({
  lessonId: { type: [Number, String], required: true },
  lessonTitle: { type: String, required: true },
  deliverableDescription: { type: String, required: true }
})

const submissionText = ref('')
const feedback = ref('')
const passed = ref(false)
const submitting = ref(false)
const errorMsg = ref('')

const renderedFeedback = computed(() => {
  if (!feedback.value) return ''
  const html = marked.parse(feedback.value)
  return DOMPurify.sanitize(html)
})

onMounted(async () => {
  try {
    const { data } = await api.get(`/api/deliverables/${props.lessonId}`)
    if (data) {
      submissionText.value = data.submission || ''
      feedback.value = data.feedback || ''
      // Re-check passed status from stored feedback
      if (feedback.value) {
        passed.value = feedback.value.includes('PASSED') && !feedback.value.includes('NOT_YET_PASSED')
      }
    }
  } catch (e) {
    // No existing submission, that's fine
  }
})

async function submitDeliverable() {
  if (!submissionText.value.trim() || submitting.value) return

  submitting.value = true
  errorMsg.value = ''
  feedback.value = ''

  try {
    const { data } = await api.post(`/api/deliverables/${props.lessonId}`, {
      submission: submissionText.value.trim()
    })
    feedback.value = data.feedback
    passed.value = data.passed
  } catch (e) {
    errorMsg.value = e.response?.data?.error || 'Failed to submit. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>
