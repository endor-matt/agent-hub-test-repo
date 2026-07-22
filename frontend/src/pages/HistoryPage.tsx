import { useEffect, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  Chip,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { bookingApi } from '../api'
import { getErrorMessage } from '../api/client'
import type { Booking } from '../types'
import { formatDateTime, formatMoney } from '../utils/format'

export function HistoryPage() {
  const [bookings, setBookings] = useState<Booking[]>([])
  const [error, setError] = useState('')
  const [cancelId, setCancelId] = useState<string | null>(null)
  const [reason, setReason] = useState('')
  const [loading, setLoading] = useState(false)

  const load = () =>
    bookingApi.mine().then(setBookings).catch((err) => setError(getErrorMessage(err)))

  useEffect(() => {
    load()
  }, [])

  const confirmCancel = async () => {
    if (!cancelId) return
    setLoading(true)
    try {
      await bookingApi.cancel(cancelId, reason)
      setCancelId(null)
      setReason('')
      await load()
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>Booking history</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        View past trips and cancel upcoming confirmed bookings.
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Stack spacing={2}>
        {bookings.map((b) => (
          <Box key={b.id} sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" spacing={2}>
              <Box>
                <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                  <Typography fontWeight={700}>{b.bookingReference}</Typography>
                  <Chip size="small" label={b.status} color={b.status === 'CANCELLED' ? 'default' : 'secondary'} />
                </Stack>
                <Typography>{b.airlineName} · {b.flightNumber}</Typography>
                <Typography color="text.secondary">{b.sourceIata} → {b.destIata} · {formatDateTime(b.departureTime)}</Typography>
                <Typography color="text.secondary">Seats {b.seats.join(', ')} · {formatMoney(b.totalAmount, b.currency)}</Typography>
              </Box>
              {b.status === 'CONFIRMED' && (
                <Button color="error" variant="outlined" onClick={() => setCancelId(b.id)}>
                  Cancel booking
                </Button>
              )}
            </Stack>
          </Box>
        ))}
        {!bookings.length && !error && (
          <Typography color="text.secondary">No bookings yet. Search flights to get started.</Typography>
        )}
      </Stack>

      <Dialog open={!!cancelId} onClose={() => setCancelId(null)} fullWidth maxWidth="sm">
        <DialogTitle>Cancel booking?</DialogTitle>
        <DialogContent>
          <TextField
            label="Reason (optional)"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            fullWidth
            multiline
            minRows={2}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCancelId(null)}>Keep booking</Button>
          <Button color="error" variant="contained" onClick={confirmCancel} disabled={loading}>
            Confirm cancel
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  )
}
