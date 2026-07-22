import { useEffect, useState } from 'react'
import { Link as RouterLink } from 'react-router-dom'
import { Box, Button, Container, Stack, Typography } from '@mui/material'
import { useAuth } from '../contexts/AuthContext'
import { bookingApi } from '../api'
import type { Booking } from '../types'
import { formatMoney } from '../utils/format'

export function DashboardPage() {
  const { user } = useAuth()
  const [bookings, setBookings] = useState<Booking[]>([])

  useEffect(() => {
    bookingApi.mine().then(setBookings).catch(() => setBookings([]))
  }, [])

  const active = bookings.filter((b) => b.status === 'CONFIRMED')

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>
        Hello, {user?.firstName}
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 4 }}>
        Your SkyBook travel hub — search, manage trips, or ask the AI assistant.
      </Typography>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' },
          gap: 2,
          mb: 4,
        }}
      >
        {[
          { label: 'Confirmed trips', value: String(active.length) },
          { label: 'Total bookings', value: String(bookings.length) },
          {
            label: 'Spend (confirmed)',
            value: formatMoney(active.reduce((s, b) => s + Number(b.totalAmount), 0)),
          },
        ].map((stat) => (
          <Box key={stat.label} sx={{ p: 3, borderRadius: 3, bgcolor: 'primary.main', color: '#fff' }}>
            <Typography variant="body2" sx={{ opacity: 0.8 }}>{stat.label}</Typography>
            <Typography variant="h4" fontWeight={800}>{stat.value}</Typography>
          </Box>
        ))}
      </Box>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <Button component={RouterLink} to="/search" variant="contained" size="large">Search flights</Button>
        <Button component={RouterLink} to="/history" variant="outlined" size="large">Booking history</Button>
        <Button component={RouterLink} to="/ai-chat" variant="outlined" size="large">Ask AI</Button>
      </Stack>
    </Container>
  )
}
