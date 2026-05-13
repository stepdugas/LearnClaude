<template>
  <div>
    <!-- Loading -->
    <div v-if="loading" class="max-w-3xl mx-auto px-6 py-16">
      <div class="skeleton h-8 w-48 mb-4"></div>
      <div class="skeleton h-12 w-3/4 mb-8"></div>
      <div class="skeleton h-40 w-full rounded-2xl mb-6"></div>
      <div class="skeleton h-32 w-full rounded-2xl"></div>
    </div>

    <!-- 404 -->
    <div v-else-if="!lesson" class="text-center py-20 px-6">
      <h1 class="text-2xl font-bold text-charcoal mb-3">Lesson not found</h1>
      <router-link to="/learn" class="text-coral hover:text-coral-hover font-medium">Browse all lessons</router-link>
    </div>

    <!-- Lesson content -->
    <template v-else>
      <div class="max-w-3xl mx-auto px-6 py-16">
        <!-- Breadcrumb -->
        <nav class="flex items-center gap-2 text-sm text-muted mb-8">
          <router-link to="/learn" class="hover:text-charcoal transition-colors">All Lessons</router-link>
          <span>/</span>
          <span class="text-charcoal">Lesson {{ lesson.number }}</span>
        </nav>

        <!-- Badges -->
        <div class="flex items-center gap-3 mb-4">
          <span class="text-xs font-bold uppercase tracking-wider px-3 py-1 rounded-full"
                :class="lesson.plan === 'FREE' ? 'bg-green-100 text-green-700' : 'bg-coral/10 text-coral'">
            {{ lesson.plan }}
          </span>
          <span class="text-xs font-medium text-muted bg-surface px-3 py-1 rounded-full">
            Tier {{ lesson.tier }} · Lesson {{ lesson.number }} of {{ totalLessons }}
          </span>
        </div>

        <!-- H1 -->
        <h1 class="text-4xl md:text-5xl font-bold text-charcoal tracking-tight leading-tight mb-6">
          How to Use {{ lesson.title }} in Claude
        </h1>

        <!-- Intro -->
        <p class="text-lg text-muted leading-relaxed mb-10">
          {{ lesson.description }}
        </p>

        <!-- What you'll learn -->
        <div class="bg-white border border-surface rounded-2xl p-8 mb-8 shadow-sm">
          <h2 class="text-xl font-semibold text-charcoal mb-5">What you'll learn</h2>
          <ul class="space-y-3">
            <li v-for="point in learnPoints" :key="point" class="flex items-start gap-3">
              <svg class="w-5 h-5 text-coral mt-0.5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span class="text-charcoal/80 leading-relaxed">{{ point }}</span>
            </li>
          </ul>
        </div>

        <!-- Free tip -->
        <div class="bg-coral/5 border border-coral/20 rounded-2xl p-8 mb-8">
          <div class="flex items-center gap-2 mb-3">
            <svg class="w-5 h-5 text-coral" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 18v-5.25m0 0a6.01 6.01 0 001.5-.189m-1.5.189a6.01 6.01 0 01-1.5-.189m3.75 7.478a12.06 12.06 0 01-4.5 0m3.75 2.383a14.406 14.406 0 01-3 0M14.25 18v-.192c0-.983.658-1.823 1.508-2.316a7.5 7.5 0 10-7.517 0c.85.493 1.509 1.333 1.509 2.316V18" />
            </svg>
            <h2 class="text-sm font-semibold text-coral uppercase tracking-wider">Free Tip</h2>
          </div>
          <p class="text-charcoal/80 leading-relaxed">{{ freeTip }}</p>
        </div>

        <!-- CTA -->
        <div class="bg-charcoal rounded-2xl p-10 text-center mb-8">
          <h2 class="text-2xl font-bold text-white mb-3">Practice this interactively with an AI tutor</h2>
          <p class="text-white/70 mb-6 max-w-md mx-auto">
            Get a personal AI tutor that knows this topic inside out. Ask questions, get feedback, complete the challenge.
          </p>
          <router-link to="/register" class="inline-block bg-coral hover:bg-coral-hover active:scale-[0.97] text-white font-semibold px-8 py-3.5 rounded-lg text-lg transition-all">
            Start Free Lesson
          </router-link>
        </div>

        <!-- Challenge preview (blurred bottom) -->
        <div class="bg-white border border-surface rounded-2xl p-8 mb-8 shadow-sm relative overflow-hidden">
          <div class="flex items-center gap-2 mb-3">
            <svg class="w-5 h-5 text-coral" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.59 14.37a6 6 0 01-5.84 7.38v-4.8m5.84-2.58a14.98 14.98 0 006.16-12.12A14.98 14.98 0 009.631 8.41m5.96 5.96a14.926 14.926 0 01-5.841 2.58m-.119-8.54a6 6 0 00-7.381 5.84h4.8m2.581-5.84a14.927 14.927 0 00-2.58 5.84m2.699 2.7c-.103.021-.207.041-.311.06a15.09 15.09 0 01-2.448-2.448 14.9 14.9 0 01.06-.312m-2.24 2.39a4.493 4.493 0 00-1.757 4.306 4.493 4.493 0 004.306-1.758M16.5 9a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
            </svg>
            <h2 class="text-sm font-semibold text-coral uppercase tracking-wider">Hands-On Challenge Preview</h2>
          </div>
          <p class="text-charcoal/80 leading-relaxed">{{ lesson.challenge }}</p>
          <!-- Blur overlay -->
          <div class="absolute bottom-0 left-0 right-0 h-24 bg-gradient-to-t from-white via-white/90 to-transparent flex items-end justify-center pb-4">
            <router-link to="/register" class="bg-coral hover:bg-coral-hover active:scale-[0.97] text-white text-sm font-semibold px-6 py-2.5 rounded-lg transition-all">
              Unlock Full Lesson
            </router-link>
          </div>
        </div>

        <!-- Navigation -->
        <div class="flex items-center justify-between py-6 border-t border-surface mb-10">
          <router-link v-if="prevLesson" :to="`/learn/${prevLesson.slug}`"
                       class="flex items-center gap-2 text-sm text-muted hover:text-coral transition-colors">
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
            </svg>
            {{ prevLesson.title }}
          </router-link>
          <span v-else></span>
          <router-link v-if="nextLesson" :to="`/learn/${nextLesson.slug}`"
                       class="flex items-center gap-2 text-sm text-muted hover:text-coral transition-colors">
            {{ nextLesson.title }}
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </router-link>
        </div>

        <!-- Related lessons -->
        <div class="mb-10">
          <h2 class="text-xl font-semibold text-charcoal mb-6">Related Lessons</h2>
          <div class="grid md:grid-cols-3 gap-4">
            <router-link v-for="rel in relatedLessons" :key="rel.id" :to="`/learn/${rel.slug}`"
                         class="group bg-white border border-surface rounded-xl p-5 hover:shadow-md hover:-translate-y-0.5 transition-all duration-200">
              <div class="flex items-center gap-2 mb-2">
                <span class="text-xs font-bold text-coral">{{ rel.number }}.</span>
                <span class="text-[10px] font-bold uppercase px-2 py-0.5 rounded-full"
                      :class="rel.plan === 'FREE' ? 'bg-green-100 text-green-700' : 'bg-coral/10 text-coral'">
                  {{ rel.plan }}
                </span>
              </div>
              <h3 class="text-sm font-semibold text-charcoal group-hover:text-coral transition-colors">{{ rel.title }}</h3>
            </router-link>
          </div>
        </div>
      </div>

      <!-- SEO footer -->
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
    </template>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useHead } from '@unhead/vue'
