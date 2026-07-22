import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authApi } from '../api'
import type { AuthResponse, User } from '../types'

interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  isAdmin: boolean
  login: (username: string, password: string) => Promise<User>
  register: (payload: Record<string, string>) => Promise<User>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function loadUser(): User | null {
  const raw = localStorage.getItem('skybook_user')
  if (!raw) return null
  try {
    return JSON.parse(raw) as User
  } catch {
    return null
  }
}

function persist(auth: AuthResponse) {
  localStorage.setItem('skybook_access_token', auth.accessToken)
  localStorage.setItem('skybook_refresh_token', auth.refreshToken)
  localStorage.setItem('skybook_user', JSON.stringify(auth.user))
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => loadUser())

  const login = useCallback(async (username: string, password: string) => {
    const auth = await authApi.login(username, password)
    persist(auth)
    setUser(auth.user)
    return auth.user
  }, [])

  const register = useCallback(async (payload: Record<string, string>) => {
    const auth = await authApi.register(payload)
    persist(auth)
    setUser(auth.user)
    return auth.user
  }, [])

  const logout = useCallback(async () => {
    const refresh = localStorage.getItem('skybook_refresh_token')
    try {
      await authApi.logout(refresh)
    } catch {
      // ignore network logout failures in lab
    }
    localStorage.removeItem('skybook_access_token')
    localStorage.removeItem('skybook_refresh_token')
    localStorage.removeItem('skybook_user')
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      isAdmin: user?.role === 'ADMIN',
      login,
      register,
      logout,
    }),
    [user, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
