<template>
  <div class="min-h-screen bg-cream text-charcoal font-sans">
    <NavBar />
    <router-view v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </router-view>

    <!-- Toast container -->
    <Teleport to="body">
      <TransitionGroup name="msg" tag="div" class="fixed bottom-6 right-6 z-[100] flex flex-col gap-3">
        <div v-for="toast in toasts" :key="toast.id"
             class="bg-charcoal text-white text-sm px-5 py-3 rounded-xl shadow-lg flex items-center gap-3 max-w-sm">
          <svg v-if="toast.type === 'success'" class="w-5 h-5 text-green-400 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5" />
          </svg>
          <svg v-else class="w-5 h-5 text-coral shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
          </svg>
          {{ toast.message }}
        </div>
      </TransitionGroup>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import NavBar from './components/NavBar.vue'

const toasts = ref([])
let toastId = 0

function showToast(message, type = 'success') {
  const id = ++toastId
  toasts.value.push({ id, message, type })
  setTimeout(() => {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }, 3500)
}

provide('showToast', showToast)
</script>
