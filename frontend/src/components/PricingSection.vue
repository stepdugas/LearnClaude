<template>
  <section id="pricing" class="py-20 px-6 bg-surface/50">
    <div class="max-w-4xl mx-auto">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-charcoal tracking-tight mb-4">
          Simple, honest pricing
        </h2>
        <p class="text-muted text-lg">
          Free lessons teach free Claude features. Upgrade to learn the rest.
        </p>
      </div>

      <div class="grid md:grid-cols-2 gap-6 max-w-3xl mx-auto">
        <!-- Free Tier -->
        <div class="bg-white rounded-2xl p-8 border border-surface shadow-sm hover:shadow-md transition-all duration-300">
          <h3 class="font-semibold text-charcoal text-xl mb-2">Free</h3>
          <p class="text-muted text-sm mb-6">Learn what free Claude can do</p>
          <div class="mb-8">
            <span class="text-4xl font-bold text-charcoal">$0</span>
            <span class="text-muted text-sm ml-1">forever</span>
          </div>
          <ul class="space-y-3 mb-8">
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              5 lessons on free Claude features
            </li>
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              AI tutor on every free lesson
            </li>
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              Practice with free Claude — no Claude Pro needed
            </li>
          </ul>
          <router-link to="/register" class="block text-center border-2 border-coral text-coral font-semibold px-6 py-3 rounded-lg hover:bg-coral/5 active:scale-[0.98] transition-all">
            Start Learning
          </router-link>
        </div>

        <!-- Pro Tier -->
        <div class="bg-white rounded-2xl p-8 border-2 border-coral relative shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300">
          <div class="absolute -top-3 left-1/2 -translate-x-1/2 bg-coral text-white text-xs font-semibold px-4 py-1 rounded-full">
            MOST POPULAR
          </div>
          <h3 class="font-semibold text-charcoal text-xl mb-2">Pro</h3>
          <p class="text-muted text-sm mb-6">Master the full Claude ecosystem</p>
          <div class="mb-2">
            <span class="text-4xl font-bold text-charcoal">$9</span>
            <span class="text-muted text-sm ml-1">/month</span>
          </div>
          <p class="text-sm text-muted mb-8">or <span class="font-semibold text-charcoal">$79/year</span> <span class="text-coral font-medium">(save 27%)</span></p>
          <ul class="space-y-3 mb-8">
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              All 17 lessons — free + Pro + developer
            </li>
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              Learn Claude Pro features (Projects, Memory, Research)
            </li>
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              Developer tools (API, Claude Code, Agents)
            </li>
            <li class="flex items-center gap-3 text-sm text-charcoal/80">
              <svg class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
              New lessons as Claude evolves
            </li>
          </ul>
          <router-link to="/register" class="block text-center bg-coral hover:bg-coral-hover active:scale-[0.98] text-white font-semibold px-6 py-3 rounded-lg transition-all">
            Get Started Free
          </router-link>
        </div>
      </div>
    </div>
  </section>
</template>
