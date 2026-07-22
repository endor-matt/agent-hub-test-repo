import { createTheme } from '@mui/material/styles'

declare module '@mui/material/styles' {
  interface Palette {
    sky: Palette['primary']
  }
  interface PaletteOptions {
    sky?: PaletteOptions['primary']
  }
}

export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#0B3A5B',
      light: '#1A5A85',
      dark: '#062538',
      contrastText: '#F4F9FC',
    },
    secondary: {
      main: '#1FA4A0',
      light: '#4BC0BC',
      dark: '#157875',
      contrastText: '#FFFFFF',
    },
    sky: {
      main: '#5EB3F0',
      light: '#9DD2F7',
      dark: '#2E8BC8',
      contrastText: '#062538',
    },
    background: {
      default: '#EEF4F8',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#0F2433',
      secondary: '#4A6578',
    },
    divider: 'rgba(11, 58, 91, 0.12)',
  },
  typography: {
    fontFamily: '"Manrope", "Helvetica", "Arial", sans-serif',
    h1: { fontFamily: '"Sora", "Manrope", sans-serif', fontWeight: 700 },
    h2: { fontFamily: '"Sora", "Manrope", sans-serif', fontWeight: 700 },
    h3: { fontFamily: '"Sora", "Manrope", sans-serif', fontWeight: 600 },
    h4: { fontFamily: '"Sora", "Manrope", sans-serif', fontWeight: 600 },
    h5: { fontFamily: '"Sora", "Manrope", sans-serif', fontWeight: 600 },
    h6: { fontFamily: '"Sora", "Manrope", sans-serif', fontWeight: 600 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  shape: { borderRadius: 12 },
  components: {
    MuiButton: {
      styleOverrides: {
        root: { borderRadius: 10, paddingInline: 18 },
        containedPrimary: {
          boxShadow: '0 8px 20px rgba(11, 58, 91, 0.22)',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: { backgroundImage: 'none' },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: { backgroundImage: 'none' },
      },
    },
  },
})
