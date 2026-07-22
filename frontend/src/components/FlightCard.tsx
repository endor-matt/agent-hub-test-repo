import { Box, Button, Chip, Stack, Typography } from '@mui/material'
import FlightIcon from '@mui/icons-material/Flight'
import { Link as RouterLink } from 'react-router-dom'
import type { Flight } from '../types'
import { formatDateTime, formatDuration, formatMoney } from '../utils/format'

export function FlightCard({ flight }: { flight: Flight }) {
  return (
    <Box
      sx={{
        p: { xs: 2, md: 2.5 },
        borderRadius: 3,
        bgcolor: 'background.paper',
        border: '1px solid',
        borderColor: 'divider',
        display: 'grid',
        gridTemplateColumns: { xs: '1fr', md: '1.4fr 1fr auto' },
        gap: 2,
        alignItems: 'center',
        transition: 'transform 220ms ease, box-shadow 220ms ease',
        '&:hover': {
          transform: 'translateY(-2px)',
          boxShadow: '0 16px 40px rgba(11,58,91,0.10)',
        },
      }}
    >
      <Stack spacing={1}>
        <Stack direction="row" spacing={1} alignItems="center">
          <Chip size="small" label={flight.airlineCode} color="primary" variant="outlined" />
          <Typography fontWeight={700}>{flight.airlineName}</Typography>
          <Typography color="text.secondary">{flight.flightNumber}</Typography>
        </Stack>
        <Stack direction="row" spacing={2} alignItems="center">
          <Box>
            <Typography variant="h5" fontWeight={700}>{flight.sourceIata}</Typography>
            <Typography variant="body2" color="text.secondary">{flight.sourceCity}</Typography>
          </Box>
          <Stack alignItems="center" spacing={0.5} sx={{ minWidth: 100 }}>
            <Typography variant="caption" color="text.secondary">{formatDuration(flight.durationMinutes)}</Typography>
            <FlightIcon sx={{ color: 'sky.main', transform: 'rotate(90deg)' }} />
            <Typography variant="caption" color="text.secondary">{flight.cabinClass.replace('_', ' ')}</Typography>
          </Stack>
          <Box>
            <Typography variant="h5" fontWeight={700}>{flight.destIata}</Typography>
            <Typography variant="body2" color="text.secondary">{flight.destCity}</Typography>
          </Box>
        </Stack>
      </Stack>

      <Box>
        <Typography variant="body2" color="text.secondary">Departs</Typography>
        <Typography fontWeight={600}>{formatDateTime(flight.departureTime)}</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          {flight.availableSeats} seats left · {flight.baggageAllowanceKg}kg bag
        </Typography>
      </Box>

      <Stack spacing={1} alignItems={{ xs: 'stretch', md: 'flex-end' }}>
        <Typography variant="h5" color="primary.main" fontWeight={800}>
          {formatMoney(flight.basePrice, flight.currency)}
        </Typography>
        <Button component={RouterLink} to={`/booking/${flight.id}`} variant="contained">
          Book
        </Button>
      </Stack>
    </Box>
  )
}
