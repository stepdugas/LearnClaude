<template>
  <div>
    <useHead>
      <title>Learn How to Use Claude AI — Interactive Lessons | LearnClaude.ai</title>
      <meta name="description" content="17 hands-on lessons teaching every Claude AI tool — from basic chat to the API and multi-agent orchestration. Free lessons available. Powered by AI tutoring." />
      <meta property="og:title" content="Learn How to Use Claude AI — Interactive Lessons" />
      <meta property="og:description" content="From Claude basics to advanced agents — 17 hands-on lessons powered by AI tutoring." />
      <link rel="canonical" href="https://learnclaudeai.com/learn" />
    </useHead>

    <!-- Hero banner -->
    <section class="bg-coral/5 border-b border-coral/10 py-6 px-6">
      <div class="max-w-5xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
        <div>
          <p class="text-sm font-semibold text-coral">New to Claude?</p>
          <p class="text-charcoal font-medium">Start with Lesson 1 — it's free. No account needed to preview.</p>
        </div>
        <router-link to="/learn/claude-chat-basics" class="bg-coral hover:bg-coral-hover active:scale-[0.97] text-white font-semibold px-6 py-2.5 rounded-lg text-sm transition-all shrink-0">
          Start Lesson 1
        </router-link>
      </div>
    </section>

    <div class="max-w-5xl mx-auto px-6 py-16">
      <h1 class="text-4xl md:text-5xl font-bold text-charcoal tracking-tight mb-4">
        Learn How to Use Claude AI
      </h1>
      <p class="text-lg text-muted max-w-2xl mb-10">
        From Claude basics to advanced agents — 17 hands-on lessons powered by AI tutoring.
        Free lessons cover free Claude features. Pro lessons unlock the full ecosystem.
      </p>

      <!-- Tier filters -->
      <div class="flex flex-wrap gap-2 mb-10">
        <button v-for="f in filters" :key="f.value" @click="activeFilter = f.value"
                class="px-4 py-2 rounded-full text-sm font-medium transition-all"
                :class="activeFilter === f.value
                  ? 'bg-coral text-white'
                  : 'bg-surface text-muted hover:text-charcoal'">
          {{ f.label }}
        </button>
      </div>

      <!-- Skeleton -->
      <div v-if="loading" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="i in 6" :key="i" class="skeleton h-64 rounded-2xl"></div>
      </div>

      <!-- Lesson grid -->
      <div v-else class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <router-link v-for="lesson in filteredLessons" :key="lesson.id"
                     :to="`/learn/${lesson.slug}`"
                     class="group bg-white border border-surface rounded-2xl p-6 hover:shadow-md hover:-translate-y-1 transition-all duration-300">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-9 h-9 rounded-full bg-coral/10 text-coral flex items-center justify-center text-sm font-bold shrink-0">
              {{ lesson.number }}
            </div>
            <div class="flex gap-2">
              <span class="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full"
                    :class="lesson.plan === 'FREE' ? 'bg-green-100 text-green-700' : 'bg-coral/10 text-coral'">
                {{ lesson.plan }}
              </span>
              <span class="text-[10px] font-medium text-muted bg-surface px-2 py-0.5 rounded-full">
                Tier {{ lesson.tier }}
              </span>
            </div>
          </div>

          <h2 class="text-lg font-semibold text-charcoal mb-2 group-hover:text-coral transition-colors">
            {{ lesson.title }}
          </h2>

          <p class="text-sm text-muted leading-relaxed line-clamp-3 mb-4">
            {{ lesson.description }}
          </p>

          <span class="text-sm font-medium text-coral group-hover:underline">
            {{ lesson.plan === 'FREE' ? 'Start Free Lesson' : 'Preview Lesson' }} &rarr;
          </span>
        </router-link>
      </div>

      <!-- Bottom CTA -->
      <div class="mt-16 text-center bg-surface rounded-2xl p-10">
        <h2 class="text-2xl font-bold text-charcoal mb-3">Ready to master Claude?</h2>
        <p class="text-muted mb-6">Create a free account to track progress and chat with your AI tutor.</p>
        <router-link to="/register" class="bg-coral hover:bg-coral-hover active:scale-[0.97] text-white font-semibold px-8 py-3.5 rounded-lg text-lg transition-all">
          Get Started Free
        </router-link>
      </div>
    </div>

    <!-- SEO footer for /learn pages -->
    <footer class="border-t border-surface py-8 px-6">
      <div class="max-w-5xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4 text-sm text-muted">
        <router-link to="/" class="font-semibold text-charcoal">
          <span class="text-coral">Learn</span>Claude.ai
        </router-link>
        <div class="flex gap-6">
          <router-link to="/" class="hover:text-charcoal transition-colors">Home</router-link>
          <router-link to="/learn" class="hover:text-charcoal transition-colors">All Lessons</router-link>
          <router-link to="/register" class="hover:text-charcoal transition-colors">Sign Up</router-link>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useHead } from '@unhead/vue'
import { API_BASE } from '../api'

useHead({
  title: 'Learn How to Use Claude AI — Interactive Lessons | LearnClaude.ai',
  meta: [
    { name: 'description', content: '17 hands-on lessons teaching every Claude AI tool — from basic chat to the API and multi-agent orchestration. Free lessons available.' },
    { property: 'og:title', content: 'Learn How to Use Claude AI — Interactive Lessons' },
    { property: 'og:description', content: 'From Claude basics to advanced agents — 17 hands-on lessons powered by AI tutoring.' },
  ],
  link: [{ rel: 'canonical', href: 'https://learnclaudeai.com/learn' }]
})

const lessons = ref([])
const loading = ref(true)
const activeFilter = ref('all')

const filters = [
  { label: 'All Lessons', value: 'all' },
  { label: 'Free / Basics', value: '1' },
  { label: 'Claude Pro', value: '2' },
  { label: 'Power Tools', value: '3' },
]

const filteredLessons = computed(() => {
  if (activeFilter.value === 'all') return lessons.value
  return lessons.value.filter(l => l.tier === parseInt(activeFilter.value))
})

onMounted(async () => {
  try {
    const res = await fetch(`${API_BASE}/api/public/lessons`)
    lessons.value = await res.json()
  } catch (e) {
    console.error('Failed to load lessons', e)
  } finally {
    loading.value = false
  }
})
</script>
