import { FormEvent, useEffect, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  Container,
  MenuItem,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material'
import { DatePicker } from '@mui/x-date-pickers/DatePicker'
import dayjs, { Dayjs } from 'dayjs'
import { adminApi } from '../api'
import { getErrorMessage } from '../api/client'
import type { AuditExport, AuditLog } from '../types'
import { downloadBlob, formatDateTime } from '../utils/format'

const ACTIONS = [
  '',
  'USER_LOGIN',
  'USER_LOGOUT',
  'BOOKING_CREATED',
  'BOOKING_CANCELLED',
  'AI_QUERY',
  'PROFILE_UPDATE',
  'PASSWORD_CHANGE',
  'EXPORT_REQUEST',
]

export function AuditDashboardPage() {
  const [username, setUsername] = useState('')
  const [action, setAction] = useState('')
  const [dateFrom, setDateFrom] = useState<Dayjs | null>(dayjs().subtract(30, 'day'))
  const [dateTo, setDateTo] = useState<Dayjs | null>(dayjs())
  const [logs, setLogs] = useState<AuditLog[]>([])
  const [exportsList, setExportsList] = useState<AuditExport[]>([])
  const [error, setError] = useState('')
  const [total, setTotal] = useState(0)

  const params = () => ({
    username: username || undefined,
    action: action || undefined,
    dateFrom: dateFrom?.format('YYYY-MM-DD'),
    dateTo: dateTo?.format('YYYY-MM-DD'),
  })

  const load = async () => {
    setError('')
    try {
      const page = await adminApi.audit({ ...params(), page: 0, size: 100 })
      setLogs(page.content)
      setTotal(page.totalElements)
      const previous = await adminApi.exports()
      setExportsList(previous)
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const onSearch = (e: FormEvent) => {
    e.preventDefault()
    load()
  }

  const doExport = async (kind: 'csv' | 'excel' | 'monthly') => {
    try {
      let blob: Blob
      let name: string
      if (kind === 'csv') {
        blob = await adminApi.exportCsv(params())
        name = 'audit_export.csv'
      } else if (kind === 'excel') {
        blob = await adminApi.exportExcel(params())
        name = 'audit_export.xlsx'
      } else {
        blob = await adminApi.exportMonthly(dayjs().subtract(1, 'month').format('YYYY-MM'))
        name = 'audit_monthly.xlsx'
      }
      downloadBlob(blob, name)
      await load()
    } catch (err) {
      setError(getErrorMessage(err))
    }
  }

  return (
    <Container maxWidth="xl" sx={{ py: 5 }}>
      <Typography variant="h3" gutterBottom>Audit dashboard</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Search, filter, and export audit events for the SkyBook training lab.
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box component="form" onSubmit={onSearch} sx={{ p: 3, mb: 3, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
          <TextField label="Username" value={username} onChange={(e) => setUsername(e.target.value)} fullWidth />
          <TextField select label="Action" value={action} onChange={(e) => setAction(e.target.value)} fullWidth>
            {ACTIONS.map((a) => (
              <MenuItem key={a || 'all'} value={a}>{a || 'All actions'}</MenuItem>
            ))}
          </TextField>
          <DatePicker label="From" value={dateFrom} onChange={setDateFrom} slotProps={{ textField: { fullWidth: true } }} />
          <DatePicker label="To" value={dateTo} onChange={setDateTo} slotProps={{ textField: { fullWidth: true } }} />
          <Button type="submit" variant="contained">Search</Button>
        </Stack>
        <Stack direction="row" spacing={1} sx={{ mt: 2 }} flexWrap="wrap" useFlexGap>
          <Button variant="outlined" onClick={() => doExport('csv')}>Export CSV</Button>
          <Button variant="outlined" onClick={() => doExport('excel')}>Export Excel</Button>
          <Button variant="outlined" onClick={() => doExport('monthly')}>Monthly export</Button>
        </Stack>
      </Box>

      <Typography sx={{ mb: 1 }} color="text.secondary">{total} matching events</Typography>
      <Box sx={{ overflowX: 'auto', mb: 4, borderRadius: 2, border: '1px solid', borderColor: 'divider', bgcolor: '#fff' }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Time</TableCell>
              <TableCell>User</TableCell>
              <TableCell>Role</TableCell>
              <TableCell>Action</TableCell>
              <TableCell>Resource</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>IP</TableCell>
              <TableCell>Browser / OS</TableCell>
              <TableCell>ms</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {logs.map((l) => (
              <TableRow key={l.auditId} hover>
                <TableCell>{formatDateTime(l.timestamp)}</TableCell>
                <TableCell>{l.username}</TableCell>
                <TableCell>{l.role}</TableCell>
                <TableCell>{l.action}</TableCell>
                <TableCell sx={{ maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis' }}>{l.resource}</TableCell>
                <TableCell>{l.responseStatus}</TableCell>
                <TableCell>{l.ipAddress}</TableCell>
                <TableCell>{l.browser} / {l.operatingSystem}</TableCell>
                <TableCell>{l.executionTimeMs}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Box>

      <Typography variant="h5" gutterBottom>Previous exports</Typography>
      <Stack spacing={1}>
        {exportsList.map((e) => (
          <Box key={e.id} sx={{ p: 2, borderRadius: 2, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography fontWeight={600}>{e.fileName}</Typography>
            <Typography variant="body2" color="text.secondary">
              {e.exportType} · {e.rowCount} rows · {formatDateTime(e.createdAt)} · by {e.requestedByUsername}
            </Typography>
          </Box>
        ))}
        {!exportsList.length && <Typography color="text.secondary">No exports yet.</Typography>}
      </Stack>
    </Container>
  )
}
