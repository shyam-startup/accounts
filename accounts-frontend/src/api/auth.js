import axios from 'axios'

const api = axios.create({
  baseURL: '/auth',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
})

const oauthApi = axios.create({
  baseURL: '/oauth',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
})

export const signin           = (data)     => api.post('/signin', data)
export const signup           = (data)     => api.post('/signup', data)
export const signout          = ()         => api.post('/signout')
export const getMe            = ()         => api.get('/me')
export const getConnectedApps = ()         => api.get('/connected-apps')
export const getAvailableApps = ()         => api.get('/available-apps')
export const authorize        = (clientId) => oauthApi.get(`/authorize?client_id=${clientId}`)
