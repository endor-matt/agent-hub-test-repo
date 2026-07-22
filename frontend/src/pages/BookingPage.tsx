import { FormEvent, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  Alert,
  Box,
  Button,
  Container,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { bookingApi, flightApi } from '../api'
import { getErrorMessage } from '../api/client'
import { SeatMap } from '../components/SeatMap'
import type { Flight, Passenger } from '../types'
import { formatDateTime, formatMoney } from '../utils/format'
import { useAuth } from '../contexts/AuthContext'

export function BookingPage() {
  const { flightId } = useParams()
  const navigate = useNavigate()
  const { user, isAuthenticated } = useAuth()
  const [flight, setFlight] = useState<Flight | null>(null)
  const [passengerCount, setPassengerCount] = useState(1)
  const [passengers, setPassengers] = useState<Passenger[]>([{ firstName: '', lastName: '' }])
  const [seats, setSeats] = useState<string[]>([])
  const [contactEmail, setContactEmail] = useState(user?.email || '')
  const [contactPhone, setContactPhone] = useState(user?.phone || '')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!flightId) return
    flightApi.get(flightId).then(setFlight).catch((err) => setError(getErrorMessage(err)))
  }, [flightId])

  useEffect(() => {
    setPassengers((prev) => {
      const next = [...prev]
      while (next.length < passengerCount) next.push({ firstName: '', lastName: '' })
      return next.slice(0, passengerCount)
    })
    setSeats((s) => s.slice(0, passengerCount))
  }, [passengerCount])

  const updatePassenger = (idx: number, key: keyof Passenger, value: string) => {
    setPassengers((list) => list.map((p, i) => (i === idx ? { ...p, [key]: value } : p)))
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/booking/${flightId}` } })
      return
    }
    if (seats.length !== passengerCount) {
      setError('Select a seat for each passenger')
      return
    }
    setLoading(true)
    setError('')
    try {
      const booking = await bookingApi.create({
        flightId,
        passengers,
        seats,
        contactEmail,
        contactPhone,
      })
      navigate(`/confirmation/${booking.id}`)
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  if (!flight && !error) {
    return <Container sx={{ py: 6 }}><Typography>Loading flight…</Typography></Container>
  }

  return (
    <Container maxWidth="md" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>Complete your booking</Typography>
      {flight && (
        <Box sx={{ mb: 3, p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
          <Typography variant="h6">{flight.airlineName} · {flight.flightNumber}</Typography>
          <Typography color="text.secondary">
            {flight.sourceIata} → {flight.destIata} · {formatDateTime(flight.departureTime)}
          </Typography>
          <Typography fontWeight={700} sx={{ mt: 1 }}>
            {formatMoney(flight.basePrice * passengerCount, flight.currency)} total
          </Typography>
        </Box>
      )}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Box component="form" onSubmit={onSubmit}>
        <Stack spacing={3}>
          <TextField
            label="Passengers"
            type="number"
            value={passengerCount}
            onChange={(e) => setPassengerCount(Math.max(1, Math.min(4, Number(e.target.value) || 1)))}
            inputProps={{ min: 1, max: 4 }}
            sx={{ maxWidth: 160 }}
          />
          {passengers.map((p, idx) => (
            <Stack key={idx} direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField label={`Passenger ${idx + 1} first name`} value={p.firstName} onChange={(e) => updatePassenger(idx, 'firstName', e.target.value)} required fullWidth />
              <TextField label="Last name" value={p.lastName} onChange={(e) => updatePassenger(idx, 'lastName', e.target.value)} required fullWidth />
              <TextField label="Passport" value={p.passport || ''} onChange={(e) => updatePassenger(idx, 'passport', e.target.value)} fullWidth />
            </Stack>
          ))}
          <SeatMap selected={seats} maxSeats={passengerCount} onChange={setSeats} />
          <TextField label="Contact email" type="email" value={contactEmail} onChange={(e) => setContactEmail(e.target.value)} required fullWidth />
          <TextField label="Contact phone" value={contactPhone} onChange={(e) => setContactPhone(e.target.value)} fullWidth />
          <Button type="submit" variant="contained" size="large" disabled={loading || !flight}>
            {loading ? 'Booking…' : 'Confirm booking'}
          </Button>
        </Stack>
      </Box>
    </Container>
  )
}
