import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'

const API_BASE = import.meta.env.VITE_API_URL || '/api/v1'
const AI_BASE = import.meta.env.VITE_AI_URL || '/ai/api/v1'

export const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

export const aiApi = axios.create({
  baseURL: AI_BASE,
  headers: { 'Content-Type': 'application/json' },
})

function attachAuth(config: InternalAxiosRequestConfig) {
  const token = localStorage.getItem('skybook_access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}

api.interceptors.request.use(attachAuth)
aiApi.interceptors.request.use(attachAuth)

let refreshing: Promise<string | null> | null = null

async function refreshAccessToken(): Promise<string | null> {
  const refresh = localStorage.getItem('skybook_refresh_token')
  if (!refresh) return null
  try {
    const { data } = await axios.post(`${API_BASE}/auth/refresh`, { refreshToken: refresh })
    localStorage.setItem('skybook_access_token', data.accessToken)
    localStorage.setItem('skybook_refresh_token', data.refreshToken)
    localStorage.setItem('skybook_user', JSON.stringify(data.user))
    return data.accessToken as string
  } catch {
    localStorage.removeItem('skybook_access_token')
    localStorage.removeItem('skybook_refresh_token')
    localStorage.removeItem('skybook_user')
    return null
  }
}

api.interceptors.response.use(
  (r) => r,
  async (error: AxiosError) => {
    const original = error.config as InternalAxiosRequestConfig & { _retry?: boolean }
    if (error.response?.status === 401 && original && !original._retry) {
      original._retry = true
      refreshing = refreshing ?? refreshAccessToken().finally(() => { refreshing = null })
      const token = await refreshing
      if (token) {
        original.headers.Authorization = `Bearer ${token}`
        return api(original)
      }
    }
    return Promise.reject(error)
  },
)

export function getErrorMessage(err: unknown): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as { detail?: string; message?: string } | undefined
    return data?.detail || data?.message || err.message
  }
  if (err instanceof Error) return err.message
  return 'Unexpected error'
}
