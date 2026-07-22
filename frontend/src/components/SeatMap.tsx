import { Box, Button, Stack, Typography } from '@mui/material'

const ROWS = 8
const COLS = ['A', 'B', 'C', 'D', 'E', 'F']

interface SeatMapProps {
  selected: string[]
  maxSeats: number
  onChange: (seats: string[]) => void
}

export function SeatMap({ selected, maxSeats, onChange }: SeatMapProps) {
  const toggle = (seat: string) => {
    if (selected.includes(seat)) {
      onChange(selected.filter((s) => s !== seat))
      return
    }
    if (selected.length >= maxSeats) return
    onChange([...selected, seat])
  }

  return (
    <Box>
      <Typography variant="subtitle1" fontWeight={700} gutterBottom>
        Select seats ({selected.length}/{maxSeats})
      </Typography>
      <Box
        sx={{
          p: 2,
          borderRadius: 3,
          bgcolor: 'rgba(11,58,91,0.04)',
          border: '1px dashed rgba(11,58,91,0.2)',
        }}
      >
        {Array.from({ length: ROWS }, (_, rowIdx) => {
          const row = rowIdx + 8
          return (
            <Stack key={row} direction="row" spacing={1} justifyContent="center" sx={{ mb: 1 }}>
              {COLS.map((col, colIdx) => {
                const seat = `${row}${col}`
                const active = selected.includes(seat)
                const aisle = colIdx === 2
                return (
                  <Box key={seat} sx={{ display: 'flex', alignItems: 'center' }}>
                    <Button
                      size="small"
                      variant={active ? 'contained' : 'outlined'}
                      color={active ? 'secondary' : 'primary'}
                      onClick={() => toggle(seat)}
                      sx={{ minWidth: 44, px: 0 }}
                    >
                      {seat}
                    </Button>
                    {aisle && <Box sx={{ width: 18 }} />}
                  </Box>
                )
              })}
            </Stack>
          )
        })}
      </Box>
    </Box>
  )
}
