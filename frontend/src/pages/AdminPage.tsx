import { useEffect, useState } from 'react'
import { Link as RouterLink } from 'react-router-dom'
import { Box, Button, Container, Stack, Typography } from '@mui/material'
import { adminApi } from '../api'
import type { User } from '../types'

export function AdminPage() {
  const [users, setUsers] = useState<User[]>([])

  useEffect(() => {
    adminApi.users().then(setUsers).catch(() => setUsers([]))
  }, [])

  return (
    <Container maxWidth="lg" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>Admin dashboard</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Oversee users and jump into the audit log workspace.
      </Typography>
      <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
        <Button component={RouterLink} to="/admin/audit" variant="contained">Open audit dashboard</Button>
      </Stack>
      <Typography variant="h5" gutterBottom>Users ({users.length})</Typography>
      <Box sx={{ display: 'grid', gap: 1.5 }}>
        {users.map((u) => (
          <Box key={u.id} sx={{ p: 2, borderRadius: 2, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography fontWeight={700}>{u.username} · {u.role}</Typography>
            <Typography color="text.secondary">{u.firstName} {u.lastName} · {u.email}</Typography>
          </Box>
        ))}
      </Box>
    </Container>
  )
}
