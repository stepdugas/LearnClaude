<template>
  <div class="max-w-7xl mx-auto px-6 py-12">
    <!-- Skeleton loader -->
    <div v-if="loading" class="lg:grid lg:grid-cols-[1fr_380px] lg:gap-8">
      <div>
        <div class="skeleton h-4 w-32 mb-8"></div>
        <div class="skeleton h-6 w-40 mb-3"></div>
        <div class="skeleton h-10 w-3/4 mb-8"></div>
        <div class="skeleton h-40 w-full mb-6 rounded-2xl"></div>
        <div class="skeleton h-14 w-64 mb-6 rounded-xl"></div>
        <div class="skeleton h-36 w-full rounded-2xl"></div>
      </div>
      <div class="hidden lg:block">
        <div class="skeleton h-[500px] w-full rounded-2xl"></div>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-center py-20">
      <p class="text-muted mb-4">{{ error }}</p>
      <router-link to="/dashboard" class="text-coral hover:text-coral-hover font-medium text-sm">
        Back to Dashboard
      </router-link>
    </div>

    <!-- Lesson Content + AI Tutor -->
    <template v-else-if="lesson">
      <div class="lg:grid lg:grid-cols-[1fr_380px] lg:gap-8">

        <!-- Left: Lesson Content -->
        <div>
          <router-link to="/dashboard" class="inline-flex items-center gap-1.5 text-sm text-muted hover:text-charcoal transition-colors mb-8">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
            </svg>
            Back to Dashboard
          </router-link>

          <div class="mb-8">
            <div class="flex items-center gap-3 mb-3">
              <span class="text-xs font-semibold px-3 py-1 rounded-full"
                    :class="lesson.plan === 'FREE' ? 'bg-surface text-muted' : 'bg-coral/10 text-coral'">
                Tier {{ lesson.tier }} {{ lesson.plan === 'PRO' ? '— PRO' : '— FREE' }}
              </span>
              <span class="text-xs text-muted">Lesson {{ lesson.number }} of 17</span>
            </div>
            <h1 class="text-3xl md:text-4xl font-bold text-charcoal tracking-tight">
              {{ lesson.title }}
            </h1>
          </div>

          <div class="bg-white border border-surface rounded-2xl p-8 mb-6 shadow-sm">
            <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-3">About This Lesson</h2>
            <p class="text-charcoal/80 leading-relaxed">{{ lesson.description }}</p>
          </div>

          <div class="mb-6">
            <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-3">Watch a Video</h2>
            <a :href="youtubeUrl" target="_blank" rel="noopener noreferrer"
               class="inline-flex items-center gap-2.5 bg-white border border-surface hover:border-coral/30 hover:shadow-sm rounded-xl px-5 py-3.5 transition-all group">
              <svg class="w-5 h-5 text-red-500 shrink-0" viewBox="0 0 24 24" fill="currentColor">
                <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.546 12 3.546 12 3.546s-7.505 0-9.377.504A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.504 9.376.504 9.376.504s7.505 0 9.377-.504a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814z"/>
                <path d="M9.545 15.568V8.432L15.818 12l-6.273 3.568z" fill="white"/>
              </svg>
              <span class="text-sm font-medium text-charcoal group-hover:text-coral transition-colors">
                {{ lesson.youtubeSearchQuery }}
              </span>
              <svg class="w-3.5 h-3.5 text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 19.5l15-15m0 0H8.25m11.25 0v11.25" />
              </svg>
            </a>
          </div>

          <div class="bg-coral/5 border border-coral/20 rounded-2xl p-8 mb-8">
            <div class="flex items-center gap-2.5 mb-3">
              <svg class="w-5 h-5 text-coral" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15.59 14.37a6 6 0 01-5.84 7.38v-4.8m5.84-2.58a14.98 14.98 0 006.16-12.12A14.98 14.98 0 009.631 8.41m5.96 5.96a14.926 14.926 0 01-5.841 2.58m-.119-8.54a6 6 0 00-7.381 5.84h4.8m2.581-5.84a14.927 14.927 0 00-2.58 5.84m2.699 2.7c-.103.021-.207.041-.311.06a15.09 15.09 0 01-2.448-2.448 14.9 14.9 0 01.06-.312m-2.24 2.39a4.493 4.493 0 00-1.757 4.306 4.493 4.493 0 004.306-1.758M16.5 9a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
              </svg>
              <h2 class="text-sm font-semibold text-coral uppercase tracking-wider">Hands-On Challenge</h2>
            </div>
            <p class="text-charcoal/80 leading-relaxed">{{ lesson.challenge }}</p>
          </div>

          <!-- Deliverable Section -->
          <DeliverableSection
            v-if="lesson.deliverable"
            :lesson-id="lesson.id"
            :lesson-title="lesson.title"
            :deliverable-description="lesson.deliverable"
            class="mb-8"
          />

          <!-- Mark Complete -->
          <div class="flex items-center gap-4 mb-8 lg:mb-0">
            <button v-if="!completed" @click="markComplete" :disabled="marking"
                    class="bg-coral hover:bg-coral-hover active:scale-[0.97] disabled:opacity-50 text-white font-semibold px-8 py-3 rounded-lg transition-all">
              {{ marking ? 'Saving...' : 'Mark as Complete' }}
            </button>
            <div v-else class="flex items-center gap-2 text-green-600 font-medium animate-celebrate">
              <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              Completed!
            </div>
            <router-link to="/dashboard" class="text-sm text-muted hover:text-charcoal transition-colors">
              Back to Dashboard
            </router-link>
          </div>

          <!-- Mobile AI Tutor toggle -->
          <div class="lg:hidden mt-4">
            <button @click="showMobileTutor = !showMobileTutor"
                    class="w-full flex items-center justify-center gap-2 bg-white border border-surface hover:border-coral/30 hover:shadow-sm rounded-xl px-5 py-3.5 transition-all">
              <svg class="w-5 h-5 text-coral" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M8.625 12a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0H8.25m4.125 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0H12m4.125 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 01-2.555-.337A5.972 5.972 0 015.41 20.97a5.969 5.969 0 01-.474-.065 4.48 4.48 0 00.978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25z" />
              </svg>
              <span class="text-sm font-semibold text-charcoal">
                {{ showMobileTutor ? 'Hide' : 'Show' }} AI Tutor
              </span>
              <svg class="w-4 h-4 text-muted transition-transform" :class="showMobileTutor ? 'rotate-180' : ''" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
              </svg>
            </button>
            <AiTutor v-if="showMobileTutor" :lesson-id="lesson.id" :lesson-title="lesson.title" :mobile="true" class="mt-4" />
          </div>
        </div>

        <!-- Right: AI Tutor (desktop) -->
        <div class="hidden lg:block">
          <AiTutor :lesson-id="lesson.id" :lesson-title="lesson.title" />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AiTutor from '../components/AiTutor.vue'
