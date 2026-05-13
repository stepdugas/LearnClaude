import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createUnhead } from '@unhead/vue'
import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
createUnhead()
app.mount('#app')
