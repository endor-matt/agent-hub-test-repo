import { Box } from '@mui/material'
import { Outlet } from 'react-router-dom'
import { Navbar } from '../components/Navbar'
import { Footer } from '../components/Footer'
import { ChatWidget } from '../components/ChatWidget'
import { useAuth } from '../contexts/AuthContext'

export function MainLayout() {
  const { isAuthenticated } = useAuth()
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <Box component="main" sx={{ flex: 1 }}>
        <Outlet />
      </Box>
      <Footer />
      {isAuthenticated && <ChatWidget />}
    </Box>
  )
}
