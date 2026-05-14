<template>
  <section class="relative pt-24 pb-20 px-6 overflow-hidden">
    <!-- Background orbs -->
    <div class="absolute inset-0 pointer-events-none overflow-hidden">
      <div class="hero-orb absolute -top-32 -right-32 w-96 h-96 bg-coral/[0.07] rounded-full blur-3xl"></div>
      <div class="hero-orb absolute -bottom-48 -left-48 w-[500px] h-[500px] bg-coral/[0.04] rounded-full blur-3xl" style="animation-delay: -3s"></div>
    </div>

    <div class="max-w-3xl mx-auto text-center relative">
      <div class="animate-fade-in-up inline-flex items-center gap-2 bg-surface text-muted text-sm font-medium px-4 py-2 rounded-full mb-8">
        <span class="w-2 h-2 bg-coral rounded-full"></span>
        17 interactive lessons across 3 tiers
      </div>

      <h1 class="hero-shimmer animate-fade-in-up text-5xl md:text-6xl font-bold text-charcoal leading-tight tracking-tight mb-6" style="animation-delay: 0.1s">
        Learn to use Claude AI<br />
        <span class="text-coral">like a power user</span>
      </h1>

      <p class="animate-fade-in-up text-lg md:text-xl text-muted leading-relaxed max-w-2xl mx-auto mb-10" style="animation-delay: 0.2s">
        From your first conversation to building with the API.
        Master every Claude tool with hands-on lessons and a personal AI tutor.
      </p>

      <div class="animate-fade-in-up flex flex-col sm:flex-row items-center justify-center gap-4" style="animation-delay: 0.3s">
        <router-link to="/register" class="bg-coral hover:bg-coral-hover active:scale-[0.97] hover:scale-[1.02] text-white font-semibold px-8 py-3.5 rounded-lg text-lg transition-all shadow-md hover:shadow-lg">
          Get Started Free
        </router-link>
        <a href="#curriculum" class="text-charcoal font-medium px-8 py-3.5 rounded-lg text-lg border border-surface hover:border-muted/30 hover:shadow-lg hover:scale-[1.02] transition-all">
          View Curriculum
        </a>
      </div>
    </div>
  </section>
</template>
