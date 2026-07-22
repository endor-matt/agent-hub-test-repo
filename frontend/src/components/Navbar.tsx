import { Link as RouterLink, useNavigate } from 'react-router-dom'
import {
  AppBar,
  Box,
  Button,
  Container,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Stack,
  Toolbar,
  Typography,
  useMediaQuery,
  useTheme,
} from '@mui/material'
import MenuIcon from '@mui/icons-material/Menu'
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff'
import { useState } from 'react'
import { useAuth } from '../contexts/AuthContext'

const publicLinks = [
  { to: '/search', label: 'Search flights' },
]

export function Navbar() {
  const { isAuthenticated, isAdmin, user, logout } = useAuth()
  const navigate = useNavigate()
  const theme = useTheme()
  const mobile = useMediaQuery(theme.breakpoints.down('md'))
  const [open, setOpen] = useState(false)

  const links = [
    ...publicLinks,
    ...(isAuthenticated
      ? [
          { to: '/dashboard', label: 'Dashboard' },
          { to: '/history', label: 'My bookings' },
          { to: '/ai-chat', label: 'Ask AI' },
          { to: '/profile', label: 'Profile' },
        ]
      : []),
    ...(isAdmin
      ? [
          { to: '/admin', label: 'Admin' },
          { to: '/admin/audit', label: 'Audit' },
        ]
      : []),
  ]

  const handleLogout = async () => {
    await logout()
    navigate('/')
  }

  const navButtons = (
    <Stack direction={mobile ? 'column' : 'row'} spacing={1} alignItems={mobile ? 'stretch' : 'center'}>
      {links.map((l) => (
        <Button key={l.to} component={RouterLink} to={l.to} color="inherit" onClick={() => setOpen(false)}>
          {l.label}
        </Button>
      ))}
      {isAuthenticated ? (
        <>
          <Typography variant="body2" sx={{ px: 1, opacity: 0.85 }}>
            {user?.firstName}
          </Typography>
          <Button variant="outlined" color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        </>
      ) : (
        <>
          <Button component={RouterLink} to="/login" color="inherit">
            Login
          </Button>
          <Button component={RouterLink} to="/register" variant="contained" color="secondary">
            Register
          </Button>
        </>
      )}
    </Stack>
  )

  return (
    <AppBar position="sticky" elevation={0} sx={{ bgcolor: 'primary.main', borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ minHeight: 72 }}>
          <Stack direction="row" spacing={1} alignItems="center" component={RouterLink} to="/" sx={{ color: 'inherit', textDecoration: 'none', mr: 3 }}>
            <FlightTakeoffIcon sx={{ color: 'sky.main' }} />
            <Typography variant="h6" sx={{ fontFamily: 'Sora, Manrope, sans-serif', fontWeight: 700, letterSpacing: '-0.02em' }}>
              SkyBook AI
            </Typography>
          </Stack>
          <Box sx={{ flexGrow: 1 }} />
          {mobile ? (
            <>
              <IconButton color="inherit" onClick={() => setOpen(true)} aria-label="Open menu">
                <MenuIcon />
              </IconButton>
              <Drawer anchor="right" open={open} onClose={() => setOpen(false)}>
                <Box sx={{ width: 280, p: 2 }}>{navButtons}</Box>
              </Drawer>
            </>
          ) : (
            navButtons
          )}
        </Toolbar>
      </Container>
    </AppBar>
  )
}