import { API_BASE } from '../api'

const route = useRoute()

const lesson = ref(null)
const allLessons = ref([])
const loading = ref(true)

const totalLessons = computed(() => allLessons.value.length)

const prevLesson = computed(() => {
  if (!lesson.value) return null
  return allLessons.value.find(l => l.number === lesson.value.number - 1)
})

const nextLesson = computed(() => {
  if (!lesson.value) return null
  return allLessons.value.find(l => l.number === lesson.value.number + 1)
})

const relatedLessons = computed(() => {
  if (!lesson.value) return []
  return allLessons.value
    .filter(l => l.id !== lesson.value.id)
    .filter(l => l.tier === lesson.value.tier || Math.abs(l.number - lesson.value.number) <= 3)
    .slice(0, 3)
})

// Generate learning points from the lesson description
const learnPoints = computed(() => {
  if (!lesson.value) return []
  const t = lesson.value.title
  return [
    `What ${t} is and how it fits into the Claude ecosystem`,
    `Step-by-step setup and configuration`,
    `Practical use cases and real-world examples`,
    `Best practices and common mistakes to avoid`,
    `How to combine ${t} with other Claude features`
  ]
})

const freeTip = computed(() => {
  if (!lesson.value) return ''
  const tips = {
    'claude-chat-basics': 'Start your prompts with a clear role: "You are a [role]. Help me [task]." This simple framework instantly improves Claude\'s responses compared to bare questions.',
    'writing-great-prompts': 'The most powerful prompt technique is giving Claude an example of what you want. Show one input-output pair and Claude will match the pattern for all future inputs.',
    'files-image-uploads': 'When uploading a long PDF, tell Claude exactly what to look for: "Read pages 5-10 and extract all dollar amounts mentioned." Focused instructions get dramatically better results than "summarize this."',
    'artifacts-markdown': 'Ask Claude to "create an Artifact" explicitly — it triggers a different mode where Claude generates standalone, editable documents. Try: "Create an Artifact with a React component that..."',
    'claude-plans-free-vs-pro': 'Free Claude gives you access to Claude Sonnet and basic features. The biggest Pro upgrade is Projects with system prompts — it turns Claude from a chatbot into a customized assistant.',
    'projects-system-prompts': 'The best system prompts are specific about format, not just tone. Instead of "be professional," try "respond in bullet points, max 3 sentences each, with a one-line summary at the top."',
    'memory-personalization': 'Memory works best when you tell Claude facts it can reference later: "Remember: I\'m a marketing manager at a SaaS company." Don\'t store instructions — those belong in Project system prompts.',
    'web-search-deep-research': 'Deep Research works best when you give it a specific angle, not just a topic. Instead of "research AI," try "compare the pricing and features of the top 5 AI coding assistants as of 2025."',
    'connectors-integrations': 'Start with Google Drive integration — it\'s the most immediately useful. Claude can read your docs, spreadsheets, and slides directly, which means you can ask questions about your own data.',
    'claude-mobile-app': 'Voice mode on the Claude mobile app is surprisingly powerful for brainstorming. Talk through your ideas and Claude transcribes + responds. It\'s like having a smart colleague on call.',
    'cowork-desktop-app': 'Cowork shines for repetitive tasks. Let Claude watch you do something once, then ask it to help you do the same thing faster. It learns from your screen context.',
    'claude-in-chrome': 'The Chrome extension is most useful on data-heavy pages. Navigate to a competitor\'s pricing page and ask Claude to extract and compare all plans in a table.',
    'claude-excel-powerpoint': 'For Excel, describe your formula in plain English: "Calculate the running average of column B, but only for rows where column A is \'active\'." Claude will write the exact formula.',
    'claude-code': 'Start Claude Code with a clear, small task: "Add input validation to the signup form." Let it read the codebase first, then make targeted changes. Small asks get better results than big rewrites.',
    'claude-api': 'Always use streaming for any user-facing API integration. Non-streaming calls make users wait 5-15 seconds staring at nothing. Streaming shows text appearing in real-time.',
    'agents-orchestration': 'The key to multi-agent workflows is giving each agent a very narrow job. A "research agent" that also writes and edits will do all three poorly. Separate concerns.',
    'ai-powered-artifacts': 'The trick to AI-powered Artifacts is using the Artifact as a UI shell and Claude as the brain. Build the interface in the Artifact, then have it call Claude for the intelligence.'
  }
  return tips[lesson.value.slug] || lesson.value.description
})

