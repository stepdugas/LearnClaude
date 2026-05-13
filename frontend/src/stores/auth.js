import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user')))
  const accessToken = ref(localStorage.getItem('accessToken'))
  const refreshToken = ref(localStorage.getItem('refreshToken'))

  const isLoggedIn = computed(() => !!accessToken.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  function setAuth(data) {
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('user', JSON.stringify(data.user))
  }

  function clearAuth() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
  }

  async function register(email, password) {
    const { data } = await api.post('/api/auth/register', { email, password })
    setAuth(data)
    return data
  }

  async function login(email, password) {
    const { data } = await api.post('/api/auth/login', { email, password })
    setAuth(data)
    return data
  }

  async function refresh() {
    try {
      const { data } = await api.post('/api/auth/refresh', {
        refreshToken: refreshToken.value
      })
      setAuth(data)
      return data.accessToken
    } catch {
      clearAuth()
      return null
    }
  }

  async function logout() {
    try {
      await api.post('/api/auth/logout', { refreshToken: refreshToken.value })
    } catch {
      // logout even if request fails
    }
    clearAuth()
  }

  return { user, accessToken, refreshToken, isLoggedIn, isAdmin, setAuth, register, login, refresh, logout, clearAuth }
})
