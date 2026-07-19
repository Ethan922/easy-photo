import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截：附加 JWT
http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

// 响应拦截：统一处理 Result 结构与错误
http.interceptors.response.use(
  (response) => {
    const body = response.data
    // 后端统一 Result：{ code, message, data }
    if (body && typeof body.code !== 'undefined') {
      if (body.code === 0) {
        return body.data
      }
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body
  },
  (error) => {
    const status = error.response?.status
    const msg = error.response?.data?.message
    if (status === 401) {
      const auth = useAuthStore()
      auth.logout()
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else if (status === 403) {
      ElMessage.error(msg || '禁止访问')
    } else {
      ElMessage.error(msg || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default http
