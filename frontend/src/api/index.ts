import { api, aiApi } from './client'
import type {
  Airline,
  Airport,
  AuditExport,
  AuditLog,
  AuthResponse,
  Booking,
  Flight,
  User,
} from '../types'

export const authApi = {
  login: (username: string, password: string) =>
    api.post<AuthResponse>('/auth/login', { username, password }).then((r) => r.data),
  register: (payload: Record<string, string>) =>
    api.post<AuthResponse>('/auth/register', payload).then((r) => r.data),
  logout: (refreshToken?: string | null) =>
    api.post('/auth/logout', refreshToken ? { refreshToken } : {}).then((r) => r.data),
}

export const flightApi = {
  search: (params: Record<string, string | number | undefined>) =>
    api.get<Flight[]>('/flights/search', { params }).then((r) => r.data),
  get: (id: string) => api.get<Flight>(`/flights/${id}`).then((r) => r.data),
  airlines: () => api.get<Airline[]>('/airlines').then((r) => r.data),
  airports: () => api.get<Airport[]>('/airports').then((r) => r.data),
}

export const bookingApi = {
  create: (payload: unknown) => api.post<Booking>('/bookings', payload).then((r) => r.data),
  mine: () => api.get<Booking[]>('/bookings/me').then((r) => r.data),
  get: (id: string) => api.get<Booking>(`/bookings/${id}`).then((r) => r.data),
  cancel: (id: string, reason?: string) =>
    api.post<Booking>(`/bookings/${id}/cancel`, { reason }).then((r) => r.data),
}

export const userApi = {
  me: () => api.get<User>('/users/me').then((r) => r.data),
  update: (payload: Partial<User>) => api.put<User>('/users/me', payload).then((r) => r.data),
  changePassword: (currentPassword: string, newPassword: string) =>
    api.post('/users/me/password', { currentPassword, newPassword }).then((r) => r.data),
}

export const adminApi = {
  users: () => api.get<User[]>('/admin/users').then((r) => r.data),
  audit: (params: Record<string, string | number | undefined>) =>
    api.get<{ content: AuditLog[]; totalElements: number; totalPages: number }>('/admin/audit', { params }).then((r) => r.data),
  exports: () => api.get<AuditExport[]>('/admin/audit/exports').then((r) => r.data),
  exportCsv: (params: Record<string, string | undefined>) =>
    api.get('/admin/audit/export/csv', { params, responseType: 'blob' }).then((r) => r.data as Blob),
  exportExcel: (params: Record<string, string | undefined>) =>
    api.get('/admin/audit/export/excel', { params, responseType: 'blob' }).then((r) => r.data as Blob),
  exportMonthly: (month?: string) =>
    api.get('/admin/audit/export/monthly', { params: { month }, responseType: 'blob' }).then((r) => r.data as Blob),
  recordAiQuery: (details: Record<string, unknown>) =>
    api.post('/admin/audit/ai-query', details).then((r) => r.data),
}

export const chatApi = {
  send: (message: string, sessionId: string) =>
    aiApi.post<{ reply: string; sessionId: string; intent?: string }>('/chat', { message, sessionId }).then((r) => r.data),
  history: (sessionId: string) =>
    aiApi.get<{ messages: { role: string; content: string }[] }>(`/chat/history/${sessionId}`).then((r) => r.data),
}
