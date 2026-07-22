import { FormEvent, useState } from 'react'
import { Alert, Box, Button, Container, Link, Paper, Stack, TextField, Typography } from '@mui/material'
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { getErrorMessage } from '../api/client'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from || '/dashboard'
  const [username, setUsername] = useState('jdoe')
  const [password, setPassword] = useState('Customer@123')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await login(username, password)
      navigate(user.role === 'ADMIN' ? '/admin' : from)
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Paper sx={{ p: { xs: 3, md: 4 }, borderRadius: 3 }}>
        <Typography variant="h4" gutterBottom>Welcome back</Typography>
        <Typography color="text.secondary" sx={{ mb: 3 }}>
          Sign in to SkyBook AI. Lab demo: <code>jdoe / Customer@123</code>
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={onSubmit}>
          <Stack spacing={2}>
            <TextField label="Username" value={username} onChange={(e) => setUsername(e.target.value)} required fullWidth />
            <TextField label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required fullWidth />
            <Button type="submit" variant="contained" size="large" disabled={loading}>
              {loading ? 'Signing in…' : 'Sign in'}
            </Button>
          </Stack>
        </Box>
        <Typography sx={{ mt: 2 }}>
          New here? <Link component={RouterLink} to="/register">Create an account</Link>
        </Typography>
      </Paper>
    </Container>
  )
}
