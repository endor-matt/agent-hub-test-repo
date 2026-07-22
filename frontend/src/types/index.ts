export interface User {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  phone?: string
  role: 'CUSTOMER' | 'ADMIN'
  status: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresInMinutes: number
  user: User
}

export interface Flight {
  id: string
  flightNumber: string
  airlineCode: string
  airlineName: string
  sourceIata: string
  sourceCity: string
  destIata: string
  destCity: string
  departureTime: string
  arrivalTime: string
  durationMinutes: number
  aircraftType?: string
  cabinClass: string
  basePrice: number
  currency: string
  availableSeats: number
  totalSeats: number
  status: string
  baggageAllowanceKg: number
}

export interface Airline {
  id: string
  code: string
  name: string
  country: string
}

export interface Airport {
  id: string
  iataCode: string
  name: string
  city: string
  country: string
  timezone: string
}

export interface Passenger {
  firstName: string
  lastName: string
  dob?: string
  passport?: string
}

export interface Booking {
  id: string
  bookingReference: string
  status: string
  flightId: string
  flightNumber: string
  airlineName: string
  sourceIata: string
  destIata: string
  departureTime: string
  arrivalTime: string
  passengerCount: number
  seats: string[]
  passengersJson: string
  totalAmount: number
  currency: string
  contactEmail: string
  contactPhone?: string
  createdAt: string
  cancelledAt?: string
  cancellationReason?: string
}

export interface AuditLog {
  auditId: string
  timestamp: string
  username?: string
  userId?: string
  role?: string
  ipAddress?: string
  sessionId?: string
  action: string
  resource?: string
  httpMethod?: string
  responseStatus?: number
  browser?: string
  operatingSystem?: string
  executionTimeMs?: number
  details?: string
}

export interface AuditExport {
  id: string
  exportType: string
  dateFrom?: string
  dateTo?: string
  filterUsername?: string
  filterAction?: string
  fileName: string
  rowCount: number
  createdAt: string
  requestedByUsername?: string
}

export interface ChatMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
  intent?: string
}
