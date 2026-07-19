import http from './http'

// 认证
export const authApi = {
  register: (data) => http.post('/auth/register', data),
  login: (data) => http.post('/auth/login', data)
}

// 分类与维度
export const categoryApi = {
  list: () => http.get('/categories'),
  createCategory: (name) => http.post('/categories', { name }),
  deleteCategory: (id) => http.delete(`/categories/${id}`),
  createDimension: (categoryId, name) => http.post(`/categories/${categoryId}/dimensions`, { name }),
  deleteDimension: (id) => http.delete(`/categories/dimensions/${id}`)
}

// 教程
export const tutorialApi = {
  list: (params) => http.get('/tutorials', { params }),
  detail: (id) => http.get(`/tutorials/${id}`),
  create: (data) => http.post('/tutorials', data),
  update: (id, data) => http.put(`/tutorials/${id}`, data),
  remove: (id) => http.delete(`/tutorials/${id}`)
}

// 互动
export const engagementApi = {
  like: (id) => http.post(`/tutorials/${id}/like`),
  unlike: (id) => http.delete(`/tutorials/${id}/like`),
  favorite: (id) => http.post(`/tutorials/${id}/favorite`),
  unfavorite: (id) => http.delete(`/tutorials/${id}/favorite`),
  myFavorites: (params) => http.get('/favorites', { params })
}

// 图片上传
export const imageApi = {
  upload: (formData) => http.post('/images/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 管理端用户管理
export const userApi = {
  list: (params) => http.get('/admin/users', { params }),
  create: (data) => http.post('/admin/users', data),
  remove: (id) => http.delete(`/admin/users/${id}`)
}
