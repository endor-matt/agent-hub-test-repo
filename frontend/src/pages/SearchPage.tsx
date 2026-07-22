import { FormEvent, useEffect, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  Container,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import { DatePicker } from '@mui/x-date-pickers/DatePicker'
import dayjs, { Dayjs } from 'dayjs'
import { flightApi } from '../api'
import { getErrorMessage } from '../api/client'
import { FlightCard } from '../components/FlightCard'
import type { Airline, Airport, Flight } from '../types'

export function SearchPage() {
  const [source, setSource] = useState('JFK')
  const [destination, setDestination] = useState('LAX')
  const [date, setDate] = useState<Dayjs | null>(dayjs().add(3, 'day'))
  const [airline, setAirline] = useState('')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [airlines, setAirlines] = useState<Airline[]>([])
  const [airports, setAirports] = useState<Airport[]>([])
  const [flights, setFlights] = useState<Flight[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)

  useEffect(() => {
    Promise.all([flightApi.airlines(), flightApi.airports()])
      .then(([a, p]) => {
        setAirlines(a)
        setAirports(p)
      })
      .catch(() => {
        /* backend may be offline during UI-only work */
      })
  }, [])

  const onSearch = async (e?: FormEvent) => {
    e?.preventDefault()
    setError('')
    setLoading(true)
    setSearched(true)
    try {
      const results = await flightApi.search({
        source: source || undefined,
        destination: destination || undefined,
        date: date ? date.format('YYYY-MM-DD') : undefined,
        airline: airline || undefined,
        minPrice: minPrice ? Number(minPrice) : undefined,
        maxPrice: maxPrice ? Number(maxPrice) : undefined,
      })
      setFlights(results)
    } catch (err) {
      setError(getErrorMessage(err))
      setFlights([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <Box sx={{ py: 5, background: 'linear-gradient(180deg, #E8F2F8 0%, #EEF4F8 40%, #EEF4F8 100%)' }}>
      <Container maxWidth="lg">
        <Typography variant="h3" gutterBottom>Find your flight</Typography>
        <Typography color="text.secondary" sx={{ mb: 3 }}>
          Filter by route, date, airline, and price — then book with seat selection.
        </Typography>

        <Box
          component="form"
          onSubmit={onSearch}
          sx={{
            p: 3,
            mb: 4,
            borderRadius: 3,
            bgcolor: 'background.paper',
            border: '1px solid',
            borderColor: 'divider',
          }}
        >
          <Stack spacing={2}>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField select label="From" value={source} onChange={(e) => setSource(e.target.value)} fullWidth>
                {airports.map((a) => (
                  <MenuItem key={a.id} value={a.iataCode}>{a.iataCode} — {a.city}</MenuItem>
                ))}
                {!airports.length && <MenuItem value="JFK">JFK — New York</MenuItem>}
              </TextField>
              <TextField select label="To" value={destination} onChange={(e) => setDestination(e.target.value)} fullWidth>
                {airports.map((a) => (
                  <MenuItem key={a.id} value={a.iataCode}>{a.iataCode} — {a.city}</MenuItem>
                ))}
                {!airports.length && <MenuItem value="LAX">LAX — Los Angeles</MenuItem>}
              </TextField>
              <DatePicker
                label="Date"
                value={date}
                onChange={(v) => setDate(v)}
                slotProps={{ textField: { fullWidth: true } }}
              />
            </Stack>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField select label="Airline" value={airline} onChange={(e) => setAirline(e.target.value)} fullWidth>
                <MenuItem value="">Any airline</MenuItem>
                {airlines.map((a) => (
                  <MenuItem key={a.id} value={a.code}>{a.code} — {a.name}</MenuItem>
                ))}
              </TextField>
              <TextField label="Min price" type="number" value={minPrice} onChange={(e) => setMinPrice(e.target.value)} fullWidth />
              <TextField label="Max price" type="number" value={maxPrice} onChange={(e) => setMaxPrice(e.target.value)} fullWidth />
            </Stack>
            <Button type="submit" variant="contained" size="large" disabled={loading} sx={{ alignSelf: 'flex-start' }}>
              {loading ? 'Searching…' : 'Search flights'}
            </Button>
          </Stack>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {searched && !loading && !error && (
          <Typography sx={{ mb: 2 }} color="text.secondary">
            {flights.length} flight{flights.length === 1 ? '' : 's'} found
          </Typography>
        )}
        <Stack spacing={2}>
          {flights.map((f) => (
            <FlightCard key={f.id} flight={f} />
          ))}
        </Stack>
      </Container>
    </Box>
  )
}
