import { useRef, useState } from 'react'
import {
  Box,
  Button,
  Container,
  IconButton,
  Stack,
  TextField,
  Typography,
} from '@mui/material'
import SendIcon from '@mui/icons-material/Send'
import { chatApi } from '../api'
import { getErrorMessage } from '../api/client'
import type { ChatMessage } from '../types'

export function AiChatPage() {
  const [sessionId] = useState(() => `chat-${crypto.randomUUID()}`)
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      role: 'assistant',
      content:
        'Welcome to Ask SkyBook AI. I can help with flight search tips, refund policies, baggage allowance, booking help, and travel FAQs.',
    },
  ])
  const endRef = useRef<HTMLDivElement>(null)

  const send = async () => {
    const text = input.trim()
    if (!text || loading) return
    setInput('')
    setMessages((m) => [...m, { role: 'user', content: text }])
    setLoading(true)
    try {
      const res = await chatApi.send(text, sessionId)
      setMessages((m) => [...m, { role: 'assistant', content: res.reply, intent: res.intent }])
    } catch (err) {
      setMessages((m) => [
        ...m,
        {
          role: 'assistant',
          content: `AI service unavailable (${getErrorMessage(err)}). Start the Python AI service in Phase 5/6 to enable live answers.`,
        },
      ])
    } finally {
      setLoading(false)
      setTimeout(() => endRef.current?.scrollIntoView({ behavior: 'smooth' }), 50)
    }
  }

  return (
    <Container maxWidth="md" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>Ask AI Assistant</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Conversations are stored when the AI service is online.
      </Typography>
      <Box
        sx={{
          height: { xs: 420, md: 520 },
          borderRadius: 3,
          border: '1px solid',
          borderColor: 'divider',
          bgcolor: '#F3F8FB',
          p: 2,
          overflowY: 'auto',
          mb: 2,
        }}
      >
        {messages.map((m, i) => (
          <Box
            key={i}
            sx={{
              mb: 1.5,
              maxWidth: '80%',
              ml: m.role === 'user' ? 'auto' : 0,
              p: 1.5,
              borderRadius: 2,
              bgcolor: m.role === 'user' ? 'primary.main' : '#fff',
              color: m.role === 'user' ? '#fff' : 'text.primary',
              whiteSpace: 'pre-wrap',
            }}
          >
            <Typography variant="body2">{m.content}</Typography>
          </Box>
        ))}
        <div ref={endRef} />
      </Box>
      <Stack direction="row" spacing={1}>
        <TextField
          fullWidth
          placeholder="e.g. What is the international baggage allowance?"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && send()}
        />
        <IconButton color="primary" onClick={send} disabled={loading} aria-label="Send">
          <SendIcon />
        </IconButton>
        <Button variant="contained" onClick={send} disabled={loading}>
          Send
        </Button>
      </Stack>
    </Container>
  )
}
