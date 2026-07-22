-- ============================================================================
-- SkyBook AI — Database Schema
-- MySQL 8.x
-- Environment: Security research & training lab ONLY (not production-secure)
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS skybook
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE skybook;

-- ---------------------------------------------------------------------------
-- Roles
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
    id              CHAR(36)        NOT NULL,
    name            VARCHAR(32)     NOT NULL,
    description     VARCHAR(255)    NULL,
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Users
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id              CHAR(36)        NOT NULL,
    username        VARCHAR(64)     NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    phone           VARCHAR(32)     NULL,
    role_id         CHAR(36)        NOT NULL,
    status          ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_role_id (role_id),
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Refresh Tokens
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS refresh_tokens;
CREATE TABLE refresh_tokens (
    id              CHAR(36)        NOT NULL,
    user_id         CHAR(36)        NOT NULL,
    token_hash      VARCHAR(255)    NOT NULL,
    expires_at      TIMESTAMP(3)    NOT NULL,
    revoked         TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    revoked_at      TIMESTAMP(3)    NULL,
    user_agent      VARCHAR(512)    NULL,
    ip_address      VARCHAR(64)     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_hash (token_hash),
    KEY idx_refresh_tokens_user_id (user_id),
    KEY idx_refresh_tokens_expires (expires_at),
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Airlines
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS airlines;
CREATE TABLE airlines (
    id              CHAR(36)        NOT NULL,
    code            VARCHAR(8)      NOT NULL,
    name            VARCHAR(150)    NOT NULL,
    country         VARCHAR(100)    NOT NULL,
    logo_url        VARCHAR(512)    NULL,
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_airlines_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Airports
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS airports;
CREATE TABLE airports (
    id              CHAR(36)        NOT NULL,
    iata_code       CHAR(3)         NOT NULL,
    icao_code       CHAR(4)         NULL,
    name            VARCHAR(200)    NOT NULL,
    city            VARCHAR(100)    NOT NULL,
    country         VARCHAR(100)    NOT NULL,
    timezone        VARCHAR(64)     NOT NULL DEFAULT 'UTC',
    latitude        DECIMAL(10, 7)  NULL,
    longitude       DECIMAL(10, 7)  NULL,
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_airports_iata (iata_code),
    KEY idx_airports_city (city),
    KEY idx_airports_country (country)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Flights
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS flights;
CREATE TABLE flights (
    id                  CHAR(36)        NOT NULL,
    flight_number       VARCHAR(16)     NOT NULL,
    airline_id          CHAR(36)        NOT NULL,
    source_airport_id   CHAR(36)        NOT NULL,
    dest_airport_id     CHAR(36)        NOT NULL,
    departure_time      TIMESTAMP(3)    NOT NULL,
    arrival_time        TIMESTAMP(3)    NOT NULL,
    duration_minutes    INT             NOT NULL,
    aircraft_type       VARCHAR(64)     NULL,
    cabin_class         ENUM('ECONOMY', 'PREMIUM_ECONOMY', 'BUSINESS', 'FIRST')
                                        NOT NULL DEFAULT 'ECONOMY',
    base_price          DECIMAL(12, 2)  NOT NULL,
    currency            CHAR(3)         NOT NULL DEFAULT 'USD',
    total_seats         INT             NOT NULL,
    available_seats     INT             NOT NULL,
    status              ENUM('SCHEDULED', 'DELAYED', 'CANCELLED', 'COMPLETED')
                                        NOT NULL DEFAULT 'SCHEDULED',
    baggage_allowance_kg INT            NOT NULL DEFAULT 23,
    version             INT             NOT NULL DEFAULT 0 COMMENT 'Optimistic lock',
    created_at          TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_flights_number_departure (flight_number, departure_time),
    KEY idx_flights_route_date (source_airport_id, dest_airport_id, departure_time),
    KEY idx_flights_airline (airline_id),
    KEY idx_flights_price (base_price),
    KEY idx_flights_status (status),
    CONSTRAINT fk_flights_airline
        FOREIGN KEY (airline_id) REFERENCES airlines (id),
    CONSTRAINT fk_flights_source
        FOREIGN KEY (source_airport_id) REFERENCES airports (id),
    CONSTRAINT fk_flights_dest
        FOREIGN KEY (dest_airport_id) REFERENCES airports (id),
    CONSTRAINT chk_flights_seats CHECK (available_seats >= 0 AND available_seats <= total_seats),
    CONSTRAINT chk_flights_times CHECK (arrival_time > departure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Bookings
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS bookings;
CREATE TABLE bookings (
    id                  CHAR(36)        NOT NULL,
    booking_reference   VARCHAR(12)     NOT NULL,
    user_id             CHAR(36)        NOT NULL,
    flight_id           CHAR(36)        NOT NULL,
    status              ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')
                                        NOT NULL DEFAULT 'CONFIRMED',
    passenger_count     INT             NOT NULL DEFAULT 1,
    passengers_json     JSON            NOT NULL COMMENT '[{firstName,lastName,dob,passport,seat}]',
    seats               VARCHAR(255)    NOT NULL COMMENT 'Comma-separated seats e.g. 12A,12B',
    total_amount        DECIMAL(12, 2)  NOT NULL,
    currency            CHAR(3)         NOT NULL DEFAULT 'USD',
    contact_email       VARCHAR(255)    NOT NULL,
    contact_phone       VARCHAR(32)     NULL,
    cancelled_at        TIMESTAMP(3)    NULL,
    cancellation_reason VARCHAR(500)    NULL,
    created_at          TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    UNIQUE KEY uk_bookings_reference (booking_reference),
    KEY idx_bookings_user (user_id),
    KEY idx_bookings_flight (flight_id),
    KEY idx_bookings_status (status),
    KEY idx_bookings_created (created_at),
    CONSTRAINT fk_bookings_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_bookings_flight
        FOREIGN KEY (flight_id) REFERENCES flights (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Audit Logs
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS audit_logs;
CREATE TABLE audit_logs (
    id                  CHAR(36)        NOT NULL,
    timestamp           TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    username            VARCHAR(64)     NULL,
    user_id             CHAR(36)        NULL,
    role                VARCHAR(32)     NULL,
    ip_address          VARCHAR(64)     NULL,
    session_id          VARCHAR(128)    NULL,
    action              VARCHAR(64)     NOT NULL,
    resource            VARCHAR(512)    NULL,
    http_method         VARCHAR(16)     NULL,
    response_status     INT             NULL,
    browser             VARCHAR(128)    NULL,
    operating_system    VARCHAR(128)    NULL,
    execution_time_ms   INT             NULL,
    details             JSON            NULL,
    PRIMARY KEY (id),
    KEY idx_audit_timestamp (timestamp),
    KEY idx_audit_username (username),
    KEY idx_audit_user_id (user_id),
    KEY idx_audit_action (action),
    KEY idx_audit_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Chat History
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS chat_history;
CREATE TABLE chat_history (
    id              CHAR(36)        NOT NULL,
    session_id      VARCHAR(64)     NOT NULL,
    user_id         CHAR(36)        NULL,
    role            ENUM('user', 'assistant', 'system') NOT NULL,
    content         TEXT            NOT NULL,
    intent          VARCHAR(64)     NULL COMMENT 'e.g. flight_search, baggage, refund',
    metadata_json   JSON            NULL,
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_chat_session (session_id),
    KEY idx_chat_user (user_id),
    KEY idx_chat_created (created_at),
    CONSTRAINT fk_chat_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Audit export history (admin: retrieve previous exports)
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS audit_exports;
CREATE TABLE audit_exports (
    id              CHAR(36)        NOT NULL,
    requested_by    CHAR(36)        NOT NULL,
    export_type     ENUM('CSV', 'EXCEL', 'MONTHLY') NOT NULL,
    date_from       DATE            NULL,
    date_to         DATE            NULL,
    filter_username VARCHAR(64)     NULL,
    filter_action   VARCHAR(64)     NULL,
    file_name       VARCHAR(255)    NOT NULL,
    row_count       INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    KEY idx_audit_exports_requester (requested_by),
    KEY idx_audit_exports_created (created_at),
    CONSTRAINT fk_audit_exports_user
        FOREIGN KEY (requested_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- End of schema
-- ============================================================================
