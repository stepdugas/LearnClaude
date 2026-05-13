<template>
  <div class="flex flex-col bg-white border border-surface rounded-2xl overflow-hidden shadow-lg"
       :class="mobile ? 'mt-8' : 'h-[calc(100vh-7rem)] sticky top-24'">

    <!-- Header -->
    <div class="flex items-center justify-between px-5 py-3.5 border-b border-surface bg-cream/50">
      <div class="flex items-center gap-2.5">
        <div class="w-7 h-7 bg-coral/10 rounded-lg flex items-center justify-center">
          <svg aria-hidden="true" class="w-4 h-4 text-coral" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M8.625 12a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0H8.25m4.125 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0H12m4.125 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 01-2.555-.337A5.972 5.972 0 015.41 20.97a5.969 5.969 0 01-.474-.065 4.48 4.48 0 00.978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25z" />
          </svg>
        </div>
        <span class="text-sm font-semibold text-charcoal">AI Tutor</span>
      </div>
      <button @click="resetConversation"
              class="text-xs text-muted hover:text-charcoal transition-colors px-2.5 py-1 rounded-md hover:bg-surface">
        New conversation
      </button>
    </div>

    <!-- Messages -->
    <div ref="messagesContainer" class="flex-1 overflow-y-auto px-5 py-4 space-y-4"
         :class="mobile ? 'max-h-[60vh]' : ''">
      <TransitionGroup name="msg">
        <div v-for="(msg, i) in visibleMessages" :key="'msg-' + i"
             class="flex" :class="msg.role === 'user' ? 'justify-end' : 'justify-start'">
          <div class="max-w-[85%] rounded-2xl px-4 py-3 text-sm leading-relaxed"
               :class="msg.role === 'user'
                 ? 'bg-coral text-white rounded-br-md'
                 : 'bg-cream border border-surface text-charcoal/85 rounded-bl-md'">
            <div v-html="renderMarkdown(msg.content)"></div>
          </div>
        </div>
      </TransitionGroup>

      <!-- Streaming message -->
      <div v-if="streamingContent" class="flex justify-start">
        <div class="max-w-[85%] rounded-2xl rounded-bl-md px-4 py-3 text-sm leading-relaxed bg-cream border border-surface text-charcoal/85">
          <div v-html="renderMarkdown(streamingContent)"></div>
          <span class="inline-block w-1.5 h-4 bg-coral/60 ml-0.5 animate-pulse align-text-bottom"></span>
        </div>
      </div>

      <!-- Typing indicator -->
      <div v-if="isStreaming && !streamingContent" class="flex justify-start">
        <div class="bg-cream border border-surface rounded-2xl rounded-bl-md px-4 py-3">
          <div class="flex gap-1.5">
            <span class="w-2 h-2 bg-muted/40 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
            <span class="w-2 h-2 bg-muted/40 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
            <span class="w-2 h-2 bg-muted/40 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
          </div>
        </div>
      </div>
    </div>

    <!-- Error -->
    <div v-if="chatError" class="px-5 pb-2">
      <div class="text-xs text-red-500 bg-red-50 rounded-lg px-3 py-2">{{ chatError }}</div>
    </div>

    <!-- Input -->
    <div class="border-t border-surface px-4 py-3">
      <div class="flex items-end gap-2">
        <textarea
          ref="inputRef"
          v-model="input"
          @keydown="handleKeydown"
          :disabled="isStreaming"
          :placeholder="`Ask anything about ${lessonTitle}...`"
          rows="1"
          class="flex-1 resize-none bg-cream border border-surface rounded-xl px-4 py-2.5 text-sm text-charcoal placeholder-muted focus:outline-none focus:ring-2 focus:ring-coral/30 focus:border-coral disabled:opacity-50 transition-colors max-h-32 overflow-y-auto"
          @input="autoResize"
        ></textarea>
        <button @click="sendMessage()" :disabled="isStreaming || !input.trim()"
                aria-label="Send message"
                class="shrink-0 w-9 h-9 bg-coral hover:bg-coral-hover active:scale-[0.93] disabled:opacity-30 disabled:hover:bg-coral text-white rounded-xl flex items-center justify-center transition-all">
          <svg v-if="!isStreaming" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" />
          </svg>
          <div v-else class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
        </button>
      </div>
      <p class="text-[11px] text-muted/60 mt-1.5 text-right">Shift+Enter for new line</p>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted, computed } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { API_BASE } from '../api'

marked.setOptions({ breaks: true, gfm: true })

const props = defineProps({
  lessonId: { type: [Number, String], required: true },
  lessonTitle: { type: String, required: true },
  mobile: { type: Boolean, default: false }
})

const messages = ref([])
const input = ref('')
const isStreaming = ref(false)
const streamingContent = ref('')
const chatError = ref('')
const messagesContainer = ref(null)
const inputRef = ref(null)
let abortController = null

// Hide the starter prompt from the user — only show assistant responses and real user messages
const visibleMessages = computed(() => messages.value.filter(m => !m._hidden))

function renderMarkdown(text) {
  if (!text) return ''
  const html = marked.parse(text)
  return DOMPurify.sanitize(html)
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

function autoResize(e) {
  const el = e.target
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 128) + 'px'
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

async function sendMessage(messageOverride) {
  const text = messageOverride || input.value.trim()
  if (!text || isStreaming.value) return

  chatError.value = ''

  // Add user message to history
  if (messageOverride) {
    // Starter message — hidden from UI but included in history for context
    messages.value.push({ role: 'user', content: text, _hidden: true })
  } else {
    messages.value.push({ role: 'user', content: text })
    input.value = ''
    nextTick(() => {
      if (inputRef.value) inputRef.value.style.height = 'auto'
    })
  }
  scrollToBottom()

  isStreaming.value = true
  streamingContent.value = ''

  const token = localStorage.getItem('accessToken')
  abortController = new AbortController()

  // Build history: all messages except the one we just added (sent separately as 'message')
  const history = messages.value.slice(0, -1).map(m => ({ role: m.role, content: m.content }))

  try {
    const response = await fetch(`${API_BASE}/api/chat/${props.lessonId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        message: text,
        conversationHistory: history
      }),
      signal: abortController.signal
    })

    if (!response.ok) {
      throw new Error('Request failed')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('event:') || line.startsWith('event: ')) {
          const eventName = line.replace(/^event:\s*/, '')
          if (eventName === 'error') {
            chatError.value = 'Tutor is unavailable right now, try again in a moment'
          }
          continue
        }
        if (line.startsWith('data:')) {
          const data = line.slice(5).replace(/^ /, '')
          if (data === '[DONE]') continue
          streamingContent.value += data
          scrollToBottom()
        }
      }
    }

    if (streamingContent.value) {
      messages.value.push({ role: 'assistant', content: streamingContent.value })
    }
  } catch (e) {
    if (e.name !== 'AbortError') {
      chatError.value = 'Tutor is unavailable right now, try again in a moment'
    }
  } finally {
    streamingContent.value = ''
    isStreaming.value = false
    abortController = null
    scrollToBottom()
  }
}

function resetConversation() {
  if (abortController) abortController.abort()
  messages.value = []
  streamingContent.value = ''
  chatError.value = ''
  isStreaming.value = false
  sendStarterMessage()
}

function sendStarterMessage() {
  const starterPrompt = `Introduce yourself and give me a one sentence overview of what I will learn in this lesson, then ask me what I already know about ${props.lessonTitle}`
  sendMessage(starterPrompt)
}

onMounted(() => {
  sendStarterMessage()
})

onUnmounted(() => {
  if (abortController) abortController.abort()
})
</script>
