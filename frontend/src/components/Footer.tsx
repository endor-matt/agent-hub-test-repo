import { Box, Container, Link, Stack, Typography } from '@mui/material'
import { Link as RouterLink } from 'react-router-dom'

export function Footer() {
  return (
    <Box component="footer" sx={{ mt: 'auto', py: 4, bgcolor: '#062538', color: 'rgba(255,255,255,0.82)' }}>
      <Container maxWidth="lg">
        <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" spacing={2}>
          <Box>
            <Typography variant="h6" sx={{ color: '#fff', fontFamily: 'Sora, Manrope, sans-serif', mb: 1 }}>
              SkyBook AI
            </Typography>
            <Typography variant="body2" sx={{ maxWidth: 420 }}>
              A modern airline booking portal for security research and training. Intentionally not production-secure.
            </Typography>
          </Box>
          <Stack direction="row" spacing={3}>
            <Link component={RouterLink} to="/search" color="inherit" underline="hover">
              Flights
            </Link>
            <Link component={RouterLink} to="/ai-chat" color="inherit" underline="hover">
              Ask AI
            </Link>
            <Link component={RouterLink} to="/login" color="inherit" underline="hover">
              Sign in
            </Link>
          </Stack>
        </Stack>
        <Typography variant="caption" display="block" sx={{ mt: 3, opacity: 0.7 }}>
          © {new Date().getFullYear()} SkyBook AI Lab · Training environment only
        </Typography>
      </Container>
    </Box>
  )
}