import DeliverableSection from '../components/DeliverableSection.vue'
import api from '../api'

const route = useRoute()
const router = useRouter()
const showToast = inject('showToast')

const lesson = ref(null)
const loading = ref(true)
const error = ref('')
const completed = ref(false)
const marking = ref(false)
const showMobileTutor = ref(false)

const youtubeUrl = computed(() => {
  if (!lesson.value) return '#'
  return `https://www.youtube.com/results?search_query=${encodeURIComponent(lesson.value.youtubeSearchQuery)}`
})

onMounted(async () => {
  try {
    const [lessonRes, progressRes] = await Promise.all([
      api.get(`/api/lessons/${route.params.id}`),
      api.get('/api/progress')
    ])
    lesson.value = lessonRes.data
    completed.value = progressRes.data.includes(lesson.value.id)
  } catch (e) {
    if (e.response?.status === 403) {
      router.push({ path: '/', hash: '#pricing' })
      return
    }
    error.value = e.response?.data?.error || 'Failed to load lesson'
  } finally {
    loading.value = false
  }
})

async function markComplete() {
  marking.value = true
  try {
    await api.post(`/api/progress/${lesson.value.id}`)
    completed.value = true
    showToast(`Lesson "${lesson.value.title}" complete!`, 'success')
    setTimeout(() => router.push('/dashboard'), 1500)
  } catch (e) {
    showToast('Failed to save progress, please try again', 'error')
  } finally {
    marking.value = false
  }
}
</script>
