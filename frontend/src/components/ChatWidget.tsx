import { useEffect, useRef, useState } from 'react'
import {
  Box,
  Fab,
  IconButton,
  Paper,
  Stack,
  TextField,
  Typography,
  CircularProgress,
} from '@mui/material'
import ChatIcon from '@mui/icons-material/Chat'
import CloseIcon from '@mui/icons-material/Close'
import SendIcon from '@mui/icons-material/Send'
import { chatApi, adminApi } from '../api'
import { getErrorMessage } from '../api/client'
import { useAuth } from '../contexts/AuthContext'
import type { ChatMessage } from '../types'

function newSessionId() {
  return `chat-${crypto.randomUUID()}`
}

const FALLBACK_REPLY =
  'AI service is offline. In the full lab stack (Phase 5+), I can help with flights, baggage, refunds, and FAQs. Try asking about baggage allowance or refund policy once the AI service is running.'

export function ChatWidget() {
  const { isAdmin } = useAuth()
  const [open, setOpen] = useState(false)
  const [sessionId] = useState(newSessionId)
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      role: 'assistant',
      content: 'Hi — I am the SkyBook AI assistant. Ask about flights, baggage, refunds, or booking help.',
    },
  ])
  const bottomRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, open])

  const send = async () => {
    const text = input.trim()
    if (!text || loading) return
    setInput('')
    setMessages((m) => [...m, { role: 'user', content: text }])
    setLoading(true)
    try {
      const res = await chatApi.send(text, sessionId)
      setMessages((m) => [...m, { role: 'assistant', content: res.reply, intent: res.intent }])
      if (isAdmin) {
        try {
          await adminApi.recordAiQuery({ sessionId, message: text, intent: res.intent })
        } catch {
          /* optional */
        }
      }
    } catch (err) {
      setMessages((m) => [
        ...m,
        {
          role: 'assistant',
          content: `${FALLBACK_REPLY}\n\n(${getErrorMessage(err)})`,
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <Fab
        color="secondary"
        aria-label="Open AI chat"
        onClick={() => setOpen(true)}
        sx={{ position: 'fixed', right: 24, bottom: 24, zIndex: 1300 }}
      >
        <ChatIcon />
      </Fab>

      {open && (
        <Paper
          elevation={8}
          sx={{
            position: 'fixed',
            right: 24,
            bottom: 96,
            width: { xs: 'calc(100vw - 32px)', sm: 380 },
            height: 480,
            zIndex: 1300,
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden',
            borderRadius: 3,
          }}
        >
          <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ px: 2, py: 1.5, bgcolor: 'primary.main', color: '#fff' }}>
            <Typography fontWeight={700}>Ask SkyBook AI</Typography>
            <IconButton size="small" onClick={() => setOpen(false)} sx={{ color: '#fff' }} aria-label="Close chat">
              <CloseIcon />
            </IconButton>
          </Stack>
          <Box sx={{ flex: 1, overflowY: 'auto', p: 2, bgcolor: '#F3F8FB' }}>
            {messages.map((m, idx) => (
              <Box
                key={idx}
                sx={{
                  mb: 1.5,
                  maxWidth: '85%',
                  ml: m.role === 'user' ? 'auto' : 0,
                  px: 1.5,
                  py: 1,
                  borderRadius: 2,
                  bgcolor: m.role === 'user' ? 'primary.main' : '#fff',
                  color: m.role === 'user' ? '#fff' : 'text.primary',
                  border: m.role === 'user' ? 'none' : '1px solid rgba(11,58,91,0.08)',
                  whiteSpace: 'pre-wrap',
                }}
              >
                <Typography variant="body2">{m.content}</Typography>
              </Box>
            ))}
            {loading && (
              <Stack direction="row" spacing={1} alignItems="center">
                <CircularProgress size={16} />
                <Typography variant="caption">Thinking…</Typography>
              </Stack>
            )}
            <div ref={bottomRef} />
          </Box>
          <Stack direction="row" spacing={1} sx={{ p: 1.5, borderTop: '1px solid', borderColor: 'divider' }}>
            <TextField
              size="small"
              fullWidth
              placeholder="Ask about flights, refunds…"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && send()}
            />
            <IconButton color="primary" onClick={send} disabled={loading} aria-label="Send">
              <SendIcon />
            </IconButton>
          </Stack>
        </Paper>
      )}
    </>
  )
}
