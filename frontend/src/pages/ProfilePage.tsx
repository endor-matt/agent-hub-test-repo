import { FormEvent, useState } from 'react'
import { Alert, Box, Button, Container, Paper, Stack, TextField, Typography } from '@mui/material'
import { useAuth } from '../contexts/AuthContext'
import { userApi } from '../api'
import { getErrorMessage } from '../api/client'

export function ProfilePage() {
  const { user } = useAuth()
  const [form, setForm] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    phone: user?.phone || '',
  })
  const [pw, setPw] = useState({ currentPassword: '', newPassword: '' })
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const saveProfile = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setMessage('')
    try {
      const updated = await userApi.update(form)
      localStorage.setItem('skybook_user', JSON.stringify(updated))
      setMessage('Profile updated')
      window.location.reload()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  const changePassword = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setMessage('')
    try {
      await userApi.changePassword(pw.currentPassword, pw.newPassword)
      setMessage('Password changed')
      setPw({ currentPassword: '', newPassword: '' })
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  return (
    <Container maxWidth="sm" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>Profile</Typography>
      {message && <Alert severity="success" sx={{ mb: 2 }}>{message}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ p: 3, mb: 3, borderRadius: 3 }}>
        <Typography variant="h6" gutterBottom>Account details</Typography>
        <Box component="form" onSubmit={saveProfile}>
          <Stack spacing={2}>
            <TextField label="Username" value={user?.username || ''} disabled fullWidth />
            <TextField label="Role" value={user?.role || ''} disabled fullWidth />
            <TextField label="First name" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required fullWidth />
            <TextField label="Last name" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required fullWidth />
            <TextField label="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required fullWidth />
            <TextField label="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} fullWidth />
            <Button type="submit" variant="contained">Save profile</Button>
          </Stack>
        </Box>
      </Paper>

      <Paper sx={{ p: 3, borderRadius: 3 }}>
        <Typography variant="h6" gutterBottom>Change password</Typography>
        <Box component="form" onSubmit={changePassword}>
          <Stack spacing={2}>
            <TextField label="Current password" type="password" value={pw.currentPassword} onChange={(e) => setPw({ ...pw, currentPassword: e.target.value })} required fullWidth />
            <TextField label="New password" type="password" value={pw.newPassword} onChange={(e) => setPw({ ...pw, newPassword: e.target.value })} required fullWidth />
            <Button type="submit" variant="outlined">Update password</Button>
          </Stack>
        </Box>
      </Paper>
    </Container>
  )
}
