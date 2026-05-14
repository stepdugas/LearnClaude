<template>
  <section class="py-20 px-6">
    <div class="max-w-2xl mx-auto">
      <h2 class="text-3xl font-bold text-charcoal text-center mb-12 tracking-tight">
        Frequently Asked Questions
      </h2>

      <div class="space-y-3">
        <div v-for="(faq, i) in faqs" :key="i"
             class="bg-white border border-surface rounded-xl overflow-hidden transition-shadow hover:shadow-sm">
          <button @click="toggle(i)"
                  class="w-full flex items-center justify-between px-6 py-4 text-left">
            <span class="font-semibold text-charcoal text-sm pr-4">{{ faq.q }}</span>
            <svg class="w-5 h-5 text-coral shrink-0 transition-transform duration-300"
                 :class="{ 'rotate-180': openIndex === i }"
                 fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
            </svg>
          </button>
          <Transition name="faq">
            <div v-if="openIndex === i" class="px-6 pb-5">
              <p class="text-sm text-muted leading-relaxed">{{ faq.a }}</p>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const openIndex = ref(null)

function toggle(i) {
  openIndex.value = openIndex.value === i ? null : i
}

const faqs = [
  {
    q: 'Do I need a Claude subscription?',
    a: 'Yes, you need at least Claude Free to follow along. Some advanced lessons require Claude Pro.'
  },
  {
    q: 'Is this affiliated with Anthropic?',
    a: "LearnClaude.ai is an independent learning platform, not affiliated with Anthropic. We're powered by Claude."
  },
  {
    q: 'What if I already know some Claude basics?',
    a: 'Start anywhere. Each lesson is self-contained. Free tier covers the basics, Pro unlocks everything.'
  },
  {
    q: 'Can I get a refund?',
    a: "Yes, within 7 days if you haven't completed more than 3 lessons."
  }
]
</script>

<style scoped>
.faq-enter-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.faq-leave-active {
  transition: all 0.2s ease;
  overflow: hidden;
}
.faq-enter-from,
.faq-leave-to {
  opacity: 0;
  max-height: 0;
  padding-bottom: 0;
}
.faq-enter-to,
.faq-leave-from {
  opacity: 1;
  max-height: 200px;
}
</style>
