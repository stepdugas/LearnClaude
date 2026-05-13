<template>
  <section class="py-20 px-6">
    <div class="max-w-5xl mx-auto">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-charcoal tracking-tight mb-4">
          How it works
        </h2>
        <p class="text-muted text-lg max-w-xl mx-auto">
          Three steps to Claude mastery. No fluff, just hands-on learning.
        </p>
      </div>

      <div class="grid md:grid-cols-3 gap-8">
        <div v-for="step in steps" :key="step.num" class="text-center">
          <div class="w-14 h-14 rounded-2xl bg-coral/10 flex items-center justify-center mx-auto mb-5">
            <span class="text-2xl" v-html="step.icon"></span>
          </div>
          <div class="text-xs font-semibold text-coral mb-2">STEP {{ step.num }}</div>
          <h3 class="text-lg font-semibold text-charcoal mb-2">{{ step.title }}</h3>
          <p class="text-muted text-sm leading-relaxed">{{ step.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const steps = [
  {
    num: 1,
    icon: '📖',
    title: 'Pick a lesson',
    description: 'Choose from 17 structured lessons covering every Claude tool — from basic chat to the API and multi-agent orchestration.'
  },
  {
    num: 2,
    icon: '💬',
    title: 'Chat with your AI tutor',
    description: 'Every lesson comes with a personal AI tutor that knows the topic inside out. Ask questions, get feedback, learn by doing.'
  },
  {
    num: 3,
    icon: '🏆',
    title: 'Complete the challenge',
    description: 'Each lesson ends with a hands-on challenge. Apply what you learned, mark it complete, and track your progress.'
  }
]
</script>
