import { describe, expect, it } from 'vitest'
import { formatDuration, formatMoney } from '../utils/format'

describe('format utils', () => {
  it('formats money', () => {
    expect(formatMoney(249)).toContain('249')
  })

  it('formats duration', () => {
    expect(formatDuration(210)).toBe('3h 30m')
  })
})
