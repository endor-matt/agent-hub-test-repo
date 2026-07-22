import { FormEvent, useState } from 'react'
import { Alert, Box, Button, Container, Link, Paper, Stack, TextField, Typography } from '@mui/material'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { getErrorMessage } from '../api/client'

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const set = (key: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }))

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(form)
      navigate('/dashboard')
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Paper sx={{ p: { xs: 3, md: 4 }, borderRadius: 3 }}>
        <Typography variant="h4" gutterBottom>Create your SkyBook account</Typography>
        <Typography color="text.secondary" sx={{ mb: 3 }}>
          Join as a customer to search, book, and chat with Ask AI.
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={onSubmit}>
          <Stack spacing={2}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField label="First name" value={form.firstName} onChange={set('firstName')} required fullWidth />
              <TextField label="Last name" value={form.lastName} onChange={set('lastName')} required fullWidth />
            </Stack>
            <TextField label="Username" value={form.username} onChange={set('username')} required fullWidth />
            <TextField label="Email" type="email" value={form.email} onChange={set('email')} required fullWidth />
            <TextField label="Phone" value={form.phone} onChange={set('phone')} fullWidth />
            <TextField label="Password" type="password" value={form.password} onChange={set('password')} required fullWidth helperText="Min 8 characters" />
            <Button type="submit" variant="contained" size="large" disabled={loading}>
              {loading ? 'Creating…' : 'Register'}
            </Button>
          </Stack>
        </Box>
        <Typography sx={{ mt: 2 }}>
          Already have an account? <Link component={RouterLink} to="/login">Sign in</Link>
        </Typography>
      </Paper>
    </Container>
  )
}
