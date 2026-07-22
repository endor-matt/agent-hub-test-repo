import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { ThemeProvider } from '@mui/material'
import { describe, expect, it } from 'vitest'
import { LandingPage } from '../pages/LandingPage'
import { theme } from '../theme/theme'

describe('LandingPage', () => {
  it('shows SkyBook AI brand as hero', () => {
    render(
      <ThemeProvider theme={theme}>
        <MemoryRouter>
          <LandingPage />
        </MemoryRouter>
      </ThemeProvider>,
    )
    expect(screen.getByRole('heading', { name: 'SkyBook AI' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /search flights/i })).toBeInTheDocument()
  })
})
