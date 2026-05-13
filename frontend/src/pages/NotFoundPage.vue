<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center px-6 text-center">
    <div class="text-6xl mb-6">🔍</div>
    <h1 class="text-3xl font-bold text-charcoal mb-3">Page not found</h1>
    <p class="text-muted mb-8 max-w-md">The page you're looking for doesn't exist or has been moved.</p>
    <router-link to="/" class="bg-coral hover:bg-coral-hover active:scale-[0.97] text-white font-semibold px-8 py-3 rounded-lg transition-all">
      Back to Home
    </router-link>
  </div>
</template>
