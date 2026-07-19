import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: Number(localStorage.getItem('userId')) || null,
    username: localStorage.getItem('username') || '',
    role: localStorage.getItem('role') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin'
  },
  actions: {
    setAuth({ token, userId, username, role }) {
      this.token = token
      this.userId = userId
      this.username = username
      this.role = role
      localStorage.setItem('token', token)
      localStorage.setItem('userId', String(userId))
      localStorage.setItem('username', username)
      localStorage.setItem('role', role)
    },
    logout() {
      this.token = ''
      this.userId = null
      this.username = ''
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
    }
  }
})