// Dynamic head tags
useHead(computed(() => {
  if (!lesson.value) return {}
  const title = `${lesson.value.title} — Learn Claude AI | LearnClaude.ai`
  const desc = `Learn how to use ${lesson.value.title} in Claude AI. Interactive lesson with AI tutor, hands-on challenge, and practical tips. ${lesson.value.plan === 'FREE' ? 'Free lesson.' : ''}`
  return {
    title,
    meta: [
      { name: 'description', content: desc.slice(0, 155) },
      { property: 'og:title', content: title },
      { property: 'og:description', content: desc.slice(0, 155) },
      { property: 'og:type', content: 'article' },
    ],
    link: [
      { rel: 'canonical', href: `https://learnclaudeai.com/learn/${lesson.value.slug}` }
    ]
  }
}))

async function loadLesson(slug) {
  loading.value = true
  try {
    const [lessonRes, allRes] = await Promise.all([
      fetch(`${API_BASE}/api/public/lessons/${slug}`),
      fetch(`${API_BASE}/api/public/lessons`)
    ])
    if (lessonRes.ok) {
      lesson.value = await lessonRes.json()
    } else {
      lesson.value = null
    }
    allLessons.value = await allRes.json()
  } catch (e) {
    console.error('Failed to load lesson', e)
    lesson.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => loadLesson(route.params.slug))

watch(() => route.params.slug, (newSlug) => {
  if (newSlug) loadLesson(newSlug)
})
</script>
