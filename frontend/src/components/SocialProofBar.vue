<template>
  <section class="bg-surface py-6 px-6">
    <div class="max-w-3xl mx-auto flex flex-col sm:flex-row items-center justify-center gap-6">
      <!-- Avatars -->
      <div class="flex items-center">
        <div class="w-9 h-9 rounded-full bg-coral text-white text-xs font-bold flex items-center justify-center ring-2 ring-surface">SK</div>
        <div class="w-9 h-9 rounded-full bg-coral/70 text-white text-xs font-bold flex items-center justify-center -ml-2.5 ring-2 ring-surface">MJ</div>
        <div class="w-9 h-9 rounded-full bg-coral/50 text-white text-xs font-bold flex items-center justify-center -ml-2.5 ring-2 ring-surface">AP</div>
      </div>

      <!-- Text -->
      <p class="text-sm text-muted font-medium text-center sm:text-left">
        Join learners mastering the full Claude ecosystem
      </p>

      <!-- Stars -->
      <div class="flex items-center gap-1.5">
        <div class="flex gap-0.5">
          <svg v-for="n in 5" :key="n" class="w-4 h-4 text-coral" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
          </svg>
        </div>
        <span class="text-sm font-semibold text-charcoal">5.0</span>
      </div>
    </div>
  </section>
</template>
