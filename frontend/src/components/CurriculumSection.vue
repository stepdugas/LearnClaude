<template>
  <section id="curriculum" class="py-20 px-6">
    <div class="max-w-5xl mx-auto">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-charcoal tracking-tight mb-4">
          What you'll learn
        </h2>
        <p class="text-muted text-lg max-w-xl mx-auto">
          Free lessons cover free Claude features. Pro lessons cover Pro features.
          You can always practice what you learn.
        </p>
      </div>

      <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="tier in tiers" :key="tier.name"
             class="bg-surface rounded-2xl p-8 transition-all duration-300 hover:shadow-md hover:-translate-y-1"
             :class="tier.free ? 'border-2 border-coral/20' : ''">
          <div class="flex items-center gap-3 mb-4">
            <span class="text-2xl">{{ tier.icon }}</span>
            <div>
              <h3 class="font-semibold text-charcoal text-lg">{{ tier.name }}</h3>
              <span class="text-sm text-muted">{{ tier.subtitle }}</span>
            </div>
          </div>
          <div class="flex items-center gap-2 mb-5">
            <span v-if="tier.free" class="text-xs font-semibold bg-coral/10 text-coral px-3 py-1 rounded-full">
              FREE
            </span>
            <span class="text-xs text-muted">{{ tier.claudeLevel }}</span>
          </div>
          <ul class="space-y-3">
            <li v-for="topic in tier.topics" :key="topic" class="flex items-start gap-3">
              <svg class="w-5 h-5 text-coral mt-0.5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span class="text-charcoal/80 text-sm leading-relaxed">{{ topic }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const tiers = [
  {
    name: 'Tier 1 — The Basics',
    subtitle: '5 lessons',
    icon: '🌱',
    free: true,
    claudeLevel: 'Works with free Claude',
    topics: [
      'Claude.ai Chat',
      'Writing Great Prompts',
      'Files & Image Uploads',
      'Artifacts & Markdown',
      'Understanding Claude Plans'
    ]
  },
  {
    name: 'Tier 2 — Claude Pro',
    subtitle: '6 lessons',
    icon: '🔧',
    free: false,
    claudeLevel: 'Requires Claude Pro',
    topics: [
      'Projects & System Prompts',
      'Memory & Personalization',
      'Web Search & Deep Research',
      'Connectors & Integrations',
      'Claude Mobile App',
      'Cowork (Desktop App)'
    ]
  },
  {
    name: 'Tier 3 — Power Tools',
    subtitle: '6 lessons',
    icon: '⚡',
    free: false,
    claudeLevel: 'Developer & advanced',
    topics: [
      'Claude in Chrome (Browser Agent)',
      'Claude in Excel & PowerPoint',
      'Claude Code',
      'Claude API & Building Apps',
      'Agents & Orchestration',
      'AI-Powered Artifacts'
    ]
  }
]
</script>
