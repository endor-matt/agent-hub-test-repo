import { CssBaseline, ThemeProvider } from '@mui/material'
import { LocalizationProvider } from '@mui/x-date-pickers'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import { MainLayout } from './layouts/MainLayout'
import { AdminRoute, ProtectedRoute } from './routes/ProtectedRoute'
import { theme } from './theme/theme'
import { LandingPage } from './pages/LandingPage'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { SearchPage } from './pages/SearchPage'
import { BookingPage } from './pages/BookingPage'
import { ConfirmationPage } from './pages/ConfirmationPage'
import { HistoryPage } from './pages/HistoryPage'
import { DashboardPage } from './pages/DashboardPage'
import { ProfilePage } from './pages/ProfilePage'
import { AiChatPage } from './pages/AiChatPage'
import { AdminPage } from './pages/AdminPage'
import { AuditDashboardPage } from './pages/AuditDashboardPage'

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <CssBaseline />
        <AuthProvider>
          <BrowserRouter>
            <Routes>
              <Route element={<MainLayout />}>
                <Route index element={<LandingPage />} />
                <Route path="search" element={<SearchPage />} />
                <Route path="login" element={<LoginPage />} />
                <Route path="register" element={<RegisterPage />} />
                <Route path="booking/:flightId" element={<BookingPage />} />

                <Route element={<ProtectedRoute />}>
                  <Route path="dashboard" element={<DashboardPage />} />
                  <Route path="history" element={<HistoryPage />} />
                  <Route path="confirmation/:bookingId" element={<ConfirmationPage />} />
                  <Route path="profile" element={<ProfilePage />} />
                  <Route path="ai-chat" element={<AiChatPage />} />
                </Route>

                <Route element={<AdminRoute />}>
                  <Route path="admin" element={<AdminPage />} />
                  <Route path="admin/audit" element={<AuditDashboardPage />} />
                </Route>

                <Route path="*" element={<Navigate to="/" replace />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </AuthProvider>
      </LocalizationProvider>
    </ThemeProvider>
  )
}
