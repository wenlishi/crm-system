import { defineStore } from 'pinia'
import { login, logout, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: (() => {
      try {
        const info = localStorage.getItem('userInfo')
        return info ? JSON.parse(info) : {}
      } catch (e) {
        return {}
      }
    })(),
    permissions: []
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo.username || '',
    nickname: (state) => state.userInfo.nickname || '',
    avatar: (state) => state.userInfo.avatar || ''
  },
  
  actions: {
    async login(loginForm) {
      try {
        const res = await login(loginForm)
        this.token = res.data.token
        // 适配后端返回的数据结构
        this.userInfo = {
          userId: res.data.userId,
          username: res.data.username,
          nickname: res.data.username,
          avatar: ''
        }
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        router.push('/')
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    async getUserInfo() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        this.token = ''
        this.userInfo = {}
        this.permissions = []
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
    }
  }
})
