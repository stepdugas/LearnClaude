<template>
  <footer class="border-t border-surface py-12 px-6">
    <div class="max-w-5xl mx-auto flex flex-col md:flex-row items-center justify-between gap-6">
      <div>
        <router-link to="/" class="text-lg font-semibold text-charcoal tracking-tight">
          <span class="text-coral">Learn</span>Claude.ai
        </router-link>
        <p class="text-muted text-sm mt-1">Master Claude AI tools, step by step.</p>
      </div>
      <div class="flex flex-wrap justify-center items-center gap-4 md:gap-8 text-sm text-muted">
        <a href="#curriculum" class="hover:text-charcoal transition-colors">Curriculum</a>
        <a href="#pricing" class="hover:text-charcoal transition-colors">Pricing</a>
        <router-link to="/terms" class="hover:text-charcoal transition-colors">Terms</router-link>
        <router-link to="/privacy" class="hover:text-charcoal transition-colors">Privacy</router-link>
      </div>
    </div>
    <div class="max-w-5xl mx-auto mt-8 pt-6 border-t border-surface text-center text-xs text-muted">
      &copy; {{ new Date().getFullYear() }} LearnClaude.ai. Not affiliated with Anthropic.
    </div>
  </footer>
</template>
