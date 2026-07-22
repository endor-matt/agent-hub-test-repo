import { useEffect, useState } from 'react'
import { Link as RouterLink, useParams } from 'react-router-dom'
import { Alert, Box, Button, Container, Stack, Typography } from '@mui/material'
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'
import { bookingApi } from '../api'
import { getErrorMessage } from '../api/client'
import type { Booking } from '../types'
import { formatDateTime, formatMoney } from '../utils/format'

export function ConfirmationPage() {
  const { bookingId } = useParams()
  const [booking, setBooking] = useState<Booking | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!bookingId) return
    bookingApi.get(bookingId).then(setBooking).catch((err) => setError(getErrorMessage(err)))
  }, [bookingId])

  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      {error && <Alert severity="error">{error}</Alert>}
      {booking && (
        <Box sx={{ textAlign: 'center', p: 4, borderRadius: 4, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
          <CheckCircleOutlineIcon color="secondary" sx={{ fontSize: 64, mb: 2 }} />
          <Typography variant="h4" gutterBottom>Booking confirmed</Typography>
          <Typography color="text.secondary" sx={{ mb: 3 }}>
            Reference <strong>{booking.bookingReference}</strong>
          </Typography>
          <Stack spacing={1} sx={{ textAlign: 'left', mb: 3 }}>
            <Typography>{booking.airlineName} · {booking.flightNumber}</Typography>
            <Typography>{booking.sourceIata} → {booking.destIata}</Typography>
            <Typography>{formatDateTime(booking.departureTime)}</Typography>
            <Typography>Seats: {booking.seats.join(', ')}</Typography>
            <Typography fontWeight={700}>{formatMoney(booking.totalAmount, booking.currency)}</Typography>
          </Stack>
          <Stack direction="row" spacing={2} justifyContent="center">
            <Button component={RouterLink} to="/history" variant="contained">My bookings</Button>
            <Button component={RouterLink} to="/search" variant="outlined">Search again</Button>
          </Stack>
        </Box>
      )}
    </Container>
  )
}
