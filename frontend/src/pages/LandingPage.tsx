import { Box, Button, Container, Stack, Typography } from '@mui/material'
import { Link as RouterLink } from 'react-router-dom'
import { keyframes } from '@mui/system'

const drift = keyframes`
  0% { transform: translateX(-2%) translateY(0); }
  50% { transform: translateX(2%) translateY(-8px); }
  100% { transform: translateX(-2%) translateY(0); }
`

const fadeUp = keyframes`
  from { opacity: 0; transform: translateY(18px); }
  to { opacity: 1; transform: translateY(0); }
`

export function LandingPage() {
  return (
    <Box
      sx={{
        minHeight: { xs: 'calc(100vh - 72px)', md: '78vh' },
        position: 'relative',
        overflow: 'hidden',
        color: '#fff',
        background: `
          radial-gradient(circle at 15% 20%, rgba(94,179,240,0.35), transparent 40%),
          radial-gradient(circle at 80% 10%, rgba(31,164,160,0.28), transparent 35%),
          linear-gradient(145deg, #062538 0%, #0B3A5B 45%, #134E75 100%)
        `,
      }}
    >
      <Box
        aria-hidden
        sx={{
          position: 'absolute',
          inset: 0,
          backgroundImage:
            'url("data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.04\'%3E%3Cpath d=\'M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")',
        }}
      />
      <Box
        aria-hidden
        sx={{
          position: 'absolute',
          right: { xs: '-20%', md: '5%' },
          bottom: { xs: '8%', md: '12%' },
          width: { xs: 280, md: 520 },
          height: { xs: 140, md: 260 },
          borderRadius: '50%',
          background: 'linear-gradient(90deg, rgba(94,179,240,0.15), rgba(255,255,255,0.08))',
          filter: 'blur(2px)',
          animation: `${drift} 9s ease-in-out infinite`,
        }}
      />
      <Container maxWidth="lg" sx={{ position: 'relative', pt: { xs: 8, md: 14 }, pb: { xs: 10, md: 16 } }}>
        <Stack spacing={3} sx={{ maxWidth: 680, animation: `${fadeUp} 700ms ease both` }}>
          <Typography
            component="h1"
            sx={{
              fontFamily: 'Sora, Manrope, sans-serif',
              fontWeight: 800,
              fontSize: { xs: '2.6rem', md: '4.2rem' },
              lineHeight: 1.05,
              letterSpacing: '-0.03em',
            }}
          >
            SkyBook AI
          </Typography>
          <Typography variant="h5" sx={{ fontWeight: 500, color: 'rgba(255,255,255,0.88)', maxWidth: 520 }}>
            Book flights with clarity — search routes, pick seats, and get instant travel answers.
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ pt: 1 }}>
            <Button
              component={RouterLink}
              to="/search"
              size="large"
              variant="contained"
              color="secondary"
              sx={{ px: 3.5, py: 1.4, fontSize: '1.05rem' }}
            >
              Search flights
            </Button>
            <Button
              component={RouterLink}
              to="/register"
              size="large"
              variant="outlined"
              sx={{ px: 3.5, py: 1.4, color: '#fff', borderColor: 'rgba(255,255,255,0.45)' }}
            >
              Create account
            </Button>
          </Stack>
        </Stack>
      </Container>
    </Box>
  )
}
