<template>
  <div class="max-w-4xl mx-auto px-6 py-12">
    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-3xl font-bold text-charcoal">Admin — Lessons</h1>
        <p class="text-muted mt-1">Add, edit, or remove lessons. Changes go live instantly.</p>
      </div>
      <router-link to="/dashboard" class="text-sm text-muted hover:text-charcoal transition-colors">Back to Dashboard</router-link>
    </div>

    <!-- Add / Edit Form -->
    <div class="bg-white border border-surface rounded-2xl p-8 mb-8 shadow-sm">
      <h2 class="text-lg font-semibold text-charcoal mb-6">{{ editing ? 'Edit Lesson' : 'Add New Lesson' }}</h2>

      <form @submit.prevent="saveLesson" class="space-y-4">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div>
            <label class="block text-xs font-medium text-muted mb-1">Number</label>
            <input v-model.number="form.number" type="number" min="1" required
                   class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30" />
          </div>
          <div>
            <label class="block text-xs font-medium text-muted mb-1">Tier</label>
            <input v-model.number="form.tier" type="number" min="1" max="10" required
                   class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30" />
          </div>
          <div>
            <label class="block text-xs font-medium text-muted mb-1">Plan</label>
            <select v-model="form.plan"
                    class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30">
              <option value="FREE">FREE</option>
              <option value="PRO">PRO</option>
            </select>
          </div>
          <div class="flex items-end">
            <button v-if="editing" type="button" @click="cancelEdit"
                    class="w-full text-sm text-muted hover:text-charcoal border border-surface rounded-lg px-3 py-2 transition-colors">
              Cancel Edit
            </button>
          </div>
        </div>

        <div>
          <label class="block text-xs font-medium text-muted mb-1">Title</label>
          <input v-model="form.title" type="text" required maxlength="200"
                 class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30"
                 placeholder="e.g., Claude for Small Businesses" />
        </div>

        <div>
          <label class="block text-xs font-medium text-muted mb-1">Description (2-3 sentences)</label>
          <textarea v-model="form.description" required maxlength="500" rows="3"
                    class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30 resize-none"
                    placeholder="What will the user learn in this lesson?"></textarea>
        </div>

        <div>
          <label class="block text-xs font-medium text-muted mb-1">YouTube Search Query</label>
          <input v-model="form.youtubeSearchQuery" type="text" required maxlength="200"
                 class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30"
                 placeholder="e.g., Claude AI small business tutorial" />
        </div>

        <div>
          <label class="block text-xs font-medium text-muted mb-1">Hands-On Challenge</label>
          <textarea v-model="form.challenge" required maxlength="500" rows="3"
                    class="w-full px-3 py-2 rounded-lg border border-surface bg-cream text-sm focus:outline-none focus:ring-2 focus:ring-coral/30 resize-none"
                    placeholder="A practical task for the user to try..."></textarea>
        </div>

        <div v-if="formError" class="text-sm text-red-500">{{ formError }}</div>

        <button type="submit" :disabled="saving"
                class="bg-coral hover:bg-coral-hover active:scale-[0.98] disabled:opacity-50 text-white font-semibold px-6 py-2.5 rounded-lg transition-all text-sm">
          {{ saving ? 'Saving...' : editing ? 'Update Lesson' : 'Add Lesson' }}
        </button>
      </form>
    </div>

    <!-- Existing Lessons -->
    <div class="space-y-3">
      <div v-for="lesson in lessons" :key="lesson.id"
           class="flex items-center gap-4 p-4 bg-white border border-surface rounded-xl shadow-sm">
        <div class="w-8 h-8 rounded-full bg-coral/10 text-coral flex items-center justify-center text-sm font-semibold shrink-0">
          {{ lesson.number }}
        </div>
        <div class="flex-1 min-w-0">
          <div class="text-sm font-medium text-charcoal truncate">{{ lesson.title }}</div>
          <div class="text-xs text-muted">Tier {{ lesson.tier }} · {{ lesson.plan }}</div>
        </div>
        <button @click="editLesson(lesson)" class="text-xs text-coral hover:text-coral-hover font-medium transition-colors">Edit</button>
        <button @click="deleteLesson(lesson)" class="text-xs text-muted hover:text-red-500 font-medium transition-colors">Delete</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import api from '../api'

const showToast = inject('showToast')

const lessons = ref([])
const saving = ref(false)
const formError = ref('')
const editing = ref(null)

const emptyForm = { number: null, title: '', tier: 1, plan: 'PRO', description: '', youtubeSearchQuery: '', challenge: '' }
const form = ref({ ...emptyForm })

async function loadLessons() {
  const { data } = await api.get('/api/admin/lessons')
  lessons.value = data
}

async function saveLesson() {
  formError.value = ''
  saving.value = true
  try {
    if (editing.value) {
      await api.put(`/api/admin/lessons/${editing.value}`, form.value)
      showToast('Lesson updated', 'success')
    } else {
      await api.post('/api/admin/lessons', form.value)
      showToast('Lesson added! Subscribed users will be notified.', 'success')
    }
    form.value = { ...emptyForm }
    editing.value = null
    await loadLessons()
    // Auto-set next number
    const { data } = await api.get('/api/admin/lessons/next-number')
    form.value.number = data.nextNumber
  } catch (e) {
    formError.value = e.response?.data?.error || 'Failed to save lesson'
  } finally {
    saving.value = false
  }
}

function editLesson(lesson) {
  editing.value = lesson.id
  form.value = {
    number: lesson.number,
    title: lesson.title,
    tier: lesson.tier,
    plan: lesson.plan,
    description: lesson.description,
    youtubeSearchQuery: lesson.youtubeSearchQuery,
    challenge: lesson.challenge
  }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function cancelEdit() {
  editing.value = null
  form.value = { ...emptyForm }
}

async function deleteLesson(lesson) {
  if (!confirm(`Delete "${lesson.title}"? This cannot be undone.`)) return
  try {
    await api.delete(`/api/admin/lessons/${lesson.id}`)
    showToast('Lesson deleted', 'success')
    await loadLessons()
  } catch {
    showToast('Failed to delete lesson', 'error')
  }
}

onMounted(async () => {
  await loadLessons()
  try {
    const { data } = await api.get('/api/admin/lessons/next-number')
    form.value.number = data.nextNumber
  } catch {}
})
</script>
